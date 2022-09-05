package pdl.repository.template.web.models;

import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceType {
	private String name = "";
	private String description = "";
	private List<VisualizationType> visualizations = new LinkedList<>();

	public ResourceType(String name, String description, VisualizationType... visualizations) {
		this.name = name;
		this.description = description;
		for (VisualizationType v : visualizations) {
			this.visualizations.add(v);
		}
	}
}
