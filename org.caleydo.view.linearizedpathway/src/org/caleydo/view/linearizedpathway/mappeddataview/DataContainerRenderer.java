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
package org.caleydo.view.linearizedpathway.mappeddataview;

import java.util.ArrayList;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.perspective.ADataPerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.datadomain.genetic.GeneticDataDomain;

/**
 * @author alexsb
 * 
 */
public class DataContainerRenderer {
	
	
	DataContainer dataContainer;
	ArrayList<Row> rowLayouts;
	ArrayList<Integer> davidIDs;

	/**
	 * Constructor initializing the renderer with the data source, the layouts
	 * to be used and the ids of the elements to be rendered.
	 * 
	 * @param dataContainer
	 *            the data source
	 * @param rowLayout
	 *            the layout rows in the same order as the davidIDs
	 * @param davidIDs
	 *            the davidIDs in the same order as the rowLayouts
	 */
	public DataContainerRenderer(DataContainer dataContainer, ArrayList<Row> rowLayout,
			ArrayList<Integer> daivdIDs, AGLView parentView) {
		this.dataContainer = dataContainer;
		this.rowLayouts = rowLayout;
		this.davidIDs = daivdIDs;

		prepareData();
	}

	private void prepareData() {
//		GeneticDataDomain dataDomain = (GeneticDataDomain) dataContainer.getDataDomain();
//
//		ADataPerspective<?, ?, ?, ?> experimentPerspective;
//		if (dataDomain.isGeneRecord()) {
//			experimentPerspective = dataContainer.getDimensionPerspective();
//		} else {
//			experimentPerspective = dataContainer.getRecordPerspective();
//		}
//
//		IDType geneIDTYpe = dataDomain.getGeneIDType();
//		// ArrayList<Integer> geneIDs = new ArrayList<Integer>(davidIDs.size());
//		for (int rowCount = 0; rowCount < davidIDs.size(); rowCount++) {
//			Integer davidID = davidIDs.get(rowCount);
//			Integer geneID = dataDomain.getGeneIDMappingManager().getID(
//					IDType.getIDType("DAVID"), geneIDTYpe, davidID);
//			if (geneID == null) {
//				System.out.println("No mapping for david");
//			}
//			// geneIDs.add(davidID);
//			Row row = rowLayouts.get(rowCount);
//			row.setRenderer(new RowRenderer(geneID, dataDomain, dataContainer,
//					experimentPerspective, paren));
//		}
//
	}
}
