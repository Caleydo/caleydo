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
package org.caleydo.view.enroute.mappeddataview;

import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.view.enroute.SelectionColorCalculator;

/**
 * Abstract base class for preview renderers.
 *
 * @author Christian
 *
 */
public abstract class AContentPreviewRenderer extends ALayoutRenderer {

	protected static final float z = 0.5f;

	protected SelectionColorCalculator colorCalculator = new SelectionColorCalculator();
	protected Integer geneID;
	protected TablePerspective tablePerspective;
	protected Perspective experimentPerspective;
	protected GeneticDataDomain dataDomain;
	protected Integer davidID;
	protected EventBasedSelectionManager geneSelectionManager;
	protected EventBasedSelectionManager sampleSelectionManager;

	/**
	 * @param initializer
	 */
	public AContentPreviewRenderer(int davidID, TablePerspective tablePerspective,
			EventBasedSelectionManager geneSelectionManager, EventBasedSelectionManager sampleSelectionManager) {
		// this.geneID = initializer.getGeneID();
		// this.davidID = initializer.getDavidID();
		// this.dataDomain = initializer.getDataDomain();
		// this.tablePerspective = initializer.getTablePerspective();
		// this.experimentPerspective = initializer.getExperimentPerspective();
		// this.view = initializer.getView();
		// this.geneSelectionManager = initializer.getGeneSelectionManager();
		// this.sampleSelectionManager = initializer.getSampleSelectionManager();
		this.tablePerspective = tablePerspective;
		this.davidID = davidID;
		this.geneSelectionManager = geneSelectionManager;
		this.sampleSelectionManager = sampleSelectionManager;

		dataDomain = (GeneticDataDomain) tablePerspective.getDataDomain();

		if (dataDomain.isGeneRecord()) {
			experimentPerspective = tablePerspective.getDimensionPerspective();
		} else {
			experimentPerspective = tablePerspective.getRecordPerspective();
		}

		IDType geneIDTYpe = dataDomain.getGeneIDType();
		Set<Integer> geneIDs = dataDomain.getGeneIDMappingManager().getIDAsSet(IDType.getIDType("DAVID"), geneIDTYpe,
				davidID);
		if (geneIDs == null) {
			// System.out.println("No mapping for david: " + davidID);
			geneID = null;

		} else {
			geneID = geneIDs.iterator().next();
			if (geneIDs.size() > 1) {

				Set<String> names = dataDomain.getGeneIDMappingManager().getIDAsSet(IDType.getIDType("DAVID"),
						IDCategory.getIDCategory(EGeneIDTypes.GENE.name()).getHumanReadableIDType(), davidID);
				System.out.println("Here's the problem: " + names + " / " + geneIDs);
			}
		}
	}

}
