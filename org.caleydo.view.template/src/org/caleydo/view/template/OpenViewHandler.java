/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.template;

import org.caleydo.core.gui.command.AOpenViewHandler;

public class OpenViewHandler extends AOpenViewHandler {
	/**
	 * Counter variable for determination of the secondary view ID. Needed for multiple instances of the same view type.
	 */
	private static int SECONDARY_ID = 0;

	public OpenViewHandler() {
		super(GLTemplateView.VIEW_TYPE);
	}

	@Override
	protected int getNextSecondaryId() {
		return SECONDARY_ID++;
	}
}
