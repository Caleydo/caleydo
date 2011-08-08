package org.caleydo.core.gui.preferences;

import org.caleydo.core.util.mapping.color.EColorMappingType;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	public static final String P_PATH = "pathPreference";
	public static final String P_BOOLEAN = "booleanPreference";
	public static final String P_CHOICE = "choicePreference";
	public static final String P_STRING = "stringPreference";

	// General
	public static final String FIRST_START = "firstStart";
	public static final String VERSION = "version";

	// Pathway
	public static final String PATHWAY_DATA_OK = "pathwayDataOK";
	public static final String LAST_CHOSEN_PATHWAY_DATA_SOURCES = "lastLoadedPathwayDataSources";
	public static final String LAST_CHOSEN_ORGANISM = "lastLoadedOrganism";
	// public static final String LAST_CHOSEN_USE_CASE_MODE = "lastChosenUseCaseMode";
	/** The application mode chosen during the last time. A value of EApplicationMode */
	public static final String LAST_CHOSEN_PROJECT_MODE = "lastChosenApplicationMode";

	// TODO: make it specific for each pathway database + organism combination
	public static final String LAST_PATHWAY_UPDATE = "lastPathwayDataUpdate";

	public static final String BROWSER_QUERY_DATABASE = "browserQueryDatabase";

	// Color Mapping
	public static final String GENE_EXPRESSION_PREFIX = EColorMappingType.GENE_EXPRESSION + "_";
	public static final String NUMBER_OF_COLOR_MARKER_POINTS = "numberOfColorMarkerPoints";
	public static final String COLOR_MARKER_POINT_VALUE = "colorMarkerPointValue";
	public static final String COLOR_MARKER_POINT_LEFT_SPREAD = "colorMarkerPointLeftSpread";
	public static final String COLOR_MARKER_POINT_RIGHT_SPREAD = "colorMarkerPointRightSpread";
	public static final String COLOR_MARKER_POINT_COLOR = "colorMarkerPointColor";
	public static final String NAN_COLOR = "nanColor";
	public static final String COLOR_MAPPING_USED = "colorMappingUsed";

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
	public static final String TREEMAP_DRAW_CLUSTER_FRAME= "treemapDrawClusterFrame";
}
