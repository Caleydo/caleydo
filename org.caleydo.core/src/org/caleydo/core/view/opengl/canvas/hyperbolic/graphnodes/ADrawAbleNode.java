package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.EnumMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects.DrawAbleObjectsFactory;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects.IDrawAbleObject;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections.ITreeProjection;

/**
 * Abstract of draw able node type. This type defines node objects which are self drawing.
 * 
 * @author Georg Neubauer
 */

public abstract class ADrawAbleNode
	implements IDrawAbleNode {
	private String sNodeName;
	private int iID;
	private ESelectionType eSelectionType;
	private EDrawAbleNodeDetailLevel eDetailLevel;
	private float fXCoord = 0;
	private float fYCoord = 0;
	private float fZCoord = 0;
	private float fHeight = 0;
	private float fWidth = 0;
	private ITreeProjection projection;
	private float fProjectedXCoord = 0;
	private float fProjectedYCoord = 0;
	private float fProjectedZCoord = 0;
	private ArrayList<Vec3f> alOriginalCorrespondingPoints = null;

	private EnumMap<EDrawAbleNodeDetailLevel, IDrawAbleObject> mRepresantations = null;

	@Override
	public final String getNodeName() {
		return sNodeName;
	}

	@Override
	public final int getID() {
		return iID;
	}

	@Override
	public final String toString() {
		return (sNodeName + ' ' + iID);
	}

	@Override
	public final int compareTo(IDrawAbleNode node) {
		return iID - node.getID();
	}

	@Override
	public final ArrayList<Vec3f> place(float fXCoord, float fYCoord, float fZCoord, float fHeight, float fWidth) {
		this.fXCoord = fXCoord;
		this.fYCoord = fYCoord;
		this.fZCoord = fZCoord;
		this.fHeight = fHeight;
		this.fWidth = fWidth;
		IDrawAbleObject daObj = mRepresantations.get(eDetailLevel);
		daObj.place(fXCoord, fYCoord, fZCoord, fHeight, fWidth);
		alOriginalCorrespondingPoints = new ArrayList<Vec3f>();
		alOriginalCorrespondingPoints = daObj.getConnectionPoints();
		return daObj.getConnectionPoints();
	}
	
	@Override
	public final ArrayList<Vec3f> placeAndProject(float fXCoord, float fYCoord, float fZCoord, float fHeight, float fWidth, ITreeProjection projection) {
		this.fXCoord = fXCoord;
		this.fYCoord = fYCoord;
		this.fZCoord = fZCoord;
		this.fHeight = fHeight;
		this.fWidth = fWidth;
		this.projection = projection;
		IDrawAbleObject daObj = mRepresantations.get(eDetailLevel);
		Vec3f vpProjectedPoint = new Vec3f();
		vpProjectedPoint = projection.projectCoordinates(new Vec3f(fXCoord, fYCoord, fZCoord));
		this.fProjectedXCoord = vpProjectedPoint.x();
		this.fProjectedYCoord = vpProjectedPoint.y();
		this.fProjectedZCoord = vpProjectedPoint.z();
		daObj.place(fXCoord, fYCoord, fZCoord, fHeight, fWidth);
		alOriginalCorrespondingPoints = new ArrayList<Vec3f>();
		alOriginalCorrespondingPoints = daObj.getConnectionPoints();
		daObj.place(fProjectedXCoord, fProjectedYCoord, fProjectedZCoord, fHeight, fWidth);
		return daObj.getConnectionPoints();
	}
	
	

	@Override
	public final void draw(GL gl, boolean bHighlight) {
		IDrawAbleObject daObj = mRepresantations.get(eDetailLevel);
		daObj.draw(gl, bHighlight);	
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
		IDrawAbleObject daObj = mRepresantations.get(eDetailLevel);
		return daObj.getConnectionPoints();
	}
	
	public final ArrayList<Vec3f> getConnectionPointsOfOriginalPosition() {
//		IDrawAbleObject daObj = mRepresantations.get(eDetailLevel);
//		return daObj.getConnectionPoints();
//		daObj.place(fXCoord, fYCoord, fZCoord, fHeight, fWidth);
		return alOriginalCorrespondingPoints;
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
		this.iID = iNodeID;
		mRepresantations =
			new EnumMap<EDrawAbleNodeDetailLevel, IDrawAbleObject>(EDrawAbleNodeDetailLevel.class);
		eSelectionType = ESelectionType.DESELECTED;
		int i = 0;
		for (EDrawAbleNodeDetailLevel e : EDrawAbleNodeDetailLevel.values()) {
			mRepresantations.put(e, DrawAbleObjectsFactory.getDrawAbleObject(sTypes[i++]));
		}
	}
	
	@Override
	public final Vec3f getCoordinates(){
		return new Vec3f(fXCoord, fYCoord, fZCoord);
	}
}

// private boolean bHighlight;
// @Override
// public final void setHighlight(boolean b) {
// this.bHighlight = b;
// }

