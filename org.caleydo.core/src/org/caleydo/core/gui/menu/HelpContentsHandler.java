/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.menu;

import org.caleydo.core.manager.GeneralManager;

public class HelpContentsHandler extends ABrowserContentsHandler {
	public HelpContentsHandler() {
		super(GeneralManager.HELP_URL);
	}
}
