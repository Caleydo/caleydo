/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.base;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.EventPublisher;

/**
 * Action that triggers a specified event.
 *
 * @author Christian Partl
 *
 */
public class EventBasedAction implements IAction {

	private final AEvent event;

	public EventBasedAction(AEvent event) {
		this.event = event;
	}

	@Override
	public void perform() {
		EventPublisher.trigger(event);
	}

}
