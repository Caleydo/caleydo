/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal;



import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.vis.rank.ui.RenderStyle;

public class TourGuideRenderStyle extends GeneralRenderStyle {

	public TourGuideRenderStyle(ViewFrustum viewFrustum) {
		super(viewFrustum);
	}

	private static final String ICON_PREFIX = "resources/icons/view/tourguide/";

	public static final Color STRATOMEX_FOUND_GROUP = SelectionType.SELECTION.getColor();

	public static final Color COLOR_SELECTED_ROW = SelectionType.SELECTION.getColor();
	public static final Color COLOR_STRATOMEX_ROW = RenderStyle.COLOR_SELECTED_ROW;

	public static final String ICON_FILTER = ICON_PREFIX + "filter.png";
	public static final String ICON_FILTER_DISABLED = ICON_PREFIX + "filter_disabled.png";
	public static final String ICON_ADD = ICON_PREFIX + "add.png";
	public static final String ICON_ADD_COLOR = ICON_PREFIX + "add_color.png";
	public static final String ICON_BASKET = ICON_PREFIX + "basket.png";
}
