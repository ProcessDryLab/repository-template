package pdl.repository.visualizers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

import palia.graphviz.Dot;
import palia.graphviz.DotEdge;
import palia.graphviz.DotNode;
import pdl.repository.annotations.Visualizer;
import pdl.repository.web.models.Resource;

@Visualizer(id = "model", name = "BPMN Diagram", inputType = pdl.repository.types.TypeBPMN.class, outputType = "graphviz")
public class VisualizerBPMN implements IVisualizer {

	@Override
	public String getVisualization(Resource resourceDescription, File contentFile) {
		BpmnModelInstance modelInstance = Bpmn.readModelFromFile(contentFile);

		Dot dot = new Dot();
		dot.setOption("rankdir", "LR");
		dot.setOption("outputorder", "edgesfirst");

		Map<String, DotNode> idToNodes = new HashMap<>();

		for (Event e : modelInstance.getModelElementsByType(Event.class)) {
			DotNode n = makeEventNode(dot, (e instanceof StartEvent));
			idToNodes.put(e.getId(), n);
		}

		for (Activity a : modelInstance.getModelElementsByType(Activity.class)) {
			DotNode n = makeActivityNode(dot, a.getName());
			idToNodes.put(a.getId(), n);
		}

		for (Gateway g : modelInstance.getModelElementsByType(Gateway.class)) {
			String type = "";
			if (g instanceof ExclusiveGateway) {
				type = "&times;";
			} else if (g instanceof ParallelGateway) {
				type = "+";
			}
			DotNode n = makeGatewayNode(dot, type);
			idToNodes.put(g.getId(), n);
		}

		for (SequenceFlow f : modelInstance.getModelElementsByType(SequenceFlow.class)) {
			makeEdge(dot, idToNodes.get(f.getSource().getId()), idToNodes.get(f.getTarget().getId()));
		}

		return dot.toString();
	}

	private static DotEdge makeEdge(Dot dot, DotNode source, DotNode target) {
		DotEdge edge = dot.addEdge(source, target);
		edge.setOption("tailclip", "false");
		return edge;
	}

	private static DotNode makeActivityNode(Dot dot, String name) {
		DotNode dotNode = dot.addNode(name);
		dotNode.setOption("shape", "box");
		dotNode.setOption("style", "rounded,filled");
		dotNode.setOption("fillcolor", "#FFFFCC");
		dotNode.setOption("fontname", "Arial");
		dotNode.setOption("fontsize", "8");
		return dotNode;
	}

	private static DotNode makeEventNode(Dot dot, boolean isStart) {
		DotNode dotNode = dot.addNode("");
		dotNode.setOption("shape", "circle");
		dotNode.setOption("style", "filled");
		dotNode.setOption("fillcolor", "white");
		dotNode.setOption("fontcolor", "white");
		dotNode.setOption("width", "0.3");
		dotNode.setOption("height", "0.3");
		dotNode.setOption("fixedsize", "true");
		if (isStart) {
			dotNode.setOption("penwidth", "3");
		}
		return dotNode;
	}

	private static DotNode makeGatewayNode(Dot dot, String name) {
		DotNode dotNode = dot.addNode(
				"<<table border='0'><tr><td></td></tr><tr><td valign='bottom'>" + name + "</td></tr></table>>");
		dotNode.setOption("shape", "diamond");
		dotNode.setOption("style", "filled");
		dotNode.setOption("fillcolor", "white");
		dotNode.setOption("fontcolor", "black");
		dotNode.setOption("fontname", "Arial");

		dotNode.setOption("width", "0.4");
		dotNode.setOption("height", "0.4");
		dotNode.setOption("fontsize", "30");
		dotNode.setOption("fixedsize", "true");

		return dotNode;
	}
}
