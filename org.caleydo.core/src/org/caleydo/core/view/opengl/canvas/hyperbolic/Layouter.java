package org.caleydo.core.view.opengl.canvas.hyperbolic;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.camera.IViewFrustum;

/**
 * Base Class for different ways to display trees
 * 
 * @author Helmut Pichlhöfer
 */

public class Layouter {

	protected float fRightBorder;
	protected float fLeftBorder;
	protected float fWidth;
	protected float fHight;
	protected float fCenterX;
	protected float fCenterY;
	protected IDrawAbleNode rootNode;

	public Layouter(GL gl, IViewFrustum frustum) {
		fRightBorder = frustum.getRight();
		fLeftBorder = frustum.getLeft();
		fWidth = frustum.getWidth();
		fHight = frustum.getHeight();
		fCenterX = fHight / 2;
		fCenterY = fWidth / 2;

		gl.glVertex3f(fCenterX, fCenterY, 0.0f);

	}

	public void DrawLayout() {

	}

	public void UpdateLayouter() {
		DrawLayout();

	}

}
