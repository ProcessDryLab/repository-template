package pdl.repository.types;

import pdl.repository.web.models.ResourceType;

public class TypeXES extends IType {

	@Override
	public String[] recognizedExtensions() {
		return new String[] { "xes", "xes.gz" };
	}

	@Override
	public ResourceType getResourceType() {
		return new ResourceType("XES", "An XES file", getVisualizationTypes());
	}

}
