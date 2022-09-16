package pdl.repository.utils;

import org.reflections.Reflections;

public class ReflectionsUtils {

	public static Reflections get() {
//		boolean deploy = ReflectionsUtils.class.getProtectionDomain().getCodeSource().getLocation().toString().startsWith("jar");
//		return deploy? Reflections.collect() :
		return new Reflections("pdl.repository");
	}
}
