/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.datadomain.pathway.manager;

import org.caleydo.core.io.parser.xml.XmlParserManager;
import org.xml.sax.InputSource;

public class PathwayParserManager extends XmlParserManager {

	@Override
	public InputSource getInputSource(String fileName) {

		InputSource inputSource = null;

		// FIXME: not smart to parse for hsa and mmu when searching kegg
		// pathways
		if (fileName.contains("hsa") || fileName.contains("mmu")) {
			inputSource = PathwayManager.get()
					.getPathwayResourceLoader(EPathwayDatabaseType.KEGG)
					.getInputSource(fileName);
		} else if (fileName.contains("h_") || fileName.contains("m_")) {

			inputSource = PathwayManager.get()
					.getPathwayResourceLoader(EPathwayDatabaseType.BIOCARTA)
					.getInputSource(fileName);
		}
		return inputSource;
	}
}
