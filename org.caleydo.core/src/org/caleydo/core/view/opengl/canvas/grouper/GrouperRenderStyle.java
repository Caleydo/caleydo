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

	public static final float SIDE_SPACING = 0.1f;

	public static final float SPREAD_CAPTION_THRESHOLD = 0.03f;

	public static final float CAPTION_SPACING = 0.01f;

	public GrouperRenderStyle(GLGrouper histogram, IViewFrustum viewFrustum) {

		super(viewFrustum);

	}

}
