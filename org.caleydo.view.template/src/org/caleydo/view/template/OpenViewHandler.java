/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.template;

import org.caleydo.core.gui.command.AOpenViewHandler;

public class OpenViewHandler extends AOpenViewHandler {
	public OpenViewHandler() {
		super(GLTemplateView.VIEW_TYPE, true);
	}
}
