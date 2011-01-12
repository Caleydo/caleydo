package org.caleydo.core.view.opengl.util;

import java.awt.Point;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.mouse.GLMouseListener;

/**
 * Magnifying glass that zooms an area around the mouse position. FIXME: The performance and look of the
 * magnifying glass is not satisfying yet.
 * 
 * @author Christian Partl
 */
public class GLMagnifyingGlass {

	private static final float DEFAULT_ZOOM_FACTOR_X = 2.0f;
	private static final float DEFAULT_ZOOM_FACTOR_Y = 2.0f;
	private static final int DEFAULT_CAPTURED_REGION_WIDTH = 60;
	private static final int DEFAULT_CAPTURED_REGION_HEIGHT = 60;

	private float zoomFactorX;
	private float zoomFactorY;
	private int capturedRegionWidth;
	private int capturedRegionHeight;

	/**
	 * Constructor.
	 */
	public GLMagnifyingGlass() {
		zoomFactorX = DEFAULT_ZOOM_FACTOR_X;
		zoomFactorY = DEFAULT_ZOOM_FACTOR_Y;
		capturedRegionWidth = DEFAULT_CAPTURED_REGION_WIDTH;
		capturedRegionHeight = DEFAULT_CAPTURED_REGION_HEIGHT;
	}

	/**
	 * Draws the magnifying glass. Note, that the color buffer bit has to be cleared in every frame.
	 * 
	 * @param gl
	 *            GL2 Context.
	 * @param mouseListener
	 *            Mouse listener for determining the current position of the mouse.
	 */
	public void draw(GL2 gl, GLMouseListener mouseListener) {

		gl.glDisable(GL2.GL_DEPTH_TEST);
		gl.glPixelTransferi(GL2.GL_MAP_COLOR, GL2.GL_FALSE);

		gl.glPixelTransferf(GL2.GL_RED_SCALE, 1.0f);
		gl.glPixelTransferi(GL2.GL_RED_BIAS, 0);
		gl.glPixelTransferf(GL2.GL_GREEN_SCALE, 1.0f);
		gl.glPixelTransferi(GL2.GL_GREEN_BIAS, 0);
		gl.glPixelTransferf(GL2.GL_BLUE_SCALE, 1.0f);
		gl.glPixelTransferi(GL2.GL_BLUE_BIAS, 0);
		gl.glPixelTransferi(GL2.GL_ALPHA_SCALE, 1);
		gl.glPixelTransferi(GL2.GL_ALPHA_BIAS, 0);

		gl.glPushMatrix();
		gl.glLoadIdentity();

		Point currentPoint = mouseListener.getPickedPoint();

		if (currentPoint != null) {

			IntBuffer buffer = IntBuffer.allocate(4);
			gl.glGetIntegerv(GL2.GL_VIEWPORT, buffer);
			// int xOrigin = buffer.get(0);
			int yOrigin = buffer.get(1);
			// int currentWidth = buffer.get(2);
			int currentHeight = buffer.get(3);

			gl.glLoadIdentity();

			gl.glReadBuffer(GL2.GL_BACK);

			float[] fArTargetWorldCoordinates =
				GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x
					- capturedRegionWidth, currentPoint.y + capturedRegionHeight);
			gl.glRasterPos2f(Math.max(fArTargetWorldCoordinates[0], 0),
				Math.max(fArTargetWorldCoordinates[1], 0));
			gl.glPixelZoom(zoomFactorX, zoomFactorY);
			gl.glCopyPixels(
				Math.max(currentPoint.x - (int) (capturedRegionWidth / zoomFactorX), 0),
				Math.max((yOrigin + currentHeight) - currentPoint.y
					- (int) (capturedRegionHeight / zoomFactorY), 0), capturedRegionWidth,
				capturedRegionHeight, GL2.GL_COLOR);
			gl.glPixelZoom(1.0f, 1.0f);
		}
		gl.glEnable(GL2.GL_DEPTH_TEST);

		gl.glPopMatrix();
	}

	/**
	 * Sets the zoom factors for the magnifying glass.
	 * 
	 * @param zoomFactorX
	 *            Zoom factor along the x axis.
	 * @param zoomFactorY
	 *            Zoom factor along the y axis.
	 */
	public void setZoomFactors(float zoomFactorX, float zoomFactorY) {
		this.zoomFactorX = zoomFactorX;
		this.zoomFactorY = zoomFactorY;
	}

	/**
	 * Sets the size of the region around the mouse that will be captured and zoomed.
	 * 
	 * @param capturedRegionWidth
	 *            Width of the captured region.
	 * @param capturedRegionHeight
	 *            Height of the captured region.
	 */
	public void setCapturedRegion(int capturedRegionWidth, int capturedRegionHeight) {
		this.capturedRegionWidth = capturedRegionWidth;
		this.capturedRegionHeight = capturedRegionHeight;
	}

	/**
	 * @return Zoom factor along the x axis.
	 */
	public float getZoomFactorX() {
		return zoomFactorX;
	}

	/**
	 * @return Zoom factor along the y axis.
	 */
	public float getZoomFactorY() {
		return zoomFactorY;
	}

	/**
	 * @return Width of the captured region.
	 */
	public int getCapturedRegionWidth() {
		return capturedRegionWidth;
	}

	/**
	 * @return Height of the captured region.
	 */
	public int getCapturedRegionHeight() {
		return capturedRegionHeight;
	}

}
