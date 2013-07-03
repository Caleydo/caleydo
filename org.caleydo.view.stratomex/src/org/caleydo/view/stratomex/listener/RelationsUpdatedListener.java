/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.RelationsUpdatedEvent;
import org.caleydo.view.stratomex.brick.GLBrick;

/**
 * Listener for {@link RelationsUpdatedEvent}, calling to {@link GLBrick}.
 * 
 * @author Alexander Lex
 * 
 */
public class RelationsUpdatedListener extends AEventListener<GLBrick> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RelationsUpdatedEvent) {
			handler.relationsUpdated();
		}
	}

}
