/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.pathway.wikipathways;

import static org.caleydo.data.loader.ResourceLocators.FILE;
import static org.caleydo.data.loader.ResourceLocators.chain;
import static org.caleydo.data.loader.ResourceLocators.classLoader;

import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.datadomain.pathway.manager.IPathwayResourceLoader;

/**
 * Utility classes to load pathway resources.
 *
 * @author Marc Streit
 */
public class WikiPathwaysResourceLoader extends ResourceLoader implements IPathwayResourceLoader {
	public WikiPathwaysResourceLoader() {
		super(chain(classLoader(WikiPathwaysResourceLoader.class.getClassLoader()), FILE));
	}
}
