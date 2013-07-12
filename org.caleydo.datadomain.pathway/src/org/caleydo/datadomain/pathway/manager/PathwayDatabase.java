/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.manager;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GLException;

import org.caleydo.datadomain.pathway.graph.PathwayGraph;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Class that holds information about a specific pathway database.
 *
 * @author Marc Streit
 */
public final class PathwayDatabase {

	private final EPathwayDatabaseType type;
	private File baseDir;

	/**
	 * Constructor.
	 */
	public PathwayDatabase(EPathwayDatabaseType type) {
		this.type = type;
	}

	public EPathwayDatabaseType getType() {
		return type;
	}

	public String getName() {
		return type.getName();
	}

	public String getURL() {
		return type.getURL();
	}

	/**
	 * @param baseDir
	 */
	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	/**
	 * @param pathway
	 * @return
	 */
	public Texture loadTexture(PathwayGraph pathway) {
		if (baseDir == null)
			return null;
		File file = new File(baseDir, pathway.getImageLink());
		try {
			return TextureIO.newTexture(file, true);
		} catch (GLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
