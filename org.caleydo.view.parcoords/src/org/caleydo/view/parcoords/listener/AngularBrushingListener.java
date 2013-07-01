/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.parcoords.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.parcoords.GLParallelCoordinates;

/**
 * Listener that reacts on angular brushing events for PCs.
 * 
 * @author Alexander Lex
 */
public class AngularBrushingListener extends AEventListener<GLParallelCoordinates> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof AngularBrushingEvent) {
			handler.triggerAngularBrushing();
		}

	}

}
