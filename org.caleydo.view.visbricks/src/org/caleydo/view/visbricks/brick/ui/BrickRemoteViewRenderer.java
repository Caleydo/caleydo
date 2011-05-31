package org.caleydo.view.visbricks.brick.ui;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
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
	protected PixelGLConverter pixelGLConverter;
	protected int viewportPositionX;
	protected int viewportPositionY;
	protected int viewportWidth;
	protected int viewportHeight;

	/**
	 * Constructor taking an {@link AGLView} to be rendered by this renderer.
	 * 
	 * @param view
	 * @param brick
	 */
	public BrickRemoteViewRenderer(AGLView view, GLBrick brick,
			PixelGLConverter pixelGLConverter) {
		this.brick = brick;
		this.view = view;
		this.pixelGLConverter = pixelGLConverter;
	}

	/**
	 * Calls the displayRemote of the view to be rendered plus pushes the ID of
	 * the Brick.
	 */
	@Override
	public void render(GL2 gl) {
		// gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
		// EPickingType.BRICK, brick.getID()));
		gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
		gl.glPushMatrix();
		gl.glViewport(viewportPositionX, viewportPositionY, viewportWidth,
				viewportHeight);
		gl.glLoadIdentity();
		// gl.glScalef(2f, 2f, 1);
		// gl.glMatrixMode(GL2.GL_PROJECTION);
		// gl.glPushMatrix();
		// gl.glLoadIdentity();
		//
		// ViewFrustum viewFrustum = view.getViewFrustum();
		// gl.glOrtho(viewFrustum.getLeft(), viewFrustum.getRight(),
		// viewFrustum.getBottom(), viewFrustum.getTop(),
		// viewFrustum.getNear(), viewFrustum.getFar());
		// gl.glMatrixMode(GL2.GL_MODELVIEW);
		// gl.glLoadIdentity();
		// view.reshape(drawable, viewportPositionX, viewportPositionY,
		// viewportWidth, viewportHeight);
		view.displayRemote(gl);
		// gl.glMatrixMode(GL2.GL_PROJECTION);
		// gl.glPopMatrix();
		// gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPopMatrix();
		gl.glPopAttrib();
		// gl.glPopName();
	}

	@Override
	public void setLimits(float x, float y) {
		super.setLimits(x, y);

		viewportWidth = pixelGLConverter.getPixelWidthForGLWidth(x);
		viewportHeight = pixelGLConverter.getPixelHeightForGLHeight(y);
		
		//FIXME: This is very ugly, but necessary atm.
		viewportPositionX = pixelGLConverter.getPixelWidthForGLWidth(brick
				.getWrappingLayout().getTranslateX()
				+ brick.getDimensionGroup().getVisBricksView()
						.getArchInnerWidth() + elementLayout.getTranslateX());
		viewportPositionY = pixelGLConverter.getPixelHeightForGLHeight(brick
				.getWrappingLayout().getTranslateY()
				+ elementLayout.getTranslateY());

		ViewFrustum templateFrustum = view.getSerializableRepresentation()
				.getViewFrustum();

		float value = y / x
				* (templateFrustum.getRight() - templateFrustum.getLeft());
		templateFrustum.setTop(value);
		ViewFrustum viewFrustum = view.getViewFrustum();
		viewFrustum.setLeft(templateFrustum.getLeft());
		viewFrustum.setBottom(templateFrustum.getBottom());
		viewFrustum.setRight(templateFrustum.getRight());
		viewFrustum.setTop(templateFrustum.getTop());
		viewFrustum.setNear(templateFrustum.getNear());
		viewFrustum.setFar(templateFrustum.getFar());
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
