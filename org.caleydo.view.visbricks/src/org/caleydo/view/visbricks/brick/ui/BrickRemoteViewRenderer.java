package org.caleydo.view.visbricks.brick.ui;

import java.awt.Point;

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
	protected boolean handleMouseWheel;

	// protected float viewportWidth;
	// protected float viewportHeight;

	/**
	 * Constructor taking an {@link AGLView} to be rendered by this renderer.
	 * 
	 * @param view
	 * @param brick
	 * @param pixelGLConverter
	 * @param handleMouseWheel
	 *            Determines, whether the remote view should handle mouse
	 *            wheeling.
	 */
	public BrickRemoteViewRenderer(AGLView view, GLBrick brick,
			PixelGLConverter pixelGLConverter, boolean handleMouseWheel) {
		this.brick = brick;
		this.view = view;
		this.pixelGLConverter = pixelGLConverter;
		this.handleMouseWheel = handleMouseWheel;
	}

	/**
	 * Calls the displayRemote of the view to be rendered plus pushes the ID of
	 * the Brick.
	 */
	@Override
	public void render(GL2 gl) {
		// gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
		// EPickingType.BRICK, brick.getID()));
		// gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
		// gl.glPushMatrix();
		// gl.glViewport(viewportPositionX, viewportPositionY, viewportWidth,
		// viewportHeight);
		// gl.glLoadIdentity();
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

		// gl.glScissor(viewportPositionX, viewportPositionY,
		// viewportWidth, viewportHeight/2);
		// gl.glEnable(GL2.GL_SCISSOR_TEST);

		viewportPositionX = pixelGLConverter
				.getPixelWidthForCurrentGLTransform(gl);
		viewportPositionY = pixelGLConverter
				.getPixelHeightForCurrentGLTransform(gl);
		// gl.glPushMatrix();
		// gl.glLoadIdentity();
		// GLHelperFunctions.drawPointAt(gl, point);
		// gl.glPopMatrix();
		view.clipToFrustum(gl);
		view.beginZoom(gl);
		view.displayRemote(gl);
		view.endZoom(gl);
		// gl.glDisable(GL2.GL_SCISSOR_TEST);
		gl.glDisable(GL2.GL_STENCIL_TEST);

		// gl.glMatrixMode(GL2.GL_PROJECTION);
		// gl.glPopMatrix();
		// gl.glMatrixMode(GL2.GL_MODELVIEW);
		// gl.glPopMatrix();
		// gl.glPopAttrib();
		// gl.glPopName();
	}

	@Override
	public void setLimits(float x, float y) {
		super.setLimits(x, y);

		// viewportWidth = pixelGLConverter.getPixelWidthForGLWidth(x);
		// viewportHeight = pixelGLConverter.getPixelHeightForGLHeight(y);
		//
		// // FIXME: This is very ugly, but necessary atm.
		// viewportPositionX = pixelGLConverter.getPixelWidthForGLWidth(brick
		// .getWrappingLayout().getTranslateX()
		// + brick.getDimensionGroup().getVisBricksView()
		// .getArchInnerWidth() + elementLayout.getTranslateX());
		// viewportPositionY = pixelGLConverter.getPixelHeightForGLHeight(brick
		// .getWrappingLayout().getTranslateY()
		// + elementLayout.getTranslateY());
		//
		// ViewFrustum templateFrustum = view.getSerializableRepresentation()
		// .getViewFrustum();
		//
		// float value = y / x
		// * (templateFrustum.getRight() - templateFrustum.getLeft());
		// templateFrustum.setTop(value);
		// ViewFrustum viewFrustum = view.getViewFrustum();
		// viewFrustum.setLeft(templateFrustum.getLeft());
		// viewFrustum.setBottom(templateFrustum.getBottom());
		// viewFrustum.setRight(templateFrustum.getRight());
		// viewFrustum.setTop(templateFrustum.getTop());
		// viewFrustum.setNear(templateFrustum.getNear());
		// viewFrustum.setFar(templateFrustum.getFar());

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

	@Override
	public boolean handleMouseWheel(int wheelAmount, Point wheelPosition) {
		
		if(!handleMouseWheel)
			return false;

		int viewportWidth = pixelGLConverter.getPixelWidthForGLWidth(x);
		int viewportHeight = pixelGLConverter.getPixelHeightForGLHeight(y);

		if ((wheelPosition.x >= viewportPositionX)
				&& (wheelPosition.x <= viewportPositionX + viewportWidth)
				&& (brick.getParentGLCanvas().getHeight() - wheelPosition.y >= viewportPositionY)
				&& (brick.getParentGLCanvas().getHeight() - wheelPosition.y <= viewportPositionY
						+ viewportHeight)) {

			view.handleMouseWheel(wheelAmount, wheelPosition);

			return true;
		}
		return false;
	}

	public boolean isHandleMouseWheel() {
		return handleMouseWheel;
	}

	public void setHandleMouseWheel(boolean handleMouseWheel) {
		this.handleMouseWheel = handleMouseWheel;
	}

}
