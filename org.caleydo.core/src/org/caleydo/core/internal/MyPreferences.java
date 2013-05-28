/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
