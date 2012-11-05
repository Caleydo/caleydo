/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.dvi.layout.edge.rendering;

import java.util.List;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.dvi.Edge;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.layout.edge.rendering.connectors.ANodeConnector;
import org.caleydo.view.dvi.layout.edge.rendering.connectors.BottomSideConnector;
import org.caleydo.view.dvi.layout.edge.rendering.connectors.LeftSideConnector;
import org.caleydo.view.dvi.layout.edge.rendering.connectors.RightSideConnector;
import org.caleydo.view.dvi.layout.edge.rendering.connectors.TopSideConnector;
import org.caleydo.view.dvi.node.IDVINode;

public class FreeLayoutEdgeBandRenderer extends AEdgeBandRenderer {

	public FreeLayoutEdgeBandRenderer(Edge edge, GLDataViewIntegrator view) {
		super(edge, view);
	}

	// @Override
	// public void renderEdge(GL2 gl,
	// ConnectionBandRenderer connectionBandRenderer, boolean highlight) {
	//
	// maxDataAmount = view.getMaxDataAmount();
	//
	// List<TablePerspective> commonTablePerspectivesNode1 = new
	// ArrayList<TablePerspective>();
	// List<TablePerspective> commonTablePerspectivesNode2 = new
	// ArrayList<TablePerspective>();
	//
	// for (TablePerspective dimensionGroupData1 : node1.getTablePerspectives()) {
	// for (TablePerspective dimensionGroupData2 : node2.getTablePerspectives()) {
	// if (dimensionGroupData1.getID() == dimensionGroupData2.getID()) {
	// commonTablePerspectivesNode1.add(dimensionGroupData1);
	// commonTablePerspectivesNode2.add(dimensionGroupData2);
	// }
	// }
	// }
	//
	// // ConnectionBandRenderer connectionBandRenderer = new
	// // ConnectionBandRenderer();
	// //
	// // connectionBandRenderer.init(gl);
	//
	// Color bandColor = null;
	// int dataAmount = 0;
	//
	// if (node1 instanceof ADataNode) {
	// bandColor = ((ADataNode) node1).getDataDomain().getColor();
	// dataAmount = ((ADataNode) node1).getDataDomain().getDataAmount();
	// } else if (node2 instanceof ADataNode) {
	// bandColor = ((ADataNode) node2).getDataDomain().getColor();
	// dataAmount = ((ADataNode) node2).getDataDomain().getDataAmount();
	// }
	// if (bandColor == null)
	// bandColor = new Color(0.5f, 0.5f, 0.5f, 1f);
	//
	// ANodeConnector connector1 = null;
	// ANodeConnector connector2 = null;
	//
	// int bandWidth = 0;
	//
	// Point2D position1 = node1.getPosition();
	// Point2D position2 = node2.getPosition();
	//
	// float deltaX = (float) (position1.getX() - position2.getX());
	// float deltaY = (float) (position1.getY() - position2.getY());
	//
	// IDataGraphNode leftNode = null;
	// IDataGraphNode rightNode = null;
	// IDataGraphNode bottomNode = null;
	// IDataGraphNode topNode = null;
	//
	// if (deltaX < 0) {
	// if (deltaY < 0) {
	// // -2
	// // 1-
	//
	// leftNode = node1;
	// rightNode = node2;
	// bottomNode = node1;
	// topNode = node2;
	// } else {
	// // 1-
	// // -2
	//
	// leftNode = node1;
	// rightNode = node2;
	// bottomNode = node2;
	// topNode = node1;
	// }
	// } else {
	// if (deltaY < 0) {
	// // 2-
	// // -1
	//
	// leftNode = node2;
	// rightNode = node1;
	// bottomNode = node1;
	// topNode = node2;
	// } else {
	// // -1
	// // 2-
	//
	// leftNode = node2;
	// rightNode = node1;
	// bottomNode = node2;
	// topNode = node1;
	// }
	// }
	//
	// connector1.setHighlightBand(highlight);
	// connector2.setHighlightBand(highlight);
	//
	// List<Point2D> edgePoints = new ArrayList<Point2D>();
	//
	// edgePoints.add(connector1.getBandConnectionPoint());
	// edgePoints.add(connector2.getBandConnectionPoint());
	//
	// edgeRoutingStrategy.setNodes(node1, node2);
	// edgeRoutingStrategy.createEdge(edgePoints);
	//
	// edgePoints.add(0, connector1.getBandHelperPoint());
	// edgePoints.add(connector2.getBandHelperPoint());
	//
	// List<Vec3f> bandPoints = connectionBandRenderer.calcInterpolatedBand(
	// gl, edgePoints, bandWidth, pixelGLConverter);
	// renderBand(gl, connectionBandRenderer, bandPoints, bandColor, highlight);
	//
	// connector1.render(gl, bandPoints, true, bandColor);
	// connector2.render(gl, bandPoints, false, bandColor);
	//
	// }

	@Override
	protected void determineNodeConnectors(
			Pair<ANodeConnector, ANodeConnector> nodeConnectors, IDVINode leftNode,
			IDVINode rightNode, IDVINode bottomNode, IDVINode topNode,
			List<TablePerspective> commonTablePerspectivesNode1,
			List<TablePerspective> commonTablePerspectivesNode2,
			ConnectionBandRenderer connectionBandRenderer) {

		float spacingX = (float) ((rightNode.getPosition().getX() - rightNode.getWidth() / 2.0f) - (leftNode
				.getPosition().getX() + leftNode.getWidth() / 2.0f));
		float spacingY = (float) ((topNode.getPosition().getY() - topNode.getHeight() / 2.0f) - (bottomNode
				.getPosition().getY() + bottomNode.getHeight() / 2.0f));

		ANodeConnector connector1;
		ANodeConnector connector2;

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

		nodeConnectors.setFirst(connector1);
		nodeConnectors.setSecond(connector2);

		determineBundleConnectors(nodeConnectors, commonTablePerspectivesNode1,
				commonTablePerspectivesNode2, connectionBandRenderer);

	}
}
