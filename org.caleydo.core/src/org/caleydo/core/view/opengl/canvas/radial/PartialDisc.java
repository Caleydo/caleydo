package org.caleydo.core.view.opengl.canvas.radial;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.util.clusterer.ClusterNode;

public class PartialDisc
	implements Comparable<PartialDisc> {

	private float fSize;
	private PickingManager pickingManager;
	private int iViewID;
	private PDDrawingStrategy drawingStrategy;
	private ClusterNode clusterNode;
	private Tree<PartialDisc> partialDiscTree;

	private int iElementID;
	private float fCurrentAngle;
	private float fCurrentStartAngle;
	private int iCurrentDepth;
	private float fCurrentWidth;
	private float fCurrentInnerRadius;
	private int iDrawingStrategyDepth;

	public PartialDisc(int iElementID, float fSize, int iViewID, PickingManager pickingManager,
		Tree<PartialDisc> partialDiscTree, ClusterNode clusterNode) {

		this.iElementID = iElementID;
		this.fSize = fSize;
		this.iViewID = iViewID;
		this.pickingManager = pickingManager;
		this.partialDiscTree = partialDiscTree;
		this.clusterNode = clusterNode;
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
		ArrayList<PartialDisc> alChildren = partialDiscTree.getChildren(this);

		if (alChildren != null) {
			for (int i = 0; i < alChildren.size(); i++) {
				PartialDisc pdCurrentChild = (PartialDisc) alChildren.get(i);
				fChildStartAngle +=
					pdCurrentChild.drawHierarchy(gl, glu, fWidth, fChildStartAngle, fInnerRadius,
						fAnglePerSizeUnit, iDepth, bSimulation);
			}
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
		//TODO: Do depth calculation properly (hopefully with clusternode)
		iCurrentDepth = Math.min(iDepth, getHierarchyDepth(GLRadialHierarchy.DISP_HIER_DEPTH_DEFAULT));
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
		iDrawingStrategyDepth = iDepth;
		iDepth--;
		ArrayList<PartialDisc> alChildren = partialDiscTree.getChildren(this);

		if (iDepth > 0 && alChildren != null) {
			for (int i = 0; i < alChildren.size(); i++) {
				PartialDisc pdCurrentChild = (PartialDisc) alChildren.get(i);
				pdCurrentChild.setPDDrawingStrategyChildren(drawingStrategy, iDepth);
			}
		}
	}

	public boolean hasParent(PartialDisc pdParent, int iDepth) {
		PartialDisc pdCurrentParent = partialDiscTree.getParent(this);

		if (pdCurrentParent == null || iDepth <= 0) {
			return false;
		}
		if (pdCurrentParent == pdParent) {
			return true;
		}
		return pdCurrentParent.hasParent(pdParent, iDepth - 1);
	}

	public PartialDisc getParent() {
		return partialDiscTree.getParent(this);
	}

	public boolean hasChildren() {
		return partialDiscTree.hasChildren(this);
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

	public String getName() {
		return clusterNode.getNodeName();
	}
	
	public float getCoefficient() {
		return clusterNode.getCoefficient();
	}

	public int getDrawingStrategyDepth() {
		return iDrawingStrategyDepth;
	}

	public void setDrawingStrategyDepth(int iDrawingStrategyDepth) {
		this.iDrawingStrategyDepth = iDrawingStrategyDepth;
	}

	@Override
	public int compareTo(PartialDisc disc) {
		return clusterNode.getClusterNr() - disc.clusterNode.getClusterNr();
	}

	public int getHierarchyDepth(int iMaxDepthToSearch) {
		// TODO: Maybe this way or another
		// return clusterNode.getDepth();
//		ArrayList<PartialDisc> alChildren = partialDiscTree.getChildren(this);
//		if (alChildren == null || iMaxDepthToSearch <= 1)
//			return 1;
//		int iDepth = 1;
//		for (PartialDisc child : alChildren) {
//			int iChildDepth = child.getHierarchyDepth(1, iMaxDepthToSearch);
//			iDepth = (iChildDepth > iDepth) ? iChildDepth : iDepth;
//		}
		return getHierarchyDepth(0, iMaxDepthToSearch);
	}

	private int getHierarchyDepth(int iCurDepth, int iMaxDepthToSearch) {

		iCurDepth++;
		ArrayList<PartialDisc> alChildren = partialDiscTree.getChildren(this);
		if (alChildren == null || iMaxDepthToSearch <= iCurDepth)
			return iCurDepth;
		int iDepth = 1;
		for (PartialDisc child : alChildren) {
			int iChildDepth = child.getHierarchyDepth(iCurDepth, iMaxDepthToSearch);
			iDepth = (iChildDepth > iDepth) ? iChildDepth : iDepth;
		}
		return iDepth;
	}

}
