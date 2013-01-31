/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.data.loader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.media.opengl.GLProfile;

import org.caleydo.data.loader.ResourceLocators.IResourceLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.xml.sax.InputSource;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Utility classes to load resources within the Caleydo project.
 *
 * @author Marc Streit
 */
public class ResourceLoader implements ITextureLoader {
	private final IResourceLocator locator;

	public ResourceLoader(IResourceLocator locator) {
		this.locator = locator;
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
			return new IllegalStateException("Cannot load resource: " + res, e);
		else
			return new IllegalStateException("Cannot load resource: " + res);
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

	@Override
	public final Texture getTexture(String fileName) {
		//use the real extension, not a guess
		String extension = fileName.substring(fileName.lastIndexOf('.')+1);

		try (InputStream in = getChecked(fileName)) {
			TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), in, true, extension);
			return TextureIO.newTexture(data);
		} catch (IOException e) {
			throw cantFind(fileName, e);
		}
	}
}
