package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.EnumMap;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects.IDrawAbleObject;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections.ITreeProjection;

/**
 * Abstract of draw able node type. This type defines node objects which are self drawing.
 * 
 * @author Georg Neubauer
 */

public abstract class ADrawAbleNode
	implements IDrawAbleNode {
	private ClusterNode clNode;
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
	boolean bIsVisible = false;
	Vec3f positionOutOfDisplay = null;
	private IDrawAbleNode parentNode = null;
	private boolean bIsRoot = false;

	// TODO: create DAobjects which would handle this
	//private TextRenderer textRenderer = null;
	// private boolean bAlternativeNodeExpression = false;
	
	private EnumMap<EDrawAbleNodeDetailLevel, IDrawAbleObject> mRepresantations = null;
	protected boolean bHighlight = false;

	private IDrawAbleObject useObject = null;

	public ADrawAbleNode(ClusterNode clNode) {
		this.clNode = clNode;
		this.mRepresantations =
			new EnumMap<EDrawAbleNodeDetailLevel, IDrawAbleObject>(EDrawAbleNodeDetailLevel.class);
//		this.textRenderer =
//			new TextRenderer(new Font(HyperbolicRenderStyle.NODE_FONT_NAME,
//				HyperbolicRenderStyle.NODE_FONT_STYLE, HyperbolicRenderStyle.NODE_FONT_SIZE), true, true);
	}
	
	@Override
	public final ClusterNode getDependingClusterNode() {
		return clNode;
	}

	@Override
	public boolean isVisible() {
		return bIsVisible;
	}
	
	@Override
	public final void setHighlight(boolean b) {
		this.bHighlight = b;
	}

	protected final void registerDrawAbleObject(IDrawAbleObject DAO_VH, IDrawAbleObject DAO_H,
		IDrawAbleObject DAO_N, IDrawAbleObject DAO_L, IDrawAbleObject DAO_VL) {
		mRepresantations.put(EDrawAbleNodeDetailLevel.VeryHigh, DAO_VH);
		mRepresantations.put(EDrawAbleNodeDetailLevel.High, DAO_H);
		mRepresantations.put(EDrawAbleNodeDetailLevel.Normal, DAO_N);
		mRepresantations.put(EDrawAbleNodeDetailLevel.Low, DAO_L);
		mRepresantations.put(EDrawAbleNodeDetailLevel.VeryLow, DAO_VL);
		eDetailLevel = EDrawAbleNodeDetailLevel.Normal;
		useObject = DAO_N;
	}

	public final void translate(Vec3f vTranslation, ITreeProjection treeProjection){
		place(fXCoord + vTranslation.x(), fYCoord + vTranslation.y(), 0.0f, fHeight, fWidth,treeProjection);
	}
	
	@Override
	public void setParentOfNode(IDrawAbleNode parent){
		this.parentNode = parent;
	}
	@Override
	public void nodeIsRoot(){
		bIsRoot = true;
	}
	
	@Override
	public boolean IsNodeRoot(){
		return bIsRoot;
	}
	
	public void setVisible(boolean bIsVisible) {
		this.bIsVisible = bIsVisible;
	}

	
	

	public void setNoneDisplayedNodePosition(float x, float y, float z) {
		this.positionOutOfDisplay.set(x, y, z);
	}

	public Vec3f getNoneDisplayedNodePosition() {
		return this.positionOutOfDisplay;
	}

	@Override
	public final String getNodeName() {
		return clNode.getNodeName();
	}

	@Override
	public final int getID() {
		return clNode.getClusterNr();
	}

	@Override
	public final String toString() {
		return clNode.toString();
	}

	@Override
	public final int compareTo(IDrawAbleNode node) {
		return this.getID() - node.getID();
	}

	@Override
	public final List<Vec3f> place(float fXCoord, float fYCoord, float fZCoord, float fHeight, float fWidth,
		ITreeProjection treeProjection) {
		this.fXCoord = fXCoord;
		this.fYCoord = fYCoord;
		this.fZCoord = fZCoord;
		this.fHeight = fHeight;
		this.fWidth = fWidth;
		if (treeProjection != null) {

			float fLenthOfPointToCenter = treeProjection.getLineFromPointToCenter(fXCoord, fYCoord);
			float fViewAbleSpace = treeProjection.getProjectedLineFromCenterToBorder();
			System.out.println(String.valueOf(fViewAbleSpace));

			if (fLenthOfPointToCenter >= fViewAbleSpace ){//|| !this.foundVisibleAncestor()) {
				bIsVisible = false;
				System.out.println(String.valueOf(fLenthOfPointToCenter));
				System.out.println(String.valueOf(bIsVisible));
			}
			else {

//				if(parentNode != null){
//				if (!parentNode.isVisible() && parentNode.IsNodeRoot())
//					bIsVisible = true;
//				else if (!parentNode.isVisible())
//					bIsVisible = false;
//				else
//				bIsVisible = true;
//				}
//				else
					bIsVisible = true;

				
				System.out.println(String.valueOf(fLenthOfPointToCenter));
				System.out.println(String.valueOf(bIsVisible));
			}
			Vec3f vpProjectedPoint = treeProjection.projectCoordinates(new Vec3f(fXCoord, fYCoord, fZCoord));
			System.out.println();

			// // if(vpProjectedPoint.z() > (float)Math.PI)
			// if(vpProjectedPoint.z() < 0){
			// bIsVisible = false;
			//
			// this.fProjectedZCoord = vpProjectedPoint.z();
			//				
			// }
			// // else if(vpProjectedPoint.z() < 0)
			// // bIsVisible = false;
			// else{
			// bIsVisible = true;
			// this.fProjectedXCoord = vpProjectedPoint.x();
			// this.fProjectedYCoord = vpProjectedPoint.y();
			// this.fProjectedZCoord = vpProjectedPoint.z();
			// }
			{
				this.fProjectedXCoord = vpProjectedPoint.x();
				this.fProjectedYCoord = vpProjectedPoint.y();
				this.fProjectedZCoord = vpProjectedPoint.z();
			}
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
	public final void draw(GL gl) {
		useObject.draw(gl, bHighlight);
	}

	// TODO: delete
	@Override
	public final void setObjectToDetailLevel(EDrawAbleNodeDetailLevel eDetailLevel, IDrawAbleObject iObject) {
		mRepresantations.put(eDetailLevel, iObject);
	}

	@Override
	public final void setDetailLevel(EDrawAbleNodeDetailLevel eDetailLevel) {
		this.eDetailLevel = eDetailLevel;
		useObject = mRepresantations.get(eDetailLevel);
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

	@Override
	public final Vec3f getRealCoordinates() {
		return new Vec3f(fXCoord, fYCoord, fZCoord);
	}

	@Override
	public final Vec3f getProjectedCoordinates() {
		return new Vec3f(fProjectedXCoord, fProjectedYCoord, fProjectedZCoord);
	}

	protected List<Vec3f> getConnectionPointsSpecialNode() {
		return null;
	}

	// TODO: KILL THIS METHOD!!! --> KILL TEXTRENDERING NODE (should be replaced by an Label)
//	protected void drawSpecialNode(GL gl) {
//	}

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

	// TODO: delete
	// public final void setAlternativeNodeExpression(boolean bAlternativeNodeExpression) {
	// this.bAlternativeNodeExpression = bAlternativeNodeExpression;
	// }

	// TODO: delete
	// @Override
	// public final boolean isAlternativeNodeExpression() {
	// return bAlternativeNodeExpression;
	// }

	// TODO: Maybe replace iPostion with an Enum someday!
	// TODO: Add this functionality to special DAobjects
	@Override
	public final void placeNodeName(int iPosition) {
//		Rectangle2D rect = textRenderer.getBounds(this.getNodeName());
//		float fTextHeight = (float) rect.getHeight();
//		float fTextWidth = (float) rect.getWidth();
//		float fTextScaling = fHeight / fTextHeight / 2.0f;
//		float fTextXPos = fProjectedXCoord - fTextWidth * fTextScaling / 2.0f;
//		float fTextYPos;
//		switch (iPosition) {
//			case 0:
//				fTextYPos = fProjectedYCoord - fWidth / 2.0f - fTextHeight * fTextScaling;
//				break;
//			case 1:
//				fTextYPos = fProjectedYCoord + fWidth / 2.0f + fTextHeight * fTextScaling;
//				break;
//			default:
//				fTextYPos = fProjectedYCoord;
//				break;
//		}
//		// TODO: Specify this color in RendeStyle
//		textRenderer.setColor(0, 0, 0, 1);
//		textRenderer.setSmoothing(true);
//		textRenderer.begin3DRendering();
//		textRenderer.draw3D(this.getNodeName(), fTextXPos, fTextYPos, fProjectedZCoord, fTextScaling);
//		textRenderer.end3DRendering();
	}

	
	public void changeCurrentVisiblyOfNode() {
		this.bIsVisible = !this.bIsVisible;
	}
	
	public boolean foundVisibleAncestor(){
		if(this.parentNode == null)
			return true;
		else if(!parentNode.isVisible())
			return false;
		else{
			return true;
		}
			
	}


}

// private boolean bHighlight;

// /**
// * Constructor
// *
// * @param sNodeName
// * @param iNodeID
// * @param sTypes
// */
// public ADrawAbleNode(String sNodeName, int iNodeID, String[] sTypes) {
// this.sNodeName = sNodeName;
// this.iID = iNodeID;
// if (sTypes != null) {
// mRepresantations =
// new EnumMap<EDrawAbleNodeDetailLevel, IDrawAbleObject>(EDrawAbleNodeDetailLevel.class);
// int i = 0;
// for (EDrawAbleNodeDetailLevel e : EDrawAbleNodeDetailLevel.values()) {
// mRepresantations.put(e, DrawAbleObjectsFactory.getDrawAbleObject(sTypes[i++]));
// }
// }
// this.textRenderer =
// new TextRenderer(new Font(HyperbolicRenderStyle.NODE_FONT_NAME,
// HyperbolicRenderStyle.NODE_FONT_STYLE, HyperbolicRenderStyle.NODE_FONT_SIZE), true, true);
// }
