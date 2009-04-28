package org.caleydo.rcp.preferences;

import static org.caleydo.core.util.preferences.PreferenceConstants.COLOR_MARKER_POINT_COLOR;
import static org.caleydo.core.util.preferences.PreferenceConstants.COLOR_MARKER_POINT_LEFT_SPREAD;
import static org.caleydo.core.util.preferences.PreferenceConstants.COLOR_MARKER_POINT_RIGHT_SPREAD;
import static org.caleydo.core.util.preferences.PreferenceConstants.COLOR_MARKER_POINT_VALUE;
import static org.caleydo.core.util.preferences.PreferenceConstants.GENE_EXPRESSION_PREFIX;
import static org.caleydo.core.util.preferences.PreferenceConstants.NAN_COLOR;
import static org.caleydo.core.util.preferences.PreferenceConstants.NUMBER_OF_COLOR_MARKER_POINTS;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer
	extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {

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
		store.setDefault(PreferenceConstants.PC_NUM_RANDOM_SAMPLING_POINT, 1000);
		store.setDefault(PreferenceConstants.PC_LIMIT_REMOTE_TO_CONTEXT, true);
		store.setDefault(PreferenceConstants.XP_CLASSIC_STYLE_MODE, false);
		store.setDefault(PreferenceConstants.DATA_FILTER_LEVEL, "only_context");
	}
}
