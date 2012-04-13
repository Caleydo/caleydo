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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.media.opengl.GLProfile;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.xml.sax.InputSource;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Utility classes to load resources within the Caleydo project.
 * 
 * @author Marc Streit
 */
public class ResourceLoader {
	public BufferedReader getResource(String sFileName) throws FileNotFoundException {
		BufferedReader file;

		if (this.getClass().getClassLoader().getResourceAsStream(sFileName) != null) {
			file = new BufferedReader(new InputStreamReader(
					loadResourceAsInputStream(sFileName)));
		} else {
			file = new BufferedReader(new FileReader(sFileName));
		}

		return file;
	}

	public InputSource getInputSource(String sFileName) throws FileNotFoundException {

		InputSource inputSource;

		if (this.getClass().getClassLoader().getResourceAsStream(sFileName) != null) {
			inputSource = new InputSource(loadResourceAsInputStream(sFileName));
		} else {
			inputSource = new InputSource(new BufferedInputStream(new FileInputStream(
					sFileName)));
		}

		return inputSource;
	}

	public Image getImage(Display display, String sFileName) {
		Image image;
		if (this.getClass().getClassLoader().getResourceAsStream(sFileName) != null) {
			image = new Image(display, this.getClass().getClassLoader()
					.getResourceAsStream(sFileName));
		} else {
			image = new Image(display, sFileName);
		}

		return image;
	}

	public URL getResourceURL(String sFileName) {
		URL url = this.getClass().getClassLoader().getResource(sFileName);

		if (url == null)
			throw new IllegalStateException("Cannot load resource URL: " + sFileName);

		return url;
	}

	public Texture getTexture(String sFileName) {
		Texture texture;

		try {
			if (this.getClass().getClassLoader().getResourceAsStream(sFileName) != null) {
				texture = TextureIO.newTexture(TextureIO.newTextureData(
						GLProfile.getDefault(), loadResourceAsInputStream(sFileName),
						true, "GIF"));
			} else {

				texture = TextureIO.newTexture(TextureIO.newTextureData(
						GLProfile.getDefault(), new File(sFileName), true, "GIF"));
			}
		} catch (Exception e) {
			throw new IllegalStateException("Cannot load texture: " + sFileName);
		}

		if (texture == null)
			throw new IllegalStateException("Cannot load texture: " + sFileName);

		return texture;
	}

	private InputStream loadResourceAsInputStream(String sFileName) {
		InputStream file;

		file = this.getClass().getClassLoader().getResourceAsStream(sFileName);

		if (file == null)
			throw new IllegalStateException("Cannot load resource: " + sFileName);

		return file;
	}
}
