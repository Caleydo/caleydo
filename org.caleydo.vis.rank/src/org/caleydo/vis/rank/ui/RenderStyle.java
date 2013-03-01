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

}
