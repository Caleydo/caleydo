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
package org.caleydo.core.gui.preferences;

import org.caleydo.core.startup.gui.ChooseProjectTypePage.EProjectLoadType;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	public static final String P_PATH = "pathPreference";
	public static final String P_BOOLEAN = "booleanPreference";
	public static final String P_CHOICE = "choicePreference";
	public static final String P_STRING = "stringPreference";

	// General
	public static final String VERSION = "version";

	public static final String LAST_CHOSEN_ORGANISM = "lastLoadedOrganism";

	/**
	 * The application mode chosen during the last time. A value of
	 * EApplicationMode
	 */
	public static final String LAST_CHOSEN_PROJECT_MODE = "lastChosenApplicationMode";

	/**
	 * How was the last project loaded? From "previous project" or manually. See
	 * {@link EProjectLoadType}
	 */
	public static final String LAST_CHOSEN_PROJECT_LOAD_TYPE = "lastChosenProjectLoadType";
	/** The file path to the last loaded project */
	public static final String LAST_MANUALLY_CHOSEN_PROJECT = "lastManuallyChosenProject";

	public static final String BROWSER_QUERY_DATABASE = "browserQueryDatabase";

	// Heat Map
	public static final String HM_NUM_RANDOM_SAMPLING_POINT = "hmNumRandomSamplinPoints";
	public static final String HM_LIMIT_REMOTE_TO_CONTEXT = "hmLimitRemoteToContext";
	public static final String HM_NUM_SAMPLES_PER_TEXTURE = "hmNumSamplesPerTexture";
	public static final String HM_NUM_SAMPLES_PER_HEATMAP = "hmNumSamplesPerHeatmap";

	// Parallel Coordinates
	public static final String PC_NUM_RANDOM_SAMPLING_POINT = "pcNumRandomSamplinPoints";
	public static final String PC_LIMIT_REMOTE_TO_CONTEXT = "pcLimitRemoteToContext";

	// Visual Links
	public static final String VISUAL_LINKS_STYLE = "visualLinksStyle";
	public static final String VISUAL_LINKS_ANIMATION = "visualLinksAnimation";
	public static final String VISUAL_LINKS_WIDTH = "visualLinksWidth";
	public static final String VISUAL_LINKS_COLOR = "visualLinksColor";
	public static final String VISUAL_LINKS_ANIMATED_HALO = "visualLinksAnimatedHalo";
	public static final String VISUAL_LINKS_FOR_MOUSE_OVER = "visualLinksForMouseOver";
	public static final String VISUAL_LINKS_FOR_SELECTIONS = "visualLinksForSelections";

	public static final String USE_PROXY = "useProxy";
	public static final String PROXY_SERVER = "proxyServer";
	public static final String PROXY_PORT = "proxyPort";

	public static final String DATA_FILTER_LEVEL = "dataFilterLevel";

	public static final String PERFORMANCE_LEVEL = "performanceLevel";

	// TreeMapView
	public static final String TREEMAP_MAX_DEPTH = "treemapMaxDepth";
	public static final String TREEMAP_LAYOUT_ALGORITHM = "treemapLayoutAlgorithm";
	public static final String TREEMAP_DRAW_CLUSTER_FRAME = "treemapDrawClusterFrame";
}
