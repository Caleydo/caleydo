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
package org.caleydo.core.view.opengl.renderstyle;

import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Render styles for the info area
 * 
 * @author Alexander Lex
 */

public class InfoAreaRenderStyle
	extends GeneralRenderStyle {

	public static final float[] INFO_AREA_BORDER_COLOR = { 0.1f, 0.1f, 0.1f, 1 };

	public static final float INFO_AREA_BORDER_WIDTH = 3;

	public static final float[] INFO_AREA_COLOR = { 0.8f, 0.8f, 0.8f, 1f };

	private static final float SPACING = 0.005f;

	public InfoAreaRenderStyle(ViewFrustum viewFrustum) {

		super(viewFrustum);
	}

	public float getSpacing() {

		return SPACING * getScaling();
	}

}
