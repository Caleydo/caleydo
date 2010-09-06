package org.caleydo.core.view.opengl.renderstyle;

import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Render styles for the info area
 * 
 * @author Alexander Lex
 */

public class InfoAreaRenderStyle
	extends GeneralRenderStyle {

	public static final float[] INFO_AREA_BORDER_COLOR = { 0.1f, 0.1f, 0.1f, 1 };

	public static final float INFO_AREA_BORDER_WIDTH = 3;

	public static final float[] INFO_AREA_COLOR = { 0.8f, 0.8f, 0.8f, 1f };

	private static final float SPACING = 0.005f;

	public InfoAreaRenderStyle(ViewFrustum viewFrustum) {

		super(viewFrustum);
	}

	public float getSpacing() {

		return SPACING * getScaling();
	}

}
