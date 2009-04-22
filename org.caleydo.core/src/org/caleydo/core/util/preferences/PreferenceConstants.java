package org.caleydo.core.util.preferences;

import org.caleydo.core.util.mapping.color.EColorMappingType;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	public static final String P_PATH = "pathPreference";
	public static final String P_BOOLEAN = "booleanPreference";
	public static final String P_CHOICE = "choicePreference";
	public static final String P_STRING = "stringPreference";

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

	// Pathway
	public static final String PATHWAY_DATA_OK = "pathwayDataOK";
	public static final String LOAD_PATHWAY_DATA = "loadPathwayData";

	// General
	public static final String FIRST_START = "firstStart";

	public static final String LAST_PATHWAY_UPDATE = "lastPathwayDataUpdate";

	@Deprecated
	public static final String XP_CLASSIC_STYLE_MODE = "enableXPClassicStyleMode";

	public static final String USE_PROXY = "useProxy";
	public static final String PROXY_SERVER = "proxyServer";
	public static final String PROXY_PORT = "proxyPort";

	public static final String DATA_FILTER_LEVEL = "dataFilterLevel";
}
