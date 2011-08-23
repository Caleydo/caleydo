package org.caleydo.view.datagraph.bandlayout;

import java.awt.geom.Point2D;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.view.datagraph.IDataGraphNode;

public abstract class AEdgeBandRenderer {
	
	protected final static int EDGE_ANCHOR_MAX_NODE_DISTANCE_PIXELS = 50; 

	protected IDataGraphNode node1;
	protected IDataGraphNode node2;
	protected PixelGLConverter pixelGLConverter;
	protected ViewFrustum viewFrustum;

	public AEdgeBandRenderer(IDataGraphNode node1, IDataGraphNode node2,
			PixelGLConverter pixelGLConverter, ViewFrustum viewFrustum) {
		this.node1 = node1;
		this.node2 = node2;
		this.pixelGLConverter = pixelGLConverter;
		this.viewFrustum = viewFrustum;
	}

	public abstract void renderEdgeBand(GL2 gl, IEdgeRoutingStrategy edgeRoutingStrategy);

}
