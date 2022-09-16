package pdl.repository.types;

import pdl.repository.web.models.ResourceType;

public class TypeBPMN extends IType {

	@Override
	public String[] recognizedExtensions() {
		return new String[] { "bpmn" };
	}

	@Override
	public ResourceType getResourceType() {
		return new ResourceType("BPMN", "A BPMN model", getVisualizationTypes());
	}

}