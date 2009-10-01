package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects.DrawAbleObjectsFactory;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects.IDrawAbleObject;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections.ITreeProjection;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Abstract of draw able node type. This type defines node objects which are self drawing.
 * 
 * @author Georg Neubauer
 */

public abstract class ADrawAbleNode
	implements IDrawAbleNode {
	private String sNodeName;
	private int iID;
	private EDrawAbleNodeDetailLevel eDetailLevel;
	protected float fXCoord = 0;
	protected float fYCoord = 0;
	protected float fZCoord = 0;
	protected float fHeight = 0;
	protected float fWidth = 0;
	private float fProjectedXCoord = 0;
	private float fProjectedYCoord = 0;
	private float fProjectedZCoord = 0;
	private List<Vec3f> alOriginalCorrespondingPoints = null;
	private TextRenderer textRenderer = null;

	private EnumMap<EDrawAbleNodeDetailLevel, IDrawAbleObject> mRepresantations = null;
	private boolean bAlternativeNodeExpression = false;

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

	// @Override
	// public final ArrayList<Vec3f> place(float fXCoord, float fYCoord, float fZCoord, float fHeight, float
	// fWidth) {
	// this.fXCoord = fXCoord;
	// this.fYCoord = fYCoord;
	// this.fZCoord = fZCoord;
	// this.fHeight = fHeight;
	// this.fWidth = fWidth;
	// IDrawAbleObject daObj = mRepresantations.get(eDetailLevel);
	// daObj.place(fXCoord, fYCoord, fZCoord, fHeight, fWidth);
	// alOriginalCorrespondingPoints = new ArrayList<Vec3f>();
	// alOriginalCorrespondingPoints = daObj.getConnectionPoints();
	// return daObj.getConnectionPoints();
	// }

	@Override
	public final List<Vec3f> place(float fXCoord, float fYCoord, float fZCoord, float fHeight, float fWidth,
		ITreeProjection treeProjection) {
		this.fXCoord = fXCoord;
		this.fYCoord = fYCoord;
		this.fZCoord = fZCoord;
		this.fHeight = fHeight;
		this.fWidth = fWidth;
		if (treeProjection != null) {
			Vec3f vpProjectedPoint = treeProjection.projectCoordinates(new Vec3f(fXCoord, fYCoord, fZCoord));
			this.fProjectedXCoord = vpProjectedPoint.x();
			this.fProjectedYCoord = vpProjectedPoint.y();
			this.fProjectedZCoord = vpProjectedPoint.z();
		}
		else {
			this.fProjectedXCoord = this.fXCoord;
			this.fProjectedYCoord = this.fYCoord;
			this.fProjectedZCoord = this.fZCoord;
		}
		if (mRepresantations != null) {
			IDrawAbleObject daObj = mRepresantations.get(eDetailLevel);
			daObj.place(fXCoord, fYCoord, fZCoord, fHeight, fWidth);
			alOriginalCorrespondingPoints = daObj.getConnectionPoints();
			daObj.place(fProjectedXCoord, fProjectedYCoord, fProjectedZCoord, fHeight, fWidth);
		}
		else {
			alOriginalCorrespondingPoints = getConnectionPointsSpecialNode();
		}
		// bIsAbleToPick = fProjectedZCoord >= 0.0f;
		return alOriginalCorrespondingPoints;
	}

	@Override
	public void draw(GL gl, boolean bHighlight) {
		if (mRepresantations != null) {
			IDrawAbleObject daObj = mRepresantations.get(eDetailLevel);
			daObj.setAlternativeNodeExpression(bAlternativeNodeExpression);
			daObj.draw(gl, bHighlight);
		}
		else
			drawSpecialNode(gl);
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
	public final List<Vec3f> getConnectionPoints() {
		if (mRepresantations != null) {
			IDrawAbleObject daObj = mRepresantations.get(eDetailLevel);
			return daObj.getConnectionPoints();
		}
		else
			return getConnectionPointsSpecialNode();
	}

	@Override
	public final List<Vec3f> getConnectionPointsOfOriginalPosition() {
		// IDrawAbleObject daObj = mRepresantations.get(eDetailLevel);
		// return daObj.getConnectionPoints();
		// daObj.place(fXCoord, fYCoord, fZCoord, fHeight, fWidth);
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
		if (sTypes != null) {
			mRepresantations =
				new EnumMap<EDrawAbleNodeDetailLevel, IDrawAbleObject>(EDrawAbleNodeDetailLevel.class);
			int i = 0;
			for (EDrawAbleNodeDetailLevel e : EDrawAbleNodeDetailLevel.values()) {
				mRepresantations.put(e, DrawAbleObjectsFactory.getDrawAbleObject(sTypes[i++]));
			}
		}
		this.textRenderer =
			new TextRenderer(new Font(HyperbolicRenderStyle.NODE_FONT_NAME,
				HyperbolicRenderStyle.NODE_FONT_STYLE, HyperbolicRenderStyle.NODE_FONT_SIZE), true, true);
	}

	@Override
	public final Vec3f getRealCoordinates() {
		return new Vec3f(fXCoord, fYCoord, fZCoord);
	}

	@Override
	public final Vec3f getProjectedCoordinates() {
		return new Vec3f(fProjectedXCoord, fProjectedYCoord, fProjectedZCoord);
	}

	// @Override
	// public final boolean isPickAble() {
	// return bIsAbleToPick;
	// }

	protected List<Vec3f> getConnectionPointsSpecialNode() {
		return null;
	}

	// TODO: KILL THIS METHOD!!! --> KILL TEXTRENDERING NODE (should be replaced by an Label)
	protected void drawSpecialNode(GL gl) {
	}

	@Override
	public final Vec2f getDimension() {
		return new Vec2f(fHeight, fWidth);
	}

	@Override
	public final float getXCoord() {
		return fXCoord;
	}

	@Override
	public final void setXCoord(float fXCoord) {
		this.fXCoord = fXCoord;
	}

	@Override
	public final float getYCoord() {
		return fYCoord;
	}

	@Override
	public final void setYCoord(float fYCoord) {
		this.fYCoord = fYCoord;
	}

	@Override
	public final float getHeight() {
		return fHeight;
	}

	@Override
	public final float getWidth() {
		return fWidth;
	}

	public final void setAlternativeNodeExpression(boolean bAlternativeNodeExpression) {
		this.bAlternativeNodeExpression = bAlternativeNodeExpression;
	}

	@Override
	public final boolean isAlternativeNodeExpression() {
		return bAlternativeNodeExpression;
	}

	// TODO: Maybe replace iPostion with an Enum someday!
	@Override
	public final void placeNodeName(int iPosition){
		Rectangle2D rect = textRenderer.getBounds(this.getNodeName());
		float fTextHeight = (float)rect.getHeight();
		float fTextWidth = (float)rect.getWidth();
		float fTextScaling = fHeight/fTextHeight / 2.0f;
		float fTextXPos = fXCoord - fTextWidth * fTextScaling / 2.0f;
		float fTextYPos;
		switch (iPosition){
			case 0:
				fTextYPos = fYCoord - fWidth / 2.0f - fTextHeight * fTextScaling;
				break;
			case 1:
				fTextYPos = fYCoord + fWidth / 2.0f + fTextHeight * fTextScaling;
				break;
			default:
					fTextYPos = fYCoord;
					break;
		}	
		// TODO: Specify this color in RendeStyle
		textRenderer.setColor(0, 0, 0, 1);
		textRenderer.setSmoothing(true);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(this.getNodeName(), fTextXPos, fTextYPos, fZCoord, fTextScaling);
		textRenderer.end3DRendering();			
	}
}

// private boolean bHighlight;
// @Override
// public final void setHighlight(boolean b) {
// this.bHighlight = b;
// }

