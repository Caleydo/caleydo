package org.caleydo.core.view.opengl.util.texture;

import java.util.EnumMap;

import javax.media.opengl.GL;

import org.caleydo.core.manager.general.GeneralManager;

import com.sun.opengl.util.texture.Texture;

/**
 * Manager handles OpenGL icons as textures. TODO move this to manager to use it as a singleton
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLIconTextureManager {

	EnumMap<EIconTextures, Texture> mapIconTextures;

	/**
	 * Constructor.
	 * 
	 * @param gl
	 */
	public GLIconTextureManager() {
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
}
