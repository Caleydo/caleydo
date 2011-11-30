package org.caleydo.view.datagraph.layout.edge.rendering.connectors;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public abstract class ABundleConnector extends ANodeConnector {

	protected final static int BUNDLING_POINT_NODE_DISTANCE_Y = 30;
	protected final static int BOUNDING_BOX_BAND_CONNECTIONPOINT_DISTANCE_Y = 20;
	protected final static int MAX_BAND_ANCHOR_OFFSET_DISTANCE_Y_2_CP = 20;
	protected final static int MAX_BAND_ANCHOR_OFFSET_DISTANCE_Y_4_CP = 100;
	protected final static int MIN_BAND_ANCHOR_OFFSET_DISTANCE_Y_4_CP = 10;
	protected final static int DATACONTAINER_OFFSET_Y = 7;
	protected final static int DATACONTAINER_TO_BUNDLE_OFFSET_Y = 14;

	protected List<DataContainer> commonDataContainers;
	protected int bandWidthPixels;
	protected Map<DataContainer, Integer> bandWidthMap = new HashMap<DataContainer, Integer>();
	protected Point2D bundlingPoint;
	protected boolean use4ControlPointsForBandBundleConnection;

	public ABundleConnector(IDataGraphNode node,
			PixelGLConverter pixelGLconverter,
			ConnectionBandRenderer connectionBandRenderer,
			List<DataContainer> commonDataContainers, int minBandWidth,
			int maxBandWidth, int maxDataAmount, IDataGraphNode otherNode,
			ViewFrustum viewFrustum) {
		super(node, pixelGLconverter, connectionBandRenderer, otherNode,
				viewFrustum);

		this.commonDataContainers = commonDataContainers;
		calcBandWidths(minBandWidth, maxBandWidth, maxDataAmount);
	}

	protected float calcXPositionOfBundlingPoint(IDataGraphNode node,
			List<DataContainer> dataContainers) {
		float summedX = 0;

		for (DataContainer dataContainer : dataContainers) {
			Pair<Point2D, Point2D> anchorPoints = node
					.getBottomDataContainerAnchorPoints(dataContainer);
			if(anchorPoints == null)
				return (float)node.getPosition().getX();
			summedX += anchorPoints.getFirst().getX()
					+ anchorPoints.getSecond().getX();
		}

		return summedX / ((float) dataContainers.size() * 2.0f);
	}

	protected void calcBandWidths(int minBandWidth, int maxBandWidth,
			int maxDataAmount) {
		bandWidthPixels = 0;

		for (DataContainer dataContainer : commonDataContainers) {
			int width = calcDimensionGroupBandWidthPixels(dataContainer,
					minBandWidth, maxBandWidth, maxDataAmount);
			bandWidthPixels += width;
			bandWidthMap.put(dataContainer, width);
		}

		if (bandWidthPixels > maxBandWidth) {

			int diff = bandWidthPixels - maxBandWidth;

			int newBandWidth = 0;

			for (DataContainer dimensionGroupData : commonDataContainers) {
				int width = bandWidthMap.get(dimensionGroupData);
				int newWidth = width
						- (int) Math
								.ceil(((float) width / (float) bandWidthPixels * (float) diff));
				bandWidthMap.put(dimensionGroupData, newWidth);
				newBandWidth += newWidth;
			}

			bandWidthPixels = newBandWidth;
		}
	}

	protected int calcDimensionGroupBandWidthPixels(
			DataContainer dataContainer, int minBandWidth, int maxBandWidth,
			int maxDataAmount) {
		// TODO: implement properly

		return minBandWidth;
	}

	public int getBandWidth() {
		return bandWidthPixels;
	}

}
