package org.caleydo.core.view.opengl.util.trashcan;

import java.io.File;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;


public class TrashCan {

	private static String TRASH_CAN_PATH = "resources/icons/trashcan_empty.png";
	
	private Texture trashCanTexture;
	
	public void init(final GL gl) 
	{	
		try
		{
			if (this.getClass().getClassLoader().getResource(TRASH_CAN_PATH) != null)
			{
				trashCanTexture = TextureIO.newTexture(TextureIO
						.newTextureData(this.getClass().getClassLoader()
								.getResourceAsStream(TRASH_CAN_PATH), false,
								"PNG"));
			} else
			{
				trashCanTexture = TextureIO
						.newTexture(TextureIO.newTextureData(new File(
								TRASH_CAN_PATH), false, "PNG"));
			}

		} catch (Exception e)
		{
			System.out
					.println("GLPathwayMemoPad.init() Error loading texture from "
							+ TRASH_CAN_PATH);
			e.printStackTrace();
		}
	}
	
	public void render(final GL gl)
	{
		if (trashCanTexture == null)
		return;

		TextureCoords texCoords = trashCanTexture.getImageTexCoords();
	
		trashCanTexture.enable();
		trashCanTexture.bind();
	
		gl.glColor3f(1, 1, 1);
	
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(-0.15f, 0.09f, 4.1f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(-0.5f, 0.09f, 4.1f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(-0.5f, 0.41f, 4.1f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(-0.15f, 0.41f, 4.1f);
		gl.glEnd();
	
		trashCanTexture.disable();
	}
}
