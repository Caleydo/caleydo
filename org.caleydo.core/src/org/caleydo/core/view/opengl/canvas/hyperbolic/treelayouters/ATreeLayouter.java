package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.ADrawAbleNode;

public abstract class ATreeLayouter
	implements ITreeLayouter, Comparable<ATreeLayouter> {

	// protected float fXBoarderSpacePercentage = 0;
	// protected float fYBoarderSpacePercentage = 0;
	protected int iComparableValue;
	protected float fHeight;
	protected float fWidth;
	protected float fViewSpaceX[] = { 0f, 0f };
	protected float fViewSpaceY[] = { 0f, 0f };
	protected float fViewSpaceXAbs = 0;
	protected float fViewSpaceYAbs = 0;
	protected IViewFrustum viewFrustum;

	/*
	 * TODO: replace with standard tree someday!
	 */
	// Tree<ADrawAbleNode> tree = null;

	public ATreeLayouter(IViewFrustum frustum/* , Tree<ADrawAbleNode> tree */) {
		// this.tree = tree;
		// this.fHigh = frustum.getHeight();
		// this.fHigh = this.fHigh - this.fHigh / 100f * fYBoarderSpacePercentage;
		// this.fWidth = frustum.getWidth();
		// this.fWidth = this.fWidth - this.fWidth / 100f * fXBoarderSpacePercentage;
		this.viewFrustum = frustum;
		updateSizeInfo();
	}

	@Override
	public int compareTo(ATreeLayouter layouter) {
		return this.iComparableValue - layouter.iComparableValue;
	}

	/*
	 * @Override public final void setTree(Tree<ADrawAbleNode> tree) { this.tree = tree; }
	 */
	@Override
	public abstract void renderTreeLayout(GL gl, Tree<ADrawAbleNode> tree);

	@Override
	public final void setBoarderSpaces(float fXBoarderSpacePercentage, float fYBoarderSpacePercentage) {
//		this.fXBoarderSpacePercentage = fXBoarderSpacePercentage;
	//	this.fYBoarderSpacePercentage = fYBoarderSpacePercentage;
	}

	protected final void updateSizeInfo() {
		fHeight = viewFrustum.getHeight();
		fWidth = viewFrustum.getWidth();
		fViewSpaceX[0] = fWidth * HyperbolicRenderStyle.X_BORDER_SPACING;
		fViewSpaceX[1] = fWidth - fWidth * HyperbolicRenderStyle.X_BORDER_SPACING;
		fViewSpaceXAbs = Math.abs(fViewSpaceX[0] - fViewSpaceX[1]);
		fViewSpaceY[0] = fHeight * HyperbolicRenderStyle.Y_BORDER_SPACING;
		fViewSpaceY[1] = fHeight - fHeight * HyperbolicRenderStyle.Y_BORDER_SPACING;
		fViewSpaceYAbs = Math.abs(fViewSpaceY[0] - fViewSpaceY[1]);
	}
	

}
