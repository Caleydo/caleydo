/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui.augmentation;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * @author Christian
 *
 */
public class CompoundAugmentation extends APerVertexAugmentation {

	/**
	 *
	 */
	public CompoundAugmentation(IPathwayRepresentation pathwayRepresentation) {
		super(pathwayRepresentation);
	}

	@Override
	protected void renderVertexAugmentation(GLGraphics g, float w, float h, PathwayVertexRep vertexRep, Rect bounds) {
		if (vertexRep.getType() == EPathwayVertexType.gene) {
			IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
					IDCategory.getIDCategory(EGeneIDTypes.GENE.name()));
			if (idMappingManager != null) {
				Set<Object> compoundIDs = new HashSet<>();
				for (int davidID : vertexRep.getDavidIDs()) {

					Set<Object> ids = idMappingManager.getIDAsSet(IDType.getIDType(EGeneIDTypes.DAVID.name()),
							IDType.getIDType(EGeneIDTypes.COMPOUND_ID.name()), davidID);
					if (ids != null)
						compoundIDs.addAll(ids);
				}
				g.color(1, 1, 1, 0.5f).fillRect(bounds);
				g.drawText("" + compoundIDs.size(), bounds.x(), bounds.y(), bounds.width(), bounds.height());
			}
		}

	}
}
