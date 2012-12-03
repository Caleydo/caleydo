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
package org.caleydo.view.tourguide.renderstyle;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.Colors;
import org.caleydo.core.util.color.IColor;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.layout.Padding;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

public class TourGuideRenderStyle extends GeneralRenderStyle {

	public TourGuideRenderStyle(ViewFrustum viewFrustum) {
		super(viewFrustum);
	}

	public static final int COLX_SCORE_WIDTH = 75;

	public static final int COL_SPACING = 3;
	public static final int ROW_SPACING = 5;
	public static final int ROW_HEIGHT = 18;

	public static final Padding LABEL_PADDING = new Padding(Padding.EMode.PIXEL, 2, 6, 2, 4);

	public static final IColor SELECTED_COLOR = Colors.YELLOW;
	public static final IColor DEFAULT_SCORE_COLOR = new Color(0, 0, 0, 0.2f);

	public static final int COL0_RANK_WIDTH = 25;
	public static final int COL2_ADD_COLUMN_X_WIDTH = 16;

	public static final int DATADOMAIN_TYPE_WIDTH = 16;
	public static final int STRATIFACTION_WIDTH = 120;
	public static final int GROUP_WIDTH = 80;
}
