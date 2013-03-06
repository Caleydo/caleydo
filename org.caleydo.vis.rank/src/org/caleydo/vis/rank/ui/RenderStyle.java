/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.vis.rank.ui;

import java.awt.Color;


/**
 * @author Samuel Gratzl
 *
 */
public class RenderStyle {

	/**
	 * returns the to use bins for a histogram of the given width
	 *
	 * @param w
	 * @return
	 */
	public static int binsForWidth(float w) {
		return Math.round(w * 0.25f);
	}

	private static final String ICON_PREFIX = "resources/icons/";
	public static final String ICON_TRASH = ICON_PREFIX + "user_trash_full.png";
	public static final String ICON_CIRCLE = ICON_PREFIX + "circle.png";
	public static final String ICON_ARROW = ICON_PREFIX + "arrow.png";
	public static final String ICON_COLLAPSE = ICON_PREFIX + "bullet_toggle_plus.png";
	public static final String ICON_FILTER_DISABLED = ICON_PREFIX + "filter_disabled.png";
	public static final String ICON_FILTER = ICON_PREFIX + "filter.png";
	public static final String ICON_MAPPING = ICON_PREFIX + "pencil_ruler.png";
	public static final String ICON_EXPLODE = ICON_PREFIX + "dynamite.png";
	public static final String ICON_UNCOLLAPSE = ICON_PREFIX + "bullet_toggle_minus.png";
	public static final String ICON_HIDE = ICON_PREFIX + "delete.png";
	public static final String ICON_DRAG = ICON_PREFIX + "drag.png";
	public static final String ICON_ALIGN_CLASSIC = ICON_PREFIX + "align_classic.png";
	public static final String ICON_ALIGN_STACKED = ICON_PREFIX + "align_stacked.png";
	public static final String ICON_FREEZE = ICON_PREFIX + "camera_add.png";

	public static final String ICON_MAPPING_CROSS_LEFT = ICON_PREFIX + "cross_left.png";
	public static final String ICON_MAPPING_CROSS_RIGHT = ICON_PREFIX + "cross_right.png";
	public static final String ICON_MAPPING_PAR_HOR = ICON_PREFIX + "par_hor.png";
	public static final String ICON_MAPPING_PAR_VERT = ICON_PREFIX + "par_vert.png";
	public static final String ICON_MAPPING_RESET = ICON_PREFIX + "arrow_undo.png";
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
	public static final float LABEL_HEIGHT = 20;

	public static final Color COLOR_SELECTED_ROW = new Color(0, 0, 0, 128);
	public static final Color COLOR_BAND = new Color(0.90f, 0.90f, 0.90f);

	public static final float FROZEN_BAND_WIDTH = 50;
	public static final float BUTTON_WIDTH = 16;

	/**
	 * the duration of the hightligh animation
	 *
	 * @param delta
	 * @return
	 */
	public static int hightlightAnimationDuration(int delta) {
		return delta == Integer.MIN_VALUE ? 2000 : Math.min(Math.abs(delta) * 400, 2000);
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

}