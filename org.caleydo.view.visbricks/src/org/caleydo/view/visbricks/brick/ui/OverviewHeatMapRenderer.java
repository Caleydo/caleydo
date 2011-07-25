package org.caleydo.view.visbricks.brick.ui;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.util.clusterer.ClusterHelper;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

/**
 * Renderer for an overview heatmap of values specified by contentVA and
 * dimensionVA. It shows the average values per dimension and optionally the average
 * values + and - the standard deviation per dimension.
 * 
 * @author Christian Partl
 * 
 */
public class OverviewHeatMapRenderer extends LayoutRenderer {

	private ColorMapper colorMapper;
	private ArrayList<Float> heatMapValuesMean;
	private ArrayList<Float> heatMapValuesMeanPlusStdDev;
	private ArrayList<Float> heatMapValuesMeanMinusStdDev;
	private boolean showStandardDeviation;

	/**
	 * Constructor.
	 * 
	 * @param contentVA
	 * @param dimensionVA
	 * @param set
	 * @param showStandardDeviation
	 */
	public OverviewHeatMapRenderer(ContentVirtualArray contentVA,
			DimensionVirtualArray dimensionVA, DataTable set,
			boolean showStandardDeviation) {
		colorMapper = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);
		this.showStandardDeviation = showStandardDeviation;

		float[] expressionValues = new float[contentVA.size()];
		heatMapValuesMean = new ArrayList<Float>();
		heatMapValuesMeanMinusStdDev = new ArrayList<Float>();
		heatMapValuesMeanPlusStdDev = new ArrayList<Float>();

		for (int dimensionIndex : dimensionVA) {

			int index = 0;
			for (int contentIndex : contentVA) {
				expressionValues[index] = set.get(dimensionIndex).getFloat(
						DataRepresentation.NORMALIZED, contentIndex);
				index++;
			}

			float arithmeticMean = ClusterHelper
					.arithmeticMean(expressionValues);

			if (showStandardDeviation) {
				float standardDeviation = ClusterHelper.standardDeviation(
						expressionValues, arithmeticMean);

				heatMapValuesMean.add(arithmeticMean);
				heatMapValuesMeanMinusStdDev.add(arithmeticMean
						- standardDeviation);
				heatMapValuesMeanPlusStdDev.add(arithmeticMean
						+ standardDeviation);
			} else {
				heatMapValuesMean.add(arithmeticMean);
			}
		}

	}

	@Override
	public void render(GL2 gl) {

		if (heatMapValuesMean.size() <= 0)
			return;

		float heatMapElementWidth = x / (float) heatMapValuesMean.size();

		if (showStandardDeviation) {

			float meanHeatMapElementHeight = y / 2.0f;
			float stdDevHeatMapElementHeight = y / 4.0f;

			gl.glBegin(GL2.GL_TRIANGLES);

			float currentPositionX = 0;
			float currentPositionY = 0;
			for (Float currentValue : heatMapValuesMeanMinusStdDev) {
				if (currentValue > 1)
					currentValue = 1f;
				if (currentValue < 0)
					currentValue = 0f;

				float[] mappingColor = colorMapper.getColor(currentValue);
				gl.glColor3f(mappingColor[0], mappingColor[1], mappingColor[2]);
				gl.glVertex3f(currentPositionX, currentPositionY
						+ stdDevHeatMapElementHeight, 0);
				gl.glVertex3f(currentPositionX + heatMapElementWidth,
						currentPositionY + stdDevHeatMapElementHeight, 0);
				gl.glVertex3f(currentPositionX + heatMapElementWidth / 2.0f,
						currentPositionY, 0);
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
				gl.glVertex3f(currentPositionX + heatMapElementWidth,
						currentPositionY, 0);
				gl.glVertex3f(currentPositionX + heatMapElementWidth,
						currentPositionY + meanHeatMapElementHeight, 0);
				gl.glVertex3f(currentPositionX, currentPositionY
						+ meanHeatMapElementHeight, 0);
				currentPositionX += heatMapElementWidth;
			}

			gl.glEnd();

			gl.glBegin(GL2.GL_TRIANGLES);

			currentPositionX = 0;
			currentPositionY += meanHeatMapElementHeight;
			for (Float currentValue : heatMapValuesMeanPlusStdDev) {
				if (currentValue > 1)
					currentValue = 1f;
				if (currentValue < 0)
					currentValue = 0f;

				float[] mappingColor = colorMapper.getColor(currentValue);
				gl.glColor3f(mappingColor[0], mappingColor[1], mappingColor[2]);
				gl.glVertex3f(currentPositionX + heatMapElementWidth / 2.0f,
						currentPositionY + stdDevHeatMapElementHeight, 0);
				gl.glVertex3f(currentPositionX + heatMapElementWidth,
						currentPositionY, 0);
				gl.glVertex3f(currentPositionX, currentPositionY, 0);
				currentPositionX += heatMapElementWidth;
			}

			gl.glEnd();

			gl.glLineWidth(1);
			gl.glColor4f(1, 1, 1, 1);
			gl.glBegin(GL2.GL_LINES);
			currentPositionX = heatMapElementWidth;
			for (int i = 0; i < heatMapValuesMean.size() - 1; i++) {
				gl.glVertex3f(currentPositionX, 0, 0);
				gl.glVertex3f(currentPositionX, y, 0);
				currentPositionX += heatMapElementWidth;
			}

			gl.glVertex3f(0, stdDevHeatMapElementHeight, 0);
			gl.glVertex3f(x, stdDevHeatMapElementHeight, 0);
			gl.glVertex3f(0, stdDevHeatMapElementHeight
					+ meanHeatMapElementHeight, 0);
			gl.glVertex3f(x, stdDevHeatMapElementHeight
					+ meanHeatMapElementHeight, 0);

			gl.glEnd();

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

			gl.glLineWidth(1);
			gl.glColor4f(1, 1, 1, 1);
			gl.glBegin(GL2.GL_LINES);
			currentPositionX = heatMapElementWidth;
			for (int i = 0; i < heatMapValuesMean.size() - 1; i++) {
				gl.glVertex3f(currentPositionX, 0, 0);
				gl.glVertex3f(currentPositionX, y, 0);
				currentPositionX += heatMapElementWidth;
			}
			gl.glEnd();
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
		return Math.max(150, 16 * heatMapValuesMean.size());
	}

}
