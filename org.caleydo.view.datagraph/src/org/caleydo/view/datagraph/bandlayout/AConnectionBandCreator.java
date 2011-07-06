package org.caleydo.view.datagraph.bandlayout;

import java.awt.geom.Point2D;
import java.util.List;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.view.datagraph.IDataGraphNode;

public abstract class AConnectionBandCreator {

	protected IDataGraphNode node1;
	protected IDataGraphNode node2;
	protected PixelGLConverter pixelGLConverter;

	public AConnectionBandCreator(IDataGraphNode node1, IDataGraphNode node2, PixelGLConverter pixelGLConverter) {
		this.node1 = node1;
		this.node2 = node2;
		this.pixelGLConverter = pixelGLConverter;
	}
	
	public abstract List<List<Pair<Point2D, Point2D>>> calcConnectionBands();

}
