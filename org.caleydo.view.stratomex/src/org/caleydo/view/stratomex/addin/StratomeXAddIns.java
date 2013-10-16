/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.stratomex.addin;

import java.util.Collection;
import java.util.List;

import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.view.stratomex.GLStratomex;

/**
 * @author Samuel Gratzl
 *
 */
public class StratomeXAddIns {
	private static final String EXTENSION_POINT = "org.caleydo.view.stratomex.addin";

	/**
	 * @param glStratomex
	 * @return
	 */
	public static Collection<IStratomeXAddIn> createFor(GLStratomex stratomeX) {
		List<IStratomeXAddIn> list = ExtensionUtils.findImplementation(EXTENSION_POINT, "class",
				IStratomeXAddIn.class);
		for (IStratomeXAddIn addin : list)
			addin.stampTo(stratomeX);
		return list;
	}
}
