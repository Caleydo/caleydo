/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.parcoords.command.handler;

import org.caleydo.core.internal.cmd.AOpenViewHandler;
import org.caleydo.view.parcoords.GLParallelCoordinates;

public class OpenParCoordsHandler extends AOpenViewHandler {
	public OpenParCoordsHandler() {
		super(GLParallelCoordinates.VIEW_TYPE, MULTIPLE);
	}
}
