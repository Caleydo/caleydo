/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram;

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Histogram render styles
 * 
 * @author Alexander Lex
 */

public class HistogramRenderStyle extends GeneralRenderStyle {

	/** Spacing of the Histogram in pixel */
	public static final int SIDE_SPACING = 15;
	public static final int SIDE_SPACING_DETAIL_LOW = 5;

	public static final float SPREAD_CAPTION_THRESHOLD = 0.03f;

	public static final float CAPTION_SPACING = 0.01f;

	public HistogramRenderStyle(GLHistogram histogram, ViewFrustum viewFrustum) {

		super(viewFrustum);

	}

}
