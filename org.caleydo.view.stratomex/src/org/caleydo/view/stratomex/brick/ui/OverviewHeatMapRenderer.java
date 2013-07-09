/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.ui;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.color.mapping.ColorMapper;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;

/**
 * Renderer for an overview heatmap of values specified by recordVA and dimensionVA. It shows the average values per
 * dimension and optionally the average values + and - the standard deviation per dimension.
 *
 * @author Christian Partl
 *
 */
public class OverviewHeatMapRenderer extends ALayoutRenderer {

	private Table table;
	private ArrayList<Float> heatMapValuesMean;
	private ArrayList<Float> heatMapValuesMeanPlusStdDev;
	private ArrayList<Float> heatMapValuesMeanMinusStdDev;
	private boolean showStandardDeviation;

	/**
	 * Constructor.
	 *
	 * @param recordVA
	 * @param dimensionVA
	 * @param set
	 * @param showStandardDeviation
	 */
	public OverviewHeatMapRenderer(TablePerspective tablePerspective, Table table, boolean showStandardDeviation) {
		this.table = table;
		this.showStandardDeviation = showStandardDeviation;

		// float[] expressionValues = new float[tablePerspective.getNrRecords()];
		heatMapValuesMean = new ArrayList<Float>();
		heatMapValuesMeanMinusStdDev = new ArrayList<Float>();
		heatMapValuesMeanPlusStdDev = new ArrayList<Float>();

		ArrayList<Average> averageDimensions = tablePerspective.getContainerStatistics().getAverageDimensions();

		for (Average averageDimension : averageDimensions) {
			if (averageDimension == null)
				continue;
			heatMapValuesMean.add((float) averageDimension.getArithmeticMean());
			heatMapValuesMeanMinusStdDev.add((float) averageDimension.getArithmeticMean()
					- (float) averageDimension.getStandardDeviation());
			heatMapValuesMeanPlusStdDev.add((float) averageDimension.getArithmeticMean()
					+ (float) averageDimension.getStandardDeviation());

		}

		// for (int dimensionID :
		// tablePerspective.getDimensionPerspective().getVirtualArray()) {
		//
		// int index = 0;
		// for (int recordIndex :
		// tablePerspective.getRecordPerspective().getVirtualArray()) {
		// expressionValues[index] =
		// table.getFloat(DataRepresentation.NORMALIZED,
		// recordIndex, dimensionID);
		// index++;
		// }
		//
		// float arithmeticMean =
		// if (showStandardDeviation) {
		// float standardDeviation = ClusterHelper.standardDeviation(
		// expressionValues, arithmeticMean);
		//
		// heatMapValuesMean.add(arithmeticMean);
		// heatMapValuesMeanMinusStdDev.add(arithmeticMean - standardDeviation);
		// heatMapValuesMeanPlusStdDev.add(arithmeticMean + standardDeviation);
		// } else {
		// heatMapValuesMean.add(arithmeticMean);
		// }
		// }

	}

	@Override
	public void renderContent(GL2 gl) {
		ColorMapper colorMapper = table.getDataDomain().getTable().getColorMapper();
		if (heatMapValuesMean.size() <= 0)
			return;

		boolean renderTriangles = true;
		if (heatMapValuesMean.size() > 15)
			renderTriangles = false;

		float heatMapElementWidth = x / heatMapValuesMean.size();

		if (showStandardDeviation) {

			float meanHeatMapElementHeight = y / 2.0f;
			float stdDevHeatMapElementHeight = y / 4.0f;

			float currentPositionX = 0;
			float currentPositionY = 0;

			if (renderTriangles)
				gl.glBegin(GL.GL_TRIANGLES);
			else
				gl.glBegin(GL2.GL_QUADS);

			for (Float currentValue : heatMapValuesMeanMinusStdDev) {
				if (currentValue > 1)
					currentValue = 1f;
				if (currentValue < 0)
					currentValue = 0f;

				float[] mappingColor = colorMapper.getColor(currentValue);
				gl.glColor3f(mappingColor[0], mappingColor[1], mappingColor[2]);
				gl.glVertex3f(currentPositionX, currentPositionY + stdDevHeatMapElementHeight, 0);
				gl.glVertex3f(currentPositionX + heatMapElementWidth, currentPositionY + stdDevHeatMapElementHeight, 0);
				if (renderTriangles) {
					gl.glVertex3f(currentPositionX + heatMapElementWidth / 2.0f, currentPositionY, 0);
				} else {
					gl.glVertex3f(currentPositionX + heatMapElementWidth, currentPositionY, 0);
					gl.glVertex3f(currentPositionX, currentPositionY, 0);

				}
				// gl.glVertex3f(currentPositionX, currentPositionY
				// + stdDevHeatMapElementHeight, 0);
				currentPositionX += heatMapElementWidth;
			}

			gl.glEnd();

			gl.glBegin(GL2.GL_QUADS);

			currentPositionX = 0;
			currentPositionY += stdDevHeatMapElementHeight;
			for (Float currentValue : heatMapValuesMean) {
				if (currentValue > 1)
					currentValue = 1f;
				if (currentValue < 0)
					currentValue = 0f;

				float[] mappingColor = colorMapper.getColor(currentValue);
				gl.glColor3f(mappingColor[0], mappingColor[1], mappingColor[2]);
				gl.glVertex3f(currentPositionX, currentPositionY, 0);
				gl.glVertex3f(currentPositionX + heatMapElementWidth, currentPositionY, 0);
				gl.glVertex3f(currentPositionX + heatMapElementWidth, currentPositionY + meanHeatMapElementHeight, 0);
				gl.glVertex3f(currentPositionX, currentPositionY + meanHeatMapElementHeight, 0);
				currentPositionX += heatMapElementWidth;
			}

			gl.glEnd();

			if (renderTriangles)
				gl.glBegin(GL.GL_TRIANGLES);
			else
				gl.glBegin(GL2.GL_QUADS);

			currentPositionX = 0;
			currentPositionY += meanHeatMapElementHeight;
			for (Float currentValue : heatMapValuesMeanPlusStdDev) {
				if (currentValue > 1)
					currentValue = 1f;
				if (currentValue < 0)
					currentValue = 0f;

				float[] mappingColor = colorMapper.getColor(currentValue);
				gl.glColor3f(mappingColor[0], mappingColor[1], mappingColor[2]);
				if (renderTriangles) {
					gl.glVertex3f(currentPositionX + heatMapElementWidth / 2.0f, currentPositionY
							+ stdDevHeatMapElementHeight, 0);
				} else {
					gl.glVertex3f(currentPositionX, currentPositionY + stdDevHeatMapElementHeight, 0);
					gl.glVertex3f(currentPositionX + heatMapElementWidth,
							currentPositionY + stdDevHeatMapElementHeight, 0);
				}
				gl.glVertex3f(currentPositionX + heatMapElementWidth, currentPositionY, 0);
				gl.glVertex3f(currentPositionX, currentPositionY, 0);
				currentPositionX += heatMapElementWidth;
			}

			gl.glEnd();

			if (heatMapValuesMean.size() <= 20) {
				gl.glLineWidth(1);
				gl.glColor4f(1, 1, 1, 1);
				gl.glBegin(GL.GL_LINES);
				currentPositionX = heatMapElementWidth;
				for (int i = 0; i < heatMapValuesMean.size() - 1; i++) {
					gl.glVertex3f(currentPositionX, 0, 0);
					gl.glVertex3f(currentPositionX, y, 0);
					currentPositionX += heatMapElementWidth;
				}
				gl.glEnd();
			}

			// gl.glVertex3f(0, stdDevHeatMapElementHeight, 0);
			// gl.glVertex3f(x, stdDevHeatMapElementHeight, 0);
			// gl.glVertex3f(0, stdDevHeatMapElementHeight + meanHeatMapElementHeight, 0);
			// gl.glVertex3f(x, stdDevHeatMapElementHeight + meanHeatMapElementHeight, 0);

		} else {
			gl.glBegin(GL2.GL_QUADS);

			float currentPositionX = 0;
			for (Float currentValue : heatMapValuesMean) {
				if (currentValue > 1)
					currentValue = 1f;
				if (currentValue < 0)
					currentValue = 0f;

				float[] mappingColor = colorMapper.getColor(currentValue);
				gl.glColor3f(mappingColor[0], mappingColor[1], mappingColor[2]);
				gl.glVertex3f(currentPositionX, 0, 0);
				gl.glVertex3f(currentPositionX + heatMapElementWidth, 0, 0);
				gl.glVertex3f(currentPositionX + heatMapElementWidth, y, 0);
				gl.glVertex3f(currentPositionX, y, 0);
				currentPositionX += heatMapElementWidth;
			}

			gl.glEnd();

			if (heatMapValuesMean.size() <= 20) {
				gl.glLineWidth(1);
				gl.glColor4f(1, 1, 1, 1);
				gl.glBegin(GL.GL_LINES);
				currentPositionX = heatMapElementWidth;
				for (int i = 0; i < heatMapValuesMean.size() - 1; i++) {
					gl.glVertex3f(currentPositionX, 0, 0);
					gl.glVertex3f(currentPositionX, y, 0);
					currentPositionX += heatMapElementWidth;
				}
				gl.glEnd();
			}
		}

	}

	@Override
	public int getMinHeightPixels() {
		if (showStandardDeviation)
			return 24;
		return 11;
	}

	@Override
	public int getMinWidthPixels() {
		return 150;
		// return Math.max(150, 16 * heatMapValuesMean.size());
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}

}
