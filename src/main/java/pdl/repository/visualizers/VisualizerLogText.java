package pdl.repository.visualizers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import pdl.repository.annotations.Visualizer;
import pdl.repository.types.TypeXES;
import pdl.repository.utils.XLogUtils;
import pdl.repository.web.models.Resource;

@Visualizer(id = "description", name = "Log description", inputType = TypeXES.class, outputType = "html")
public class VisualizerLogText implements IVisualizer {

	@Override
	public String getVisualization(Resource resourceDescription, File contentFile) {
		XLog l;
		try {
			l = XLogUtils.parse(contentFile);
		} catch (Exception e1) {
			return e1.getMessage();
		}

		int events = 0;
		Map<String, Integer> activities = new HashMap<>();
		for (XTrace t : l) {
			for (XEvent e : t) {
				String activityName = XConceptExtension.instance().extractName(e);
				if (!activities.containsKey(activityName)) {
					activities.put(activityName, 1);
				} else {
					activities.put(activityName, activities.get(activityName) + 1);
				}
				events++;
			}
		}

		ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
		resolver.setTemplateMode(TemplateMode.HTML);
		resolver.setCharacterEncoding("UTF-8");
		resolver.setPrefix("/templates/");
		resolver.setSuffix(".html");

		Context context = new Context();
		context.setVariable("name", resourceDescription.getName());
		context.setVariable("events", events);
		context.setVariable("traces", l.size());
		context.setVariable("activities", activities.keySet().size());

		String actNameList = "";
		String actFreqList = "";
		for (String act : activities.keySet()) {
			actNameList = actNameList + "\"" + act + "\", ";
			actFreqList = actFreqList + activities.get(act) + ", ";
		}

		context.setVariable("actNameList", "[" + actNameList + "]");
		context.setVariable("actFreqList", "[" + actFreqList + "]");

		var templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(resolver);

		return templateEngine.process("stats", context);
	}

}
