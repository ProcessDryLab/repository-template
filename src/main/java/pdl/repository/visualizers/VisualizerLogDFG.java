package pdl.repository.visualizers;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import pdl.repository.annotations.Visualizer;
import pdl.repository.types.TypeXES;
import pdl.repository.utils.XLogUtils;
import pdl.repository.web.models.Resource;

@Visualizer(id = "dfg", name = "Directly Follows Graph", inputType = TypeXES.class, outputType = "graphviz")
public class VisualizerLogDFG implements IVisualizer {

	@Override
	public String getVisualization(Resource resourceDescription, File contentFile) {
		XLog l;
		try {
			l = XLogUtils.parse(contentFile);
		} catch (Exception e1) {
			return e1.getMessage();
		}
		Map<String, Set<String>> dfg = new HashMap<>();
		for (XTrace t : l) {
			String prec = null;
			for (XEvent e : t) {
				String curr = XConceptExtension.instance().extractName(e);
				if (prec != null) {
					if (!dfg.containsKey(prec)) {
						dfg.put(prec, new HashSet<>());
					}
					dfg.get(prec).add(curr);
				}
				prec = curr;
			}
		}
		String graph = "digraph DFG {\n";
		for (String src : dfg.keySet()) {
			for (String tgt : dfg.get(src)) {
				graph = graph + "\"" + src + "\" -> \"" + tgt + "\";\n";
			}
		}
		graph = graph + "}";
		return graph;
	}

}
