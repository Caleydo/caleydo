/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui.augmentation.path;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.pathway.IVertexRepSelectionListener;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;

/**
 * @author Christian
 *
 */
public class PathwayPathHandler implements IVertexRepSelectionListener {

	protected boolean isPathSelectionMode = false;
	protected String eventSpace;
	protected PathwayVertexRep startVertexRep;

	@ListenTo
	public void onPathSelectionModeChanged(EnablePathSelectionEvent event) {
		if (event.getEventSpace() != null && event.getEventSpace().equals(eventSpace)) {
			isPathSelectionMode = event.isPathSelectionMode();
		}
	}

	@Override
	public void onSelect(PathwayVertexRep vertexRep, Pick pick) {
		// TODO Auto-generated method stub

	}

}
