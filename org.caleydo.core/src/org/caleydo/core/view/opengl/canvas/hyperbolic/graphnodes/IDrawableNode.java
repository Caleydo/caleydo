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
	int drawAtPostion(GL gl, float fXCoord, float fYCoord, float fHeight, float fWidth, ENodeDetailLevelType eDetailLevel);

	int setBgColor3f(float fRed, float fGreen, float fBlue);
	int setAlpha(float fAlpha); 
}
