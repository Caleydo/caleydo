/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.pathway.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.pathway.GLPathway;

/**
 * Listener for {@link SampleMappingModeEvent} for pathways.
 * 
 * @author Alexander Lex
 * 
 */
public class SampleMappingModeListener
	extends AEventListener<GLPathway> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof SampleMappingModeEvent) {
			SampleMappingModeEvent sampleMappingModeEvent = (SampleMappingModeEvent) event;

			handler.setSampleMappingMode(sampleMappingModeEvent.getSampleMappingMode());
		}

	}

}
