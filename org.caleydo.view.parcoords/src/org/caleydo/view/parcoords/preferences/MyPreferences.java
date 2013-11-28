/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.parcoords.preferences;

import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.view.parcoords.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Samuel Gratzl
 *
 */
public class MyPreferences extends AbstractPreferenceInitializer {
	static final String NUM_RANDOM_SAMPLING_POINT = "pcNumRandomSamplinPoints";

	public static int getNumRandomSamplePoint() {
		return prefs().getInt(NUM_RANDOM_SAMPLING_POINT);
	}

	private static IPreferenceStore prefs() {
		if (Activator.getDefault() == null)
			return GLSandBox.prefs(MyPreferences.class);
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = prefs();
		store.setDefault(NUM_RANDOM_SAMPLING_POINT, 400);

	}
}
