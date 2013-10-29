/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;

/**
 * @author Samuel Gratzl
 *
 */
public class MyPreferences extends AbstractPreferenceInitializer {
	private static final String LAST_CHOSEN_PROJECT_MODE = "lastChosenApplicationMode";
	/**
	 * Note that this string is used as prefix for multiple last chosen projects.
	 */
	private static final String LAST_MANUALLY_CHOSEN_PROJECT = "lastManuallyChosenProject";
	private static final String LAST_CHOSE_RECENT_PROJECT = "lastChoseRecentProject";
	private static final String LAST_SAMPLE_CHOSEN_PROJECT = "lastChosenSampleProject";
	private static final String AUTO_PROJECT_LOAD = "autoload";
	private static final int MAX_MANUALLY_CHOSEN_PROJECTS_TO_SAVE = 5;

	public static final String VIEW_ZOOM_FACTOR = "view.zoomfactor";
	public static final String FPS = "view.fps";

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

	public static boolean wasRecentProjectChosenLastly() {
		return prefs().getBoolean(LAST_CHOSE_RECENT_PROJECT);
	}

	public static void setRecentProjectChosenLastly(boolean recentProjectChosen) {
		prefs().setValue(LAST_CHOSE_RECENT_PROJECT, recentProjectChosen);
	}

	public static String getLastManuallyChosenProject() {
		return prefs().getString(LAST_MANUALLY_CHOSEN_PROJECT + 0);
	}

	public static void setLastManuallyChosenProject(String value) {
		List<String> lastProjects = getLastManuallyChosenProjects();
		lastProjects.remove(value);
		lastProjects.add(0, value);
		for (int i = 0; i < MAX_MANUALLY_CHOSEN_PROJECTS_TO_SAVE && i < lastProjects.size(); i++) {
			prefs().setValue(LAST_MANUALLY_CHOSEN_PROJECT + i, lastProjects.get(i));
		}
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

	public static List<String> getLastManuallyChosenProjects() {
		List<String> projects = new ArrayList<>();
		for (int i = 0; i < MAX_MANUALLY_CHOSEN_PROJECTS_TO_SAVE; i++) {
			String project = prefs().getString(LAST_MANUALLY_CHOSEN_PROJECT + i);
			if (project != null && !project.isEmpty())
				projects.add(project);
		}
		return projects;
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
