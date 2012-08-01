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
package org.caleydo.view.enroute.mappeddataview;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.container.Average;
import org.caleydo.core.data.container.ContainerStatistics;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.perspective.ADataPerspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.genetic.GeneticDataDomain;

/**
 * @author Alexander Lex
 * 
 */
public class ContinuousContentRenderer extends ContentRenderer {

	private static Integer rendererIDCounter = 0;

	Average average;
	boolean useShading = true;
	private int rendererID;

	public ContinuousContentRenderer(Integer geneID, Integer davidID,
			GeneticDataDomain dataDomain, DataContainer dataContainer,
			ADataPerspective<?, ?, ?, ?> experimentPerspective, AGLView parentView,
			MappedDataRenderer parent, Group group) {
		super(geneID, davidID, dataDomain, dataContainer, experimentPerspective,
				parentView, parent, group);
		synchronized (rendererIDCounter) {
			rendererID = rendererIDCounter++;
		}

		topBarColor = MappedDataRenderer.BAR_COLOR;
		bottomBarColor = topBarColor;
		init();
	}

	public ContinuousContentRenderer(
			IContentRendererInitializor contentRendererInitializor) {
		super(contentRendererInitializor);
	}

	public void init() {
		if (geneID == null)
			return;
		average = ContainerStatistics.calculateAverage(
				experimentPerspective.getVirtualArray(), dataDomain.getTable(), geneID);
		
		registerPickingListener();
	}

	@Override
	public void renderContent(GL2 gl) {
		if (x / experimentPerspective.getVirtualArray().size() < parentView
				.getPixelGLConverter().getGLWidthForPixelWidth(3)) {
			useShading = false;
		}
		
		if (geneID == null)
			return;
		ArrayList<SelectionType> geneSelectionTypes = parent.geneSelectionManager
				.getSelectionTypes(davidID);

		ArrayList<SelectionType> selectionTypes = parent.sampleGroupSelectionManager
				.getSelectionTypes(group.getID());
		if (selectionTypes.size() > 0
				&& selectionTypes.contains(MappedDataRenderer.abstractGroupType)) {
			topBarColor = MappedDataRenderer.SUMMARY_BAR_COLOR;
			bottomBarColor = topBarColor;

//			ArrayList<ArrayList<SelectionType>> selectionLists = new ArrayList<ArrayList<SelectionType>>();
//			selectionLists.add(geneSelectionTypes);

//			for (Integer sampleID : experimentPerspective.getVirtualArray()) {
//				// Integer resolvedSampleID = sampleIDMappingManager.getID(
//				// dataDomain.getSampleIDType(), parent.sampleIDType,
//				// experimentID);
//
//				selectionLists.add(parent.sampleSelectionManager.getSelectionTypes(
//						sampleIDType, sampleID));
//			}

//			calculateColors(Algorithms.mergeListsToUniqueList(selectionLists));

			renderAverageBar(gl);
		} else {
			renderAllBars(gl, geneSelectionTypes);
		}

	}

	@SuppressWarnings("unchecked")
	private void renderAllBars(GL2 gl, ArrayList<SelectionType> geneSelectionTypes) {
		float xIncrement = x / experimentPerspective.getVirtualArray().size();
		int experimentCount = 0;

		float[] tempTopBarColor = topBarColor;
		float[] tempBottomBarColor = bottomBarColor;

		for (Integer sampleID : experimentPerspective.getVirtualArray()) {

			float value;
			if (geneID != null) {
				value = dataDomain.getGeneValue(DataRepresentation.NORMALIZED, geneID,
						sampleID);

				ArrayList<SelectionType> experimentSelectionTypes = parent.sampleSelectionManager
						.getSelectionTypes(sampleIDType, sampleID);

				calculateColors(Algorithms.mergeListsToUniqueList(
						experimentSelectionTypes, geneSelectionTypes));

				float leftEdge = xIncrement * experimentCount;
				float upperEdge = value * y;

				// gl.glPushName(parentView.getPickingManager().getPickingID(
				// parentView.getID(), PickingType.GENE.name(), davidID));

				Integer resolvedSampleID = sampleIDMappingManager.getID(
						dataDomain.getSampleIDType(), parent.sampleIDType, sampleID);
				if (resolvedSampleID != null) {
					gl.glPushName(parentView.getPickingManager().getPickingID(
							parentView.getID(), PickingType.SAMPLE.name(),
							resolvedSampleID));
				}

				gl.glBegin(GL2.GL_QUADS);

				gl.glColor4fv(bottomBarColor, 0);
				gl.glVertex3f(leftEdge, 0, z);
				if (useShading) {
					gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f,
							bottomBarColor[2] * 0.9f);

				}
				gl.glVertex3f(leftEdge + xIncrement, 0, z);
				if (useShading) {
					gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f,
							topBarColor[2] * 0.9f);
				} else {
					gl.glColor4fv(topBarColor, 0);
				}

				gl.glVertex3f(leftEdge + xIncrement, upperEdge, z);
				gl.glColor4fv(topBarColor, 0);

				gl.glVertex3f(leftEdge, upperEdge, z);

				gl.glEnd();
				if (resolvedSampleID != null)
					gl.glPopName();

				// gl.glPopName();
				experimentCount++;
				topBarColor = tempTopBarColor;
				bottomBarColor = tempBottomBarColor;
			}

		}
	}

	public void renderAverageBar(GL2 gl) {
		// topBarColor = MappedDataRenderer.SUMMARY_BAR_COLOR;
		// bottomBarColor = topBarColor;

		gl.glPushName(parentView.getPickingManager().getPickingID(parentView.getID(),
				PickingType.SAMPLE_GROUP_RENDERER.name(), rendererID));
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor4fv(bottomBarColor, 0);
		gl.glVertex3f(0, y / 3, z);
		gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f,
				bottomBarColor[2] * 0.9f);
		gl.glVertex3d(average.getArithmeticMean() * x, y / 3, z);
		gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);
		gl.glVertex3d(average.getArithmeticMean() * x, y / 3 * 2, z);
		gl.glColor4fv(topBarColor, 0);
		gl.glVertex3f(0, y / 3 * 2, z);
		gl.glEnd();

		gl.glColor3f(0, 0, 0);
		gl.glLineWidth(0.5f);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3f(0, y / 3, z);
		gl.glVertex3d(average.getArithmeticMean() * x, y / 3, z);
		gl.glVertex3d(average.getArithmeticMean() * x, y / 3 * 2, z);
		gl.glVertex3f(0, y / 3 * 2, z);
		gl.glEnd();

		float lineZ = z + 0.01f;

		gl.glColor3f(0, 0, 0);
		// gl.glColor3f(1 , 1, 1);

		gl.glLineWidth(0.8f);

		float xMinusDeviation = (float) (average.getArithmeticMean() - average
				.getStandardDeviation()) * x;
		float xPlusDeviation = (float) (average.getArithmeticMean() + average
				.getStandardDeviation()) * x;

		float lineTailHeight = parentView.getPixelGLConverter()
				.getGLHeightForPixelHeight(3);

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(xMinusDeviation, y / 2, lineZ);
		gl.glVertex3f(xPlusDeviation, y / 2, lineZ);

		gl.glLineWidth(0.6f);

		gl.glVertex3f(xPlusDeviation, y / 2 - lineTailHeight, lineZ);
		gl.glVertex3f(xPlusDeviation, y / 2 + lineTailHeight, lineZ);

		gl.glVertex3f(xMinusDeviation, y / 2 - lineTailHeight, lineZ);
		gl.glVertex3f(xMinusDeviation, y / 2 + lineTailHeight, lineZ);

		gl.glEnd();
		gl.glPopName();

	}

	private void registerPickingListener() {
		pickingListener = new APickingListener() {

			@Override
			public void clicked(Pick pick) {

				parent.sampleSelectionManager.clearSelection(SelectionType.SELECTION);

				parent.sampleSelectionManager.addToType(SelectionType.SELECTION,
						sampleIDType, experimentPerspective.getVirtualArray().getIDs());
				parent.sampleSelectionManager.triggerSelectionUpdateEvent();

				parent.sampleGroupSelectionManager
						.clearSelection(SelectionType.SELECTION);

				parent.sampleGroupSelectionManager.addToType(SelectionType.SELECTION,
						group.getID());
				parent.sampleGroupSelectionManager.triggerSelectionUpdateEvent();
				parentView.setDisplayListDirty();
			}

		};

		parentView.addIDPickingListener(pickingListener,
				PickingType.SAMPLE_GROUP_RENDERER.name(), rendererID);

	}

	@Override
	protected boolean permitsDisplayLists() {
		return false;
	}

}
