package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines;

import javax.media.opengl.GL;

public interface IDrawAbleConnection
	extends Comparable<IDrawAbleConnection> {

	public int getID();

	public void draw(GL gl, boolean bHighlight);
	

	boolean isPickAble();

	// public void place(List<Vec3f> lPoints);

	// public void setHighlight(boolean b);

	// /**
	// * Draw the object at a certain position.
	// *
	// * @param gl
	// * @param lPoints
	// * List of Points to connect
	// * @param fThickness
	// * @return
	// */
	// public void drawConnectionFromStartToEnd(GL gl, List<Vec3f> lPoints, float fThickness);

	// /**
	// * Set the background color of the draw able object type
	// *
	// * @param fRed
	// * @param fGreen
	// * @param fBlue
	// * @return
	// */
	// public void setConnectionColor3f(float fRed, float fGreen, float fBlue);
	//
	// /**
	// * Set the alpha value of the draw able object type
	// *
	// * @param fAlpha
	// * @return
	// */
	// public void setConnectionAlpha(float fAlpha);

	// public boolean isHighlighted();
}
