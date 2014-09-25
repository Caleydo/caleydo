/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.toolbar;

import org.caleydo.core.internal.cmd.AOpenViewHandler;

public class OpenGeneralToolbarHandler extends AOpenViewHandler {
	public OpenGeneralToolbarHandler() {
		super(RcpToolBarView.ID, SINGLE);
	}
}
