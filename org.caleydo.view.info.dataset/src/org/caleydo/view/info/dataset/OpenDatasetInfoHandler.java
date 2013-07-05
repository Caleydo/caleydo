/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.info.dataset;

import org.caleydo.core.gui.command.AOpenViewHandler;

public class OpenDatasetInfoHandler extends AOpenViewHandler {

	public OpenDatasetInfoHandler() {
		super(RcpDatasetInfoView.VIEW_TYPE);
	}
}
