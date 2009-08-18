package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.manager.picking.PickingManager;

/**
 * Abstract base class for all decorators for partial disc drawing strategies.
 * 
 * @author Christian Partl
 */
public abstract class APDDrawingStrategyDecorator
	extends APDDrawingStrategy {

	/**
	 * The drawing strategy that shall be decorated.
	 */
	protected APDDrawingStrategy drawingStrategy;

	/**
	 * Constructor.
	 * 
	 * @param pickingManager
	 *            The picking manager that should handle the picking of the drawn elements.
	 * @param iViewID
	 *            ID of the view where the elements will be displayed. Needed for picking.
	 */
	public APDDrawingStrategyDecorator(PickingManager pickingManager, int iViewID) {
		super(pickingManager, iViewID);
	}

	@Override
	public abstract void drawFullCircle(GL gl, GLU glu, PartialDisc pdDiscToDraw);

	@Override
	public abstract void drawPartialDisc(GL gl, GLU glu, PartialDisc pdDiscToDraw);

	/**
	 * Gets the decorated drawing strategy.
	 * 
	 * @return The decorated drawing strategy.
	 */
	public APDDrawingStrategy getDrawingStrategy() {
		return drawingStrategy;
	}

	/**
	 * Sets the drawing strategy that shall be decorated.
	 * 
	 * @param drawingStrategy
	 *            Drawing strategy that shall be decorated.
	 */
	public void setDrawingStrategy(APDDrawingStrategy drawingStrategy) {
		this.drawingStrategy = drawingStrategy;
	}
	
	public abstract APDDrawingStrategyDecorator clone();

}
