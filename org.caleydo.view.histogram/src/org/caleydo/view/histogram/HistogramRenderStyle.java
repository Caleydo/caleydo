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
package org.caleydo.view.histogram;

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Histogram render styles
 * 
 * @author Alexander Lex
 */

public class HistogramRenderStyle extends GeneralRenderStyle {

	/** Spacing of the Histogram in pixel */
	public static final int SIDE_SPACING = 15;
	public static final int SIDE_SPACING_DETAIL_LOW = 5;

	public static final float SPREAD_CAPTION_THRESHOLD = 0.03f;

	public static final float CAPTION_SPACING = 0.01f;

	public HistogramRenderStyle(GLHistogram histogram, ViewFrustum viewFrustum) {

		super(viewFrustum);

	}

}
