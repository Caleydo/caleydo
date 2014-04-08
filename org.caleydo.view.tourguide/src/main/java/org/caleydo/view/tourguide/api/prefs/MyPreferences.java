/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.prefs;

import org.caleydo.view.tourguide.internal.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Samuel Gratzl
 *
 */
public class MyPreferences extends AbstractPreferenceInitializer {
	public static final String MIN_CLUSTER_SIZE = "minClusterSize";
	public static final String JUMP_TO_SELECTED_ROW = "jumpToSelectedRow";

	public static IPreferenceStore prefs() {
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = prefs();
		store.setDefault(MIN_CLUSTER_SIZE, 0);
		store.setDefault(JUMP_TO_SELECTED_ROW, true);
	}

	/**
	 * @return
	 */
	public static int getMinClusterSize() {
		return prefs().getInt(MIN_CLUSTER_SIZE);
	}

	public static boolean isJumpToSelectedRow() {
		return prefs().getBoolean(JUMP_TO_SELECTED_ROW);
	}

}
