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
package org.caleydo.view.stratomex.brick.ui;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

/**
 * Renders the dimension bar, which indicates, which dimensions are currently
 * shown within the brick.
 * 
 * @author Christian Partl
 * 
 */
public class DimensionBarRenderer extends LayoutRenderer {

	private DimensionVirtualArray overallDimensionVA;
	private DimensionVirtualArray dimensionVA;

	/**
	 * 
	 * @param overallDimensionVA
	 *            The va for the whole data-set
	 * @param dimensionVA
	 *            The va for this dimension group
	 */
	public DimensionBarRenderer(DimensionVirtualArray overallDimensionVA,
			DimensionVirtualArray dimensionVA) {
		this.overallDimensionVA = overallDimensionVA;
		this.dimensionVA = dimensionVA;
	}

	@Override
	public void renderContent(GL2 gl) {

		// DimensionVirtualArray overallDimensionVA = brick.getDataDomain()
		// .getDimensionVA(Set.STORAGE);
		// DimensionVirtualArray dimensionVA = brick.getDimensionVA();

		if (overallDimensionVA == null || dimensionVA == null)
			return;

		int totalNumDimensions = overallDimensionVA.size();

		float elementWidth = x / (float) totalNumDimensions;

		for (int i = 0; i < totalNumDimensions; i++) {
			float[] baseColor;
			float colorOffset;
			if (dimensionVA.contains(overallDimensionVA.get(i))) {
				baseColor = new float[] { 0.6f, 0.6f, 0.6f, 1f };
				colorOffset = -0.25f;
			} else {
				baseColor = new float[] { 0.3f, 0.3f, 0.3f, 1f };
				colorOffset = 0.25f;
			}
			gl.glBegin(GL2.GL_QUADS);
			gl.glColor3f(baseColor[0] + colorOffset, baseColor[1] + colorOffset,
					baseColor[2] + colorOffset);
			gl.glVertex3f(i * elementWidth, 0, 0);
			gl.glColor3f(baseColor[0], baseColor[1], baseColor[2]);
			gl.glVertex3f((i + 1) * elementWidth, 0, 0);
			gl.glColor3f(baseColor[0] - colorOffset, baseColor[1] - colorOffset,
					baseColor[2] - colorOffset);
			gl.glVertex3f((i + 1) * elementWidth, y, 0);
			gl.glColor3f(baseColor[0], baseColor[1], baseColor[2]);
			gl.glVertex3f(i * elementWidth, y, 0);
			gl.glEnd();

		}

		// for (int i = 0; i < totalNumDimensions; i++) {
		// float[] baseColor;
		// if (dimensionVA.contains(overallDimensionVA.get(i))) {
		// baseColor = SelectionType.SELECTION.getColor();
		// } else {
		// baseColor = new float[] { 0.3f, 0.3f, 0.3f, 1f };
		// }
		// gl.glBegin(GL2.GL_QUADS);
		// gl.glColor3f(baseColor[0] - 0.2f, baseColor[1] - 0.2f,
		// baseColor[2] - 0.2f);
		// gl.glVertex3f(i * elementWidth, 0, 0);
		// gl.glColor3f(baseColor[0], baseColor[1], baseColor[2]);
		// gl.glVertex3f((i + 1) * elementWidth, 0, 0);
		// gl.glColor3f(baseColor[0] + 0.2f, baseColor[1] + 0.2f,
		// baseColor[2] + 0.2f);
		// gl.glVertex3f((i + 1) * elementWidth, y, 0);
		// gl.glColor3f(baseColor[0], baseColor[1], baseColor[2]);
		// gl.glVertex3f(i * elementWidth, y, 0);
		// gl.glEnd();
		// }

		gl.glLineWidth(1);
		gl.glColor3f(0.1f, 0.1f, 0.1f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();

	}

	
	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}
}
