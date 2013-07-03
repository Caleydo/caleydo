/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.treemap.preferences;

import org.caleydo.view.treemap.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Samuel Gratzl
 *
 */
public class MyPreferences extends AbstractPreferenceInitializer {
	static final String TREEMAP_MAX_DEPTH = "treemapMaxDepth";
	static final String TREEMAP_LAYOUT_ALGORITHM = "treemapLayoutAlgorithm";
	static final String TREEMAP_DRAW_CLUSTER_FRAME = "treemapDrawClusterFrame";


	private static IPreferenceStore prefs() {
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = prefs();
		store.setDefault(TREEMAP_DRAW_CLUSTER_FRAME, true);
		store.setDefault(TREEMAP_LAYOUT_ALGORITHM, 1);
		store.setDefault(TREEMAP_MAX_DEPTH, 0);
	}

	public static int getMapDepth() {
		return prefs().getInt(TREEMAP_MAX_DEPTH);
	}
	public static boolean isDrawClusterFrame() {
		return prefs().getBoolean(TREEMAP_DRAW_CLUSTER_FRAME);
	}
	public static int getLayoutAlgorithm() {
		return prefs().getInt(TREEMAP_LAYOUT_ALGORITHM);
	}

}
