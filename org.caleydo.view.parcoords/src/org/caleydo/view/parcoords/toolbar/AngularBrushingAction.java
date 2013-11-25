/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.parcoords.toolbar;

import org.caleydo.core.gui.SimpleEventAction;
import org.caleydo.view.parcoords.Activator;
import org.caleydo.view.parcoords.listener.AngularBrushingEvent;

public class AngularBrushingAction extends SimpleEventAction {
	/**
	 * Constructor.
	 */
	public AngularBrushingAction() {
		super("Set angular brush", "resources/icons/angular_brush_16.png", Activator.getResourceLoader(),
				new AngularBrushingEvent());
	}
}
