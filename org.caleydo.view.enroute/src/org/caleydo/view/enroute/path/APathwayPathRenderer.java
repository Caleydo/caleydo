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
package org.caleydo.view.enroute.path;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGroupRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.ANode;
import org.caleydo.view.enroute.path.node.ComplexNode;
import org.caleydo.view.enroute.path.node.CompoundNode;
import org.caleydo.view.enroute.path.node.GeneNode;
import org.caleydo.view.enroute.path.node.mode.ComplexNodeLinearizedMode;
import org.caleydo.view.enroute.path.node.mode.CompoundNodeLinearizedMode;
import org.caleydo.view.enroute.path.node.mode.GeneNodeLinearizedMode;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.event.EnRoutePathEvent;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.GraphPathImpl;

/**
 * Renderer that is responsible for rendering a single pathway path.
 *
 * @author Christian Partl
 *
 */
public abstract class APathwayPathRenderer extends ALayoutRenderer implements IEventBasedSelectionManagerUser,
		IListenerOwner {

	/**
	 * The pathway graph the rendered path belongs to.
	 */
	protected PathwayGraph pathway;

	/**
	 * The list of {@link PathwayVertexRep}s that represents the path.
	 */
	protected List<PathwayVertexRep> path;

	/**
	 * List of renderable nodes for the path.
	 */
	protected List<ALinearizableNode> pathNodes = new ArrayList<>();

	/**
	 * View that renders this renderer.
	 */
	protected AGLView view;

	/**
	 * ID of the last node that was added. Used to create unique node IDs for picking.
	 */
	protected int lastNodeID = 0;

	/**
	 * Table perspectives for node previews.
	 */
	protected List<TablePerspective> tablePerspectives;

	/**
	 * Event space that is used for receiving and sending path events.
	 */
	protected String pathwayPathEventSpace = GLPathway.DEFAULT_PATHWAY_PATH_EVENT_SPACE;

	/**
	 * The queue which holds the events
	 */
	private BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue = new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();

	protected EventBasedSelectionManager geneSelectionManager;
	protected EventBasedSelectionManager metaboliteSelectionManager;
	protected EventBasedSelectionManager sampleSelectionManager;

	protected PixelGLConverter pixelGLConverter;
	protected CaleydoTextRenderer textRenderer;

	private final EventListenerManager listeners = EventListenerManagers.wrap(this);

	public APathwayPathRenderer(AGLView view, List<TablePerspective> tablePerspectives) {
		this.view = view;
		this.tablePerspectives = tablePerspectives;
		this.pixelGLConverter = view.getPixelGLConverter();
		this.textRenderer = view.getTextRenderer();
	}

	/**
	 * Method that initializes the {@link APathwayPathRenderer}. Shall be called once prior use.
	 */
	public void init() {
		registerEventListeners();
	}

	/**
	 * Sets a new path to be linearized.
	 *
	 * @param pathway
	 *            The pathway the path corresponds to.
	 * @param path
	 *            List of {@link PathwayVertexRep}s that represents a path. If multiple <code>PathwayVertexRep</code>s
	 *            represent a complex node, they must occur in a sequence.
	 */
	public void setPath(PathwayGraph pathway, List<PathwayVertexRep> path) {
		this.pathway = pathway;
		this.path = path;

		createNodes(path);
		// setMinSize(0);
		// isNewPath = true;
		// setLayoutDirty();

	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	protected void onPathwayPathChanged(EnRoutePathEvent event) {
		PathwayPath path = event.getPath();
		if (path != null && path.getPath() != null) {
			PathwayGraph pathway = (PathwayGraph) path.getPath().getGraph();
			setPath(pathway, path.getNodes());
		} else {
			setPath(null, new ArrayList<PathwayVertexRep>());
		}
	}

	protected void createNodes(List<PathwayVertexRep> path) {

		for (ANode node : pathNodes) {
			node.destroy();
		}
		pathNodes.clear();
		createNodesForList(pathNodes, path);

	}

	protected void createNodesForList(List<ALinearizableNode> nodes, List<PathwayVertexRep> vertexReps) {

		CaleydoTextRenderer textRenderer = view.getTextRenderer();

		for (int i = 0; i < vertexReps.size(); i++) {
			PathwayVertexRep currentVertexRep = vertexReps.get(i);
			ALinearizableNode node = null;
			if (currentVertexRep.getType() == EPathwayVertexType.group) {
				PathwayVertexGroupRep groupRep = (PathwayVertexGroupRep) currentVertexRep;
				List<PathwayVertexRep> groupedReps = groupRep.getGroupedVertexReps();
				List<ALinearizableNode> groupedNodes = new ArrayList<ALinearizableNode>();
				createNodesForList(groupedNodes, groupedReps);
				ComplexNode complexNode = new ComplexNode(this, textRenderer, view, lastNodeID++,
						new ComplexNodeLinearizedMode(view, this));
				complexNode.setNodes(groupedNodes);
				for (ALinearizableNode groupedNode : groupedNodes) {
					groupedNode.setParentNode(complexNode);
				}
				complexNode.setPathwayVertexRep(currentVertexRep);
				complexNode.init();
				node = complexNode;
			} else if (currentVertexRep.getType() == EPathwayVertexType.compound) {
				CompoundNode compoundNode = new CompoundNode(this, view, lastNodeID++, new CompoundNodeLinearizedMode(
						view, this));

				compoundNode.setPathwayVertexRep(currentVertexRep);
				compoundNode.init();
				node = compoundNode;

			} else {

				// TODO: Verify that this is also the right approach for
				// enzymes and ortholog
				GeneNode geneNode = new GeneNode(this, textRenderer, view, lastNodeID++, new GeneNodeLinearizedMode(
						view, this));
				int commaIndex = currentVertexRep.getName().indexOf(',');
				if (commaIndex > 0) {
					geneNode.setLabel(currentVertexRep.getName().substring(0, commaIndex));
				} else {
					geneNode.setLabel(currentVertexRep.getName());
				}
				geneNode.setPathwayVertexRep(currentVertexRep);
				geneNode.init();
				node = geneNode;
			}

			nodes.add(node);
		}
	}

	@Override
	public void destroy(GL2 gl) {
		geneSelectionManager.unregisterEventListeners();
		metaboliteSelectionManager.unregisterEventListeners();
		sampleSelectionManager.unregisterEventListeners();
		unregisterEventListeners();
		super.destroy(gl);
	}

	@Override
	public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {

	}

	/**
	 * @return the geneSelectionManager, see {@link #geneSelectionManager}
	 */
	public EventBasedSelectionManager getGeneSelectionManager() {
		return geneSelectionManager;
	}

	/**
	 * @param geneSelectionManager
	 *            setter, see {@link geneSelectionManager}
	 */
	public void setGeneSelectionManager(EventBasedSelectionManager geneSelectionManager) {
		this.geneSelectionManager = geneSelectionManager;
	}

	/**
	 * @return the metaboliteSelectionManager, see {@link #metaboliteSelectionManager}
	 */
	public EventBasedSelectionManager getMetaboliteSelectionManager() {
		return metaboliteSelectionManager;
	}

	/**
	 * @param metaboliteSelectionManager
	 *            setter, see {@link metaboliteSelectionManager}
	 */
	public void setMetaboliteSelectionManager(EventBasedSelectionManager metaboliteSelectionManager) {
		this.metaboliteSelectionManager = metaboliteSelectionManager;
	}

	/**
	 * @return the sampleSelectionManager, see {@link #sampleSelectionManager}
	 */
	public EventBasedSelectionManager getSampleSelectionManager() {
		return sampleSelectionManager;
	}

	/**
	 * @param sampleSelectionManager
	 *            setter, see {@link sampleSelectionManager}
	 */
	public void setSampleSelectionManager(EventBasedSelectionManager sampleSelectionManager) {
		this.sampleSelectionManager = sampleSelectionManager;
	}

	/**
	 * @param tablePerspectives
	 *            setter, see {@link tablePerspectives}
	 */
	public void setTablePerspectives(List<TablePerspective> tablePerspectives) {
		this.tablePerspectives = tablePerspectives;
	}

	/**
	 * @return the tablePerspectives, see {@link #tablePerspectives}
	 */
	public List<TablePerspective> getTablePerspectives() {
		return tablePerspectives;
	}

	/**
	 * @return the pathNodes, see {@link #pathNodes}
	 */
	public List<ALinearizableNode> getPathNodes() {
		return pathNodes;
	}

	/**
	 * @return the view, see {@link #view}
	 */
	public AGLView getView() {
		return view;
	}

	/**
	 * @param pathwayPathEventSpace
	 *            setter, see {@link pathwayPathEventSpace}
	 */
	public void setPathwayPathEventSpace(String pathwayPathEventSpace) {
		this.pathwayPathEventSpace = pathwayPathEventSpace;
	}

	@Override
	public final synchronized void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		queue.add(new Pair<AEventListener<? extends IListenerOwner>, AEvent>(listener, event));
	}

	/**
	 * This method should be called every display cycle when it is save to change the state of the object. It processes
	 * all the previously submitted events.
	 */
	protected final void processEvents() {
		Pair<AEventListener<? extends IListenerOwner>, AEvent> pair;
		while (queue.peek() != null) {
			pair = queue.poll();
			pair.getFirst().handleEvent(pair.getSecond());
		}
	}

	@Override
	protected void prepare() {
		processEvents();
	}

	@Override
	public void registerEventListeners() {
		listeners.register(this, pathwayPathEventSpace);

	}

	@Override
	public void unregisterEventListeners() {
		listeners.unregisterAll();

	}

	/**
	 * @return the pathway, see {@link #pathway}
	 */
	public PathwayGraph getPathway() {
		return pathway;
	}

	/**
	 * Removes the specified node from the path if it is at the start or the end of the path.
	 *
	 * @param node
	 */
	public void removeNodeFromPath(ALinearizableNode node) {

		int linearizedNodeIndex = pathNodes.indexOf(node);

		if (linearizedNodeIndex == 0) {
			path.remove(0);
		} else if (linearizedNodeIndex == path.size() - 1) {
			path.remove(path.size() - 1);

		} else {
			return;
		}

		setPath(pathway, path);

		broadcastPath();
	}

	protected void broadcastPath() {

		PathwayPath pathwayPath = null;
		if (path.size() > 0) {

			PathwayVertexRep startVertexRep = path.get(0);
			PathwayVertexRep endVertexRep = path.get(path.size() - 1);
			List<DefaultEdge> edges = new ArrayList<DefaultEdge>();

			for (int i = 0; i < path.size() - 1; i++) {
				PathwayVertexRep currentVertexRep = path.get(i);
				PathwayVertexRep nextVertexRep = path.get(i + 1);

				DefaultEdge edge = pathway.getEdge(currentVertexRep, nextVertexRep);
				if (edge == null)
					edge = pathway.getEdge(nextVertexRep, currentVertexRep);
				edges.add(edge);
			}
			GraphPath<PathwayVertexRep, DefaultEdge> graphPath = new GraphPathImpl<PathwayVertexRep, DefaultEdge>(
					pathway, startVertexRep, endVertexRep, edges, edges.size());

			pathwayPath = new PathwayPath(graphPath);
		}
		EnRoutePathEvent event = new EnRoutePathEvent();
		event.setEventSpace(pathwayPathEventSpace);
		event.setPath(pathwayPath);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);

	}

}
