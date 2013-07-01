/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.layout.edge.rendering;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.dvi.Edge;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.layout.edge.rendering.connectors.ABundleConnector;
import org.caleydo.view.dvi.layout.edge.rendering.connectors.ANodeConnector;
import org.caleydo.view.dvi.layout.edge.rendering.connectors.BottomBundleConnector;
import org.caleydo.view.dvi.layout.edge.rendering.connectors.TopBundleConnector;
import org.caleydo.view.dvi.node.ADataNode;
import org.caleydo.view.dvi.node.IDVINode;

public abstract class AEdgeBandRenderer extends AEdgeRenderer {

	protected final static int MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS = 20;
	protected final static int DEFAULT_MAX_BAND_WIDTH = 40;
	protected final static int DEFAULT_MIN_BAND_WIDTH = 10;

	protected IDVINode node1;
	protected IDVINode node2;
	protected PixelGLConverter pixelGLConverter;
	protected ViewFrustum viewFrustum;
	protected int maxBandWidth = DEFAULT_MAX_BAND_WIDTH;
	protected int minBandWidth = DEFAULT_MIN_BAND_WIDTH;
	protected int maxDataAmount;

	public AEdgeBandRenderer(Edge edge, GLDataViewIntegrator view) {
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

		List<TablePerspective> commonTablePerspectivesNode1 = new ArrayList<TablePerspective>();
		List<TablePerspective> commonTablePerspectivesNode2 = new ArrayList<TablePerspective>();

		for (TablePerspective dimensionGroupData1 : node1.getTablePerspectives()) {
			for (TablePerspective dimensionGroupData2 : node2.getTablePerspectives()) {
				if (dimensionGroupData1.getID() == dimensionGroupData2.getID()) {
					commonTablePerspectivesNode1.add(dimensionGroupData1);
					commonTablePerspectivesNode2.add(dimensionGroupData2);
				}
			}
		}

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

		int bandWidth = 0;

		Point2D position1 = node1.getPosition();
		Point2D position2 = node2.getPosition();

		float deltaX = (float) (position1.getX() - position2.getX());
		float deltaY = (float) (position1.getY() - position2.getY());

		IDVINode leftNode = null;
		IDVINode rightNode = null;
		IDVINode bottomNode = null;
		IDVINode topNode = null;

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

		Pair<ANodeConnector, ANodeConnector> nodeConnectors = new Pair<ANodeConnector, ANodeConnector>();

		determineNodeConnectors(nodeConnectors, leftNode, rightNode,
				bottomNode, topNode, commonTablePerspectivesNode1,
				commonTablePerspectivesNode2, connectionBandRenderer);

		ANodeConnector connector1 = nodeConnectors.getFirst();
		ANodeConnector connector2 = nodeConnectors.getSecond();

		if (connector1 instanceof ABundleConnector) {
			bandWidth = ((ABundleConnector) connector1).getBandWidth();
		} else if (connector2 instanceof ABundleConnector) {
			bandWidth = ((ABundleConnector) connector2).getBandWidth();
		} else {
			bandWidth = calcBandWidthPixels(dataAmount);
		}

		connector1.setHighlightBand(highlight);
		connector2.setHighlightBand(highlight);

		List<Point2D> edgePoints = new ArrayList<Point2D>();

		edgePoints.add(connector1.getBandConnectionPoint());
		edgePoints.add(connector2.getBandConnectionPoint());

		edgeRoutingStrategy.createEdge(node1, node2, edgePoints);

		edgePoints.add(0, connector1.getBandHelperPoint());
		edgePoints.add(connector2.getBandHelperPoint());

		List<Vec3f> bandPoints = connectionBandRenderer.calcInterpolatedBand(
				gl, edgePoints, bandWidth, pixelGLConverter);
		renderBand(gl, connectionBandRenderer, bandPoints, bandColor, highlight);

		connector1.render(gl, bandPoints, true, bandColor);
		connector2.render(gl, bandPoints, false, bandColor);

	}

	protected abstract void determineNodeConnectors(
			Pair<ANodeConnector, ANodeConnector> nodeConnectors,
			IDVINode leftNode, IDVINode rightNode,
			IDVINode bottomNode, IDVINode topNode,
			List<TablePerspective> commonTablePerspectivesNode1,
			List<TablePerspective> commonTablePerspectivesNode2,
			ConnectionBandRenderer connectionBandRenderer);

	protected void determineBundleConnectors(
			Pair<ANodeConnector, ANodeConnector> nodeConnectors,
			List<TablePerspective> commonTablePerspectivesNode1,
			List<TablePerspective> commonTablePerspectivesNode2,
			ConnectionBandRenderer connectionBandRenderer) {

		ANodeConnector connector1 = nodeConnectors.getFirst();
		ANodeConnector connector2 = nodeConnectors.getSecond();

		if (!commonTablePerspectivesNode1.isEmpty()) {

			if (node1.showsTablePerspectives()) {

				ANodeConnector currentConnector = null;

				if (node1.isUpsideDown()) {
					currentConnector = new TopBundleConnector(node1,
							pixelGLConverter, connectionBandRenderer,
							commonTablePerspectivesNode1, minBandWidth,
							maxBandWidth, maxDataAmount, node2, viewFrustum, view);
				} else {
					currentConnector = new BottomBundleConnector(node1,
							pixelGLConverter, connectionBandRenderer,
							commonTablePerspectivesNode1, minBandWidth,
							maxBandWidth, maxDataAmount, node2, viewFrustum, view);
				}

				if (connector1.getNode() == node1) {
					connector1 = currentConnector;
				} else {
					connector2 = currentConnector;
				}
			}

			if (node2.showsTablePerspectives()) {

				ANodeConnector currentConnector = null;

				if (node2.isUpsideDown()) {
					currentConnector = new TopBundleConnector(node2,
							pixelGLConverter, connectionBandRenderer,
							commonTablePerspectivesNode2, minBandWidth,
							maxBandWidth, maxDataAmount, node1, viewFrustum, view);
				} else {
					currentConnector = new BottomBundleConnector(node2,
							pixelGLConverter, connectionBandRenderer,
							commonTablePerspectivesNode2, minBandWidth,
							maxBandWidth, maxDataAmount, node1, viewFrustum, view);
				}

				if (connector1.getNode() == node2) {
					connector1 = currentConnector;
				} else {
					connector2 = currentConnector;
				}
			}

		}

		nodeConnectors.setFirst(connector1);
		nodeConnectors.setSecond(connector2);
	}

	protected int calcBandWidthPixels(int dataAmount) {
		int bandWidth = (int) (dataAmount * (float) maxBandWidth / maxDataAmount);
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
		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i < bandPoints.size() / 2; i++) {
			gl.glVertex3f(bandPoints.get(i).x(), bandPoints.get(i).y(),
					bandPoints.get(i).z());
		}
		gl.glEnd();

		gl.glBegin(GL.GL_LINE_STRIP);
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
