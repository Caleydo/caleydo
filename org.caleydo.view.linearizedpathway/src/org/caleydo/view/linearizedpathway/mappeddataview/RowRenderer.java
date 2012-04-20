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

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.perspective.ADataPerspective;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.datadomain.genetic.GeneticDataDomain;

/**
 * @author Alexander Lex
 * 
 */
public class RowRenderer extends LayoutRenderer {

	Integer geneID;
	DataContainer dataContainer;
	ADataPerspective<?, ?, ?, ?> experimentPerspective;
	GeneticDataDomain dataDomain;

	/**
	 * 
	 */
	public RowRenderer(Integer geneID, GeneticDataDomain dataDomain,
			DataContainer dataContainer,
			ADataPerspective<?, ?, ?, ?> experimentPerspective) {
		this.geneID = geneID;
		this.dataDomain = dataDomain;
		this.dataContainer = dataContainer;
		this.experimentPerspective = experimentPerspective;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(GL2 gl) {
		float xIncrement = x / experimentPerspective.getVirtualArray().size();

		int experimentCount = 0;

		float z = 0.05f;

		// GLHelperFunctions.drawPointAt(gl, 0, 0, 1);

		float[] barColor = { 43f / 255f, 140f / 255, 190f / 255, 1f };
		float[] noMappingColor = { 166f / 255f, 189f / 255, 219f / 255, 1f };
		for (Integer experimentID : experimentPerspective.getVirtualArray()) {
			float value;
			if (geneID == null) {
				value = 1;
				gl.glColor4fv(noMappingColor, 0);
			} else {
				value = dataDomain.getGeneValue(DataRepresentation.NORMALIZED, geneID,
						experimentID);
				gl.glColor4fv(barColor, 0);

				float leftEdge = xIncrement * experimentCount;
				float upperEdge = value * y;

				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3f(leftEdge, 0, z);
				gl.glVertex3f(leftEdge, upperEdge, z);
				gl.glVertex3f(leftEdge + xIncrement, upperEdge, z);

				gl.glVertex3f(leftEdge + xIncrement, 0, z);

				gl.glEnd();
			}
			experimentCount++;

		}

	}
}
