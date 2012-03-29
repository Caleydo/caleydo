package org.caleydo.view.datagraph.layout.edge.rendering;

import java.util.List;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.datagraph.Edge;
import org.caleydo.view.datagraph.GLDataViewIntegrator;
import org.caleydo.view.datagraph.layout.edge.rendering.connectors.ANodeConnector;
import org.caleydo.view.datagraph.layout.edge.rendering.connectors.BottomSideConnector;
import org.caleydo.view.datagraph.layout.edge.rendering.connectors.TopSideConnector;
import org.caleydo.view.datagraph.node.IDVINode;

public class TwoLayeredEdgeBandRenderer extends AEdgeBandRenderer {

	public TwoLayeredEdgeBandRenderer(Edge edge, GLDataViewIntegrator view) {
		super(edge, view);
	}

	@Override
	protected void determineNodeConnectors(
			Pair<ANodeConnector, ANodeConnector> nodeConnectors, IDVINode leftNode,
			IDVINode rightNode, IDVINode bottomNode, IDVINode topNode,
			List<DataContainer> commonDataContainersNode1,
			List<DataContainer> commonDataContainersNode2,
			ConnectionBandRenderer connectionBandRenderer) {

		ANodeConnector connector1 = new TopSideConnector(bottomNode, pixelGLConverter,
				connectionBandRenderer, viewFrustum, topNode);
		ANodeConnector connector2 = new BottomSideConnector(topNode, pixelGLConverter,
				connectionBandRenderer, viewFrustum, bottomNode);

		nodeConnectors.setFirst(connector1);
		nodeConnectors.setSecond(connector2);

		determineBundleConnectors(nodeConnectors, commonDataContainersNode1,
				commonDataContainersNode2, connectionBandRenderer);

		// if (!commonDataContainersNode1.isEmpty()) {
		//
		// if (node1.showsDataContainers()) {
		//
		// ANodeConnector currentConnector = null;
		//
		// if (node1.isUpsideDown()) {
		// currentConnector = new TopBundleConnector(node1,
		// pixelGLConverter, connectionBandRenderer,
		// commonDataContainersNode1, minBandWidth,
		// maxBandWidth, maxDataAmount);
		// } else {
		// currentConnector = new BottomBundleConnector(node1,
		// pixelGLConverter, connectionBandRenderer,
		// commonDataContainersNode1, minBandWidth,
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
		// if (node2.showsDataContainers()) {
		//
		// ANodeConnector currentConnector = null;
		//
		// if (node2.isUpsideDown()) {
		// currentConnector = new TopBundleConnector(node2,
		// pixelGLConverter, connectionBandRenderer,
		// commonDataContainersNode2, minBandWidth,
		// maxBandWidth, maxDataAmount);
		// } else {
		// currentConnector = new BottomBundleConnector(node2,
		// pixelGLConverter, connectionBandRenderer,
		// commonDataContainersNode2, minBandWidth,
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
