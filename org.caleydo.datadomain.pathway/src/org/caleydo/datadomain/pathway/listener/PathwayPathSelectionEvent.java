/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.datadomain.pathway.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.datadomain.pathway.graph.PathwayPath;

/**
 * Event that specifies the selection of a {@link PathwayPath}.
 *
 * @author Christian Partl
 *
 */
public class PathwayPathSelectionEvent extends AEvent {

	/**
	 * Path object that specifies a path.
	 */
	private PathwayPath path;

	@Override
	public boolean checkIntegrity() {
		return path.checkIntegrity();
	}

	/**
	 * @return a new copy of the path, see {@link #path}
	 */
	public PathwayPath getPath() {
		return new PathwayPath(path);
	}

	/**
	 * @param path
	 *            setter, see {@link path}
	 */
	public void setPath(PathwayPath path) {
		this.path = path;
	}
}
