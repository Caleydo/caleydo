/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi;

/**
 * @author Samuel Gratzl
 *
 */
public class MyPreferences {
	private static final String ALWAYS_USE_TABLE_PERSPECTIVE_DEFAULT_NAME = "alwaysUseTablePerspectiveDefaultName";

	public static boolean isAwaysUseTablePerspectiveDefaultName() {
		return Activator.getMyPreferences().getBoolean(ALWAYS_USE_TABLE_PERSPECTIVE_DEFAULT_NAME);
	}

	public static void setIsAwaysUseTablePerspectiveDefaultName(boolean value) {
		Activator.getMyPreferences().setValue(ALWAYS_USE_TABLE_PERSPECTIVE_DEFAULT_NAME, value);
	}
}
