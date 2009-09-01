package org.caleydo.core.view.opengl.canvas.hyperbolic;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
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
	public static final float LIN_TREE_Y_SCALING_PER_LAYER = 0.9f;

	public static final float[] DA_OBJ_FALLBACK_COLORSCHEME = { 0.5f, 0.5f, 0.5f, 1f };
	public static final float[] DA_OBJ_FALLBACK_COLORSCHEME_HL = { 0.8f, 0.4f, 0.2f, 1f };

	public static final float[] DA_OBJ_QUAD_COLORSCHEME = { 0.0f, 0.0f, 0.0f, 1f };
	public static final float[] DA_OBJ_QUAD_COLORSCHEME_HL = { 0.4f, 0.6f, 0.2f, 1f };

	public static final String[] DA_TEST_NODE_DL_OBJ =
		{ "Square", "Fallback", "Fallback", "Fallback", "Fallback" };

	public static final float[] DA_LINEAR_CONNECTION_COLORSHEME = { 1f, 0f, 0f, 1f };
	public static final float DA_LINEAR_CONNECTION_THICKNESS = 1f;

	public static final float[] DA_LINEAR_CONNECTION_COLORSHEME_HL = { 0f, 1f, 0f, 1f };
	public static final float DA_LINEAR_CONNECTION_THICKNESS_HL = 5f;

	/**
	 * Specify how many contact points a DA object should return. value must be a multiple of 4 (4, 8, 12,
	 * 16,...)
	 */
	public static final int DA_OBJ_NUM_CONTACT_POINTS = 48;

	public HyperbolicRenderStyle(IViewFrustum viewFrustum) {
		super(viewFrustum);
	}

}
