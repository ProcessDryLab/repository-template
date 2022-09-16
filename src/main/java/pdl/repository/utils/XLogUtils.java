package pdl.repository.utils;

import java.io.File;

import org.deckfour.xes.in.XMxmlGZIPParser;
import org.deckfour.xes.in.XMxmlParser;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;

public class XLogUtils {

	public static XLog parse(File file) throws Exception {
		XParser[] parsers = new XParser[] { new XesXmlParser(), new XesXmlGZIPParser(), new XMxmlParser(),
				new XMxmlGZIPParser() };
		for (XParser p : parsers) {
			if (p.canParse(file)) {
				return p.parse(file).get(0);
			}
		}
		return null;
	}
}
