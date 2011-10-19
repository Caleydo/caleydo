package org.caleydo.view.datagraph.bandlayout;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.datagraph.node.ADataNode;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public class EdgeBandRenderer {

	protected final static int MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS = 20;
	protected final static int DEFAULT_MAX_BAND_WIDTH = 40;
	protected final static int DEFAULT_MIN_BAND_WIDTH = 8;

	protected IDataGraphNode node1;
	protected IDataGraphNode node2;
	protected PixelGLConverter pixelGLConverter;
	protected ViewFrustum viewFrustum;
	protected int maxBandWidth = DEFAULT_MAX_BAND_WIDTH;
	protected int minBandWidth = DEFAULT_MIN_BAND_WIDTH;
	protected int maxDataAmount;

	public EdgeBandRenderer(IDataGraphNode node1, IDataGraphNode node2,
			PixelGLConverter pixelGLConverter, ViewFrustum viewFrustum,
			int maxDataAmount) {
		this.node1 = node1;
		this.node2 = node2;
		this.pixelGLConverter = pixelGLConverter;
		this.viewFrustum = viewFrustum;
		this.maxDataAmount = maxDataAmount;
	}

	public void renderEdgeBand(GL2 gl, IEdgeRoutingStrategy edgeRoutingStrategy) {

		List<ADimensionGroupData> commonDimensionGroupsNode1 = new ArrayList<ADimensionGroupData>();
		List<ADimensionGroupData> commonDimensionGroupsNode2 = new ArrayList<ADimensionGroupData>();

		for (ADimensionGroupData dimensionGroupData1 : node1
				.getDimensionGroups()) {
			for (ADimensionGroupData dimensionGroupData2 : node2
					.getDimensionGroups()) {
				if (dimensionGroupData1.getID() == dimensionGroupData2.getID()) {
					commonDimensionGroupsNode1.add(dimensionGroupData1);
					commonDimensionGroupsNode2.add(dimensionGroupData2);
				}
			}
		}

		ConnectionBandRenderer connectionBandRenderer = new ConnectionBandRenderer();

		connectionBandRenderer.init(gl);

		Color bandColor = null;
		int dataAmount = 0;

		if (node1 instanceof ADataNode) {
			bandColor = ((ADataNode) node1).getDataDomain().getColor();
			dataAmount = ((ADataNode) node1).getDataDomain().getDataAmount();
		} else if (node2 instanceof ADataNode) {
			bandColor = ((ADataNode) node2).getDataDomain().getColor();
			dataAmount = ((ADataNode) node2).getDataDomain().getDataAmount();
		}
		if (bandColor == null)
			bandColor = new Color(0.5f, 0.5f, 0.5f, 1f);

		ANodeConnector connector1 = null;
		ANodeConnector connector2 = null;

		int bandWidth = 0;

		if (!commonDimensionGroupsNode1.isEmpty()) {

			connector1 = new BundleConnector(node1, pixelGLConverter,
					connectionBandRenderer, commonDimensionGroupsNode1,
					minBandWidth, maxBandWidth, maxDataAmount);
			connector2 = new BundleConnector(node2, pixelGLConverter,
					connectionBandRenderer, commonDimensionGroupsNode2,
					minBandWidth, maxBandWidth, maxDataAmount);

			bandWidth = ((BundleConnector) connector1).getBandWidth();

			// renderBundledBand(gl, node1, node2, commonDimensionGroupsNode1,
			// commonDimensionGroupsNode2, edgeRoutingStrategy,
			// connectionBandRenderer, bandColor);
		} else {

			Point2D position1 = node1.getPosition();
			Point2D position2 = node2.getPosition();

			float deltaX = (float) (position1.getX() - position2.getX());
			float deltaY = (float) (position1.getY() - position2.getY());

			IDataGraphNode leftNode = null;
			IDataGraphNode rightNode = null;
			IDataGraphNode bottomNode = null;
			IDataGraphNode topNode = null;

			if (deltaX < 0) {
				if (deltaY < 0) {
					// -2
					// 1-

					leftNode = node1;
					rightNode = node2;
					bottomNode = node1;
					topNode = node2;
				} else {
					// 1-
					// -2

					leftNode = node1;
					rightNode = node2;
					bottomNode = node2;
					topNode = node1;
				}
			} else {
				if (deltaY < 0) {
					// 2-
					// -1

					leftNode = node2;
					rightNode = node1;
					bottomNode = node1;
					topNode = node2;
				} else {
					// -1
					// 2-

					leftNode = node2;
					rightNode = node1;
					bottomNode = node2;
					topNode = node1;
				}
			}

			float spacingX = (float) ((rightNode.getPosition().getX() - rightNode
					.getWidth() / 2.0f) - (leftNode.getPosition().getX() + leftNode
					.getWidth() / 2.0f));
			float spacingY = (float) ((topNode.getPosition().getY() - topNode
					.getHeight() / 2.0f) - (bottomNode.getPosition().getY() + topNode
					.getHeight() / 2.0f));

			bandWidth = calcBandWidthPixels(dataAmount);

			if (spacingX > spacingY) {

				connector1 = new RightSideConnector(leftNode, pixelGLConverter,
						connectionBandRenderer, viewFrustum, rightNode);
				connector2 = new LeftSideConnector(rightNode, pixelGLConverter,
						connectionBandRenderer, viewFrustum, leftNode);

				// renderHorizontalBand(gl, leftNode, rightNode,
				// edgeRoutingStrategy, connectionBandRenderer, bandColor,
				// bandWidth);
			} else {
				connector1 = new TopSideConnector(bottomNode, pixelGLConverter,
						connectionBandRenderer, viewFrustum, topNode);
				connector2 = new BottomSideConnector(topNode, pixelGLConverter,
						connectionBandRenderer, viewFrustum, bottomNode);

				// renderVerticalBand(gl, bottomNode, topNode,
				// edgeRoutingStrategy, connectionBandRenderer, bandColor,
				// bandWidth);
			}
		}

		List<Point2D> edgePoints = new ArrayList<Point2D>();

		edgePoints.add(connector1.getBandConnectionPoint());
		edgePoints.add(connector2.getBandConnectionPoint());

		edgeRoutingStrategy.createEdge(edgePoints);

		edgePoints.add(0, connector1.getBandHelperPoint());
		edgePoints.add(connector2.getBandHelperPoint());

		List<Vec3f> bandPoints = connectionBandRenderer.calcInterpolatedBand(
				gl, edgePoints, bandWidth, pixelGLConverter);
		renderBand(gl, connectionBandRenderer, bandPoints, bandColor);

		connector1.render(gl, bandPoints, true, bandColor);
		connector2.render(gl, bandPoints, false, bandColor);

	}

	protected int calcBandWidthPixels(int dataAmount) {
		int bandWidth = (int) (dataAmount * (float) maxBandWidth / (float) maxDataAmount);
		if (bandWidth > maxBandWidth)
			return maxBandWidth;
		if (bandWidth < minBandWidth)
			return minBandWidth;
		return bandWidth;
	}


	protected void renderBand(GL2 gl,
			ConnectionBandRenderer connectionBandRenderer,
			List<Vec3f> bandPoints, Color color) {

		gl.glColor4f(color.getRGB()[0], color.getRGB()[1], color.getRGB()[2],
				0.5f);
		connectionBandRenderer.render(gl, bandPoints);
		gl.glColor4fv(color.getRGBA(), 0);
		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = 0; i < bandPoints.size() / 2; i++) {
			gl.glVertex3f(bandPoints.get(i).x(), bandPoints.get(i).y(),
					bandPoints.get(i).z());
		}
		gl.glEnd();

		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = bandPoints.size() / 2; i < bandPoints.size(); i++) {
			gl.glVertex3f(bandPoints.get(i).x(), bandPoints.get(i).y(),
					bandPoints.get(i).z());
		}
		gl.glEnd();

	}	

	public int getMaxBandWidth() {
		return maxBandWidth;
	}

	public void setMaxBandWidth(int maxBandWidth) {
		this.maxBandWidth = maxBandWidth;
	}

	public int getMinBandWidth() {
		return minBandWidth;
	}

	public void setMinBandWidth(int minBandWidth) {
		this.minBandWidth = minBandWidth;
	}
}
