package org.caleydo.core.view.opengl.canvas.hyperbolic;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.EDrawAbleNodeDetailLevel;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Hyperbolic render styles
 */

public class HyperbolicRenderStyle
	extends GeneralRenderStyle {

	public static final float X_BORDER_SPACING = 0.02f;
	public static final float Y_BORDER_SPACING = 0.02f;

	public static final float SIDE_SPACING = 0.1f;

	public static final float Y_LAYER_SPACING = 0.1f;
	public static final float X_NODE_SPACING = 0.1f;
	public static final float NODE_SCALING_PER_LAYER = 0.9f;
	public static final float MAX_NODE_SIZE = 0.2f;
	public static final int MAX_DEPTH = 10;
	public static final EDrawAbleNodeDetailLevel[] DETAIL_LEVEL_GRADING =
		{ EDrawAbleNodeDetailLevel.VeryHigh, EDrawAbleNodeDetailLevel.High, EDrawAbleNodeDetailLevel.Normal,
				EDrawAbleNodeDetailLevel.Low, EDrawAbleNodeDetailLevel.VeryLow };

	public static final float[] DA_OBJ_FALLBACK_COLORSCHEME = { 0.0f, 0.0f, 0.0f, 1f };
	public static final float[] DA_OBJ_FALLBACK_COLORSCHEME_HL = { 0.8f, 0.4f, 0.2f, 1f };
	public static final float[] DA_OBJ_FALLBACK_COLORSCHEME_NO_PICK = { 0.5f, 0.5f, 0.5f, 0.2f };

	public static final float[] DA_OBJ_QUAD_COLORSCHEME = { 0.0f, 0.0f, 0.0f, 1f };
	public static final float[] DA_OBJ_QUAD_COLORSCHEME_HL = { 0.4f, 0.6f, 0.2f, 1f };
	public static final float[] DA_OBJ_QUAD_COLORSCHEME_NO_PICK = { 0.5f, 0.5f, 0.5f, 0.2f };

	public static final String[] DA_TEST_NODE_DL_OBJ =
		{ "Fallback", "Fallback", "Fallback", "Fallback", "Fallback" };

	public static final float[] DA_LINEAR_CONNECTION_COLORSHEME = { 1f, 0f, 0f, 1f };
	public static final float DA_LINEAR_CONNECTION_THICKNESS = 1f;

	public static final float[] DA_LINEAR_CONNECTION_COLORSHEME_HL = { 0f, 1f, 0f, 1f };
	public static final float DA_LINEAR_CONNECTION_THICKNESS_HL = 5f;

	public static final float[] DA_HB_CONNECTION_COLORSHEME = { 0f, 0f, 1f, 1f };
	public static final float DA_HB_CONNECTION_THICKNESS = 2f;

	public static final float[] DA_HB_CONNECTION_COLORSHEME_NO_PICK = { 0f, 0f, 0f, 0.5f };
	public static final float DA_HB_CONNECTION_THICKNESS_NO_PICK = 1f;

	public static final float[] DA_HB_GEOM_CONNECTION_COLORSHEME_HL = { 0f, 1f, 0f, 1f };
	public static final float DA_HB_GEOM_CONNECTION_THICKNESS_HL = 5f;
	public static final int DA_SPLINE_CONNECTION_NR_CTRLPOINTS = 20;

	public static final float[] DA_HB_GEOM_GLOBE_COLORSHEME_HL = { 0f, 1f, 1f, 0.3f };
	public static final float DA_HB_GEOM_GLOBE_THICKNESS_HL = 2.5f;

	// public static final String LINEAR_TREE_LAYOUTER_CONNECTION_TYPE = "Spline";
	// public static final String HYPERBOLIC_TREE_LAYOUTER_CONNECTION_TYPE = "Spline";

	/**
	 * Specify how many contact points a DA object should return. value must be a multiple of 4 (4, 8, 12,
	 * 16,...)
	 */
	public static final int DA_OBJ_NUM_CONTACT_POINTS = 48;

	public HyperbolicRenderStyle(IViewFrustum viewFrustum) {
		super(viewFrustum);
	}

}
