/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.parcoords.toolbar;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.parcoords.Activator;
import org.caleydo.view.parcoords.listener.AngularBrushingEvent;
import org.eclipse.jface.action.Action;

public class AngularBrushingAction extends Action {

	public static final String LABEL = "Set angular brush";
	public static final String ICON = "resources/icons/angular_brush_16.png";

	/**
	 * Constructor.
	 */
	public AngularBrushingAction() {
		setText(LABEL);
		setToolTipText(LABEL);
		setImageDescriptor(Activator.getImageDescriptor(ICON));
	}

	@Override
	public void run() {
		super.run();
		GeneralManager.get().getEventPublisher().triggerEvent(new AngularBrushingEvent());
	}
}
