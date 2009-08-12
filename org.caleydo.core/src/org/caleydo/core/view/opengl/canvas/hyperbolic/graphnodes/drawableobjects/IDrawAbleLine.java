package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

public interface IDrawAbleLine {

	/**
	 * Draw the object at a certain position.
	 * 
	 * @param gl
	 * @param pStartPoint
	 * @param pEndPoint
	 * @param fThickness
	 * @return
	 */
	public void drawLineFromStartToEnd(GL gl, Vec3f pStartPoint, Vec3f pEndPoint, float fThickness);

	/**
	 * Set the background color of the draw able object type
	 * 
	 * @param fRed
	 * @param fGreen
	 * @param fBlue
	 * @return
	 */
	void setLineColor3f(float fRed, float fGreen, float fBlue);

	/**
	 * Set the alpha value of the draw able object type
	 * 
	 * @param fAlpha
	 * @return
	 */
	void setLineAlpha(float fAlpha);
}
