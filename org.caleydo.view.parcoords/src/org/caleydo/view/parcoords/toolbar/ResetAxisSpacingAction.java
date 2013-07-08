/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.parcoords.toolbar;

import org.caleydo.core.gui.SimpleEventAction;
import org.caleydo.view.parcoords.Activator;
import org.caleydo.view.parcoords.listener.ResetAxisSpacingEvent;

/**
 * Action that resets the spacing of the axis in the PCs
 *
 * @author Alexander Lex
 */
public class ResetAxisSpacingAction extends SimpleEventAction {
	private static final String LABEL = "Reset Axis Spacing";
	private static final String ICON = "resources/icons/reset_axis_spacing_16.png";

	public ResetAxisSpacingAction() {
		super(LABEL, ICON, Activator.getResourceLoader(), new ResetAxisSpacingEvent());
	}
}
