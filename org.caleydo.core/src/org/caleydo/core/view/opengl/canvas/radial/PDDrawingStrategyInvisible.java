package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.manager.picking.PickingManager;

public class PDDrawingStrategyInvisible
	extends PDDrawingStrategy {

	public PDDrawingStrategyInvisible(PickingManager pickingManager, int viewID) {
		super(pickingManager, viewID);
	}

	@Override
	public void drawFullCircle(GL gl, GLU glu, PartialDisc pdDiscToDraw) {
		// Don't draw anything

	}

	@Override
	public void drawPartialDisc(GL gl, GLU glu, PartialDisc pdDiscToDraw) {
		// Don't draw anything
	}

}
