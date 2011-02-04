package org.caleydo.core.view.opengl.canvas;

import org.caleydo.core.view.opengl.camera.ViewFrustum;

public class PixelGLConverter {
	ViewFrustum viewFrustum;
	GLCaleydoCanvas canvas;

	public PixelGLConverter(ViewFrustum viewFrustum, GLCaleydoCanvas canvas) {
		this.viewFrustum = viewFrustum;
		this.canvas = canvas;
	}

	public float getGLWidthForPixelWidth(int pixelWidth) {
		float totalWidthGL = viewFrustum.getWidth();
		float totalWidthPixel = (float) canvas.getBounds().getWidth();
		float width = totalWidthGL / totalWidthPixel * pixelWidth;
		return width;
	}

	public float getGLHeightForPixelHeight(int pixelHeight) {
		float totalHeightGL = viewFrustum.getHeight();
		float totalHeightPixel =  (float)canvas.getBounds().getHeight();
		float height = totalHeightGL / totalHeightPixel * pixelHeight;
		return height;
	}

}
