package org.caleydo.core.view.opengl.layout.util;

import gleem.linalg.Vec3f;
import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

/**
 * Renders a texture within the layout element.
 * 
 * @author Partl
 */
public class TextureRenderer
	extends LayoutRenderer {

	private String imagePath;
	private boolean isImagePathAbsolute;
	private TextureManager textureManager;

	/**
	 * Constructor.
	 * 
	 * @param imagePath Path to the image that shall be used as texture.
	 * @param textureManager
	 * @param isImagePathAbsolute Specifies whether the image path is absolute
	 *            or relative.
	 */
	public TextureRenderer(String imagePath, TextureManager textureManager,
			boolean isImagePathAbsolute) {
		this.imagePath = imagePath;
		this.isImagePathAbsolute = isImagePathAbsolute;
		this.textureManager = textureManager;
	}

	@Override
	public void render(GL2 gl) {

		Vec3f lowerLeftCorner = new Vec3f(0, 0, 0);
		Vec3f lowerRightCorner = new Vec3f(x, 0, 0);
		Vec3f upperRightCorner = new Vec3f(x, y, 0);
		Vec3f upperLeftCorner = new Vec3f(0, y, 0);

		textureManager.renderTexture(gl, imagePath, lowerLeftCorner, lowerRightCorner,
				upperRightCorner, upperLeftCorner, 1, 1, 1, 1);
	}

}
