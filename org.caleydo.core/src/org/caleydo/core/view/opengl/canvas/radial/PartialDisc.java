package org.caleydo.core.view.opengl.canvas.radial;

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
			DrawingStrategyManager.get().getDrawingStrategy(
				DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW);
		fCurrentStartAngle = 0;
	}

	public PartialDisc(int iID, float fSize, int iViewID, PickingManager pickingManager) {
		super(iID);
		this.fSize = fSize;
		this.iViewID = iViewID;
		this.pickingManager = pickingManager;
		drawingStrategy =
			DrawingStrategyManager.get().getDrawingStrategy(
				DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW);
		fCurrentStartAngle = 0;
	}

	public void drawHierarchyFull(GL gl, GLU glu, float fWidth, int iDepth) {

		setCurrentDisplayParameters(fWidth, fCurrentStartAngle, 360, 0, iDepth);

		if (iDepth <= 0)
			return;

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.RAD_HIERARCHY_PDISC_SELECTION,
			iElementID));
		drawingStrategy.drawFullCircle(gl, glu, this);
		gl.glPopName();
		iDepth--;

		float fAnglePerSizeUnit = 360 / fSize;

		if (iDepth > 0) {
			drawAllChildren(gl, glu, fWidth, fCurrentStartAngle, fWidth, fAnglePerSizeUnit, iDepth, false);
		}
	}

	public void drawHierarchyAngular(GL gl, GLU glu, float fWidth, int iDepth, float fStartAngle,
		float fAngle, float fInnerRadius) {
		
		fStartAngle = getValidAngle(fStartAngle);
		setCurrentDisplayParameters(fWidth, fStartAngle, fAngle, fInnerRadius, iDepth);

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.RAD_HIERARCHY_PDISC_SELECTION,
			iElementID));
		drawingStrategy.drawPartialDisc(gl, glu, this);
		gl.glPopName();
		iDepth--;

		float fAnglePerSizeUnit = fAngle / fSize;

		if (iDepth > 0) {
			drawAllChildren(gl, glu, fWidth, fStartAngle, fInnerRadius + fWidth, fAnglePerSizeUnit, iDepth,
				false);
		}
	}

	public void simulateDrawHierarchyAngular(float fWidth, int iDepth, float fStartAngle, float fAngle,
		float fInnerRadius) {

		fStartAngle = getValidAngle(fStartAngle);
		setCurrentDisplayParameters(fWidth, fStartAngle, fAngle, fInnerRadius, iDepth);
		iDepth--;

		float fAnglePerSizeUnit = fAngle / fSize;

		if (iDepth > 0) {
			drawAllChildren(null, null, fWidth, fStartAngle, fInnerRadius + fWidth, fAnglePerSizeUnit,
				iDepth, true);
		}
	}

	private float drawHierarchy(GL gl, GLU glu, float fWidth, float fStartAngle, float fInnerRadius,
		float fAnglePerSizeUnit, int iDepth, boolean bSimulation) {

		float fAngle = fSize * fAnglePerSizeUnit;
		fStartAngle = getValidAngle(fStartAngle);
		setCurrentDisplayParameters(fWidth, fStartAngle, fAngle, fInnerRadius, iDepth);

		if (!bSimulation) {
			gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.RAD_HIERARCHY_PDISC_SELECTION,
				iElementID));
			drawingStrategy.drawPartialDisc(gl, glu, this);
			gl.glPopName();
		}

		iDepth--;

		if (iDepth > 0) {
			drawAllChildren(gl, glu, fWidth, fStartAngle, fInnerRadius + fWidth, fAnglePerSizeUnit, iDepth,
				bSimulation);
		}
		return fAngle;
	}

	private void drawAllChildren(GL gl, GLU glu, float fWidth, float fStartAngle, float fInnerRadius,
		float fAnglePerSizeUnit, int iDepth, boolean bSimulation) {

		float fChildStartAngle = fStartAngle;

		for (int i = 0; i < vecChildren.size(); i++) {
			PartialDisc pdCurrentChild = (PartialDisc) vecChildren.elementAt(i);
			fChildStartAngle +=
				pdCurrentChild.drawHierarchy(gl, glu, fWidth, fChildStartAngle, fInnerRadius,
					fAnglePerSizeUnit, iDepth, bSimulation);
		}
	}

	private float getValidAngle(float fAngle) {
		while (fAngle > 360) {
			fAngle -= 360;
		}
		while (fAngle < 0) {
			fAngle += 360;
		}
		return fAngle;
	}

	private void setCurrentDisplayParameters(float fWidth, float fStartAngle, float fAngle,
		float fInnerRadius, int iDepth) {
		fCurrentAngle = fAngle;
		iCurrentDepth = Math.min(iDepth, iHierarchyDepth);
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

	public void setPDDrawingStrategyChildren(PDDrawingStrategy drawingStrategy, int iDepth) {
		this.drawingStrategy = drawingStrategy;
		iDepth--;

		for (int i = 0; i < vecChildren.size(); i++) {
			PartialDisc pdCurrentChild = (PartialDisc) vecChildren.elementAt(i);
			pdCurrentChild.setPDDrawingStrategyChildren(drawingStrategy, iDepth);
		}
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
