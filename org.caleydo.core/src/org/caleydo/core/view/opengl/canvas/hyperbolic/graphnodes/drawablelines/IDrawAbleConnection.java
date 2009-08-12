package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines;

import gleem.linalg.Vec3f;

import java.util.List;

import javax.media.opengl.GL;

public interface IDrawAbleConnection {

//	/**
//	 * Draw the object at a certain position.
//	 * 
//	 * @param gl
//	 * @param pStartPoint
//	 * @param pEndPoint
//	 * @param fThickness
//	 * @return
//	 */
//	public void drawLineFromStartToEnd(GL gl, Vec3f pStartPoint, Vec3f pEndPoint, float fThickness);
	
	/**
	 * Draw the object at a certain position.
	 * 
	 * @param gl
	 * @param lPoints List of Points to connect
	 * @param fThickness
	 * @return
	 */
	public void drawConnectionFromStartToEnd(GL gl, List<Vec3f> lPoints, float fThickness);

	/**
	 * Set the background color of the draw able object type
	 * 
	 * @param fRed
	 * @param fGreen
	 * @param fBlue
	 * @return
	 */
	public void setConnectionColor3f(float fRed, float fGreen, float fBlue);

	/**
	 * Set the alpha value of the draw able object type
	 * 
	 * @param fAlpha
	 * @return
	 */
	public void setConnectionAlpha(float fAlpha);
}
