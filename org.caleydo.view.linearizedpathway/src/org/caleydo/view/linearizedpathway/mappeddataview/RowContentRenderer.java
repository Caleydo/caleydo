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

import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.container.Average;
import org.caleydo.core.data.container.ContainerStatistics;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.perspective.ADataPerspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.datadomain.genetic.GeneticDataDomain;

/**
 * @author Alexander Lex
 * 
 */
public class RowContentRenderer extends SelectableRenderer {

	Integer geneID;
	DataContainer dataContainer;
	ADataPerspective<?, ?, ?, ?> experimentPerspective;
	GeneticDataDomain dataDomain;
	Integer davidID;
	float z = 0.05f;
	Average average;

	public RowContentRenderer(Integer geneID, Integer davidID,
			GeneticDataDomain dataDomain, DataContainer dataContainer,
			ADataPerspective<?, ?, ?, ?> experimentPerspective, AGLView parentView,
			MappedDataRenderer parent) {
		super(parentView, parent);
		this.davidID = davidID;
		this.geneID = geneID;

		topBarColor = MappedDataRenderer.BAR_COLOR;
		bottomBarColor = topBarColor;
		this.dataDomain = dataDomain;
		this.dataContainer = dataContainer;
		this.experimentPerspective = experimentPerspective;
		init();
	}

	public void init() {
		average = ContainerStatistics.calculateAverage(
				experimentPerspective.getVirtualArray(), dataDomain.getTable(), geneID);

	}

	@Override
	public void render(GL2 gl) {
		ArrayList<SelectionType> geneSelectionTypes = parent.geneSelectionManager
				.getSelectionTypes(davidID);

		calculateColors(geneSelectionTypes);
		renderAllBars(gl);
//		renderAverageBar(gl);

	}

	public void renderAverageBar(GL2 gl) {

		// gl.glPushName(parentView.getPickingManager().getPickingID(
		// parentView.getID(), PickingType.GENE.name(), davidID));
		// gl.glPushName(parentView.getPickingManager().getPickingID(
		// parentView.getID(), PickingType.SAMPLE.name(), experimentID));
		gl.glColor4fv(topBarColor, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, y/3, z);
		gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);
		gl.glVertex3d(average.getArithmeticMean() * x, y/3, z);
		gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f,
				bottomBarColor[2] * 0.9f);

		gl.glVertex3d(average.getArithmeticMean() * x, y/3*2, z);
		gl.glColor4fv(bottomBarColor, 0);

		gl.glVertex3f(0, y/3*2, z);

		gl.glEnd();
		// gl.glPopName();
		// gl.glPopName();

	}

	private void renderAllBars(GL2 gl) {
		float xIncrement = x / experimentPerspective.getVirtualArray().size();
		int experimentCount = 0;

		float[] tempTopBarColor = topBarColor;
		float[] tempBottomBarColor = bottomBarColor;

		for (Integer experimentID : experimentPerspective.getVirtualArray()) {

			float value;
			if (geneID != null) {
				value = dataDomain.getGeneValue(DataRepresentation.NORMALIZED, geneID,
						experimentID);
				ArrayList<SelectionType> experimentSelectionTypes = parent.sampleSelectionManager
						.getSelectionTypes(experimentID);
				calculateColors(experimentSelectionTypes);

				float leftEdge = xIncrement * experimentCount;
				float upperEdge = value * y;

				gl.glPushName(parentView.getPickingManager().getPickingID(
						parentView.getID(), PickingType.GENE.name(), davidID));
				gl.glPushName(parentView.getPickingManager().getPickingID(
						parentView.getID(), PickingType.SAMPLE.name(), experimentID));
				gl.glColor4fv(topBarColor, 0);
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3f(leftEdge, 0, z);
				gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f,
						topBarColor[2] * 0.9f);
				gl.glVertex3f(leftEdge + xIncrement, 0, z);
				gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f,
						bottomBarColor[2] * 0.9f);

				gl.glVertex3f(leftEdge + xIncrement, upperEdge, z);
				gl.glColor4fv(bottomBarColor, 0);

				gl.glVertex3f(leftEdge, upperEdge, z);

				gl.glEnd();
				gl.glPopName();
				gl.glPopName();
				experimentCount++;
				topBarColor = tempTopBarColor;
				bottomBarColor = tempBottomBarColor;
			}

		}
	}

}
