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
package org.caleydo.core.view.opengl.util.overlay.infoarea;

import java.util.ArrayList;

import org.caleydo.core.id.IDType;

/**
 * Creates the content for e.g. the InfoArea. Just pass it an ID and an Inputdatatype, it returns an AL of
 * relevant data
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */

public class InformationContentCreator {
	private ArrayList<String> sContent;

	// private GeneAnnotationMapper mapper;

	/**
	 * Constructor
	 * 
	 * @param generalManager
	 */
	public InformationContentCreator() {
		sContent = new ArrayList<String>();
		// mapper = new GeneAnnotationMapper();
	}

	/**
	 * Returns an AL of Strings when you pass it an ID and a data type The list is in such order that the
	 * first element is suitable for a title
	 * 
	 * @param uniqueID
	 * @param eInputDataTypes
	 * @return
	 */
	ArrayList<String> getStringContentForID(final int iUniqueID, final IDType eInputDataTypes) {
		ArrayList<String> result = new ArrayList<String>();
		result.add("Not implemented");
		return result;

		// sContent.clear();
		// switch (eInputDataTypes) {
		// case EXPRESSION_INDEX:
		//
		// String sRefSeq = "unknown";
		// String sGeneName = "unknown";
		// String sGeneSymbol = "unknown";
		//
		// if (uniqueID != -1) {
		// sRefSeq = "as";
		// // generalManager.getIDMappingManager().getID(EMappingType.DAVID_2_REFSEQ_MRNA,
		// // uniqueID);
		// sGeneName = "as";
		// // generalManager.getIDMappingManager().getID(EMappingType.DAVID_2_GENE_NAME, uniqueID);
		// sGeneSymbol = "aas";
		// // generalManager.getIDMappingManager().getID(EMappingType.DAVID_2_GENE_SYMBOL,
		// // uniqueID);
		// }
		//
		// // Cut too long gene names
		// if (sGeneName.length() >= 50) {
		// sGeneName = sGeneName.substring(0, 50) + "...";
		// }
		//
		// sContent.add("Type: Gene");
		// sContent.add("RefSeq: " + sRefSeq);
		// sContent.add("Symbol:" + sGeneSymbol);
		// sContent.add("Name: " + sGeneName);
		//
		// break;
		//
		// case PATHWAY:
		//
		// PathwayGraph pathway = generalManager.getPathwayManager().getItem(uniqueID);
		//
		// if (pathway == null) {
		// break;
		// }
		//
		// String sPathwayTitle = pathway.getTitle();
		//
		// sContent.add("Type: " + pathway.getType().getName() + "Pathway");
		// sContent.add("PW: " + sPathwayTitle);
		// break;
		//
		// case EXPERIMENT:
		//
		// sContent.add("Type: Experiment");
		// break;
		//
		// default:
		// sContent.add("No Data");
		// }
		//
		// return sContent;
	}
}
