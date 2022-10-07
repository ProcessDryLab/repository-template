package pdl.repository.miners;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.deckfour.xes.model.XLog;

import palia.algorithm.Palia;
import palia.model.Node;
import palia.model.TPA;
import palia.model.Transition;
import pdl.repository.types.TypeBPMN;
import pdl.repository.types.TypeXES;
import pdl.repository.utils.RepositoryUtils;
import pdl.repository.utils.XLogUtils;
import pdl.repository.web.models.Miner;
import pdl.repository.web.models.MinerConfiguration;

public class PaliaMiner implements IMiner {

	public static final String id = UUID.randomUUID().toString();
	public static final String PARAMETER_SOURCE = "Source file";

	@Override
	public Miner getMinerWebModel() {
		Miner m = new Miner();
		m.setId(id);
		m.setName("Palia Miner");
		m.addInput(PARAMETER_SOURCE, TypeXES.instance.getResourceType());
		m.addOutput(TypeBPMN.instance.getResourceType());
		return m;
	}

	@Override
	public void mine(MinerConfiguration configuration) {

		try {
			File sourceFile = RepositoryUtils.retrieveResource(configuration.getInputs().get(PARAMETER_SOURCE));
			XLog log = XLogUtils.parse(sourceFile);

			Palia palia = new Palia();
			TPA tpa = palia.mine(log);
			BpmnModelInstance bpmn = convert(tpa);

			File tmpFile = File.createTempFile("result-of-bpmn", ".bpmn");
			Bpmn.writeModelToFile(tmpFile, bpmn);

			RepositoryUtils.storeResource(tmpFile, configuration.getRepositoryHostname());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static BpmnModelInstance convert(TPA tpa) {
		BpmnModelInstance modelInstance = Bpmn.createEmptyModel();
		Definitions definitions = modelInstance.newInstance(Definitions.class);
		definitions.setTargetNamespace("http://camunda.org/examples");
		modelInstance.setDefinitions(definitions);
		Process process = modelInstance.newInstance(Process.class);
		process.setId("process");
		definitions.addChildElement(process);

		StartEvent startEvent = modelInstance.newInstance(StartEvent.class);
		process.addChildElement(startEvent);
		EndEvent endEvent = modelInstance.newInstance(EndEvent.class);
		process.addChildElement(endEvent);

		Map<UUID, FlowNode> idToStartNodes = new HashMap<>();
		Map<UUID, FlowNode> idToTargetNodes = new HashMap<>();

		for (Node n : tpa.getNodes()) {
			Task activityNode = modelInstance.newInstance(Task.class);
			activityNode.setName(n.getName());
			process.addChildElement(activityNode);

			idToStartNodes.put(n.getId(), activityNode);
			idToTargetNodes.put(n.getId(), activityNode);

			if (n.isStartingNode()) {
				addSequenceFlow(process, startEvent, activityNode);
			}
			if (n.isFinalNode()) {
				addSequenceFlow(process, activityNode, endEvent);
			}
		}

		for (Node n : tpa.getNodes()) {
			Set<Transition> outgoing = n.getOutTransitions(false);
			if (outgoing.size() > 1) {
				Gateway gateway = modelInstance.newInstance(ExclusiveGateway.class);
				process.addChildElement(gateway);
				addSequenceFlow(process, idToStartNodes.get(n.getId()), gateway);
				idToStartNodes.put(n.getId(), gateway);
			}

			Set<Transition> incoming = n.getInTransitions(false);
			if (incoming.size() > 1) {
				Gateway gateway = modelInstance.newInstance(ExclusiveGateway.class);
				process.addChildElement(gateway);
				addSequenceFlow(process, gateway, idToTargetNodes.get(n.getId()));
				idToTargetNodes.put(n.getId(), gateway);
			}
		}

		for (Transition t : tpa.getTransitions()) {
			Collection<Node> sources = t.getSourceNodes();
			Collection<Node> targets = t.getEndNodes();

			// sequence flow or XOR gateway
			if (sources.size() == 1 && targets.size() == 1) {
				FlowNode sourceNode = idToStartNodes.get(sources.stream().findAny().get().getId());
				FlowNode targetNode = idToTargetNodes.get(targets.stream().findAny().get().getId());
				addSequenceFlow(process, sourceNode, targetNode);
			}

			// parallel split
			if (sources.size() == 1 && targets.size() > 1) {
				FlowNode sourceNode = idToStartNodes.get(sources.stream().findAny().get().getId());
				Gateway gateway = modelInstance.newInstance(ParallelGateway.class);
				process.addChildElement(gateway);
				addSequenceFlow(process, sourceNode, gateway);
				sourceNode = gateway;

				for (Node target : targets) {
					FlowNode targetNode = idToTargetNodes.get(target.getId());
					addSequenceFlow(process, sourceNode, targetNode);
				}
			}

			// parallel join
			if (sources.size() > 1 && targets.size() == 1) {
				FlowNode targetNode = idToTargetNodes.get(targets.stream().findAny().get().getId());
				Gateway gateway = modelInstance.newInstance(ParallelGateway.class);
				process.addChildElement(gateway);
				addSequenceFlow(process, gateway, targetNode);
				targetNode = gateway;

				for (Node source : sources) {
					FlowNode sourceNode = idToStartNodes.get(source.getId());
					addSequenceFlow(process, sourceNode, targetNode);
				}
			}

			// parallel join AND parallel split
			if (sources.size() > 1 && targets.size() > 1) {
				Gateway gateway1 = modelInstance.newInstance(ParallelGateway.class);
				process.addChildElement(gateway1);
				Gateway gateway2 = modelInstance.newInstance(ParallelGateway.class);
				process.addChildElement(gateway2);
				addSequenceFlow(process, gateway1, gateway2);
				for (Node source : sources) {
					addSequenceFlow(process, idToStartNodes.get(source.getId()), gateway1);
				}
				for (Node target : targets) {
					addSequenceFlow(process, gateway2, idToTargetNodes.get(target.getId()));
				}
			}
		}

		return modelInstance;
	}

	private static void addSequenceFlow(Process process, FlowNode source, FlowNode target) {
		BpmnModelInstance modelInstance = (BpmnModelInstance) process.getModelInstance();
		SequenceFlow sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
		sequenceFlow.setSource(source);
		sequenceFlow.setTarget(target);
		process.addChildElement(sequenceFlow);
	}
}
