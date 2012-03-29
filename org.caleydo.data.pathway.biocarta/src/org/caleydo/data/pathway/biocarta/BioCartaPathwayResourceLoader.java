package org.caleydo.data.pathway.biocarta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.media.opengl.GLProfile;
import org.caleydo.datadomain.pathway.manager.IPathwayResourceLoader;
import org.xml.sax.InputSource;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Utility classes to load pathway resources.
 * 
 * @author Marc Streit
 */
public class BioCartaPathwayResourceLoader implements IPathwayResourceLoader {

	@Override
	public BufferedReader getResource(String sFileName) {
		BufferedReader file;

		if (this.getClass().getClassLoader().getResourceAsStream(sFileName) != null) {
			file = new BufferedReader(new InputStreamReader(
					loadResourceAsInputStream(sFileName)));
		} else {
			try {
				file = new BufferedReader(new FileReader(sFileName));
			} catch (FileNotFoundException e) {
				throw new IllegalStateException("Cannot load resource: " + sFileName);
			}
		}

		return file;
	}

	@Override
	public InputSource getInputSource(String sFileName) {
		InputSource inputSource;

		if (this.getClass().getClassLoader().getResourceAsStream(sFileName) != null) {
			inputSource = new InputSource(loadResourceAsInputStream(sFileName));
		} else {
			try {
				inputSource = new InputSource(new FileReader(new File(sFileName)));
			} catch (IOException e) {
				throw new IllegalStateException("Cannot load resource: " + sFileName);
			}
		}

		return inputSource;
	}

	private InputStream loadResourceAsInputStream(String sFileName) {
		InputStream file;

		file = this.getClass().getClassLoader().getResourceAsStream(sFileName);

		if (file == null)
			throw new IllegalStateException("Cannot load resource: " + sFileName);

		return file;
	}

	@Override
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
}