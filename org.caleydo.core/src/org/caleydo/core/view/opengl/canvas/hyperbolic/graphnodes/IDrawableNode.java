package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import javax.media.opengl.GL;

/**
 * Defines interface for drawable nodes
 * 
 * @author Georg Neubauer
 * @author Helmut Pichlhoefer
 */
public interface IDrawableNode {
	
	/**
	 * Defines the interface for drawing the node at a certain position
	 * in a certain way.
	 * 
	 * @param gl
	 * @param fXCoord
	 * @param fYCoord
	 * @param fHeight
	 * @param fWidth
	 * @param eDrawType
	 * @return
	 */
	void drawAtPostion(GL gl, float fXCoord, float fYCoord, float fHeight, float fWidth, EDrawAbleNodeDetailLevel eDetailLevel);

	/**
	 * Set the background color of the drawable node type
	 * 
	 * @param fRed
	 * @param fGreen
	 * @param fBlue
	 * @return
	 */
	void setBgColor3f(float fRed, float fGreen, float fBlue);
	
	/**
	 * Set the alpha value of the drawable node type
	 * 
	 * @param fAlpha
	 * @return
	 */
	void setAlpha(float fAlpha); 
}
