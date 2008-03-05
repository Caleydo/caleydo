package org.geneview.core.view.opengl.util;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

/**
 * TODO move this to manager to use it as a singleton
 * 
 * @author Alexander Lex
 *
 */

public class GLIconTextureManager 
{
	
	EnumMap<EIconTextures, Texture> mapIconTextures;
	
	public GLIconTextureManager(final GL gl)
	{
		mapIconTextures = new EnumMap<EIconTextures, Texture>(EIconTextures.class);
		for(EIconTextures eIconTextures : EIconTextures.values())
		{
			try
			{
				Texture tempTexture = TextureIO.newTexture(TextureIO.newTextureData(
						new File(eIconTextures.getFileName()), true, "PNG"));
				mapIconTextures.put(eIconTextures, tempTexture);
			} catch (GLException e)
			{
				e.printStackTrace();
			} catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}	
		}
	
	}
	
	public Texture getIconTexture(final EIconTextures eIconTextures)
	{
		return mapIconTextures.get(eIconTextures);
	}
	
	
}
