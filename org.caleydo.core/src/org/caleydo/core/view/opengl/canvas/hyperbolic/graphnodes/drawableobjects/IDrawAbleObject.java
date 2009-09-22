package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;

/**
 * Defines the interface to draw able objects, held by draw able nodes with reference to a specific detail
 * level.
 * 
 * @author Georg Neubauer
 */
public interface IDrawAbleObject {
	/**
	 * Place the object on a specific position
	 * 
	 * @param fXCoord
	 * @param fYCoord
	 * @param fZCoord
	 * @param fHeight
	 * @param fWidth
	 */
	public void place(float fXCoord, float fYCoord, float fZCoord, float fHeight, float fWidth);

//	/**
//	 * Place the node on a specific position calcutated by the projection type
//	 * 
//	 * @param fXCoord
//	 * @param fYCoord
//	 * @param fZCoord
//	 * @param fHeight
//	 * @param fWidth
//	 * @param projection
//	 */
//	public void placeAndProject(float fXCoord, float fYCoord, float fZCoord, float fHeight, float fWidth, ITreeProjection projection);

	
	/**
	 * Draw the object in normal representation, taking color mapping from {@link HyperbolicRenderStyle}
	 * 
	 * @param gl
	 * @return ArrayList<Vec3f>
	 */
	public ArrayList<Vec3f> draw(GL gl, boolean bHighlight);

	/**
	 * Returns the connection points for the current representation and placing
	 * 
	 * @return
	 */
	public ArrayList<Vec3f> getConnectionPoints();

	void setPickAble(boolean bIsAbleToPick);


	// /**
	// * Draw the object in highlight representation, taking
	// * color mapping from {@link HyperbolicRenderStyle}
	// *
	// * @param gl
	// * @return ArrayList<Vec3f>
	// */
	// public ArrayList<Vec3f> drawHighlight(GL gl);

	// /**
	// * Set the background color of the draw able object type
	// *
	// * @param fRed
	// * @param fGreen
	// * @param fBlue
	// * @return
	// */
	// public void setBgColor3f(float fRed, float fGreen, float fBlue);
	//
	// /**
	// * Set the alpha value of the draw able object type
	// *
	// * @param fAlpha
	// * @return
	// */
	// public void setAlpha(float fAlpha);

	// /**
	// * Draw the object with current color mapping
	// *
	// * @param gl
	// * @return
	// */
	// ArrayList<Vec3f> drawObject(GL gl);

	// public void setHighlight(boolean b);

	// public boolean isHighlighted();

	// /**
	// * Draw the object at a certain position.
	// *
	// * @param gl
	// * @param fXCoord
	// * @param fYCoord
	// * @param fZCoord
	// * @param fHeight
	// * @param fWidth
	// * @return
	// */
	// public ArrayList<Vec3f> drawObjectAtPosition(GL gl, float fXCoord, float fYCoord, float fZCoord,
	// float fHeight, float fWidth);
}
