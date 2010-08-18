package org.caleydo.core.view.opengl.util.wavefrontobjectloader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * loads a wavefront object file model into a ObjectModel.
 * 
 * @author Stefan Sauer
 */
public class ObjectLoader {

	/**
	 * Loads a Wavefront Object File.
	 * 
	 * @param resourceName
	 */
	public ObjectModel loadFile(String resourceName) {
		ResourceLoader loader = new ResourceLoader();
		try {
			return loadFile(loader.getResource(resourceName));
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Loads a Wavefront Object File
	 * 
	 * @param resourceReader
	 */
	public ObjectModel loadFile(BufferedReader resourceReader) {
		if (resourceReader == null)
			throw new IllegalStateException("Cannot load wavefront object.");

		ObjectModel model = new ObjectModel();

		try {
			String line;

			do {
				line = resourceReader.readLine();

				if (line == null)
					continue;

				// empty line
				if (line.length() <= 0)
					continue;

				// comment line
				if (line.startsWith("#"))
					continue;

				// handle groups
				if (line.startsWith("g"))
					model.handleGroupCommand(line);

				// handle smoothing groups
				if (line.startsWith("s")) {
					// TODO wavefront smoothing groups are not implemented
					GeneralManager.get().getLogger().log(
						new Status(IStatus.INFO, GeneralManager.PLUGIN_ID,
							"Wavefront Object Loader hasn't implemented smoothing groups yet."));
				}
				// handle merging group
				if (line.startsWith("mg")) {
					// TODO wavefront merging groups is not implemented
					GeneralManager.get().getLogger().log(
						new Status(IStatus.INFO, GeneralManager.PLUGIN_ID,
							"Wavefront Object Loader hasn't implemented merging groups yet."));
				}
				// handle object name
				if (line.startsWith("o")) {
					// TODO wavefront object name is not implemented
					GeneralManager.get().getLogger().log(
						new Status(IStatus.INFO, GeneralManager.PLUGIN_ID,
							"Wavefront Object Loader hasn't implemented object names yet."));
				}

				// handle vertex
				if (line.startsWith("v "))
					model.handleVertexCommand(line);

				// handle vertex normal
				if (line.startsWith("vn"))
					model.handleVertexNormalCommand(line);

				// handle texture coordinate
				if (line.startsWith("vt"))
					model.handleVertexTextureCommand(line);

				// handle faces
				if (line.startsWith("f "))
					model.handleFaceCommand(line);

			} while (line != null);

		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return model;
	}

	/**
	 * Startup for testing purpose
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ObjectLoader loader = new ObjectLoader();

		// works as stand alone application
		// can't use rcp plugin path because of that
		loader
			.loadFile("D:/work/Eclipse Workspace work/org.caleydo.data/resources/3dmodels/glyph/square1.obj");

		System.out.println("");
	}

}
