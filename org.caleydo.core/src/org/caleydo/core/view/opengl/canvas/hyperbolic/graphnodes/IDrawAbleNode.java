package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects.IDrawAbleObject;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections.ITreeProjection;

/**
 * Defines interface for drawable nodes
 * 
 * @author Georg Neubauer
 * @author Helmut Pichlhoefer
 */
public interface IDrawAbleNode
	extends Comparable<IDrawAbleNode> {

	/**
	 * Returns the name of the node
	 * 
	 * @return String
	 */
	public String getNodeName();

	/**
	 * Returns the comparable number of the node (ID)
	 * 
	 * @return int
	 */
	int getID();

	/**
	 * Place the node on a specific position
	 * 
	 * @param fXCoord
	 * @param fYCoord
	 * @param fZCoord
	 * @param fHeight
	 * @param fWidth
	 */
	List<Vec3f> place(float fXCoord, float fYCoord, float fZCoord, float fHeight, float fWidth,
		ITreeProjection treeProjection);

	/**
	 * Place the node on a specific position calcutated by the projection type
	 * 
	 * @param fXCoord
	 * @param fYCoord
	 * @param fZCoord
	 * @param fHeight
	 * @param fWidth
	 * @param projection
	 */
	// ArrayList<Vec3f> placeAndProject(float fXCoord, float fYCoord, float fZCoord, float fHeight, float
	// fWidth, ITreeProjection projection);

	/**
	 * Draw the node in current representation
	 * 
	 * @param gl
	 * @return ArrayList<Vec3f>
	 */
	void draw(GL gl, boolean bHighlight);

	/**
	 * Set or replace the representation for a specific detail-level for this node
	 * 
	 * @param eDetailLevel
	 * @param iObject
	 */
	void setObjectToDetailLevel(EDrawAbleNodeDetailLevel eDetailLevel, IDrawAbleObject iObject);

	/**
	 * Set the current detail level
	 * 
	 * @param eDetailLevel
	 */
	void setDetailLevel(EDrawAbleNodeDetailLevel eDetailLevel);

	/**
	 * Returns the connection points for the current representation and placing
	 * 
	 * @return
	 */
	List<Vec3f> getConnectionPoints();

	/**
	 * Returns the connection points for the original representation and placing
	 * 
	 * @return
	 */
	List<Vec3f> getConnectionPointsOfOriginalPosition();

	Vec3f getRealCoordinates();

//	public void setAlternativeNodeExpression(boolean bAlternativeNodeExpression);

	Vec3f getProjectedCoordinates();

	Vec2f getDimension();

	float getXCoord();

	void setXCoord(float fXCoord);

	float getYCoord();

	void setYCoord(float fYCoord);

	public float getHeight();

	public float getWidth();

//	boolean isAlternativeNodeExpression();

	void placeNodeName(int iPosition);

	ClusterNode getDependingClusterNode();

	// /**
	// * Set how the node is selected
	// *
	// * @param eSelectionType
	// */
	// void setHighlight(boolean b);

	//	
	// /**
	// * Defines the interface for drawing the node at a certain position in a certain way.
	// *
	// * @param gl
	// * @param fXCoord
	// * @param fYCoord
	// * @param fZCoord
	// * @param fHeight
	// * @param fWidth
	// * @param eDetailLevel
	// * @return
	// */
	// ArrayList<Vec3f> drawAtPostion(GL gl, float fXCoord, float fYCoord, float fZCoord, float fHeight,
	// float fWidth, EDrawAbleNodeDetailLevel eDetailLevel);
	//	
	//	
	// ArrayList<Vec3f> getConnectionPoints();
	//	
	// //void place(float fXCoord, float fYCoord, float fZCoord, float fHeight, float fWidth);
	//	
	// void setNodeDetailLavel(EDrawAbleNodeDetailLevel eDetailLevel);
	//	
	// ArrayList<Vec3f> draw(GL gl);
	//	
	// // void drawHighlight(GL gl);
	//	
	// int getID();
}
