/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.kaplanmeier;

import org.caleydo.core.gui.command.AOpenViewHandler;

public class OpenViewHandler extends AOpenViewHandler {
	protected OpenViewHandler() {
		super(GLKaplanMeier.VIEW_TYPE);
	}
}
