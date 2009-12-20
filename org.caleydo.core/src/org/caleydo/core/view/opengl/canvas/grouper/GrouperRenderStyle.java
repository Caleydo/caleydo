package org.caleydo.core.view.opengl.canvas.grouper;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Histogram render styles
 * 
 * @author Alexander Lex
 */

public class GrouperRenderStyle
	extends GeneralRenderStyle {
	
	public static final float GUI_ELEMENT_MIN_SIZE = 50.0f;
	public static final float ELEMENT_LEFT_SPACING = 0.3f;
	public static final float ELEMENT_TOP_SPACING = 0.2f;
	public static final float ELEMENT_BOTTOM_SPACING = 0.2f;
	
	public static final float TEXT_SCALING = 0.02f;
	public static final float TEXT_SPACING = 0.02f;
	public static final float[] TEXT_COLOR = { 0.0f, 0.0f, 0.0f, 1f };

	public GrouperRenderStyle(GLGrouper histogram, IViewFrustum viewFrustum) {

		super(viewFrustum);

	}

}
