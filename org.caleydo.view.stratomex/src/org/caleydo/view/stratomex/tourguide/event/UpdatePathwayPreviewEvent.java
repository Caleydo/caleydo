/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.tourguide.event;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

/**
 * @author Samuel Gratzl
 *
 */
public class UpdatePathwayPreviewEvent extends ADirectedEvent {
	private final PathwayGraph pathway;

	public UpdatePathwayPreviewEvent(PathwayGraph pathway) {
		this.pathway = pathway;
	}

	/**
	 * @return the pathway, see {@link #pathway}
	 */
	public PathwayGraph getPathway() {
		return pathway;
	}


	@Override
	public boolean checkIntegrity() {
		return pathway != null;
	}
}
