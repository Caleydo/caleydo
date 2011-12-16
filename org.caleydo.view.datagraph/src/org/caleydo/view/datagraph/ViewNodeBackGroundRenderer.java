package org.caleydo.view.datagraph;

import gleem.linalg.Vec3f;
import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

public class ViewNodeBackGroundRenderer
	extends LayoutRenderer {

	private float[] color;
	private String imagePath;
	private TextureManager textureManager;

	/**
	 * Constructor.
	 * 
	 * @param color Color of the rendered rectangle. The array must have a
	 *            length of 4 specifying the RGBA values of the color.
	 * @param imagePath Path to the image that shall be used as texture.
	 * @param textureManager
	 */
	public ViewNodeBackGroundRenderer(float[] color, String imagePath,
			TextureManager textureManager) {
		this.color = color;
		this.imagePath = imagePath;
		this.textureManager = textureManager;
	}

	@Override
	public void render(GL2 gl) {

		gl.glColor4fv(color, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();

		float textureSize = Math.min(x, y);
		float posX = x / 2.0f - textureSize / 2.0f;
		float posY = y / 2.0f - textureSize / 2.0f;

		Vec3f lowerLeftCorner = new Vec3f(posX, posY, 0);
		Vec3f lowerRightCorner = new Vec3f(posX + textureSize, posY, 0);
		Vec3f upperRightCorner = new Vec3f(posX + textureSize, posY + textureSize, 0);
		Vec3f upperLeftCorner = new Vec3f(posX, posY + textureSize, 0);

		textureManager.renderTexture(gl, imagePath, lowerLeftCorner, lowerRightCorner,
				upperRightCorner, upperLeftCorner, color[0], color[1], color[2], 0.5f);
	}

}
