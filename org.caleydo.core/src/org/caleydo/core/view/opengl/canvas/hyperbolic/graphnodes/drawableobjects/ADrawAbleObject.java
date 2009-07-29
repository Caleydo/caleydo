package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

/**
 * Abstract of draw able node type. This type defines objects which are self drawing,
 * which are held by nodes.
 * 
 * @author Georg Neubauer
 */
public abstract class ADrawAbleObject
	implements IDrawAbleObject {
	
	protected float fRed = 0;
	protected float fGreen = 0;
	protected float fBlue = 0;
	protected float fAlpha = 1;
	
	@Override
	public abstract ArrayList<Vec3f> drawObjectAtPosition(GL gl, float fXCoord, float fYCoord, float fZCoord,
		float fHeight, float fWidth);
	
	@Override
	public final void setAlpha(float fAlpha) {
		this.fAlpha = fAlpha;
	}

	@Override
	public final void setBgColor3f(float fRed, float fGreen, float fBlue) {
		this.fRed = fRed;
		this.fGreen = fGreen;
		this.fBlue = fBlue;
	}
}
