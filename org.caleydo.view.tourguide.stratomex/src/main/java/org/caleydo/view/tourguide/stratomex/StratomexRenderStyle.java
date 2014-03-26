/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex;



import java.net.URL;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.StyledColor;

public class StratomexRenderStyle {
	private static final Color STRATOMEX_HIT_GROUP = new StyledColor(SelectionType.SELECTION.getColor())
			.setLineWidth(5)
			.setDashing(2, 0xAAAA);
	private static final Color STRATOMEX_HIT_BAND = new StyledColor(SelectionType.SELECTION.getColor()).setLineWidth(5)
			.setDashing(2,
			0xAAAA);

	public static final URL ICON_UNDO = icon("arrow_undo.png");
	public static final URL ICON_UNDO_DIASBLED = icon("arrow_undo_disabled.png");
	public static final URL ICON_ADD_INDEPENDENT = icon("add_independent.png");
	public static final URL ICON_ADD_DEPENDENT = icon("add_dependent.png");
	public static final URL ICON_CANCEL = icon("cancel.png");
	public static final URL ICON_ACCEPT = icon("accept.png");
	public static final URL ICON_ACCEPT_DISABLED = icon("accept_disabled.png");
	public static final URL ICON_STRATOMEX = icon("icon.png");

	public static Color stratomexHitGroup() {
		return STRATOMEX_HIT_GROUP;
	}

	private static final URL icon(String icon) {
		return StratomexRenderStyle.class.getResource("/org/caleydo/view/tourguide/stratomex/icons/" + icon);
	}
	public static Color stratomexHitBand() {
		return STRATOMEX_HIT_BAND;
	}

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
