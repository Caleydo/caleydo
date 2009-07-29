package org.caleydo.core.view.opengl.canvas.hyperbolic;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Hyperbolic render styles
 */

public class HyperbolicRenderStyle
	extends GeneralRenderStyle {
	
	public static final float X_BORDER_SPACING = 0.7f;
	public static final float Y_BORDER_SPACING = 0.7f;
	
	public static final float SIDE_SPACING = 0.1f;
	
	public static final float[] DA_OBJ_FALLBACK_COLORSHEME = {0.0f, 0.0f, 0.0f};
	public static final float DA_OBJ_FALLBACK_ALPHA = 1;

	/**
	 * Specify how many contact points a DA object should return.
	 * value must be a multiple of 4 (4, 8, 12, 16,...)
	 */
	public static final int DA_OBJ_NUM_CONTACT_POINTS = 16;
	public HyperbolicRenderStyle(IViewFrustum viewFrustum) {
		super(viewFrustum);
		// TODO Auto-generated constructor stub
	}

}
