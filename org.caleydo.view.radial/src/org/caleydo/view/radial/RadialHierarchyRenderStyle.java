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
package org.caleydo.view.radial;

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Radial render styles
 */

public class RadialHierarchyRenderStyle extends GeneralRenderStyle {

	public static float[] PARTIAL_DISC_BORDER_COLOR = { 1, 1, 1, 1 };
	public static float[] PARTIAL_DISC_ROOT_COLOR = { 1, 1, 1, 1 };
	public static float[] PARTIAL_DISC_MOUSE_OVER_COLOR = { 0.6f, 0.8f, 0.8f, 1.0f };
	public static float PARTIAL_DISC_BORDER_WIDTH = 2;
	public static float PARTIAL_DISC_TRANSPARENCY = 0.5f;
	public static float PARTIAL_DISC_MIN_DISPLAYED_ANGLE = 1.0f;
	public static int NUM_SLICES_PER_FULL_DISC = 80;

	public static float[] CHILD_INDICATOR_COLOR = { 0.3f, 0.3f, 0.3f, 1.0f };
	public static final float TRIANGLE_HEIGHT_PERCENTAGE = 0.03f;
	public static final float TRIANGLE_TOP_RADIUS_PERCENTAGE = 1.025f;

	public static final float MIN_DETAIL_SCREEN_PERCENTAGE = 0.4f;
	public static final float MAX_DETAIL_SCREEN_PERCENTAGE = 0.6f;
	public static final float USED_SCREEN_PERCENTAGE = 0.9f;
	public static final float DETAIL_RADIUS_DELTA_SCREEN_PERCENTAGE = 0.1f;
	public static final int MIN_DISPLAYED_DETAIL_DEPTH = 2;

	public static final int MAX_LABELING_DEPTH = 5;
	public static final float[] LABEL_TEXT_COLOR = { 0.2f, 0.2f, 0.2f, 1f };
	public static final float[] LABEL_BACKGROUND_COLOR = { 1f, 1f, 1f, 0.6f };
	public static final int LABEL_TEXT_MIN_SIZE = 60;

	public RadialHierarchyRenderStyle(ViewFrustum viewFrustum) {
		super(viewFrustum);
	}

}
