/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui.augmentation;

import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.pathway.IVertexRepSelectionListener;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.pathway.v2.ui.APathwayElementRepresentation;

/**
 * @author Christian
 *
 */
public class VertexHighlightAugmentation extends APerVertexAugmentation implements IVertexRepSelectionListener,
		IEventBasedSelectionManagerUser {

	protected EventBasedSelectionManager vertexSelectionManager;
	protected EventBasedSelectionManager geneSelectionManager;

	/**
	 * @param pathwayRepresentation
	 */
	public VertexHighlightAugmentation(APathwayElementRepresentation pathwayRepresentation) {
		super(pathwayRepresentation);
		vertexSelectionManager = new EventBasedSelectionManager(this, PathwayVertexRep.getIdType());
		geneSelectionManager = new EventBasedSelectionManager(this, IDType.getIDType("DAVID"));
		vertexSelectionManager.registerEventListeners();
		geneSelectionManager.registerEventListeners();
		pathwayRepresentation.addVertexRepSelectionListener(this);
	}

	@Override
	protected void renderVertexAugmentation(GLGraphics g, float w, float h, PathwayVertexRep vertexRep, Rect bounds) {

		for (Integer davidId : vertexRep.getDavidIDs()) {
			List<SelectionType> selectionTypes = geneSelectionManager.getSelectionTypes(davidId);
			if (selectionTypes.contains(SelectionType.SELECTION)) {
				renderHighlight(g, bounds, SelectionType.SELECTION.getColor(), vertexRep);
				return;
			} else if (selectionTypes.contains(SelectionType.MOUSE_OVER)) {
				renderHighlight(g, bounds, SelectionType.MOUSE_OVER.getColor(), vertexRep);
				return;
			}
		}

		if (vertexSelectionManager.checkStatus(SelectionType.SELECTION, vertexRep.getID())) {
			renderHighlight(g, bounds, SelectionType.SELECTION.getColor(), vertexRep);
		} else if (vertexSelectionManager.checkStatus(SelectionType.MOUSE_OVER, vertexRep.getID())) {
			renderHighlight(g, bounds, SelectionType.MOUSE_OVER.getColor(), vertexRep);
		}

	}

	protected void renderHighlight(GLGraphics g, Rect bounds, Color color, PathwayVertexRep vertexRep) {
		g.gl.glPushAttrib(GL2.GL_LINE_BIT);
		g.lineWidth(3).color(color);
		if (vertexRep.getType() == EPathwayVertexType.compound) {
			float radius = bounds.width() / 2.0f;
			g.drawCircle(bounds.x() + radius + 0.5f, bounds.y() + radius + 0.5f, radius);
		} else {
			g.drawRect(bounds.x(), bounds.y(), bounds.width() + 1, bounds.height());
		}
		g.gl.glPopAttrib();
	}

	@Override
	public void onSelect(PathwayVertexRep vertexRep, Pick pick) {
		switch (pick.getPickingMode()) {

		case MOUSE_OVER:
			vertexSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
			vertexSelectionManager.addToType(SelectionType.MOUSE_OVER, vertexRep.getID());
			break;

		case MOUSE_OUT:
			vertexSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
			break;

		case CLICKED:
			vertexSelectionManager.clearSelection(SelectionType.SELECTION);
			vertexSelectionManager.addToType(SelectionType.SELECTION, vertexRep.getID());
			break;

		default:
			// Do not trigger a selection update for other picking modes
			return;
		}

		vertexSelectionManager.triggerSelectionUpdateEvent();
		repaint();
	}

	@Override
	public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {
		repaint();
	}

	@Override
	protected void takeDown() {
		vertexSelectionManager.unregisterEventListeners();
		vertexSelectionManager = null;
		geneSelectionManager.unregisterEventListeners();
		geneSelectionManager = null;
		super.takeDown();
	}

}
