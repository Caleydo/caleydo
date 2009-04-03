package org.caleydo.core.view.opengl.canvas.hierarchy;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;

public class PartialDisc
	extends HierarchyElement {

	private float fSize;
	private PickingManager pickingManager;
	private int iViewID;
	private PDDrawingStrategy drawingStrategy;
	
	private float fCurrentAngle;
	private float fCurrentStartAngle;
	private int iCurrentDepth;
	private float fCurrentWidth;
	private float fCurrentInnerRadius;

	public PartialDisc(int iElementID) {
		super(iElementID);
		fSize = 0;
		drawingStrategy =
			DrawingStrategyManager.getInstance().getDrawingStrategy(
				DrawingStrategyManager.PD_DRAWING_STRATEGY_NORMAL);
	}

	public PartialDisc(int iID, float fSize, int iViewID, PickingManager pickingManager) {
		super(iID);
		this.fSize = fSize;
		this.iViewID = iViewID;
		this.pickingManager = pickingManager;
		drawingStrategy =
			DrawingStrategyManager.getInstance().getDrawingStrategy(
				DrawingStrategyManager.PD_DRAWING_STRATEGY_NORMAL);
	}

	public void drawHierarchyFull(GL gl, GLU glu, float fWidth, int iDepth) {
		
		setCurrentDisplayParameters(fWidth, 0, 360, 0, iDepth);
		
		if (iDepth <= 0)
			return;
		
		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.RAD_HIERARCHY_PDISC_SELECTION,
			iElementID));
		drawingStrategy.drawFullCircle(gl, glu, fWidth);
		gl.glPopName();
		iDepth--;

		float fAnglePerSizeUnit = 360 / fSize;

		if (iDepth > 0) {
			drawAllChildren(gl, glu, fWidth, 0, fWidth, fAnglePerSizeUnit, iDepth);
		}
	}
	
	public void drawHierarchyAngular(GL gl, GLU glu, float fWidth, int iDepth, 
		float fStartAngle, float fAngle, float fInnerRadius) {
		
		setCurrentDisplayParameters(fWidth, fStartAngle, fAngle, fInnerRadius, iDepth);
		
		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.RAD_HIERARCHY_PDISC_SELECTION,
			iElementID));
		drawingStrategy.drawPartialDisc(gl, glu, fWidth, fInnerRadius, fStartAngle, fAngle);
		gl.glPopName();
		iDepth--;

		float fAnglePerSizeUnit = fAngle / fSize;

		if (iDepth > 0) {
			drawAllChildren(gl, glu, fWidth, fStartAngle, fInnerRadius + fWidth, fAnglePerSizeUnit, iDepth);
		}
		
	}

	private float drawHierarchy(GL gl, GLU glu, float fWidth, float fStartAngle, float fInnerRadius,
		float fAnglePerSizeUnit, int iDepth) {

		float fAngle = fSize * fAnglePerSizeUnit;

		setCurrentDisplayParameters(fWidth, fStartAngle, fAngle, fInnerRadius, iDepth);
		
		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.RAD_HIERARCHY_PDISC_SELECTION,
			iElementID));
		drawingStrategy.drawPartialDisc(gl, glu, fWidth, fInnerRadius, fStartAngle, fAngle);
		gl.glPopName();
		
		iDepth--;

		if (iDepth > 0) {
			drawAllChildren(gl, glu, fWidth, fStartAngle, fInnerRadius + fWidth, fAnglePerSizeUnit, iDepth);
		}
		return fAngle;
	}

	private void drawAllChildren(GL gl, GLU glu, float fWidth, float fStartAngle, float fInnerRadius,
		float fAnglePerSizeUnit, int iDepth) {

		float fChildStartAngle = fStartAngle;

		for (int i = 0; i < vecChildren.size(); i++) {
			PartialDisc pdCurrentChild = (PartialDisc) vecChildren.elementAt(i);
			fChildStartAngle +=
				pdCurrentChild.drawHierarchy(gl, glu, fWidth, fChildStartAngle, fInnerRadius,
					fAnglePerSizeUnit, iDepth);
		}
	}
	
	private void setCurrentDisplayParameters(float fWidth, float fStartAngle, float fAngle, 
		float fInnerRadius, int iDepth) {
		fCurrentAngle = fAngle;
		iCurrentDepth = iDepth;
		fCurrentInnerRadius = fInnerRadius;
		fCurrentStartAngle = fStartAngle;
		fCurrentWidth = fWidth;
	}

	public float getSize() {
		return fSize;
	}

	public void setSize(float fSize) {
		this.fSize = fSize;
	}

	public void setPickingManager(PickingManager pickingManager) {
		this.pickingManager = pickingManager;
	}

	public void setViewID(int iViewID) {
		this.iViewID = iViewID;
	}

	public void setPDDrawingStrategy(PDDrawingStrategy drawingStrategy) {
		this.drawingStrategy = drawingStrategy;
	}
	
	public boolean hasChildren() {
		return (vecChildren.size() > 0);
	}
	
	public float getCurrentAngle() {
		return fCurrentAngle;
	}

	public float getCurrentStartAngle() {
		return fCurrentStartAngle;
	}

	public int getCurrentDepth() {
		return iCurrentDepth;
	}

	public float getCurrentWidth() {
		return fCurrentWidth;
	}

	public float getCurrentInnerRadius() {
		return fCurrentInnerRadius;
	}

}
