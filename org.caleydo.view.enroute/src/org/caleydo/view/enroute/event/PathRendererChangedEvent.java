/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.view.enroute.path.APathwayPathRenderer;

/**
 * Event that is triggered when a {@link APathwayPathRenderer} changes, i.e., its path changes, branches are uncollapsed
 * etc.
 *
 * @author Christian Partl
 *
 */
public class PathRendererChangedEvent extends AEvent {

	/**
	 * The {@link APathwayPathRenderer} that changed.
	 */
	private APathwayPathRenderer pathRenderer;

	public PathRendererChangedEvent(APathwayPathRenderer pathRenderer) {
		this.pathRenderer = pathRenderer;
	}

	@Override
	public boolean checkIntegrity() {
		return pathRenderer != null;
	}

	/**
	 * @param pathRenderer
	 *            setter, see {@link pathRenderer}
	 */
	public void setPathRenderer(APathwayPathRenderer pathRenderer) {
		this.pathRenderer = pathRenderer;
	}

	/**
	 * @return the pathRenderer, see {@link #pathRenderer}
	 */
	public APathwayPathRenderer getPathRenderer() {
		return pathRenderer;
	}

}
