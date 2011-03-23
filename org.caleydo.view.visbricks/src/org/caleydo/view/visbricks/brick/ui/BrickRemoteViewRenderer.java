package org.caleydo.view.visbricks.brick.ui;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * View renderer specifically for brick remote views.
 * 
 * @author Christian Partl
 * 
 */
public class BrickRemoteViewRenderer extends AContainedViewRenderer {

	private GLBrick brick;

	protected AGLView view;

	/**
	 * Constructor taking an {@link AGLView} to be rendered by this renderer.
	 * 
	 * @param view
	 * @param brick
	 */
	public BrickRemoteViewRenderer(AGLView view, GLBrick brick) {
		this.brick = brick;
		this.view = view;
	}

	/**
	 * Calls the displayRemote of the view to be rendered plus pushes the ID of
	 * the Brick.
	 */
	@Override
	public void render(GL2 gl) {
//		gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
//				EPickingType.BRICK, brick.getID()));
		view.displayRemote(gl);
//		gl.glPopName();
	}

	@Override
	public void setLimits(float x, float y) {
		super.setLimits(x, y);
		ViewFrustum viewFrustum = view.getViewFrustum();
		viewFrustum.setLeft(0);
		viewFrustum.setBottom(0);
		viewFrustum.setRight(x);
		viewFrustum.setTop(y);
		view.setFrustum(viewFrustum);
		view.setDisplayListDirty();
	}

	@Override
	public int getMinHeightPixels() {
		return view.getMinPixelHeight();
	}

	@Override
	public int getMinWidthPixels() {
		return view.getMinPixelWidth();
	}

}
