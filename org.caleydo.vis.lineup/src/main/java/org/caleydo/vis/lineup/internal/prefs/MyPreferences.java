/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.internal.prefs;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.vis.lineup.internal.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

/**
 * @author Samuel Gratzl
 *
 */
public class MyPreferences extends AbstractPreferenceInitializer {
	private static IPreferenceStore prefs() {
		if (Activator.getDefault() == null) {
			return GLSandBox.prefs(MyPreferences.class);
		}
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = prefs();
		PreferenceConverter.setDefault(store, "transitions.color.up", Color.GREEN.asRGB());
		PreferenceConverter.setDefault(store, "transitions.color.down", Color.RED.asRGB());
	}

	public static Color getTransitionUpColor() {
		return asColor("transitions.color.up");
	}

	public static Color getTransitionDownColor() {
		return asColor("transitions.color.down");
	}

	private static Color asColor(String key) {
		RGB rgb = PreferenceConverter.getColor(prefs(), key);
		return new Color(rgb.red, rgb.green, rgb.blue);
	}


}
