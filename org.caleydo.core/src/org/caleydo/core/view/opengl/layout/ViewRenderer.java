package org.caleydo.core.view.opengl.layout;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;

public class ViewRenderer
	extends Renderer {

	AGLView view;

	public ViewRenderer(AGLView view) {
		this.view = view;
	}

	@Override
	public void render(GL2 gl) {
		super.render(gl);
		view.displayRemote(gl);
	}

	@Override
	public void setLimits(float x, float y) {
		super.setLimits(x, y);
		ViewFrustum viewFrustum = view.getViewFrustum();
		viewFrustum.setLeft(0);
		viewFrustum.setBottom(0);
		viewFrustum.setRight(x);
		viewFrustum.setTop(y);
//		view.setFrustum(viewFrustum);
	}

}
