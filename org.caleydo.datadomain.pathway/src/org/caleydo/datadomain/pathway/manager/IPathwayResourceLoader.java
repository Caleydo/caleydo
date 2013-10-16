/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.manager;

import java.io.BufferedReader;

import org.caleydo.data.loader.ITextureLoader;
import org.xml.sax.InputSource;

public interface IPathwayResourceLoader extends ITextureLoader {

	public BufferedReader getResource(String file);

	public InputSource getInputSource(String file);
}
