package org.caleydo.view.datagraph.bandlayout;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.datagraph.Edge;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.node.ADataNode;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public class CustomLayoutEdgeBandRenderer extends AEdgeRenderer {

	protected final static int MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS = 20;
	protected final static int DEFAULT_MAX_BAND_WIDTH = 40;
	protected final static int DEFAULT_MIN_BAND_WIDTH = 10;

	protected IDataGraphNode node1;
	protected IDataGraphNode node2;
	protected PixelGLConverter pixelGLConverter;
	protected ViewFrustum viewFrustum;
	protected int maxBandWidth = DEFAULT_MAX_BAND_WIDTH;
	protected int minBandWidth = DEFAULT_MIN_BAND_WIDTH;
	protected int maxDataAmount;

	public CustomLayoutEdgeBandRenderer(Edge edge, GLDataGraph view) {
		super(edge, view);
		this.node1 = edge.getNode1();
		this.node2 = edge.getNode2();
		this.pixelGLConverter = view.getPixelGLConverter();
		this.viewFrustum = view.getViewFrustum();
	}

	@Override
	public void renderEdge(GL2 gl,
			ConnectionBandRenderer connectionBandRenderer, boolean highlight) {

		maxDataAmount = view.getMaxDataAmount();

		List<DataContainer> commonDataContainersNode1 = new ArrayList<DataContainer>();
		List<DataContainer> commonDataContainersNode2 = new ArrayList<DataContainer>();

		for (DataContainer dimensionGroupData1 : node1.getDataContainers()) {
			for (DataContainer dimensionGroupData2 : node2.getDataContainers()) {
				if (dimensionGroupData1.getID() == dimensionGroupData2.getID()) {
					commonDataContainersNode1.add(dimensionGroupData1);
					commonDataContainersNode2.add(dimensionGroupData2);
				}
			}
		}

		// ConnectionBandRenderer connectionBandRenderer = new
		// ConnectionBandRenderer();
		//
		// connectionBandRenderer.init(gl);

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

		} else {
			connector1 = new TopSideConnector(bottomNode, pixelGLConverter,
					connectionBandRenderer, viewFrustum, topNode);
			connector2 = new BottomSideConnector(topNode, pixelGLConverter,
					connectionBandRenderer, viewFrustum, bottomNode);

		}

		if (!commonDataContainersNode1.isEmpty()) {

			// TODO: finish this
			if (node1.showsDataContainers()) {
				// AGLView representedView =
				// ((ViewNode)node1).getRepresentedView();
				//
				// if(representedView instanceof ATableBasedView) {
				//
				// }

				ANodeConnector currentConnector = null;

				if (node1.isUpsideDown()) {
					currentConnector = new TopBundleConnector(node1,
							pixelGLConverter, connectionBandRenderer,
							commonDataContainersNode1, minBandWidth,
							maxBandWidth, maxDataAmount);
				} else {
					currentConnector = new BottomBundleConnector(node1,
							pixelGLConverter, connectionBandRenderer,
							commonDataContainersNode1, minBandWidth,
							maxBandWidth, maxDataAmount);
				}

				if (connector1.getNode() == node1) {
					connector1 = currentConnector;
				} else {
					connector2 = currentConnector;
				}

				bandWidth = ((ABundleConnector) currentConnector)
						.getBandWidth();
			}

			if (node2.showsDataContainers()) {
				// AGLView representedView =
				// ((ViewNode)node1).getRepresentedView();
				//
				// if(representedView instanceof ATableBasedView) {
				//
				// }
				ANodeConnector currentConnector = null;

				if (node2.isUpsideDown()) {
					currentConnector = new TopBundleConnector(node2,
							pixelGLConverter, connectionBandRenderer,
							commonDataContainersNode2, minBandWidth,
							maxBandWidth, maxDataAmount);
				} else {
					currentConnector = new BottomBundleConnector(node2,
							pixelGLConverter, connectionBandRenderer,
							commonDataContainersNode2, minBandWidth,
							maxBandWidth, maxDataAmount);
				}

				if (connector1.getNode() == node2) {
					connector1 = currentConnector;
				} else {
					connector2 = currentConnector;
				}
				bandWidth = ((ABundleConnector) currentConnector)
						.getBandWidth();
			}

		}

		connector1.setHighlightBand(highlight);
		connector2.setHighlightBand(highlight);

		List<Point2D> edgePoints = new ArrayList<Point2D>();

		edgePoints.add(connector1.getBandConnectionPoint());
		edgePoints.add(connector2.getBandConnectionPoint());

		edgeRoutingStrategy.setNodes(node1, node2);
		edgeRoutingStrategy.createEdge(edgePoints);

		edgePoints.add(0, connector1.getBandHelperPoint());
		edgePoints.add(connector2.getBandHelperPoint());

		List<Vec3f> bandPoints = connectionBandRenderer.calcInterpolatedBand(
				gl, edgePoints, bandWidth, pixelGLConverter);
		renderBand(gl, connectionBandRenderer, bandPoints, bandColor, highlight);

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
			List<Vec3f> bandPoints, Color color, boolean highlightBand) {

		gl.glColor4f(color.getRGB()[0], color.getRGB()[1], color.getRGB()[2],
				(highlightBand) ? 1 : 0.5f);
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
