package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import gleem.linalg.Vec3f;
import java.util.ArrayList;
import java.util.EnumMap;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects.IDrawAbleObject;

/**
 * Abstract of draw able node type. This type defines node objects which are self drawing.
 * 
 * @author Georg Neubauer
 */

public abstract class ADrawAbleNode
	implements IDrawAbleNode, Comparable<ADrawAbleNode> {
	String nodeName;
	int iComparableValue;
	protected EnumMap<EDrawAbleNodeDetailLevel, IDrawAbleObject> mRepresantations = null;
	protected int iLayer;

	/**
	 * Constructor
	 * 
	 * @param nodeName
	 * @param iComparableValue
	 */
	public ADrawAbleNode(String nodeName, int iComparableValue) {
		this.nodeName = nodeName;
		this.iComparableValue = iComparableValue;
		mRepresantations =
			new EnumMap<EDrawAbleNodeDetailLevel, IDrawAbleObject>(EDrawAbleNodeDetailLevel.class);
	}

	/**
	 * Returns the name of the node.
	 * 
	 * @return
	 */
	public final String getNodeName() {
		return this.nodeName;
	}

	@Override
	public final int compareTo(ADrawAbleNode node) {
		return this.iComparableValue - node.iComparableValue;
	}

	@Override
	public final String toString() {
		return this.nodeName + " " + this.iComparableValue;
	}

	// TODO: needs implementation of GLList

	@Override
	public final ArrayList<Vec3f> drawAtPostion(GL gl, float fXCoord, float fYCoord, float fZCoord,
		float fHeight, float fWidth, EDrawAbleNodeDetailLevel eDetailLevel) {
		return mRepresantations.get(eDetailLevel).drawObjectAtPosition(gl, fXCoord, fYCoord, fZCoord,
			fHeight, fWidth);
	}

	/**
	 * Add a draw able object to a specific detail level.
	 * 
	 * @param eDetailLevel
	 * @param iObject
	 */
	public final void setDetailLevel(EDrawAbleNodeDetailLevel eDetailLevel, IDrawAbleObject iObject) {
		mRepresantations.put(eDetailLevel, iObject);
	}

	/**
	 * Set the Tree-Layer of the Node
	 * 
	 * @param iLayer
	 */
	public final void setLayer(int iLayer) {
		this.iLayer = iLayer;
	}

	/**
	 * Get the Tree-Layer of the node
	 * 
	 * @return iLayer
	 */
	public final int getLayer() {
		return this.iLayer;
	}
}
