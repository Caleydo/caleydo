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
