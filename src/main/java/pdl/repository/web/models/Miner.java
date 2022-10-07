package pdl.repository.web.models;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Miner {

	@EqualsAndHashCode.Include
	private String id = UUID.randomUUID().toString();
	private String name = "";
	private Map<String, ResourceType> input = new HashMap<>();
	private List<Parameter> parameters = new LinkedList<>();
	private List<ResourceType> output = new LinkedList<>();

	public void addInput(String name, ResourceType rt) {
		input.put(name, rt);
	}

	public void addOutput(ResourceType rt) {
		output.add(rt);
	}

	public void addParameter(Parameter p) {
		parameters.add(p);
	}
}
