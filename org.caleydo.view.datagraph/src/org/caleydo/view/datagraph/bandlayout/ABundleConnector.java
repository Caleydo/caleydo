package org.caleydo.view.datagraph.bandlayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public abstract class ABundleConnector extends ANodeConnector {

	protected List<ADimensionGroupData> commonDimensionGroups;
	protected int bandWidth;
	protected Map<ADimensionGroupData, Integer> bandWidthMap = new HashMap<ADimensionGroupData, Integer>();

	public ABundleConnector(IDataGraphNode node,
			PixelGLConverter pixelGLconverter,
			ConnectionBandRenderer connectionBandRenderer,
			List<ADimensionGroupData> commonDimensionGroups, int minBandWidth,
			int maxBandWidth, int maxDataAmount) {
		super(node, pixelGLconverter, connectionBandRenderer);

		this.commonDimensionGroups = commonDimensionGroups;
		calcBandWidths(minBandWidth, maxBandWidth, maxDataAmount);
	}

	protected void calcBandWidths(int minBandWidth, int maxBandWidth,
			int maxDataAmount) {
		bandWidth = 0;

		for (ADimensionGroupData dimensionGroupData : commonDimensionGroups) {
			int width = calcDimensionGroupBandWidthPixels(dimensionGroupData,
					minBandWidth, maxBandWidth, maxDataAmount);
			bandWidth += width;
			bandWidthMap.put(dimensionGroupData, width);
		}

		if (bandWidth > maxBandWidth) {

			int diff = bandWidth - maxBandWidth;

			int newBandWidth = 0;

			for (ADimensionGroupData dimensionGroupData : commonDimensionGroups) {
				int width = bandWidthMap.get(dimensionGroupData);
				int newWidth = width
						- (int) Math
								.ceil(((float) width / (float) bandWidth * (float) diff));
				bandWidthMap.put(dimensionGroupData, newWidth);
				newBandWidth += newWidth;
			}

			bandWidth = newBandWidth;
		}
	}

	protected int calcDimensionGroupBandWidthPixels(
			ADimensionGroupData dimensionGroupData, int minBandWidth,
			int maxBandWidth, int maxDataAmount) {
		// TODO: implement properly

		return minBandWidth;
	}

	public int getBandWidth() {
		return bandWidth;
	}

}
