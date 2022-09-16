package pdl.repository.types;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;

import lombok.EqualsAndHashCode;
import pdl.repository.annotations.Visualizer;
import pdl.repository.web.models.ResourceType;
import pdl.repository.web.models.VisualizationType;

@EqualsAndHashCode
public abstract class IType {

	public abstract String[] recognizedExtensions();

	public abstract ResourceType getResourceType();

	public static IType construct(String fileName) throws Exception {
		Reflections reflections = new Reflections("pdl.repository.types");
		for (Class<?> clazz : reflections.getSubTypesOf(IType.class)) {
			try {
				clazz.getDeclaredConstructor();
			} catch (NoSuchMethodException e) {
				continue;
			}
			IType t = (IType) clazz.getDeclaredConstructor().newInstance();
			if (t.fileMatches(fileName)) {
				return t;
			}
		}
		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
		return new IType() {

			@Override
			public String[] recognizedExtensions() {
				return new String[] { extension };
			}

			@Override
			public ResourceType getResourceType() {
				return new ResourceType(extension.toUpperCase(), "A " + extension + " file");
			}
		};
	}

	public boolean fileMatches(String filename) {
		for (String extension : recognizedExtensions()) {
			if (filename.endsWith(extension)) {
				return true;
			}
		}
		return false;
	}

	public Collection<VisualizationType> getVisualizationTypes() {
		Set<VisualizationType> visualizations = new HashSet<>();
		Reflections reflections = new Reflections("pdl.repository.visualizers");
		for (Class<?> clazz : reflections.getTypesAnnotatedWith(Visualizer.class)) {
			Visualizer v = clazz.getAnnotation(Visualizer.class);
			if (this.getClass().equals(v.inputType())) {
				visualizations.add(new VisualizationType(v.id(), v.name(), v.outputType()));
			}
		}
		return visualizations;
	}
}
