/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.pathway.listener;

import java.util.Set;

import org.caleydo.core.event.AEvent;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

/**
 * @author Christian
 *
 */
public class LoadPathwaysEvent extends AEvent {

	private Set<PathwayGraph> pathways;

	public LoadPathwaysEvent(Set<PathwayGraph> pathways) {
		this.pathways = pathways;
	}

	@Override
	public boolean checkIntegrity() {
		return pathways != null;
	}

	/**
	 * @return the pathways, see {@link #pathways}
	 */
	public Set<PathwayGraph> getPathways() {
		return pathways;
	}

	/**
	 * @param pathways
	 *            setter, see {@link pathways}
	 */
	public void setPathways(Set<PathwayGraph> pathways) {
		this.pathways = pathways;
	}

}
