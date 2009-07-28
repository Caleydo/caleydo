package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.ADrawableNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.lineartree.Tree;

public final class HTLayouter
	extends ATreeLayouter
	implements ITreeLayouter {

	public HTLayouter(GL gl, IViewFrustum frustum, Tree<ADrawableNode> tree) {
		super(gl, frustum, tree);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void drawLayout() {
		// TODO Auto-generated method stub

	}

}
