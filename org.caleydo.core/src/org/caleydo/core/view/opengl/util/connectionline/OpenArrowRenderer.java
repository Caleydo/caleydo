/**
 * 
 */
package org.caleydo.core.view.opengl.util.connectionline;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;

import gleem.linalg.Vec3f;

/**
 * Renderer of an open arrow with no baseline.
 * 
 * @author Christian
 * 
 */
public class OpenArrowRenderer extends AArrowRenderer {

	/**
	 * @param pixelGLConverter
	 */
	public OpenArrowRenderer(PixelGLConverter pixelGLConverter) {
		super(pixelGLConverter);
	}

	@Override
	protected void render(GL2 gl, Vec3f arrowHead, Vec3f corner1, Vec3f corner2) {

		gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_LINE_BIT);
		gl.glColor4fv(lineColor, 0);
		gl.glLineWidth(lineWidth);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3f(arrowHead.x(), arrowHead.y(), arrowHead.z());
		gl.glVertex3f(corner1.x(), corner1.y(), corner1.z());
		gl.glVertex3f(arrowHead.x(), arrowHead.y(), arrowHead.z());
		gl.glVertex3f(corner2.x(), corner2.y(), corner2.z());
		gl.glEnd();
		gl.glPopAttrib();

	}

}
