/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.manager;

import java.io.BufferedReader;

import org.xml.sax.InputSource;

import com.jogamp.opengl.util.texture.Texture;

public interface IPathwayResourceLoader {

	public BufferedReader getResource(String file);

	public InputSource getInputSource(String file);

	public Texture getTexture(String file);
}
