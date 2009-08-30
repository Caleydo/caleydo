package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.EnumMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects.DrawAbleObjectsFactory;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects.IDrawAbleObject;

/**
 * Abstract of draw able node type. This type defines node objects which are self drawing.
 * 
 * @author Georg Neubauer
 */

public abstract class ADrawAbleNode
	implements IDrawAbleNode {
	private String sNodeName;
	private int iNodeID;
	private ESelectionType eSelectionType;
	private EDrawAbleNodeDetailLevel eDetailLevel;
	private float fXCoord = 0;
	private float fYCoord = 0;
	private float fZCoord = 0;
	private float fHeight = 0;
	private float fWidth = 0;

	private EnumMap<EDrawAbleNodeDetailLevel, IDrawAbleObject> mRepresantations = null;
	private boolean bHighlight;

	@Override
	public final String getNodeName() {
		return sNodeName;
	}

	@Override
	public final int getNodeNr() {
		return iNodeID;
	}

	@Override
	public final String toString() {
		return (sNodeName + ' ' + iNodeID);
	}

	@Override
	public final int compareTo(IDrawAbleNode node) {
		return iNodeID - node.getNodeNr();
	}

	@Override
	public final void setHighlight(boolean b) {
		this.bHighlight = b;
	}

	@Override
	public final void place(float fXCoord, float fYCoord, float fZCoord, float fHeight, float fWidth) {
		this.fXCoord = fXCoord;
		this.fYCoord = fYCoord;
		this.fZCoord = fZCoord;
		this.fHeight = fHeight;
		this.fWidth = fWidth;
	}

	@Override
	public final ArrayList<Vec3f> draw(GL gl) {
		IDrawAbleObject daObj = mRepresantations.get(eDetailLevel);
		daObj.place(fXCoord, fYCoord, fZCoord, fHeight, fWidth);
		if (bHighlight)
			daObj.drawHighlight(gl);
		else
			daObj.draw(gl);
		return daObj.getConnectionPoints();
	}

	@Override
	public final void setObjectToDetailLevel(EDrawAbleNodeDetailLevel eDetailLevel, IDrawAbleObject iObject) {
		mRepresantations.put(eDetailLevel, iObject);
	}

	@Override
	public final void setDetailLevel(EDrawAbleNodeDetailLevel eDetailLevel) {
		this.eDetailLevel = eDetailLevel;
	}

	@Override
	public final ArrayList<Vec3f> getConnectionPoints() {
		IDrawAbleObject daObj = mRepresantations.get(eSelectionType);
		daObj.place(fXCoord, fYCoord, fZCoord, fHeight, fWidth);
		return daObj.getConnectionPoints();
	}

	/**
	 * Constructor
	 * 
	 * @param sNodeName
	 * @param iNodeID
	 * @param sTypes
	 */
	public ADrawAbleNode(String sNodeName, int iNodeID, String[] sTypes) {
		this.sNodeName = sNodeName;
		this.iNodeID = iNodeID;
		mRepresantations =
			new EnumMap<EDrawAbleNodeDetailLevel, IDrawAbleObject>(EDrawAbleNodeDetailLevel.class);
		eSelectionType = ESelectionType.DESELECTED;
		int i = 0;
		for (EDrawAbleNodeDetailLevel e : EDrawAbleNodeDetailLevel.values()) {
			mRepresantations.put(e, DrawAbleObjectsFactory.getDrawAbleObject(sTypes[i++]));
		}
	}
}
