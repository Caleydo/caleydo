/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.path;

import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.VertexRepBasedContextMenuItem;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.enroute.path.node.ALinearizableNode;

/**
 * Base class for decorators that forwards all methods to the decoratee.
 *
 * @author Christian Partl
 *
 */
public abstract class APathwayPathRendererDecorator extends APathwayPathRenderer {

	protected APathwayPathRenderer decoratee;

	/**
	 * @param view
	 * @param tablePerspectives
	 */
	public APathwayPathRendererDecorator(AGLView view, List<TablePerspective> tablePerspectives,
			APathwayPathRenderer decoratee) {
		super(view, tablePerspectives);
		// We do not want to listen here
		geneSelectionManager.unregisterEventListeners();
		metaboliteSelectionManager.unregisterEventListeners();
		sampleSelectionManager.unregisterEventListeners();
		this.decoratee = decoratee;
	}

	@Override
	protected void updateLayout() {
		decoratee.updateLayout();
	}

	@Override
	protected void renderContent(GL2 gl) {
		decoratee.renderContent(gl);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return decoratee.permitsWrappingDisplayLists();
	}

	@Override
	public void init() {
		decoratee.init();
	}

	@Override
	public void setPath(PathwayPath pathSegments) {
		decoratee.setPath(pathSegments);
	}

	@Override
	protected void createNodes(PathwayPath pathSegments) {
		decoratee.createNodes(pathSegments);
	}

	@Override
	protected void destroyNodes() {
		decoratee.destroyNodes();
	}

	@Override
	protected void appendNodes(List<ALinearizableNode> pathNodes, List<ALinearizableNode> nodesToAppend) {
		decoratee.appendNodes(pathNodes, nodesToAppend);
	}

	@Override
	protected boolean mergeNodes(ALinearizableNode node1, ALinearizableNode node2) {
		return decoratee.mergeNodes(node1, node2);
	}

	@Override
	protected void createNodesForList(List<ALinearizableNode> nodes, List<PathwayVertexRep> vertexReps) {
		decoratee.createNodesForList(nodes, vertexReps);
	}

	@Override
	public void destroy(GL2 gl) {
		decoratee.destroy(gl);
		super.destroy(gl);
	}

	@Override
	public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {
		decoratee.notifyOfSelectionChange(selectionManager);
	}

	@Override
	public EventBasedSelectionManager getGeneSelectionManager() {
		return decoratee.getGeneSelectionManager();
	}

	@Override
	public EventBasedSelectionManager getMetaboliteSelectionManager() {
		return decoratee.getMetaboliteSelectionManager();
	}

	@Override
	public EventBasedSelectionManager getSampleSelectionManager() {
		return decoratee.getSampleSelectionManager();
	}

	@Override
	public void setTablePerspectives(List<TablePerspective> tablePerspectives) {
		decoratee.setTablePerspectives(tablePerspectives);
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {
		return decoratee.getTablePerspectives();
	}

	@Override
	public List<ALinearizableNode> getPathNodes() {
		return decoratee.getPathNodes();
	}

	@Override
	public AGLView getView() {
		return decoratee.getView();
	}

	@Override
	protected void prepare() {
		decoratee.prepare();
	}

	@Override
	public void removeNodeFromPath(ALinearizableNode node) {
		decoratee.removeNodeFromPath(node);
	}

	@Override
	public boolean isFirstNode(ALinearizableNode node) {
		return decoratee.isFirstNode(node);
	}

	@Override
	public boolean isLastNode(ALinearizableNode node) {
		return decoratee.isLastNode(node);
	}

	@Override
	protected Pair<Integer, Integer> determinePathSegmentAndIndexOfPathNode(ALinearizableNode node,
			PathwayVertexRep vertexRep) {
		return decoratee.determinePathSegmentAndIndexOfPathNode(node, vertexRep);
	}

	@Override
	public int getMinWidthPixels() {
		return decoratee.getMinWidthPixels();
	}

	@Override
	public int getMinHeightPixels() {
		return decoratee.getMinHeightPixels();
	}

	@Override
	public void setPixelGLConverter(PixelGLConverter pixelGLConverter) {
		decoratee.setPixelGLConverter(pixelGLConverter);
	}

	@Override
	public void setTextRenderer(CaleydoTextRenderer textRenderer) {
		decoratee.setTextRenderer(textRenderer);
	}

	@Override
	public void setPathway(PathwayGraph pathway) {
		decoratee.setPathway(pathway);
	}

	@Override
	public PathSizeConfiguration getSizeConfig() {
		return decoratee.getSizeConfig();
	}

	@Override
	public void setSizeConfig(PathSizeConfiguration sizeConfig) {
		decoratee.setSizeConfig(sizeConfig);
	}

	@Override
	public PathwayGraph getPathway() {
		return decoratee.getPathway();
	}

	@Override
	public List<PathwayGraph> getPathways() {
		return decoratee.getPathways();
	}

	@Override
	public Rect getVertexRepBounds(PathwayVertexRep vertexRep) {
		return decoratee.getVertexRepBounds(vertexRep);
	}

	@Override
	protected Rect getLeftTopAlignedNodeBounds(ALinearizableNode node) {
		return decoratee.getLeftTopAlignedNodeBounds(node);
	}

	@Override
	public List<Rect> getVertexRepsBounds(PathwayVertexRep vertexRep) {
		return decoratee.getVertexRepsBounds(vertexRep);
	}

	@Override
	public synchronized void addVertexRepBasedContextMenuItem(VertexRepBasedContextMenuItem item) {
		decoratee.addVertexRepBasedContextMenuItem(item);
	}

}
