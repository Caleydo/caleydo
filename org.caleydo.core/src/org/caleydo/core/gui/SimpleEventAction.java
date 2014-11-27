/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.data.loader.ResourceLoader;

/**
 * Common simplification for an action that triggers an {@link AEvent}.
 *
 * @author Samuel Gratzl
 *
 */
public class SimpleEventAction extends SimpleAction {

	private final AEvent event;

	public SimpleEventAction(String label, String iconResource, AEvent event) {
		super(label, iconResource);
		this.event = event;
	}

	public SimpleEventAction(String label, String iconResource, ResourceLoader loader, AEvent event) {
		super(label, iconResource, loader);
		this.event = event;
	}

	@Override
	public void run() {
		super.run();
		EventPublisher.trigger(event);
	}
}
