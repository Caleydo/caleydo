package org.caleydo.view.datagraph.layout.edge.rendering.connectors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public abstract class ABundleConnector extends ANodeConnector {

	protected List<DataContainer> commonDataContainers;
	protected int bandWidth;
	protected Map<DataContainer, Integer> bandWidthMap = new HashMap<DataContainer, Integer>();

	public ABundleConnector(IDataGraphNode node,
			PixelGLConverter pixelGLconverter,
			ConnectionBandRenderer connectionBandRenderer,
			List<DataContainer> commonDataContainers, int minBandWidth,
			int maxBandWidth, int maxDataAmount) {
		super(node, pixelGLconverter, connectionBandRenderer);

		this.commonDataContainers = commonDataContainers;
		calcBandWidths(minBandWidth, maxBandWidth, maxDataAmount);
	}

	protected void calcBandWidths(int minBandWidth, int maxBandWidth,
			int maxDataAmount) {
		bandWidth = 0;

		for (DataContainer dataContainer : commonDataContainers) {
			int width = calcDimensionGroupBandWidthPixels(dataContainer,
					minBandWidth, maxBandWidth, maxDataAmount);
			bandWidth += width;
			bandWidthMap.put(dataContainer, width);
		}

		if (bandWidth > maxBandWidth) {

			int diff = bandWidth - maxBandWidth;

			int newBandWidth = 0;

			for (DataContainer dimensionGroupData : commonDataContainers) {
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
			DataContainer dataContainer, int minBandWidth, int maxBandWidth,
			int maxDataAmount) {
		// TODO: implement properly

		return minBandWidth;
	}

	public int getBandWidth() {
		return bandWidth;
	}

}
