/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal;



import java.net.URL;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.StyledColor;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.vis.lineup.ui.RenderStyle;

public class TourGuideRenderStyle extends GeneralRenderStyle {

	public TourGuideRenderStyle(ViewFrustum viewFrustum) {
		super(viewFrustum);
	}

	private static final URL icon(String icon) {
		return TourGuideRenderStyle.class.getResource("/org/caleydo/view/tourguide/icons/" + icon);
	}

	public static final URL ICON_FILTER = icon("filter.png");
	public static final URL ICON_FILTER_DISABLED = icon("filter_disabled.png");
	public static final URL ICON_ADD = icon("add.png");
	public static final URL ICON_ADD_COLOR = icon("add_color.png");
	public static final URL ICON_BASKET = icon("basket.png");
	public static final URL ICON_EXTERNAL = icon("external.png");

	public static final Color COLOR_STRATOMEX_ROW = RenderStyle.COLOR_SELECTED_ROW;

	public static final StyledColor COLOR_PREVIEW_BORDER_ROW = new StyledColor(SelectionType.SELECTION.getColor())
			.setDashing(2, 0xAAAA);

	public static Color colorSelectedRow() {
		Color r = new Color(1.f, 1.f, 1.f, 1.f);
		Color c = SelectionType.SELECTION.getColor();
		Color b = SelectionType.NORMAL.getColor();
		// as we mix the colors by overplotting normal with selected simulate that
		r.r = b.r * 0.15f + r.r * 0.85f;
		r.g = b.g * 0.15f + r.g * 0.85f;
		r.b = b.b * 0.15f + r.b * 0.85f;

		r.r = c.r * 0.5f + r.r * 0.5f;
		r.g = c.g * 0.5f + r.g * 0.5f;
		r.b = c.b * 0.5f + r.b * 0.5f;
		r.a = 1.0f;
		return r;
	}
}
