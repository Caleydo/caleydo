/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;

/**
 * @author Samuel Gratzl
 *
 */
public class MyPreferences extends AbstractPreferenceInitializer {
	public static final String VIEW_ZOOM_FACTOR = "view.zoomfactor";
	public static final String FPS = "view.fps";

	/**
	 * chooses the implementation for the jogl canvas, possible values are: awt, swt (default) and newt
	 */
	public static final String CANVAS_IMPLEMENTATION = System.getProperty("org.caleydo.opengl", "swt");

	public static IPreferenceStore prefs() {
		Activator a = Activator.getDefault();
		if (a == null)
			return new PreferenceStore();
		return a.getPreferenceStore();
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = prefs();
		store.setDefault(VIEW_ZOOM_FACTOR, 100);
		store.setDefault(FPS, 30);
	}

	public static float getViewZoomFactor() {
		return prefs().getInt(VIEW_ZOOM_FACTOR) / 100.f;
	}

	/**
	 * @return
	 */
	public static int getFPS() {
		int fps = prefs().getInt(FPS);
		if (fps <= 0)
			fps = 30;
		return fps;
	}
}
