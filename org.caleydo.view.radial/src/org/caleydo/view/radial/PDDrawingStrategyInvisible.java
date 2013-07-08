/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.util.clusterer.EPDDrawingStrategyType;
import org.caleydo.core.view.opengl.picking.PickingManager;

/**
 * PDDrawingStrategyInvisible does not draw anything.
 * 
 * @author Christian Partl
 */
public class PDDrawingStrategyInvisible extends APDDrawingStrategy {

	/**
	 * Constructor.
	 * 
	 * @param pickingManager
	 *            The picking manager that should handle the picking of the
	 *            drawn elements.
	 * @param viewID
	 *            ID of the view where the elements will be displayed. Needed
	 *            for picking.
	 */
	public PDDrawingStrategyInvisible(PickingManager pickingManager, int viewID) {
		super(pickingManager, viewID);
	}

	@Override
	public void drawFullCircle(GL2 gl, GLU glu, PartialDisc pdDiscToDraw) {
		// Don't draw anything

	}

	@Override
	public void drawPartialDisc(GL2 gl, GLU glu, PartialDisc pdDiscToDraw) {
		// Don't draw anything
	}

	@Override
	public EPDDrawingStrategyType getDrawingStrategyType() {
		return EPDDrawingStrategyType.INVISIBLE;
	}

	@Override
	public float[] getColor(PartialDisc disc) {
		return new float[] { 1.0f, 1.0f, 1.0f, 0.0f };
	}

}
