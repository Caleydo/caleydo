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
package org.caleydo.view.heatmap.dendrogram;

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Dendrogram render styles
 * 
 * @author Bernhard Schlegl
 */

public class DendrogramRenderStyle extends GeneralRenderStyle {

	public static final float SIDE_SPACING = 0.1f;

	public static final float DENDROGRAM_Z = 0.001f;
	public static final float SELECTION_Z = 0.005f;
	public static final float SUB_DENDROGRAM_Z = 0.01f;
	public static final float CUT_OFF_Z = 0.01f;

	private float fWidthCutOff = 0.2f;
	private float fDendrogramLineWidth = 0.1f;
	private float fSizeDendrogramArrow = 0.17f;

	GLDendrogram<?> dendrogram;

	public DendrogramRenderStyle(GLDendrogram<?> dendrogram, ViewFrustum viewFrustum) {

		super(viewFrustum);

		this.dendrogram = dendrogram;
	}

	public float getWidthCutOff() {
		return fWidthCutOff;
	}

	public float getDendrogramLineWidth() {
		return fDendrogramLineWidth;
	}

	public float getSizeDendrogramArrow() {
		return fSizeDendrogramArrow;
	}
}
