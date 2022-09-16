package pdl.repository.visualizers;

import java.io.File;

import org.deckfour.xes.model.XLog;

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
		return "<html><body><h1>" + resourceDescription.getName() + "</h1><p>" + l.size()
				+ " traces.</p></body></html>";
	}

}
