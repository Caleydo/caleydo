package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.manager.picking.PickingManager;

public abstract class PDDrawingStrategyDecorator
	extends PDDrawingStrategy {
	
	protected PDDrawingStrategy drawingStrategy;
	
	public PDDrawingStrategyDecorator(PickingManager pickingManager, int iViewID) {
		super(pickingManager, iViewID);
	}

	@Override
	public abstract void drawFullCircle(GL gl, GLU glu, PartialDisc pdDiscToDraw);

	@Override
	public abstract void drawPartialDisc(GL gl, GLU glu, PartialDisc pdDiscToDraw);

	public PDDrawingStrategy getDrawingStrategy() {
		return drawingStrategy;
	}

	public void setDrawingStrategy(PDDrawingStrategy drawingStrategy) {
		this.drawingStrategy = drawingStrategy;
	}

	
}
