/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.path.node.mode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.datadomain.pathway.listener.ESampleMappingMode;
import org.caleydo.view.enroute.path.APathwayPathRenderer;
import org.caleydo.view.enroute.path.node.ALinearizableNode;

/**
 * @author Alexander Lex
 *
 */
public class MappingRenderer extends ColorRenderer {

	private IDType idType = IDType.getIDType("DAVID");

	private APathwayPathRenderer pathRenderer;
	private ALinearizableNode node;

	/**
	 *
	 */
	public MappingRenderer(AGLView view, APathwayPathRenderer pathRenderer, ALinearizableNode node) {
		super(new float[] { 0, 0, 0, 1f });
		this.view = view;
		this.pathRenderer = pathRenderer;
		this.node = node;

	}

	@Override
	protected void renderContent(GL2 gl) {
		TablePerspective mappedPerspective = pathRenderer.getMappedPerspective();
		List<Integer> ids = node.getMappedDavidIDs();
		if (ids == null || ids.isEmpty())
			return;

		float onePxlWidth = view.getPixelGLConverter().getGLWidthForPixelWidth(1);
		float onePxlHeight = view.getPixelGLConverter().getGLHeightForPixelHeight(1);
		float z = 1f;

		if (mappedPerspective != null) {

			Average average = null;

			if (pathRenderer.getSampleMappingMode() == ESampleMappingMode.ALL) {
				average = mappedPerspective.getContainerStatistics().getAverage(idType, ids.get(0));
			} else {

				Set<Integer> selectedSamples = pathRenderer.getSampleSelectionManager().getElements(
						SelectionType.SELECTION);
				List<Integer> selectedSamplesArray = new ArrayList<Integer>();

				selectedSamplesArray.addAll(selectedSamples);
				if (!selectedSamplesArray.isEmpty()) {

					VirtualArray selectedSamplesVA = new VirtualArray(pathRenderer.getSampleSelectionManager()
							.getIDType(), selectedSamplesArray);
					GroupList groupList = new GroupList();
					groupList.append(new Group(selectedSamplesVA.size()));
					selectedSamplesVA.setGroupList(groupList);

					average = TablePerspectiveStatistics.calculateAverage(selectedSamplesVA,
							mappedPerspective.getDataDomain(), idType, ids);
					if (Double.isNaN(average.getArithmeticMean()))
						average = null;
				}
			}

			if (average != null) {

				setColor(mappedPerspective.getDataDomain().getTable().getColorMapper()
						.getColor((float) average.getArithmeticMean()));

				// gl.glColor3f(1, 0, 0);
				float yStart = -1 * onePxlHeight;
				float yEnd = -5 * onePxlHeight;

				gl.glColor3f(1, 1, 1);
				gl.glBegin(GL2.GL_POLYGON);
				gl.glVertex3f(0, yStart, z);
				gl.glVertex3f(0, yEnd, z);
				gl.glVertex3f(x, yEnd, z);
				gl.glVertex3f(x, yStart, z);
				gl.glEnd();
				gl.glColor3fv(mappedPerspective.getDataDomain().getColor().getRGB(), 0);

				gl.glBegin(GL2.GL_POLYGON);
				gl.glVertex3f(0, yStart, z);
				gl.glVertex3f(0, yEnd, z);
				gl.glVertex3f(x * (float) average.getStandardDeviation() * 2, yEnd, z);
				gl.glVertex3f(x * (float) average.getStandardDeviation() * 2, yStart, z);
				gl.glEnd();

				gl.glColor3f(0, 0, 0);
				gl.glBegin(GL.GL_LINE_LOOP);
				gl.glVertex3f(0, yStart, z);
				gl.glVertex3f(0, yEnd, z);
				gl.glVertex3f(x, yEnd, z);
				gl.glVertex3f(x, yStart, z);
				gl.glEnd();
				gl.glColor3fv(mappedPerspective.getDataDomain().getColor().getRGB(), 0);
			} else {
				setColor(new float[] { 1, 1, 1, 1 });
			}
		} else {
			setColor(new float[] { 1, 1, 1, 1 });
		}

		Pair<TablePerspective, Average> highestAverage = null;
		Average average;
		for (TablePerspective tablePerspective : pathRenderer.getTablePerspectives()) {
			average = tablePerspective.getContainerStatistics().getAverage(idType, ids.get(0));
			if (average == null)
				continue;
			if (average.getStandardDeviation() > 0.2f) {
				if (highestAverage == null
						|| average.getStandardDeviation() > highestAverage.getSecond().getStandardDeviation()) {
					highestAverage = new Pair<>(tablePerspective, average);
				}
			}
		}

		if (highestAverage != null) {

			gl.glColor3fv(highestAverage.getFirst().getDataDomain().getColor().darker().getRGB(), 0);
			// gl.glColor3f(1, 0, 0);
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(x + 4 * onePxlWidth, 3 * onePxlHeight, z);
			gl.glVertex3f(x + 8 * onePxlWidth, 3 * onePxlHeight, z);
			gl.glVertex3f(x + 10 * onePxlWidth, y + 2 * onePxlHeight, z);
			gl.glVertex3f(x + 2 * onePxlWidth, y + 2 * onePxlHeight, z);
			gl.glEnd();

			// // gl.glColor3fv(tablePerspective.getDataDomain().getColor().getRGB(), 0);
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(x + 3 * onePxlWidth, 1 * onePxlHeight, z);
			gl.glVertex3f(x + 9 * onePxlWidth, 1 * onePxlHeight, z);
			gl.glVertex3f(x + 9 * onePxlWidth, -3 * onePxlHeight, z);
			gl.glVertex3f(x + 3 * onePxlWidth, -3 * onePxlHeight, z);
			gl.glEnd();

		}

		super.renderContent(gl);

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
