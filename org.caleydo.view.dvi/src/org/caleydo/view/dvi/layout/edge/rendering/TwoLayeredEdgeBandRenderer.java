/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.layout.edge.rendering;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.dvi.Edge;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.layout.edge.rendering.connectors.ANodeConnector;
import org.caleydo.view.dvi.layout.edge.rendering.connectors.BottomSideConnector;
import org.caleydo.view.dvi.layout.edge.rendering.connectors.TopSideConnector;
import org.caleydo.view.dvi.node.IDVINode;

public class TwoLayeredEdgeBandRenderer extends AEdgeBandRenderer {

	public TwoLayeredEdgeBandRenderer(Edge edge, GLDataViewIntegrator view) {
		super(edge, view);
	}

	@Override
	protected void determineNodeConnectors(
			Pair<ANodeConnector, ANodeConnector> nodeConnectors, IDVINode leftNode,
			IDVINode rightNode, IDVINode bottomNode, IDVINode topNode,
			List<TablePerspective> commonTablePerspectivesNode1,
			List<TablePerspective> commonTablePerspectivesNode2,
			ConnectionBandRenderer connectionBandRenderer) {

		ANodeConnector connector1 = new TopSideConnector(bottomNode, pixelGLConverter,
				connectionBandRenderer, viewFrustum, topNode);
		ANodeConnector connector2 = new BottomSideConnector(topNode, pixelGLConverter,
				connectionBandRenderer, viewFrustum, bottomNode);

		nodeConnectors.setFirst(connector1);
		nodeConnectors.setSecond(connector2);

		determineBundleConnectors(nodeConnectors, commonTablePerspectivesNode1,
				commonTablePerspectivesNode2, connectionBandRenderer);

		// if (!commonTablePerspectivesNode1.isEmpty()) {
		//
		// if (node1.showsTablePerspectives()) {
		//
		// ANodeConnector currentConnector = null;
		//
		// if (node1.isUpsideDown()) {
		// currentConnector = new TopBundleConnector(node1,
		// pixelGLConverter, connectionBandRenderer,
		// commonTablePerspectivesNode1, minBandWidth,
		// maxBandWidth, maxDataAmount);
		// } else {
		// currentConnector = new BottomBundleConnector(node1,
		// pixelGLConverter, connectionBandRenderer,
		// commonTablePerspectivesNode1, minBandWidth,
		// maxBandWidth, maxDataAmount);
		// }
		//
		// if (connector1.getNode() == node1) {
		// connector1 = currentConnector;
		// } else {
		// connector2 = currentConnector;
		// }
		// }
		//
		// if (node2.showsTablePerspectives()) {
		//
		// ANodeConnector currentConnector = null;
		//
		// if (node2.isUpsideDown()) {
		// currentConnector = new TopBundleConnector(node2,
		// pixelGLConverter, connectionBandRenderer,
		// commonTablePerspectivesNode2, minBandWidth,
		// maxBandWidth, maxDataAmount);
		// } else {
		// currentConnector = new BottomBundleConnector(node2,
		// pixelGLConverter, connectionBandRenderer,
		// commonTablePerspectivesNode2, minBandWidth,
		// maxBandWidth, maxDataAmount);
		// }
		//
		// if (connector1.getNode() == node2) {
		// connector1 = currentConnector;
		// } else {
		// connector2 = currentConnector;
		// }
		// }
		// }

	}

}
