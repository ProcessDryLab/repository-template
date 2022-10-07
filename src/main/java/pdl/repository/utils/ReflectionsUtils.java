package pdl.repository.utils;

import org.reflections.Reflections;

public class ReflectionsUtils {

	private static Reflections instance = null;

	public static Reflections get() {
		if (instance == null) {
			instance = new Reflections("pdl.repository");
		}
		return instance;
	}
}
