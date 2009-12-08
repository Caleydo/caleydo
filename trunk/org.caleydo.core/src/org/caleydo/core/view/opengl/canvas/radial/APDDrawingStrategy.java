package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.manager.picking.PickingManager;

/**
 * APDDrawingStrategy is the abstract base class for all partial disc drawing strategies. The drawing
 * strategies determine the way partial discs are displayed.
 * 
 * @author Christian Partl
 */
public abstract class APDDrawingStrategy {

	/**
	 * The number of slices which shall be drawn to approximate a full disc. Higher numbers produce a better
	 * approximation but the performance is worse.
	 */
	protected int iNumSlicesPerFullDisc;
	protected PickingManager pickingManager;
	protected int iViewID;

	/**
	 * Constructor.
	 * 
	 * @param pickingManager
	 *            The picking manager that should handle the picking of the drawn elements.
	 * @param iViewID
	 *            ID of the view where the elements will be displayed. Needed for picking.
	 */
	public APDDrawingStrategy(PickingManager pickingManager, int iViewID) {
		this.pickingManager = pickingManager;
		this.iViewID = iViewID;
		iNumSlicesPerFullDisc = RadialHierarchyRenderStyle.NUM_SLICES_PER_FULL_DISC;
	}

	/**
	 * Draws a partial disc in a way determined by the concrete drawing strategy class.
	 * 
	 * @param gl
	 *            GL object that shall be used for drawing.
	 * @param glu
	 *            GLU object that shall be used for drawing.
	 * @param pdDiscToDraw
	 *            Partial disc that shall be drawn.
	 */
	public abstract void drawPartialDisc(GL gl, GLU glu, PartialDisc pdDiscToDraw);

	/**
	 * Draws a full circle in a way determined by the concrete drawing strategy class.
	 * 
	 * @param gl
	 *            GL object that shall be used for drawing.
	 * @param glu
	 *            GLU object that shall be used for drawing.
	 * @param pdDiscToDraw
	 *            Partial disc that shall be drawn.
	 */
	public abstract void drawFullCircle(GL gl, GLU glu, PartialDisc pdDiscToDraw);

	/**
	 * Gets the drawing strategy type of the current instance.
	 */
	public abstract EPDDrawingStrategyType getDrawingStrategyType();

	/**
	 * Gets the color the specified partial disc would be drawn with.
	 * 
	 * @param disc
	 *            Patrtial disc for which the color should be returned.
	 * @return Color of the specified partial disc as array of RGBA values.
	 */
	public abstract float[] getColor(PartialDisc disc);

	/**
	 * Gets the number of slices which shall be drawn to approximate a disc.
	 * 
	 * @return Number of slices per full disc.
	 */
	public int getNumSlicesPerFullDisc() {
		return iNumSlicesPerFullDisc;
	}

	/**
	 * Sets the number of slices which shall be drawn to approximate a disc. Higher numbers produce a better
	 * approximation but the performance is worse.
	 * 
	 * @param iNumSlicesPerFullDisc
	 *            Number of slices per full disc.
	 */
	public void setNumSlicesPerFullDisc(int iNumSlicesPerFullDisc) {
		this.iNumSlicesPerFullDisc = iNumSlicesPerFullDisc;
	}

	public PickingManager getPickingManager() {
		return pickingManager;
	}

	public void setPickingManager(PickingManager pickingManager) {
		this.pickingManager = pickingManager;
	}

	public int getViewID() {
		return iViewID;
	}

	public void setViewID(int iViewID) {
		this.iViewID = iViewID;
	}

}
