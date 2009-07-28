package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

/**
 * Defines the interface to draw able objects, held by draw able nodes with reference to
 * a specific detail level.
 * 
 * @author Georg Neubauer
 */
public interface IDrawAbleDetailLevelObject {
	/**
	 * Draw the object at a certain position.
	 * 
	 * @param gl
	 * @param fXCoord
	 * @param fYCoord
	 * @param fZCoord
	 * @param fHeight
	 * @param fWidth
	 * @return
	 */
	public ArrayList<Vec3f> drawObjectAtPosition(GL gl, float fXCoord, float fYCoord, float fZCoord, float fHeight, float fWidth);
	/**
	 * Set the background color of the draw able object type
	 * 
	 * @param fRed
	 * @param fGreen
	 * @param fBlue
	 * @return
	 */
	void setBgColor3f(float fRed, float fGreen, float fBlue);

	/**
	 * Set the alpha value of the draw able object type
	 * 
	 * @param fAlpha
	 * @return
	 */
	void setAlpha(float fAlpha);
}
