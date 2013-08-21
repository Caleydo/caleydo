/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui;



import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.picking.AdvancedPick;
import org.caleydo.core.view.opengl.picking.Pick;


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

	private static final String ICON_PREFIX = "resources/icons/vis/rank/";
	public static final String ICON_TRASH = ICON_PREFIX + "user_trash_full.png";
	public static final String ICON_CIRCLE = ICON_PREFIX + "circle.png";
	public static final String ICON_ARROW = ICON_PREFIX + "arrow.png";
	public static final String ICON_FILTER_DISABLED = ICON_PREFIX + "filter_disabled.png";
	public static final String ICON_FILTER = ICON_PREFIX + "filter.png";
	public static final String ICON_MAPPING = ICON_PREFIX + "filter.png";
	public static final String ICON_EXPLODE = ICON_PREFIX + "dynamite.png";
	public static final String ICON_EDIT_ANNOTATION = ICON_PREFIX + "note_edit.png";
	public static final String ICON_COLLAPSE = ICON_PREFIX + "bullet_toggle_plus.png";
	public static final String ICON_UNCOLLAPSE = ICON_PREFIX + "bullet_toggle_minus.png";
	public static final String ICON_COMPRESS = ICON_PREFIX + "bullet_arrow_left.png";
	public static final String ICON_UNCOMPRESS = ICON_PREFIX + "bullet_arrow_right.png";

	public static final String ICON_SMALL_HEADER_ON = ICON_PREFIX + "bullet_arrow_up.png";
	public static final String ICON_SMALL_HEADER_OFF = ICON_PREFIX + "bullet_arrow_down.png";

	public static final String ICON_HIDE = ICON_PREFIX + "delete.png";
	public static final String ICON_DRAG = ICON_PREFIX + "drag.png";
	public static final String ICON_ALIGN_CLASSIC = ICON_PREFIX + "align_classic.png";
	public static final String ICON_ALIGN_STACKED = ICON_PREFIX + "align_stacked.png";
	public static final String ICON_ALIGN_STACKED_ORDERED = ICON_PREFIX + "align_ordered.png";

	public static final String ICON_SHOW_RANK_DELTA = ICON_PREFIX + "chart_down_color.png";
	public static final String ICON_HIDE_RANK_DELTA = ICON_PREFIX + "chart_down_color_disabled.png";

	public static final String ICON_ALIGN_FISH = ICON_PREFIX + "align_fish.jpg";
	public static final String ICON_ALIGN_UNIFORM = ICON_PREFIX + "align_uniform.png";

	public static final String ICON_FREEZE = ICON_PREFIX + "camera_add.png";

	public static final String ICON_MAPPING_CROSS_LEFT = ICON_PREFIX + "cross_left.png";
	public static final String ICON_MAPPING_CROSS_RIGHT = ICON_PREFIX + "cross_right.png";
	public static final String ICON_MAPPING_PAR_HOR = ICON_PREFIX + "par_hor.png";
	public static final String ICON_MAPPING_PAR_VERT = ICON_PREFIX + "par_vert.png";
	public static final String ICON_MAPPING_RESET = ICON_PREFIX + "arrow_undo.png";

	public static final String ICON_STAR = ICON_PREFIX + "star.png";
	public static final String ICON_STAR_DISABLED = ICON_PREFIX + "star_disabled.png";
	public static final String ICON_FIND = ICON_PREFIX + "find.png";

	public static final String ICON_ADD_SEPARATOR = ICON_PREFIX + "table_relationship.png";
	public static final String ICON_ADD_STACKED = ICON_PREFIX + "table_sum_add.png";
	public static final String ICON_ADD_NESTED = ICON_PREFIX + "table_nested_add.png";
	public static final String ICON_ADD_SCRIPTED = ICON_PREFIX + "table_scripted_add.png";

	public static final String ICON_COMPLEX_MAPPING = ICON_PREFIX + "hatching.png";

	public static final String ICON_SORT_BY_WEIGHT = ICON_PREFIX + "sortByWeight.png";
	public static final String ICON_EDIT_CODE = ICON_PREFIX + "script_js_edit.png";
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
	 * @param pick
	 * @return
	 */
	public static boolean isCloneDragging(Pick pick) {
		if (pick instanceof AdvancedPick)
			return ((AdvancedPick) pick).isCtrlDown();
		return false;
	}
}
