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
package org.caleydo.view.browser;

import java.util.ArrayList;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;

public class IDExtractionLocationListener extends LocationAdapter {
	// private GeneralManager generalManager;
	private IDMappingManager idManager;

	private boolean bSkipNextChangeEvent = false;

	/**
	 * Constructor.
	 */
	public IDExtractionLocationListener(IDMappingManager idManager,
			final Browser browser, final int iBrowserId, final int iSelectionSetId) {
		this.idManager = idManager;
	}

	@Override
	public void changed(LocationEvent event) {

	}

	@Override
	// http://www.genome.jp/dbget-bin/show_pathway?map00020+1.1.1.37
	// http://www.genome.jp/dbget-bin/www_bget?hsa+4190
	// http://www.genome.jp/dbget-bin/www_bget?compound+C00003
	// http://www.genome.jp/dbget-bin/www_bget?enzyme+3.6.3.5
	public void changing(LocationEvent event) {

		if (bSkipNextChangeEvent == true) {
			bSkipNextChangeEvent = false;
			return;
		}

		bSkipNextChangeEvent = false;

		String sSearchPhrase_NCBIGeneId = "http://www.genome.jp/dbget-bin/www_bget?hsa+";
		String sSearchPhrase_Pathway = "http://www.genome.jp/dbget-bin/show_pathway?hsa";

		ArrayList<Integer> iAlSelectionId = null;
		ArrayList<Integer> iAlSelectionDepth = null;
		if (event.location.contains(sSearchPhrase_NCBIGeneId)) {
			String sExtractedID = event.location.substring(sSearchPhrase_NCBIGeneId
					.length());

			Integer davidID = idManager.getID(IDType.getIDType("ENTREZ_GENE_ID"),
					IDType.getIDType("DAVID"), Integer.valueOf(sExtractedID));

			if (davidID == null || davidID == -1)
				return;

			PathwayVertex vertex = PathwayItemManager.get().getPathwayVertexByDavidId(
					davidID);

			if (vertex == null)
				return;

			iAlSelectionId = new ArrayList<Integer>();
			iAlSelectionDepth = new ArrayList<Integer>();

			for (PathwayVertexRep vertexRep : vertex.getPathwayVertexReps()) {
				iAlSelectionId.add(vertexRep.getID());
				iAlSelectionDepth.add(0);
			}

		} else if (event.location.contains(sSearchPhrase_Pathway)) {
			// Prevent loading of clicked pathway URL
			event.doit = false;

			int iPathwayIdIndex = 0;

			// Extract clicked pathway ID
			if (event.location.contains("map0")) {
				iPathwayIdIndex = event.location.lastIndexOf("map0") + 4;
			} else if (event.location.contains("hsa0")) {
				iPathwayIdIndex = event.location.lastIndexOf("hsa0") + 4;
			} else
				return;

			Integer.valueOf(
					event.location.substring(iPathwayIdIndex,
							event.location.lastIndexOf('+'))).intValue();
		} else
			return;

	}

	public void updateSkipNextChangeEvent(boolean bSkipNextChangeEvent) {

		this.bSkipNextChangeEvent = bSkipNextChangeEvent;
	}
}
