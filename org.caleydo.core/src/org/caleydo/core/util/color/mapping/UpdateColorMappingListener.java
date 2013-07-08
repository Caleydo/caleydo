/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.color.mapping;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;

/**
 * Listener for {@link UpdateColorMappingEvent}
 * 
 * @author Alexander Lex
 */
public class UpdateColorMappingListener
	extends AEventListener<IColorMappingUpdateListener> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof UpdateColorMappingEvent) {
			handler.updateColorMapping();
		}
	}

}
