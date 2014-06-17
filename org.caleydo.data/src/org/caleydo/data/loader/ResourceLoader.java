/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.loader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.caleydo.data.loader.ResourceLocators.IResourceLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.xml.sax.InputSource;

/**
 * Utility classes to load resources within the Caleydo project.
 *
 * @author Marc Streit
 */
public class ResourceLoader {
	private final IResourceLocator locator;

	public ResourceLoader(IResourceLocator locator) {
		this.locator = locator;
	}

	/**
	 * @return the locator, see {@link #locator}
	 */
	public IResourceLocator getLocator() {
		return locator;
	}

	private InputStream getChecked(String res) {
		InputStream in = locator.get(res);
		if (in == null)
			throw cantFind(res, null);
		if (!(in instanceof BufferedInputStream))
			in = new BufferedInputStream(in);
		return in;
	}

	private IllegalStateException cantFind(String res, IOException e) {
		if (e != null)
			return new IllegalStateException("Cannot load resource: " + res + " in locator: " + locator, e);
		else
			return new IllegalStateException("Cannot load resource: " + res + " in locator: " + locator);
	}

	public final InputStream get(String res) {
		return getChecked(res);
	}

	public final BufferedReader getResource(String fileName) {
		return new BufferedReader(new InputStreamReader(getChecked(fileName)));
	}

	public final InputSource getInputSource(String fileName) {
		return new InputSource(getChecked(fileName));
	}

	public final Image getImage(Display display, String fileName) {
		return new Image(display, getChecked(fileName));
	}

	public final ImageDescriptor getImageDescriptor(Display display, String res) {
		return ImageDescriptor.createFromImage(getImage(display, res));
	}
}
