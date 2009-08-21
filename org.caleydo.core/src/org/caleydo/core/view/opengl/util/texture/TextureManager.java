package org.caleydo.core.view.opengl.util.texture;

import java.nio.IntBuffer;
import java.util.EnumMap;
import gleem.linalg.Vec3f;
import javax.media.opengl.GL;

import org.caleydo.core.manager.general.GeneralManager;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Manager handles OpenGL icons as textures. The manager must be created for each GL view because it needs a
 * current GL context!
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class TextureManager {

	private EnumMap<EIconTextures, Texture> mapIconTextures;

	/**
	 * Constructor.
	 */
	public TextureManager() {
		mapIconTextures = new EnumMap<EIconTextures, Texture>(EIconTextures.class);
	}

	public Texture getIconTexture(GL gl, final EIconTextures eIconTextures) {
		if (!mapIconTextures.containsKey(eIconTextures)) {
			Texture tmpTexture =
				GeneralManager.get().getResourceLoader().getTexture(eIconTextures.getFileName());
			mapIconTextures.put(eIconTextures, tmpTexture);
		}
		return mapIconTextures.get(eIconTextures);
	}

	public void renderGUITexture(GL gl, final EIconTextures eIconTextures, Vec3f lowerLeftCorner,
		Vec3f lowerRightCorner, Vec3f upperRightCorner, Vec3f upperLeftCorner, float colorR, float colorG,
		float colorB, float alpha, int minSize) {
		
		IntBuffer buffer = BufferUtil.newIntBuffer(4);
		gl.glGetIntegerv(GL.GL_VIEWPORT, buffer);
		int currentWidth = buffer.get(2);

		float referenceWidth = (float) minSize * 10.0f;
		float scaling = 1;

		if (referenceWidth > (float) currentWidth)
			scaling = referenceWidth / (float) currentWidth;

		gl.glPushMatrix();
		
		gl.glLoadIdentity();
		
		
//		gl.glTranslatef(lowerLeftCorner.x(), lowerLeftCorner.y(), lowerLeftCorner.z());
		gl.glScalef(scaling, scaling, scaling);
//		gl.glTranslatef(-lowerLeftCorner.x(), -lowerLeftCorner.y(), -lowerLeftCorner.z());
//		

		
		Texture tempTexture = getIconTexture(gl, eIconTextures);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glColor4f(colorR, colorG, colorB, alpha);
		gl.glBegin(GL.GL_POLYGON);
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
}
