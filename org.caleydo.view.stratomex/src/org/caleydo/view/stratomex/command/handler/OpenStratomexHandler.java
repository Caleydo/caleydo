/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.command.handler;

import org.caleydo.core.gui.command.AOpenViewHandler;
import org.caleydo.view.stratomex.GLStratomex;

public class OpenStratomexHandler extends AOpenViewHandler {
	public OpenStratomexHandler() {
		super(GLStratomex.VIEW_TYPE, true);
	}
}
