package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

/**
 * Defines interface for drawable nodes
 * 
 * @author Georg Neubauer
 * @author Helmut Pichlhoefer
 */
public interface IDrawAbleNode {

	/**
	 * Defines the interface for drawing the node at a certain position in a certain way.
	 * 
	 * @param gl
	 * @param fXCoord
	 * @param fYCoord
	 * @param fZCoord
	 * @param fHeight
	 * @param fWidth
	 * @param eDetailLevel
	 * @return
	 */
	ArrayList<Vec3f> drawAtPostion(GL gl, float fXCoord, float fYCoord, float fZCoord, float fHeight,
		float fWidth, EDrawAbleNodeDetailLevel eDetailLevel);
}
