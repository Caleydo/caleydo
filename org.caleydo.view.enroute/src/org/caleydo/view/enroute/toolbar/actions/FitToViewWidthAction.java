/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.enroute.toolbar.actions;

import org.caleydo.core.gui.SimpleAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.enroute.Activator;
import org.caleydo.view.enroute.event.FitToViewWidthEvent;

/**
 * Button that enables the fit to view width mode in enRoute.
 *
 * @author Christian Partl
 *
 */
public class FitToViewWidthAction extends SimpleAction {

	public static final String LABEL = "Fit to view width";
	public static final String ICON = "resources/icons/fit_to_width.png";

	/**
	 * Constructor.
	 */
	public FitToViewWidthAction(boolean fitToViewWidth) {
		super(LABEL, ICON, new ResourceLoader(Activator.getResourceLocator()));
		setChecked(fitToViewWidth);
	}

	@Override
	public void run() {
		super.run();
		GeneralManager.get().getEventPublisher()
				.triggerEvent(new FitToViewWidthEvent(isChecked()));
	}
}
