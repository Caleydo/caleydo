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
package org.caleydo.core.manager;

import java.io.File;
import java.io.IOException;
import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.specialized.Organism;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Handles creation and initialization of preference store. Preferences store enables storing and restoring of
 * application specific preference data.
 */
public class PreferenceManager {

	public static final String PREFERENCE_FILE_NAME = "caleydo.prefs";

	private static PreferenceManager preferenceManager;

	private PreferenceStore preferenceStore;

	public static PreferenceManager get() {
		if (preferenceManager == null) {
			preferenceManager = new PreferenceManager();
		}
		return preferenceManager;
	}

	public PreferenceStore getPreferenceStore() {
		return preferenceStore;
	}

	public static String getPreferencePath() {
		return GeneralManager.CALEYDO_HOME_PATH + PREFERENCE_FILE_NAME;
	}

	void initialize() {

		preferenceStore = new PreferenceStore(getPreferencePath());
		initializeDefaultPreferences();

		try {
			if (GeneralManager.VERSION == null)
				throw new IllegalStateException("Cannot determine current version of Caleydo.");

			preferenceStore.load();
			String storedVersion = preferenceStore.getString(PreferenceConstants.VERSION);

			// If stored version is older then current version - remove old Caleydo folder
			// Test 1st and 2nd number of version string
			if (storedVersion.equals("")
				|| (new Integer(storedVersion.substring(0, 1)) <= new Integer(
					GeneralManager.VERSION.substring(0, 1)) && new Integer(storedVersion.substring(2, 3)) < new Integer(
					GeneralManager.VERSION.substring(2, 3)))) {

				MessageBox messageBox = new MessageBox(new Shell(), SWT.OK);
				messageBox.setText("Clean old data");
				messageBox
					.setMessage("You have downloaded a new major version of Caleydo ("
						+ GeneralManager.VERSION
						+ "). \nYour old Caleydo settings and pathway data will not be converted. \n Caleydo settings and logs are stored at "
						+ GeneralManager.CALEYDO_HOME_PATH);
				messageBox.open();

				initCaleydoFolder();
			}

		}
		catch (IOException e) {
			initCaleydoFolder();
		}

		// System.setProperty("network.proxy_host", "proxy.kages.at");
		// System.setProperty("network.proxy_port", "8080");

		if (preferenceStore.getBoolean(PreferenceConstants.USE_PROXY)) {
			System.setProperty("network.proxy_host",
				preferenceStore.getString(PreferenceConstants.PROXY_SERVER));
			System.setProperty("network.proxy_port",
				preferenceStore.getString(PreferenceConstants.PROXY_PORT));
		}
	}

	private void initializeDefaultPreferences() {

		IPreferenceStore store = GeneralManager.get().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_BOOLEAN, true);
		store.setDefault(PreferenceConstants.P_CHOICE, "choice2");
		store.setDefault(PreferenceConstants.P_STRING, "Default value");

		store.setDefault(PreferenceConstants.HM_NUM_RANDOM_SAMPLING_POINT, 100);
		store.setDefault(PreferenceConstants.HM_NUM_SAMPLES_PER_TEXTURE, 500);
		store.setDefault(PreferenceConstants.HM_NUM_SAMPLES_PER_HEATMAP, 30);
		store.setDefault(PreferenceConstants.HM_LIMIT_REMOTE_TO_CONTEXT, true);
		store.setDefault(PreferenceConstants.PC_NUM_RANDOM_SAMPLING_POINT, 400);
		store.setDefault(PreferenceConstants.PC_LIMIT_REMOTE_TO_CONTEXT, true);
		// store.setDefault(PreferenceConstants.XP_CLASSIC_STYLE_MODE, false);
		store.setDefault(PreferenceConstants.DATA_FILTER_LEVEL, "only_context");
		store.setDefault(PreferenceConstants.PERFORMANCE_LEVEL, "low");

		store.setDefault(PreferenceConstants.VERSION, GeneralManager.VERSION);
		store.setDefault(PreferenceConstants.LAST_CHOSEN_ORGANISM, Organism.HOMO_SAPIENS.name());
		store.setDefault(PreferenceConstants.LAST_CHOSEN_PROJECT_MODE, "SAMPLE_PROJECT");
		store.setDefault(PreferenceConstants.USE_PROXY, false);
		store.setDefault(PreferenceConstants.BROWSER_QUERY_DATABASE, "GeneCards");

		// visual links
		store.setDefault(PreferenceConstants.VISUAL_LINKS_STYLE, 2);
		store.setDefault(PreferenceConstants.VISUAL_LINKS_ANIMATION, false);
		store.setDefault(PreferenceConstants.VISUAL_LINKS_WIDTH, 2.0f);
		store.setDefault(PreferenceConstants.VISUAL_LINKS_COLOR, "255,255,0,255");
		store.setDefault(PreferenceConstants.VISUAL_LINKS_ANIMATED_HALO, false);
		store.setDefault(PreferenceConstants.VISUAL_LINKS_FOR_MOUSE_OVER, false);
		store.setDefault(PreferenceConstants.VISUAL_LINKS_FOR_SELECTIONS, true);

		// treemap
		store.setDefault(PreferenceConstants.TREEMAP_DRAW_CLUSTER_FRAME, true);
		store.setDefault(PreferenceConstants.TREEMAP_LAYOUT_ALGORITHM, 1);
		store.setDefault(PreferenceConstants.TREEMAP_MAX_DEPTH, 0);
		
		// DVI
		store.setDefault(PreferenceConstants.DVI_ALWAYS_USE_TABLE_PERSPECTIVE_DEFAULT_NAME, false);
	}

	private void initCaleydoFolder() {

		// Create .caleydo folder
		if (!new File(GeneralManager.CALEYDO_HOME_PATH).exists()) {
			if (!new File(GeneralManager.CALEYDO_HOME_PATH).mkdir()) {
				String errorMessage =
					"Unable to create home folder at" + GeneralManager.CALEYDO_HOME_PATH
						+ ". Check user permissions!";
				Logger.log(new Status(IStatus.ERROR, toString(), errorMessage));
				throw new IllegalStateException(errorMessage);
			}
		}

		// Create log folder in .caleydo
		if (!new File(GeneralManager.CALEYDO_LOG_PATH).exists()) {
			if (!new File(GeneralManager.CALEYDO_LOG_PATH).mkdir()) {
				String errorMessage =
					"Unable to create log folder at" + GeneralManager.CALEYDO_LOG_PATH
						+ ". Check user permissions!";
				Logger.log(new Status(IStatus.ERROR, toString(), errorMessage));
				throw new IllegalStateException(errorMessage);
			}
		}

	}
}
