package org.caleydo.datadomain.pathway.manager;

import org.caleydo.core.parser.xml.XmlParserManager;
import org.xml.sax.InputSource;

public class PathwayParserManager extends XmlParserManager {

	@Override
	public InputSource getInputSource(String fileName) {

		InputSource inputSource = null;

		// FIXME: not smart to parse for hsa and mmu when searching kegg
		// pathways
		if (fileName.contains("hsa") || fileName.contains("mmu")) {
			inputSource = PathwayManager.get()
					.getPathwayResourceLoader(PathwayDatabaseType.KEGG)
					.getInputSource(fileName);
		} else if (fileName.contains("h_") || fileName.contains("m_")) {

			inputSource = PathwayManager.get()
					.getPathwayResourceLoader(PathwayDatabaseType.BIOCARTA)
					.getInputSource(fileName);
		}
		return inputSource;
	}
}
