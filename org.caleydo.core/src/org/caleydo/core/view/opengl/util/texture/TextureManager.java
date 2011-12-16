package org.caleydo.core.view.opengl.util.texture;

import gleem.linalg.Vec3f;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Manager handles OpenGL2 icons as textures. The manager must be created for each GL2 view because it needs a
 * current GL2 context!
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class TextureManager {

	private HashMap<String, Texture> mapPathToTexture;

	/**
	 * Constructor.
	 */
	public TextureManager() {
		mapPathToTexture = new HashMap<String, Texture>();
	}

	public Texture getIconTexture(GL2 gl, final String texturePath) {
		if (!mapPathToTexture.containsKey(texturePath)) {
			Texture tmpTexture = GeneralManager.get().getResourceLoader().getTexture(texturePath);
			mapPathToTexture.put(texturePath, tmpTexture);
		}
		return mapPathToTexture.get(texturePath);
	}

	public void renewTexture(String texturePath) {
		Texture tmpTexture = GeneralManager.get().getResourceLoader().getTexture(texturePath);
		mapPathToTexture.put(texturePath, tmpTexture);
	}

	public Texture getIconTexture(GL2 gl, final EIconTextures eIconTexture) {

		String texturePath = eIconTexture.getFileName();
		if (!mapPathToTexture.containsKey(texturePath)) {
			Texture tmpTexture = GeneralManager.get().getResourceLoader().getTexture(texturePath);
			mapPathToTexture.put(texturePath, tmpTexture);
		}
		return mapPathToTexture.get(texturePath);
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
	public void renderTexture(GL2 gl, final EIconTextures eIconTextures, Vec3f lowerLeftCorner,
		Vec3f lowerRightCorner, Vec3f upperRightCorner, Vec3f upperLeftCorner, float colorR, float colorG,
		float colorB, float alpha) {

		renderTexture(gl, eIconTextures.getFileName(), lowerLeftCorner, lowerRightCorner, upperRightCorner,
			upperLeftCorner, colorR, colorG, colorB, alpha);
	}

	public void renderTexture(GL2 gl, final EIconTextures eIconTextures, Vec3f lowerLeftCorner,
		Vec3f lowerRightCorner, Vec3f upperRightCorner, Vec3f upperLeftCorner, float[] color) {
		renderTexture(gl, eIconTextures, lowerLeftCorner, lowerRightCorner, upperRightCorner,
			upperLeftCorner, color[0], color[1], color[2], color[3]);
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
		Vec3f lowerRightCorner, Vec3f upperRightCorner, Vec3f upperLeftCorner, Vec3f scalingPivot,
		float colorR, float colorG, float colorB, float alpha, int minSize) {

		IntBuffer buffer = IntBuffer.allocate(4);
		gl.glGetIntegerv(GL2.GL_VIEWPORT, buffer);
		int currentWidth = buffer.get(2);

		float referenceWidth = minSize * 10.0f;
		float scaling = 1;

		if (referenceWidth > currentWidth)
			scaling = referenceWidth / currentWidth;

		gl.glPushMatrix();
		gl.glTranslatef(scalingPivot.x(), scalingPivot.y(), scalingPivot.z());
		gl.glScalef(scaling, scaling, scaling);
		gl.glTranslatef(-scalingPivot.x(), -scalingPivot.y(), -scalingPivot.z());

		Texture tempTexture = getIconTexture(gl, eIconTextures.getFileName());
		tempTexture.enable();
		tempTexture.bind();

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

		tempTexture.disable();

		gl.glPopMatrix();
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
	public void renderTexture(GL2 gl, final String texturePath, Vec3f lowerLeftCorner,
		Vec3f lowerRightCorner, Vec3f upperRightCorner, Vec3f upperLeftCorner, float colorR, float colorG,
		float colorB, float alpha) {

		Texture tempTexture = getIconTexture(gl, texturePath);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

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

		tempTexture.disable();
	}
}
