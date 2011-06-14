package org.caleydo.core.view.opengl.layout;

import java.awt.Point;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.canvas.listener.IMouseWheelHandler;

public class ZoomableViewLayoutRenderer
	extends ViewLayoutRenderer
	implements IMouseWheelHandler {

	protected PixelGLConverter pixelGLConverter;
	protected int viewportPositionX;
	protected int viewportPositionY;
	protected AGLView parentView;

	public ZoomableViewLayoutRenderer(AGLView remoteView, AGLView parentView) {
		super(remoteView);
		this.parentView = parentView;
		pixelGLConverter = parentView.getParentGLCanvas().getPixelGLConverter();
	}

	@Override
	public void render(GL2 gl) {
		viewportPositionX = pixelGLConverter.getPixelWidthForCurrentGLTransform(gl);
		viewportPositionY = pixelGLConverter.getPixelHeightForCurrentGLTransform(gl);

		double[] clipPlane1 = new double[] { 0.0, 1.0, 0.0, 0.0 };
		double[] clipPlane2 = new double[] { 1.0, 0.0, 0.0, 0.0 };
		double[] clipPlane3 = new double[] { -1.0, 0.0, 0.0, x };
		double[] clipPlane4 = new double[] { 0.0, -1.0, 0.0, y };

		gl.glClipPlane(GL2.GL_CLIP_PLANE0, clipPlane1, 0);
		gl.glClipPlane(GL2.GL_CLIP_PLANE1, clipPlane2, 0);
		gl.glClipPlane(GL2.GL_CLIP_PLANE2, clipPlane3, 0);
		gl.glClipPlane(GL2.GL_CLIP_PLANE3, clipPlane4, 0);
		gl.glEnable(GL2.GL_CLIP_PLANE0);
		gl.glEnable(GL2.GL_CLIP_PLANE1);
		gl.glEnable(GL2.GL_CLIP_PLANE2);
		gl.glEnable(GL2.GL_CLIP_PLANE3);
		view.beginZoom(gl);
		super.render(gl);
		view.endZoom(gl);
		gl.glDisable(GL2.GL_CLIP_PLANE0);
		gl.glDisable(GL2.GL_CLIP_PLANE1);
		gl.glDisable(GL2.GL_CLIP_PLANE2);
		gl.glDisable(GL2.GL_CLIP_PLANE3);
	}

	@Override
	public void handleMouseWheel(int wheelAmount, Point wheelPosition) {

		int viewportWidth = pixelGLConverter.getPixelWidthForGLWidth(x);
		int viewportHeight = pixelGLConverter.getPixelHeightForGLHeight(y);

		if ((wheelPosition.x >= viewportPositionX) && (wheelPosition.x <= viewportPositionX + viewportWidth)
			&& (view.getParentGLCanvas().getHeight() - wheelPosition.y >= viewportPositionY)
			&& (view.getParentGLCanvas().getHeight() - wheelPosition.y <= viewportPositionY + viewportHeight)) {

			view.handleMouseWheel(wheelAmount, wheelPosition);

		}
	}

}
