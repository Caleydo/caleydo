package org.caleydo.core.manager.specialized.genetic.pathway;

import java.io.BufferedReader;

import org.xml.sax.InputSource;

import com.sun.opengl.util.texture.Texture;

public interface IPathwayResourceLoader {

	public BufferedReader getResource(String file);

	public InputSource getInputSource(String file);

	public Texture getTexture(String file);
}
