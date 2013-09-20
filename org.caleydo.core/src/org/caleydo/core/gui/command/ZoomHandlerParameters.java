/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.gui.command;
import java.util.Map;

import org.eclipse.core.commands.IParameterValues;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Samuel Gratzl
 *
 */
public class ZoomHandlerParameters implements IParameterValues {
	@Override
	public Map<?, ?> getParameterValues() {
		return ImmutableMap.of("Zoom In", "zoomIn", "Zoom Out", "zoomOut", "Reset Zoom", "reset");
	}
}
