package pdl.repository.web.models;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MinerConfiguration {

	private Miner miner = null;
	private Map<String, Resource> inputs = new HashMap<>();
	private List<ParameterValue> parameters = new LinkedList<>();
	@JsonProperty("repository")
	private String repositoryHostname = "";
}
