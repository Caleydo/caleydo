/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.bookmark;

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

class BookmarkRenderStyle extends GeneralRenderStyle {

	// private static final float LIST_SELECTED_FIELD_WIDTH = 0.3f;

	// public static final float LIST_SPACING = 0.05f;

	public static final int TEXT_MIN_SIZE = 50;

	public static final float SIDE_SPACING = 0.05f;
	public static final float TOP_SPACING = 0.1f;
	public static final float CONTAINER_HEADING_SIZE = 0.08f;
	/** The spacing between a highlighting frame and the highlighted element **/
	public static final float FRAME_SPACING = 0.01f;

	public static final int NUMBER_OF_LIST_ITEMS_PER_PAGE = 30;

	public BookmarkRenderStyle(ViewFrustum viewFrustum) {
		super(viewFrustum);
		// TODO Auto-generated constructor stub
	}

}
