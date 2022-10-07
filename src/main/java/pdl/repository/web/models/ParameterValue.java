package pdl.repository.web.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ParameterValue {

	private String name = "";
	private Object value = "";
}
