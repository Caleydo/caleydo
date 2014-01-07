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
 * @author Christian
 *
 */
public class ContextPathsAugmentation extends GLElementContainer {

	public static final Color CONTEX_PATH_COLOR = new Color(0f, 0f, 1f, 1f);

	protected List<PathwayPath> contextPaths = new ArrayList<>();
	protected IPathwayRepresentation pathwayRepresentation;

	public ContextPathsAugmentation(IPathwayRepresentation pathwayRepresentation) {
		setLayout(GLLayouts.LAYERS);
		this.pathwayRepresentation = pathwayRepresentation;
	}

	/**
	 * @return the contextPaths, see {@link #contextPaths}
	 */
	public List<PathwayPath> getContextPaths() {
		return contextPaths;
	}

	public void addContextPath(PathwayPath path) {
		contextPaths.add(path);
		MergedPathSegmentsAugmentation aug = new MergedPathSegmentsAugmentation(pathwayRepresentation);
		aug.setPath(path);
		aug.setColor(CONTEX_PATH_COLOR);
		add(aug);
	}

	public void clearPaths() {
		contextPaths.clear();
		clear();
	}

}
