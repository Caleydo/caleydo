/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui.augmentation.path;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.graph.PathwayPath;

/**
 * Renders multiple {@link PathwayPath} using {@link MergedPathSegmentsAugmentation}s.
 *
 * @author Christian
 *
 */
public class MultiplePathsAugmentation extends GLElementContainer {

	public static final Color PATH_COLOR = new Color(0f, 0f, 1f, 1f);

	/**
	 * The paths to render.
	 */
	protected List<PathwayPath> paths = new ArrayList<>();
	/**
	 * The pathway presentation that shall be augmented.
	 */
	protected IPathwayRepresentation pathwayRepresentation;
	/**
	 * The color of the paths.
	 */
	protected Color pathColor = PATH_COLOR;

	public MultiplePathsAugmentation(IPathwayRepresentation pathwayRepresentation) {
		setLayout(GLLayouts.LAYERS);
		this.pathwayRepresentation = pathwayRepresentation;
	}

	/**
	 * @return the paths, see {@link #paths}
	 */
	public List<PathwayPath> getPaths() {
		return paths;
	}

	/**
	 * Adds a specified path to the augmentation.
	 *
	 * @param path
	 */
	public void addPath(PathwayPath path) {
		paths.add(path);
		MergedPathSegmentsAugmentation aug = new MergedPathSegmentsAugmentation(pathwayRepresentation);
		aug.setPath(path);
		aug.setColor(pathColor);
		add(aug);
	}

	/**
	 * Removes all paths.
	 */
	public void clearPaths() {
		paths.clear();
		clear();
	}

	/**
	 * @param pathColor
	 *            setter, see {@link pathColor}
	 */
	public void setPathColor(Color pathColor) {
		this.pathColor = pathColor;
	}

}
