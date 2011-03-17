package org.caleydo.view.visbricks.brick.ui;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.util.clusterer.ClusterHelper;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;

/**
 * Renderer for an overview heatmap of values specified by contentVA and
 * storageVA. It shows the average values per storage and optionally the average
 * values + and - the standard deviation per storage.
 * 
 * @author Christian Partl
 * 
 */
public class OverviewHeatMapRenderer extends AContainedViewRenderer {

	private ColorMapping colorMapper;
	private ArrayList<float[]> heatMapValues;

	/**
	 * Constructor.
	 * 
	 * @param contentVA
	 * @param storageVA
	 * @param set
	 * @param showStandardDeviation
	 */
	public OverviewHeatMapRenderer(ContentVirtualArray contentVA,
			StorageVirtualArray storageVA, ISet set,
			boolean showStandardDeviation) {
		colorMapper = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);
		heatMapValues = new ArrayList<float[]>();

		float[] expressionValues = new float[contentVA.size()];

		for (int storageIndex : storageVA) {

			int index = 0;
			for (int contentIndex : contentVA) {
				expressionValues[index] = set.get(storageIndex).getFloat(
						EDataRepresentation.NORMALIZED, contentIndex);
				index++;
			}

			float arithmeticMean = ClusterHelper
					.arithmeticMean(expressionValues);

			float[] currentValues;

			if (showStandardDeviation) {
				float standardDeviation = ClusterHelper.standardDeviation(
						expressionValues, arithmeticMean);

				currentValues = new float[] {
						arithmeticMean - standardDeviation, arithmeticMean,
						arithmeticMean + standardDeviation };
			} else {
				currentValues = new float[] { arithmeticMean };
			}

			heatMapValues.add(currentValues);
		}

	}

	@Override
	public void render(GL2 gl) {

		if (heatMapValues.size() <= 0)
			return;

		float heatMapElementWidth = x / (float) heatMapValues.size();
		float heatMapElementHeight = y / (float) heatMapValues.get(0).length;

		gl.glBegin(GL2.GL_QUADS);
		float currentPositionX = 0;
		for (float[] currentValues : heatMapValues) {
			float currentPositionY = 0;

			for (float value : currentValues) {
				float[] mappingColor = colorMapper.getColor(value);

				gl.glColor3f(mappingColor[0], mappingColor[1], mappingColor[2]);
				gl.glVertex3f(currentPositionX, currentPositionY, 0);
				gl.glVertex3f(currentPositionX + heatMapElementWidth,
						currentPositionY, 0);
				gl.glVertex3f(currentPositionX + heatMapElementWidth,
						currentPositionY + heatMapElementHeight, 0);
				gl.glVertex3f(currentPositionX, currentPositionY
						+ heatMapElementHeight, 0);

				currentPositionY += heatMapElementHeight;
			}
			currentPositionX += heatMapElementWidth;
		}
		gl.glEnd();

		gl.glLineWidth(1);
		gl.glColor4f(1, 1, 1, 1);
		gl.glBegin(GL2.GL_LINES);
		currentPositionX = heatMapElementWidth;
		for (int i = 0; i < heatMapValues.size() - 1; i++) {
			gl.glVertex3f(currentPositionX, 0, 0);
			gl.glVertex3f(currentPositionX, y, 0);
			currentPositionX += heatMapElementWidth;
		}

		float currentPositionY = heatMapElementHeight;
		for (int i = 0; i < heatMapValues.get(0).length - 1; i++) {
			gl.glVertex3f(0, currentPositionY, 0);
			gl.glVertex3f(x, currentPositionY, 0);
			currentPositionY += heatMapElementHeight;
		}

		gl.glEnd();

	}

	@Override
	public int getMinHeightPixels() {
		return heatMapValues.get(0).length * 16;
	}
}
