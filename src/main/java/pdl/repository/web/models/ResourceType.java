package pdl.repository.web.models;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ResourceType {
	private String name = "";
	private String description = "";
	@EqualsAndHashCode.Exclude
	private List<VisualizationType> visualizations = new LinkedList<>();

	public ResourceType(String name, String description, Collection<VisualizationType> visualizations) {
		this.name = name;
		this.description = description;
		for (VisualizationType v : visualizations) {
			this.visualizations.add(v);
		}
	}

	public ResourceType(String name, String description, VisualizationType... visualizations) {
		this(name, description, Arrays.asList(visualizations));
	}
}
