package pdl.repository.web.models;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Resource {
	private UUID id;
	private String name;
	private ResourceType type;
	private Date creationDate;
	private String host;

	public Resource(UUID id, String name, ResourceType type, Date creationDate) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.creationDate = creationDate;
	}
}
