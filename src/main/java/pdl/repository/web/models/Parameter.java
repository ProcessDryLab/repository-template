package pdl.repository.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Parameter {
	public enum TYPES {
		STRING, INTEGER, DOUBLE;
	}

	private String name = "";
	private TYPES type = null;
	@JsonProperty("default")
	private Object _default = null;
}
