package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.manager.picking.PickingManager;

/**
 * PDDrawingStrategyInvisible does not draw anything.
 * 
 * @author Christian Partl
 */
public class PDDrawingStrategyInvisible
	extends APDDrawingStrategy {

	/**
	 * Constructor.
	 * 
	 * @param pickingManager
	 *            The picking manager that should handle the picking of the drawn elements.
	 * @param iViewID
	 *            ID of the view where the elements will be displayed. Needed for picking.
	 */
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

	@Override
	public EPDDrawingStrategyType getDrawingStrategyType() {
		return EPDDrawingStrategyType.INVISIBLE;
	}

}
