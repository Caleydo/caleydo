package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public abstract class DrawingState {

	protected DrawingController drawingController;
	protected GLRadialHierarchy radialHierarchy;
	protected NavigationHistory navigationHistory;

	public DrawingState(DrawingController drawingController, GLRadialHierarchy radialHierarchy, NavigationHistory navigationHistory) {
		this.drawingController = drawingController;
		this.radialHierarchy = radialHierarchy;
		this.navigationHistory = navigationHistory;
	}

	public abstract void draw(float fXCenter, float fYCenter, GL gl, GLU glu);

	public abstract void handleMouseOver(PartialDisc pdMouseOver);

	public abstract void handleClick(PartialDisc pdClicked);
	
	public abstract void handleDoubleClick(PartialDisc pdClicked);
}
