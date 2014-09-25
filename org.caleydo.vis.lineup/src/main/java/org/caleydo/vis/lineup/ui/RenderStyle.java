/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui;



import java.net.URL;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.dnd.EDnDType;
import org.caleydo.core.view.opengl.layout2.dnd.IDnDItem;


/**
 * @author Samuel Gratzl
 *
 */
public class RenderStyle {

	/**
	 * returns the to use bins for a histogram of the given width
	 *
	 * @param w
	 *            the target pixel width
	 * @return either the number of desired bins or -1 to trigger that the second version
	 *         {@link #binsForWidth(float, int)} will be evaluated (which needs more time)
	 */
	public static int binsForWidth(float w) {
		if (w < 20)
			return Math.round(w / 5);
		return -1;

	}

	/**
	 * returns the to use bins for a histogram of the given width
	 *
	 * @param w
	 *            the target pixel width
	 * @param dataSize
	 *            the number of items
	 * @return
	 */
	public static int binsForWidth(float w, int dataSize) {
		if (w < 20)
			return Math.round(w / 5);
		else {
			int optimal = Math.round((float) Math.sqrt(dataSize));
			while (optimal * 5 > w) { // not enough space for optimal
				optimal /= 2;
			}
			if (optimal <= 0) {
				optimal = Math.round(w / 5);
			}
			return optimal;
		}

	}

	private static final URL icon(String icon) {
		return RenderStyle.class.getResource("/org/caleydo/vis/lineup/icons/" + icon);
	}

	public static final URL ICON_TRASH = icon("user_trash_full.png");
	public static final URL ICON_CIRCLE = icon("circle.png");
	public static final URL ICON_ARROW = icon("arrow.png");
	public static final URL ICON_FILTER_DISABLED = icon("filter_disabled.png");
	public static final URL ICON_FILTER = icon("filter.png");
	public static final URL ICON_MAPPING = icon("filter.png");
	public static final URL ICON_EXPLODE = icon("dynamite.png");
	public static final URL ICON_EDIT_ANNOTATION = icon("note_edit.png");
	public static final URL ICON_COLLAPSE = icon("bullet_toggle_plus.png");
	public static final URL ICON_UNCOLLAPSE = icon("bullet_toggle_minus.png");
	public static final URL ICON_COMPRESS = icon("bullet_arrow_left.png");
	public static final URL ICON_UNCOMPRESS = icon("bullet_arrow_right.png");

	public static final URL ICON_SMALL_HEADER_ON = icon("bullet_arrow_up.png");
	public static final URL ICON_SMALL_HEADER_OFF = icon("bullet_arrow_down.png");

	public static final URL ICON_HIDE = icon("delete.png");
	public static final URL ICON_DRAG = icon("drag.png");
	public static final URL ICON_ALIGN_CLASSIC = icon("align_classic.png");
	public static final URL ICON_ALIGN_STACKED = icon("align_stacked.png");
	public static final URL ICON_ALIGN_STACKED_ORDERED = icon("align_ordered.png");

	public static final URL ICON_SHOW_RANK_DELTA = icon("chart_down_color.png");
	public static final URL ICON_HIDE_RANK_DELTA = icon("chart_down_color_disabled.png");
	public static final URL ICON_ADD_RANK_DELTA = icon("chart_down_color_add.png");

	public static final URL ICON_ALIGN_FISH = icon("align_fish.jpg");
	public static final URL ICON_ALIGN_UNIFORM = icon("align_uniform.png");

	public static final URL ICON_FREEZE = icon("camera_add.png");

	public static final URL ICON_MAPPING_CROSS_LEFT = icon("cross_left.png");
	public static final URL ICON_MAPPING_CROSS_RIGHT = icon("cross_right.png");
	public static final URL ICON_MAPPING_PAR_HOR = icon("par_hor.png");
	public static final URL ICON_MAPPING_PAR_VERT = icon("par_vert.png");
	public static final URL ICON_MAPPING_RESET = icon("arrow_undo.png");

	public static final URL ICON_STAR = icon("star.png");
	public static final URL ICON_STAR_DISABLED = icon("star_disabled.png");
	public static final URL ICON_FIND = icon("find.png");

	public static final URL ICON_ADD_SEPARATOR = icon("table_relationship.png");
	public static final URL ICON_ADD_STACKED = icon("table_sum_add.png");
	public static final URL ICON_ADD_NESTED = icon("table_nested_add.png");
	public static final URL ICON_ADD_SCRIPTED = icon("table_scripted_add.png");

	public static final URL ICON_COMPLEX_MAPPING = icon("hatching.png");

	public static final URL ICON_SORT_BY_WEIGHT = icon("sortByWeight.png");
	public static final URL ICON_EDIT_CODE = icon("script_js_edit.png");
	/**
	 * space between columns
	 */
	public static final float COLUMN_SPACE = 1;

	/**
	 * height of a hist
	 */
	public static final float HIST_HEIGHT = 40;
	/**
	 * height of the label above the hist
	 */
	public static final float LABEL_HEIGHT = 21;

	public static final Color COLOR_SELECTED_ROW = new Color(180, 180, 180);
	public static final Color COLOR_SELECTED_BORDER = new Color(128, 128, 128);
	public static final Color COLOR_BACKGROUND_EVEN = new Color(240, 240, 240);
	public static final Color COLOR_BAND = new Color(0.90f, 0.90f, 0.90f);
	public static final Color COLOR_ALIGN_MARKER = Color.DARK_GRAY;
	public static final Color ORDER_BY_COLOR = Color.BLACK;

	public static final float BUTTON_WIDTH = 16;
	public static final Color COLOR_STACKED_BORDER = new Color(0.85f, .85f, .85f);
	public static final float COLOR_STACKED_BORDER_WIDTH = 1;
	public static final float HEADER_ROUNDED_RADIUS_X = 9;
	public static final float HEADER_ROUNDED_RADIUS_Y = 14;
	public static final float SCROLLBAR_WIDTH = 8;
	public static final float SEPARATOR_PICK_WIDTH = 11;
	public static final int STACKED_COLUMN_PADDING = 5;
	public static final int GROUP_COLUMN_PADDING = 5;


	/**
	 * the duration of the highlight animation
	 *
	 * @param delta
	 *            the number of ranks the item moved up, {@link Integer#MIN_VALUE} if it was not visible before
	 * @return
	 */
	public static int hightlightAnimationDuration(int delta) {
		return delta == Integer.MIN_VALUE ? 2000 : Math.min(Math.abs(delta) * 2000, 2000);
	}

	/**
	 * computes the alpha value to use
	 *
	 * @param alpha
	 *            the animation alpha value
	 * @param delta
	 *            the rank delta of this animation
	 * @return
	 */
	public static float computeHighlightAlpha(float alpha, int delta) {
		return (1 - alpha) * 0.5f;
	}

	/**
	 * are we dragging for cloning
	 *
	 * @param input
	 * @return
	 */
	public static boolean isCloneDragging(IDnDItem input) {
		return input.getType() == EDnDType.COPY;
	}
}
