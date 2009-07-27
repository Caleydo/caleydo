package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.ADrawableNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawableNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.lineartree.Tree;

public abstract class ATreeLayouter
	implements ITreeLayouter, Comparable<ATreeLayouter> {

	protected float fXBoarderSpacePercentage = 0;
	protected float fYBoarderSpacePercentage = 0;
	protected int iComparableValue;
	protected float fHigh;
	protected float fWidth;

	/*
	 * TODO: replace with standard tree someday!
	 */
	Tree<ADrawableNode> tree = null;
	protected GL gl;

	public ATreeLayouter(GL gl, IViewFrustum frustum, Tree<ADrawableNode> tree) {
		this.tree = tree;
		this.fHigh = frustum.getHeight();
		this.fHigh = this.fHigh - this.fHigh / 100f * fYBoarderSpacePercentage;
		this.fWidth = frustum.getWidth();
		this.fWidth = this.fWidth -this.fWidth / 100f * fXBoarderSpacePercentage;
		this.gl = gl;
	}

	@Override
	public int compareTo(ATreeLayouter layouter) {
		return this.iComparableValue - layouter.iComparableValue;
	}

	@Override
	public final void setTree(Tree<ADrawableNode> tree) {
		this.tree = tree;
	}

	@Override
	public abstract void drawLayout();

	@Override
	public final void setBoarderSpaces(float fXBoarderSpacePercentage, float fYBoarderSpacePercentage) {
		this.fXBoarderSpacePercentage = fXBoarderSpacePercentage;
		this.fYBoarderSpacePercentage = fYBoarderSpacePercentage;
	}

}
