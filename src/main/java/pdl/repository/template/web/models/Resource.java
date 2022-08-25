package pdl.repository.template.web.models;

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
}
