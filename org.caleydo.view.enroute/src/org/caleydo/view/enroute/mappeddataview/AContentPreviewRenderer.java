/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
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

	/** The primary mapping type of the id category for rows */
	IDType rowIDType;
	/** The id for this row in the primary mapping tytpe */
	Integer rowID;
	/** The id type matching the {@link #rowIDType} resolved for the specific {@link #dataDomain} */
	IDType resolvedRowIDType;
	/** The resolved row ID */
	Integer resolvedRowID;

	protected SelectionColorCalculator colorCalculator = new SelectionColorCalculator();

	protected TablePerspective tablePerspective;
	protected Perspective experimentPerspective;
	protected GeneticDataDomain dataDomain;

	protected EventBasedSelectionManager geneSelectionManager;
	protected EventBasedSelectionManager sampleSelectionManager;

	public AContentPreviewRenderer(int rowID, TablePerspective tablePerspective,
			EventBasedSelectionManager geneSelectionManager, EventBasedSelectionManager sampleSelectionManager) {

		this.tablePerspective = tablePerspective;
		this.rowID = rowID;
		this.rowIDType = IDType.getIDType("DAVID");

		this.geneSelectionManager = geneSelectionManager;
		this.sampleSelectionManager = sampleSelectionManager;

		dataDomain = (GeneticDataDomain) tablePerspective.getDataDomain();

		if (dataDomain.isGeneRecord()) {
			experimentPerspective = tablePerspective.getDimensionPerspective();
		} else {
			experimentPerspective = tablePerspective.getRecordPerspective();
		}

		resolvedRowIDType = dataDomain.getGeneIDType();
		Set<Integer> geneIDs = dataDomain.getGeneIDMappingManager().getIDAsSet(rowIDType, resolvedRowIDType, rowID);
		if (geneIDs == null) {
			// System.out.println("No mapping for david: " + rowID);
			resolvedRowID = null;

		} else {
			resolvedRowID = geneIDs.iterator().next();
			if (geneIDs.size() > 1) {

				Set<String> names = dataDomain.getGeneIDMappingManager().getIDAsSet(rowIDType,
						IDCategory.getIDCategory(EGeneIDTypes.GENE.name()).getHumanReadableIDType(), rowID);
				System.out.println("Here's the problem: " + names + " / " + geneIDs);
			}
		}
	}

}
