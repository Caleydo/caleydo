/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.texture;

import gleem.linalg.Vec3f;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.data.loader.ITextureLoader;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Manager handles OpenGL2 icons as textures. The manager must be created for each GL2 view because it needs a current
 * GL2 context!
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
public final class TextureManager {

	private final Map<String, Texture> cache = new HashMap<String, Texture>();
	private final ITextureLoader loader;

	/**
	 * Constructor.
	 */
	public TextureManager() {
		this(GeneralManager.get().getResourceLoader());
	}

	public TextureManager(ITextureLoader loader) {
		this.loader = loader;
	}

	/**
	 * alias to {@link #get(String)}
	 *
	 * @param texturePath
	 * @return
	 */
	public Texture getIconTexture(final String texturePath) {
		return get(texturePath);
	}

	/**
	 * return the cached texture or loads the given texture using the default {@link ITextureLoader}
	 *
	 * @param texturePath
	 * @return
	 */
	public Texture get(final String texturePath) {
		return get(texturePath, this.loader);
	}

	/**
	 * load a texture once, using the specified {@link ITextureLoader}
	 *
	 * @param texture
	 * @param locator
	 * @return
	 */
	public Texture get(String texturePath, ITextureLoader loader) {
		if (!cache.containsKey(texturePath)) {
			Texture tmpTexture = loader.getTexture(texturePath);
			cache.put(texturePath, tmpTexture);
		}
		return cache.get(texturePath);
	}

	/**
	 * load a texture once, using the specified {@link ITextureLoader}
	 * 
	 * @param texture
	 * @param locator
	 * @return
	 */
	public Texture get(URL textureURL) {
		String path = textureURL.getPath();
		if (!cache.containsKey(path)) {
			Texture tmpTexture;
			try {
				tmpTexture = TextureIO.newTexture(textureURL, true, ".png");
				cache.put(path, tmpTexture);
			} catch (GLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return cache.get(path);
	}

	public void renewTexture(String texturePath) {
		Texture tmpTexture = loader.getTexture(texturePath);
		cache.put(texturePath, tmpTexture);
	}

	@Deprecated
	public Texture getIconTexture(final EIconTextures eIconTexture) {
		return getIconTexture(eIconTexture.getFileName());
	}

	/**
	 * Convenience method for rendering textures on a rectangle.
	 *
	 * @param gl
	 *            GL2 Context.
	 * @param eIconTextures
	 *            Texture that should be rendered.
	 * @param lowerLeftCorner
	 *            Lower left corner of the texture.
	 * @param lowerRightCorner
	 *            Lower right corner of the texture.
	 * @param upperRightCorner
	 *            Upper right corner of the texture.
	 * @param upperLeftCorner
	 *            Upper left corner of the texture.
	 * @param colorR
	 *            Red portion of the color the Polygon should have where the texture is drawn on.
	 * @param colorG
	 *            Green portion of the color the Polygon should have where the texture is drawn on.
	 * @param colorB
	 *            Blue portion of the color the Polygon should have where the texture is drawn on.
	 * @param alpha
	 *            Alpha value the Polygon should have where the texture is drawn on.
	 */
	@Deprecated
	public void renderTexture(GL2 gl, final EIconTextures eIconTextures, Vec3f lowerLeftCorner, Vec3f lowerRightCorner,
			Vec3f upperRightCorner, Vec3f upperLeftCorner, Color color) {

		renderTexture(gl, eIconTextures.getFileName(), lowerLeftCorner, lowerRightCorner, upperRightCorner,
				upperLeftCorner, color);
	}

	/**
	 * Wrapper for {@link #renderTexture(GL2, EIconTextures, Vec3f, Vec3f, Vec3f, Vec3f, Color)} with color set to
	 * default white
	 */
	@Deprecated
	public void renderTexture(GL2 gl, final EIconTextures eIconTextures, Vec3f lowerLeftCorner, Vec3f lowerRightCorner,
			Vec3f upperRightCorner, Vec3f upperLeftCorner) {

		renderTexture(gl, eIconTextures.getFileName(), lowerLeftCorner, lowerRightCorner, upperRightCorner,
				upperLeftCorner, Color.WHITE);
	}

	/**
	 * Convenience method for rendering textures on a rectangle.
	 *
	 * @param gl
	 *            GL2 Context.
	 * @param texturePath
	 *            Path to the image.
	 * @param lowerLeftCorner
	 *            Lower left corner of the texture.
	 * @param lowerRightCorner
	 *            Lower right corner of the texture.
	 * @param upperRightCorner
	 *            Upper right corner of the texture.
	 * @param upperLeftCorner
	 *            Upper left corner of the texture.
	 * @param colorR
	 *            Red portion of the color the Polygon should have where the texture is drawn on.
	 * @param colorG
	 *            Green portion of the color the Polygon should have where the texture is drawn on.
	 * @param colorB
	 *            Blue portion of the color the Polygon should have where the texture is drawn on.
	 * @param alpha
	 *            Alpha value the Polygon should have where the texture is drawn on.
	 */
	public void renderTexture(GL2 gl, final String texturePath, Vec3f lowerLeftCorner, Vec3f lowerRightCorner,
			Vec3f upperRightCorner, Vec3f upperLeftCorner, Color color) {

		try {
			Texture tempTexture = getIconTexture(texturePath);
			renderTexture(gl, tempTexture, lowerLeftCorner, lowerRightCorner, upperRightCorner, upperLeftCorner, color);

		} catch (Exception e) {
			Logger.log(new Status(IStatus.ERROR, this.toString(), "Unable to load texture " + texturePath));
		}
	}

	/** Wrapper for {@link #renderTexture(GL2, EIconTextures, Vec3f, Vec3f, Vec3f, Vec3f, Color)} with white background */
	public void renderTexture(GL2 gl, final String texturePath, Vec3f lowerLeftCorner, Vec3f lowerRightCorner,
			Vec3f upperRightCorner, Vec3f upperLeftCorner) {
		renderTexture(gl, texturePath, lowerLeftCorner, lowerRightCorner, upperRightCorner, upperLeftCorner,
				Color.WHITE);
	}

	/**
	 * Convenience method for rendering textures on a rectangle.
	 *
	 * @param gl
	 *            GL2 Context.
	 * @param texturePath
	 *            Path to the image.
	 * @param lowerLeftCorner
	 *            Lower left corner of the texture.
	 * @param lowerRightCorner
	 *            Lower right corner of the texture.
	 * @param upperRightCorner
	 *            Upper right corner of the texture.
	 * @param upperLeftCorner
	 *            Upper left corner of the texture.
	 * @param colorR
	 *            Red portion of the color the Polygon should have where the texture is drawn on.
	 * @param colorG
	 *            Green portion of the color the Polygon should have where the texture is drawn on.
	 * @param colorB
	 *            Blue portion of the color the Polygon should have where the texture is drawn on.
	 * @param alpha
	 *            Alpha value the Polygon should have where the texture is drawn on.
	 */
	public void renderTexture(GL2 gl, final Texture texture, Vec3f lowerLeftCorner, Vec3f lowerRightCorner,
			Vec3f upperRightCorner, Vec3f upperLeftCorner, Color color) {
		texture.enable(gl);
		texture.bind(gl);

		TextureCoords texCoords = texture.getImageTexCoords();

		gl.glColor4fv(color.getRGBA(), 0);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(lowerLeftCorner.x(), lowerLeftCorner.y(), lowerLeftCorner.z());
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(lowerRightCorner.x(), lowerRightCorner.y(), lowerRightCorner.z());
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(upperRightCorner.x(), upperRightCorner.y(), upperRightCorner.z());
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(upperLeftCorner.x(), upperLeftCorner.y(), upperLeftCorner.z());

		gl.glEnd();

		texture.disable(gl);
	}
}
