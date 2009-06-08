package org.caleydo.core.view.opengl.util.texture;

import java.util.EnumMap;

import javax.media.opengl.GL;

import org.caleydo.core.manager.general.GeneralManager;

import com.sun.opengl.util.texture.Texture;

/**
 * Manager handles OpenGL icons as textures.
 * The manager must be created for each GL view because it needs a current GL context!
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
}
