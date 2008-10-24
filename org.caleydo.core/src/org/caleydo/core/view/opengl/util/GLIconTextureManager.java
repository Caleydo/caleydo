package org.caleydo.core.view.opengl.util;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import javax.media.opengl.GL;
import javax.media.opengl.GLException;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

/**
 * Manager handles OpenGL icons as textures. TODO move this to manager to use it
 * as a singleton
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLIconTextureManager
{

	EnumMap<EIconTextures, Texture> mapIconTextures;

	/**
	 * Constructor.
	 * 
	 * @param gl
	 */
	public GLIconTextureManager()
	{
		mapIconTextures = new EnumMap<EIconTextures, Texture>(EIconTextures.class);

	}

	public Texture getIconTexture(GL gl, final EIconTextures eIconTextures)
	{
		if (!mapIconTextures.containsKey(eIconTextures))
		{
			try
			{
				Texture tmpTexture;
				String sFileName = eIconTextures.getFileName();

				if (this.getClass().getClassLoader().getResourceAsStream(sFileName) != null)
				{
					tmpTexture = TextureIO.newTexture(TextureIO.newTextureData(this.getClass()
							.getClassLoader().getResourceAsStream(sFileName), true, "PNG"));
				}
				else
				{
					tmpTexture = TextureIO.newTexture(TextureIO.newTextureData(new File(
							eIconTextures.getFileName()), true, "PNG"));
				}

				mapIconTextures.put(eIconTextures, tmpTexture);
			}
			catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			}
			catch (GLException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return mapIconTextures.get(eIconTextures);
	}

	// public static Texture loadIconTexture(EIconTextures eIconTextures)
	// {
	// Texture tmpTexture;
	// String sFileName = eIconTextures.getFileName();
	//
	// if (this.getClass().getClassLoader().getResourceAsStream(sFileName) !=
	// null)
	// {
	// tmpTexture =
	// TextureIO.newTexture(TextureIO.newTextureData(this.getClass()
	// .getClassLoader().getResourceAsStream(sFileName), true, "PNG"));
	// }
	// else
	// {
	// tmpTexture = TextureIO.newTexture(TextureIO.newTextureData(new File(
	// eIconTextures.getFileName()), true, "PNG"));
	// }
	//
	// mapIconTextures.put(eIconTextures, tmpTexture);
	// }
}
