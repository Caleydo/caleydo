/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.internal;

import java.io.IOException;

import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Samuel Gratzl
 *
 */
public class MyPreferences {
	private static final String LAST_CHOSEN_PROJECT_MODE = "lastChosenApplicationMode";
	private static final String LAST_MANUALLY_CHOSEN_PROJECT = "lastManuallyChosenProject";
	private static final String LAST_SAMPLE_CHOSEN_PROJECT = "lastChosenSampleProject";
	private static final String AUTO_PROJECT_LOAD = "autoload";

	private static IPreferenceStore prefs() {
		return Activator.getDefault().getPreferenceStore();
	}

	public static String getLastManuallyChosenProject() {
		return prefs().getString(LAST_MANUALLY_CHOSEN_PROJECT);
	}

	public static void setLastManuallyChosenProject(String value) {
		prefs().setValue(LAST_MANUALLY_CHOSEN_PROJECT, value);
	}

	public static String getLastChosenSampleProject() {
		return prefs().getString(LAST_SAMPLE_CHOSEN_PROJECT);
	}

	public static void setLastChosenSampleProject(String value) {
		prefs().setValue(LAST_SAMPLE_CHOSEN_PROJECT, value);
	}

	public static String getLastChosenProjectMode() {
		return prefs().getString(LAST_CHOSEN_PROJECT_MODE);
	}

	public static void setLastChosenProjectMode(String value) {
		prefs().setValue(LAST_CHOSEN_PROJECT_MODE, value);
	}

	public static String getAutoLoadProject() {
		return prefs().getString(AUTO_PROJECT_LOAD);
	}

	public static void setAutoLoadProject(String fileName) {
		if (fileName == null)
			prefs().setToDefault(AUTO_PROJECT_LOAD);
		else
			prefs().setValue(AUTO_PROJECT_LOAD, fileName);
	}

	public static void flush() {
		IPreferenceStore prefs = prefs();
		if (prefs instanceof IPersistentPreferenceStore) {
			try {
				((IPersistentPreferenceStore) prefs).save();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
