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
package org.caleydo.view.linearizedpathway.mappeddataview;

import javax.media.opengl.GL2;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;

/**
 * @author alexsb
 * 
 */
public class RelationshipRenderer {

	float[] topLeft = new float[3];
	float[] bottomLeft = new float[3];
	float[] topRight = new float[3];
	float[] bottomRight = new float[3];
	float[] color;

	ElementLayout topRightLayout;
	ElementLayout bottomRightLayout;

	/**
	 * 
	 */
	public RelationshipRenderer(float[] color) {
		this.color = color;
	}

	public void render(GL2 gl, ConnectionBandRenderer renderer) {

		topRight[0] = topRightLayout.getTranslateX();
		topRight[1] = topRightLayout.getTranslateY() + topRightLayout.getSizeScaledY();

		bottomRight[0] = bottomRightLayout.getTranslateX();
		bottomRight[1] = bottomRightLayout.getTranslateY();

		renderer.renderSingleBand(gl, topLeft, bottomLeft, topRight, bottomRight, false,
				0.1f, 0, color);
	}
}
