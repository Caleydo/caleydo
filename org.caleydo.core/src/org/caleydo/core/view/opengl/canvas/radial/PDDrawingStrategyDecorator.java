package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public abstract class PDDrawingStrategyDecorator
	extends PDDrawingStrategy {
	
	protected PDDrawingStrategy drawingStrategy;

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
