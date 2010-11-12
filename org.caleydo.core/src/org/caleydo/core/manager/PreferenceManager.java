package org.caleydo.core.manager;

import static org.caleydo.core.util.preferences.PreferenceConstants.COLOR_MARKER_POINT_COLOR;
import static org.caleydo.core.util.preferences.PreferenceConstants.COLOR_MARKER_POINT_LEFT_SPREAD;
import static org.caleydo.core.util.preferences.PreferenceConstants.COLOR_MARKER_POINT_RIGHT_SPREAD;
import static org.caleydo.core.util.preferences.PreferenceConstants.COLOR_MARKER_POINT_VALUE;
import static org.caleydo.core.util.preferences.PreferenceConstants.GENE_EXPRESSION_PREFIX;
import static org.caleydo.core.util.preferences.PreferenceConstants.NAN_COLOR;
import static org.caleydo.core.util.preferences.PreferenceConstants.NUMBER_OF_COLOR_MARKER_POINTS;

import java.io.File;
import java.io.IOException;

import org.caleydo.core.manager.specialized.Organism;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.preferences.PreferenceConstants;
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

	void initialize() {

		preferenceStore = new PreferenceStore(GeneralManager.CALEYDO_HOME_PATH + PREFERENCE_FILE_NAME);
		initializeDefaultPreferences();

		try {
			if (GeneralManager.VERSION == null)
				throw new IllegalStateException("Cannot determine current version of Caleydo.");

			preferenceStore.load();
			String sStoredVersion = preferenceStore.getString(PreferenceConstants.VERSION);

			// If stored version is older then current version - remove old Caleydo folder
			// Test 1st and 2nd number of version string
			if (sStoredVersion.equals("")
				|| (new Integer(sStoredVersion.substring(0, 1)) <= new Integer(
					GeneralManager.VERSION.substring(0, 1)) && new Integer(sStoredVersion.substring(2, 3)) < new Integer(
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

//		System.setProperty("network.proxy_host", "proxy.kages.at");
//		System.setProperty("network.proxy_port", "8080");

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

		store.setDefault(GENE_EXPRESSION_PREFIX + COLOR_MARKER_POINT_VALUE + "1", 0.0f);
		store.setDefault(GENE_EXPRESSION_PREFIX + COLOR_MARKER_POINT_VALUE + "2", 0.5f);
		store.setDefault(GENE_EXPRESSION_PREFIX + COLOR_MARKER_POINT_VALUE + "3", 1.0f);
		store.setDefault(GENE_EXPRESSION_PREFIX + COLOR_MARKER_POINT_LEFT_SPREAD + "1", 0f);
		store.setDefault(GENE_EXPRESSION_PREFIX + COLOR_MARKER_POINT_LEFT_SPREAD + "2", 0.1f);
		store.setDefault(GENE_EXPRESSION_PREFIX + COLOR_MARKER_POINT_LEFT_SPREAD + "3", 0.1f);
		store.setDefault(GENE_EXPRESSION_PREFIX + COLOR_MARKER_POINT_RIGHT_SPREAD + "1", 0.1f);
		store.setDefault(GENE_EXPRESSION_PREFIX + COLOR_MARKER_POINT_RIGHT_SPREAD + "2", 0.1f);
		store.setDefault(GENE_EXPRESSION_PREFIX + COLOR_MARKER_POINT_RIGHT_SPREAD + "3", 0f);
		store.setDefault(GENE_EXPRESSION_PREFIX + COLOR_MARKER_POINT_COLOR + "1", "0,255,0");
		store.setDefault(GENE_EXPRESSION_PREFIX + COLOR_MARKER_POINT_COLOR + "2", "0,0,0");
		store.setDefault(GENE_EXPRESSION_PREFIX + COLOR_MARKER_POINT_COLOR + "3", "255,0,0");
		store.setDefault(NAN_COLOR, "100,100,100");

		store.setDefault(GENE_EXPRESSION_PREFIX + NUMBER_OF_COLOR_MARKER_POINTS, 3);

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
		store.setDefault(PreferenceConstants.FIRST_START, true);
		store.setDefault(PreferenceConstants.PATHWAY_DATA_OK, "");
		store.setDefault(PreferenceConstants.LAST_CHOSEN_ORGANISM, Organism.HOMO_SAPIENS.name());
		store.setDefault(PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES, "KEGG;BioCarta");
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
