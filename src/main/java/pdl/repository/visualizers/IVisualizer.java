package pdl.repository.visualizers;

import java.io.File;

import org.reflections.Reflections;

import pdl.repository.annotations.Visualizer;
import pdl.repository.types.IType;
import pdl.repository.web.models.Resource;
import pdl.repository.web.models.ResourceType;

public interface IVisualizer {
	public String getVisualization(Resource resourceDescription, File contentFile);

	public static IVisualizer construct(ResourceType type, String id) throws Exception {
		Reflections reflections = new Reflections("pdl.repository.visualizers");
		for (Class<?> clazz : reflections.getTypesAnnotatedWith(Visualizer.class)) {
			if (IVisualizer.class.isAssignableFrom(clazz)) {
				Visualizer annotation = clazz.getAnnotation(Visualizer.class);
				IType visualizerType = (IType) annotation.inputType().getDeclaredConstructor().newInstance();
				if (visualizerType.getResourceType().equals(type) && annotation.id().equalsIgnoreCase(id)) {
					return (IVisualizer) clazz.getDeclaredConstructor().newInstance();
				}
			}
		}
		return null;
	}
}
