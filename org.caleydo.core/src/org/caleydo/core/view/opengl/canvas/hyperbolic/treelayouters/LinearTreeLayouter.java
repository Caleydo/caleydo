package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.ADrawableNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.EDrawAbleNodeDetailLevel;
import org.caleydo.core.view.opengl.canvas.hyperbolic.lineartree.Tree;

public final class LinearTreeLayouter
	extends ATreeLayouter {

	public LinearTreeLayouter(IViewFrustum frustum, Tree<ADrawableNode> tree) {
		super(frustum, tree);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void drawLayout(GL gl) {
		
		tree.getRoot().drawAtPostion(gl, fHigh/2f, fWidth/2f, 0.0f, 0.2f, 0.2f, EDrawAbleNodeDetailLevel.VeryHigh);
		
		tree.getRoot().drawAtPostion(gl, fHigh/4f, fWidth/4f, 0.0f, 0.2f, 0.2f, EDrawAbleNodeDetailLevel.High);
		return;
	}
}
