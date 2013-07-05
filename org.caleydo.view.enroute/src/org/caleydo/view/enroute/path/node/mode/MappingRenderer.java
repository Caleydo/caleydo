/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.path.node.mode;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.view.enroute.path.APathwayPathRenderer;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.pathway.ESampleMappingMode;

/**
 * @author Alexander Lex
 *
 */
public class MappingRenderer extends ColorRenderer {

	private List<TablePerspective> tablePerspectives;
	private TablePerspective mappedPerspective;
	private ESampleMappingMode sampleMappingMode;
	private AGLView view;

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

			Average average = mappedPerspective.getContainerStatistics().getAverage(idType, ids.get(0));
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
			}
			else {
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
			if (average.getStandardDeviation() > 0.1) {
				if (highestAverage == null
						|| average.getStandardDeviation() > highestAverage.getSecond().getStandardDeviation()) {
					highestAverage = new Pair<>(tablePerspective, average);
				}
			}
		}

		// if (highestAverage != null) {
		//
		// gl.glColor3fv(highestAverage.getFirst().getDataDomain().getColor().getRGB(), 0);
		// // gl.glColor3f(1, 0, 0);
		// gl.glBegin(GL2.GL_POLYGON);
		// gl.glVertex3f(x + 4 * onePxlWidth, 3 * onePxlHeight, z);
		// gl.glVertex3f(x + 8 * onePxlWidth, 3 * onePxlHeight, z);
		// gl.glVertex3f(x + 10 * onePxlWidth, y + 2 * onePxlHeight, z);
		// gl.glVertex3f(x + 2 * onePxlWidth, y + 2 * onePxlHeight, z);
		// gl.glEnd();
		//
		// // // gl.glColor3fv(tablePerspective.getDataDomain().getColor().getRGB(), 0);
		// gl.glBegin(GL2.GL_POLYGON);
		// gl.glVertex3f(x + 3 * onePxlWidth, 1 * onePxlHeight, z);
		// gl.glVertex3f(x + 9 * onePxlWidth, 1 * onePxlHeight, z);
		// gl.glVertex3f(x + 9 * onePxlWidth, -3 * onePxlHeight, z);
		// gl.glVertex3f(x + 3 * onePxlWidth, -3 * onePxlHeight, z);
		// gl.glEnd();
		//
		// }

		super.renderContent(gl);

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

	/**
	 * @param tablePerspectives
	 *            setter, see {@link tablePerspectives}
	 */
	public void setTablePerspectives(List<TablePerspective> tablePerspectives) {
		this.tablePerspectives = tablePerspectives;
	}

	/**
	 * @param sampleMappingMode
	 *            setter, see {@link sampleMappingMode}
	 */
	public void setSampleMappingMode(ESampleMappingMode sampleMappingMode) {
		this.sampleMappingMode = sampleMappingMode;
	}

	/**
	 * @param mappedPerspective
	 *            setter, see {@link mappedPerspective}
	 */
	public void setMappedPerspective(TablePerspective mappedPerspective) {
		this.mappedPerspective = mappedPerspective;
	}

}
