/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.util.texture;

import gleem.linalg.Vec3f;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.data.loader.ITextureLoader;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

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

	public Texture getIconTexture(final String texturePath) {
		return get(texturePath);
	}

	private Texture get(final String texturePath) {
		if (!cache.containsKey(texturePath)) {
			Texture tmpTexture = loader.getTexture(texturePath);
			cache.put(texturePath, tmpTexture);
		}
		return cache.get(texturePath);
	}

	public void renewTexture(String texturePath) {
		Texture tmpTexture = loader.getTexture(texturePath);
		cache.put(texturePath, tmpTexture);
	}

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
	public void renderTexture(GL2 gl, final EIconTextures eIconTextures, Vec3f lowerLeftCorner, Vec3f lowerRightCorner,
			Vec3f upperRightCorner, Vec3f upperLeftCorner, float colorR, float colorG, float colorB, float alpha) {

		renderTexture(gl, eIconTextures.getFileName(), lowerLeftCorner, lowerRightCorner, upperRightCorner,
				upperLeftCorner, colorR, colorG, colorB, alpha);
	}

	public void renderTexture(GL2 gl, final EIconTextures eIconTextures, Vec3f lowerLeftCorner, Vec3f lowerRightCorner,
			Vec3f upperRightCorner, Vec3f upperLeftCorner, float[] color) {
		renderTexture(gl, eIconTextures, lowerLeftCorner, lowerRightCorner, upperRightCorner, upperLeftCorner,
				color[0], color[1], color[2], color[3]);
	}

	/**
	 * Renders a texture on a rectangle with the specified minimum size.
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
	 * @param scalingPivot
	 *            Pivot that is used when scaling the texture to the minimum size.
	 * @param colorR
	 *            Red portion of the color the Polygon should have where the texture is drawn on.
	 * @param colorG
	 *            Green portion of the color the Polygon should have where the texture is drawn on.
	 * @param colorB
	 *            Blue portion of the color the Polygon should have where the texture is drawn on.
	 * @param alpha
	 *            Alpha value the Polygon should have where the texture is drawn on.
	 * @param minSize
	 *            Minimum size the texture should have.
	 */
	public void renderGUITexture(GL2 gl, final EIconTextures eIconTextures, Vec3f lowerLeftCorner,
			Vec3f lowerRightCorner, Vec3f upperRightCorner, Vec3f upperLeftCorner, Vec3f scalingPivot, float colorR,
			float colorG, float colorB, float alpha, int minSize) {

		try {

			Texture tempTexture = getIconTexture(eIconTextures.getFileName());
			tempTexture.enable(gl);
			tempTexture.bind(gl);

			IntBuffer buffer = IntBuffer.allocate(4);
			gl.glGetIntegerv(GL.GL_VIEWPORT, buffer);
			int currentWidth = buffer.get(2);

			float referenceWidth = minSize * 10.0f;
			float scaling = 1;

			if (referenceWidth > currentWidth)
				scaling = referenceWidth / currentWidth;

			gl.glPushMatrix();
			gl.glTranslatef(scalingPivot.x(), scalingPivot.y(), scalingPivot.z());
			gl.glScalef(scaling, scaling, scaling);
			gl.glTranslatef(-scalingPivot.x(), -scalingPivot.y(), -scalingPivot.z());

			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glColor4f(colorR, colorG, colorB, alpha);
			gl.glBegin(GL2.GL_POLYGON);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(lowerLeftCorner.x(), lowerLeftCorner.y(), lowerLeftCorner.z());
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(lowerRightCorner.x(), lowerRightCorner.y(), lowerRightCorner.z());
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(upperRightCorner.x(), upperLeftCorner.y(), upperRightCorner.z());
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(upperLeftCorner.x(), upperLeftCorner.y(), upperLeftCorner.z());

			gl.glEnd();

			tempTexture.disable(gl);

			gl.glPopMatrix();
		} catch (Exception e) {
			Logger.log(new Status(IStatus.ERROR, this.toString(), "Unable to load texture " + eIconTextures));
		}
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
			Vec3f upperRightCorner, Vec3f upperLeftCorner, float colorR, float colorG, float colorB, float alpha) {

		try {
			Texture tempTexture = getIconTexture(texturePath);
			renderTexture(gl, tempTexture, lowerLeftCorner, lowerRightCorner, upperRightCorner, upperLeftCorner,
					colorR, colorG, colorB, alpha);

		} catch (Exception e) {
			Logger.log(new Status(IStatus.ERROR, this.toString(), "Unable to load texture " + texturePath));
		}
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
			Vec3f upperRightCorner, Vec3f upperLeftCorner, float colorR, float colorG, float colorB, float alpha) {
		texture.enable(gl);
		texture.bind(gl);

		TextureCoords texCoords = texture.getImageTexCoords();

		gl.glColor4f(colorR, colorG, colorB, alpha);
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
