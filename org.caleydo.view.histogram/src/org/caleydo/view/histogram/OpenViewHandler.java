/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram;

import org.caleydo.core.internal.cmd.AOpenViewHandler;

public class OpenViewHandler extends AOpenViewHandler {
	public OpenViewHandler() {
		super(GLHistogram.VIEW_TYPE, true);
	}
}
