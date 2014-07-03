/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.data.loader;

import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GLProfile;

import org.caleydo.data.loader.ResourceLocators.IResourceLocator;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * @author Samuel Gratzl
 *
 */
public class TextureResourceLoader implements ITextureLoader {
	private final IResourceLocator locator;

	public TextureResourceLoader(ResourceLoader l) {
		this(l.getLocator());
	}

	public TextureResourceLoader(IResourceLocator locator) {
		this.locator = locator;
	}

	public static ITextureLoader wrap(ResourceLoader l) {
		return new TextureResourceLoader(l);
	}

	@Override
	public final Texture getTexture(String fileName) {
		// use the real extension, not a guess
		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);

		try (InputStream in = locator.get(fileName)) {
			TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), in, true, extension);
			return TextureIO.newTexture(data);
		} catch (IOException e) {
			throw cantFind(fileName, e);
		}
	}

	private IllegalStateException cantFind(String res, IOException e) {
		if (e != null)
			return new IllegalStateException("Cannot load resource: " + res + " in locator: " + locator, e);
		else
			return new IllegalStateException("Cannot load resource: " + res + " in locator: " + locator);
	}

}
