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
import javax.media.opengl.glu.GLU;

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
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGroupRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.BranchSummaryNode;
import org.caleydo.view.enroute.path.node.ComplexNode;
import org.caleydo.view.enroute.path.node.CompoundNode;
import org.caleydo.view.enroute.path.node.GeneNode;
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
public class PathwayPathRenderer extends ALayoutRenderer implements IEventBasedSelectionManagerUser, IListenerOwner {

	/**
	 * The pathway graph the rendered path belongs to.
	 */
	private PathwayGraph pathway;

	/**
	 * The list of {@link PathwayVertexRep}s that represents the path.
	 */
	private List<PathwayVertexRep> path;

	/**
	 * List of renderable nodes for the path.
	 */
	private List<ALinearizableNode> pathNodes = new ArrayList<>();

	/**
	 * View that renders this renderer.
	 */
	private AGLView view;

	/**
	 * ID of the last node that was added. Used to create unique node IDs for picking.
	 */
	private int lastNodeID = 0;

	/**
	 * Table perspectives for node previews.
	 */
	private List<TablePerspective> tablePerspectives;

	/**
	 * Branch summary node that is currently expanded
	 */
	private BranchSummaryNode expandedBranchSummaryNode;

	/**
	 * Strategy that determines the way the path is rendered.
	 */
	private IPathwayPathRenderingStrategy renderingStrategy;

	/**
	 * Event space that is used for receiving and sending path events.
	 */
	private String pathwayPathEventSpace = GLPathway.DEFAULT_PATHWAY_PATH_EVENT_SPACE;

	/**
	 * The queue which holds the events
	 */
	private BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue = new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();

	private EventBasedSelectionManager geneSelectionManager;
	private EventBasedSelectionManager metaboliteSelectionManager;
	private EventBasedSelectionManager sampleSelectionManager;

	private final EventListenerManager listeners = EventListenerManagers.wrap(this);

	public PathwayPathRenderer(AGLView view, List<TablePerspective> tablePerspectives) {
		this.view = view;
		this.tablePerspectives = tablePerspectives;
	}

	/**
	 * Method that initializes the {@link PathwayPathRenderer}. Shall be called once prior use.
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

		// expandedBranchSummaryNode = null;
		// for (ANode node : linearizedNodes) {
		// node.unregisterPickingListeners();
		// }
		// for (ANode node : branchNodes) {
		// node.unregisterPickingListeners();
		// }

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

	private void createNodes(List<PathwayVertexRep> path) {
		pathNodes.clear();
		// branchNodes.clear();
		// branchNodesToLinearizedNodesMap.clear();
		// linearizedNodesToIncomingBranchSummaryNodesMap.clear();
		// linearizedNodesToOutgoingBranchSummaryNodesMap.clear();

		createNodesForList(pathNodes, path);

		// Create branch nodes
		// for (int i = 0; i < linearizedNodes.size(); i++) {
		// ALinearizableNode currentNode = linearizedNodes.get(i);
		// PathwayVertexRep currentVertexRep = currentNode.getPathwayVertexRep();
		// PathwayVertexRep prevVertexRep = null;
		// PathwayVertexRep nextVertexRep = null;
		//
		// if (i > 0) {
		// ALinearizableNode prevNode = linearizedNodes.get(i - 1);
		// prevVertexRep = prevNode.getPathwayVertexRep();
		// }
		// if (i != linearizedNodes.size() - 1) {
		// ALinearizableNode nextNode = linearizedNodes.get(i + 1);
		// nextVertexRep = nextNode.getPathwayVertexRep();
		// }
		//
		// BranchSummaryNode incomingNode = new BranchSummaryNode(this, lastNodeId++, currentNode);
		// BranchSummaryNode outgoingNode = new BranchSummaryNode(this, lastNodeId++, currentNode);
		// List<PathwayVertexRep> sourceVertexReps = Graphs.predecessorListOf(pathway, currentVertexRep);
		// sourceVertexReps.remove(prevVertexRep);
		// List<PathwayVertexRep> targetVertexReps = Graphs.successorListOf(pathway, currentVertexRep);
		// targetVertexReps.remove(nextVertexRep);
		//
		// if (sourceVertexReps.size() > 0) {
		// List<ALinearizableNode> sourceNodes = new ArrayList<ALinearizableNode>();
		//
		// createNodesForList(sourceNodes, sourceVertexReps);
		// incomingNode.setBranchNodes(sourceNodes);
		// linearizedNodesToIncomingBranchSummaryNodesMap.put(currentNode, incomingNode);
		// branchNodes.add(incomingNode);
		// branchNodes.addAll(sourceNodes);
		// for (ANode node : sourceNodes) {
		// ((ALinearizableNode) node).setPreviewMode(true);
		// branchNodesToLinearizedNodesMap.put(node, currentNode);
		// }
		// }
		//
		// if (targetVertexReps.size() > 0) {
		// List<ALinearizableNode> targetNodes = new ArrayList<ALinearizableNode>();
		// createNodesForList(targetNodes, targetVertexReps);
		//
		// outgoingNode.setBranchNodes(targetNodes);
		// linearizedNodesToOutgoingBranchSummaryNodesMap.put(currentNode, outgoingNode);
		// branchNodes.add(outgoingNode);
		// branchNodes.addAll(targetNodes);
		// for (ANode node : targetNodes) {
		// ((ALinearizableNode) node).setPreviewMode(true);
		// branchNodesToLinearizedNodesMap.put(node, currentNode);
		// }
		// }
		//
		// }

	}

	private void createNodesForList(List<ALinearizableNode> nodes, List<PathwayVertexRep> vertexReps) {

		CaleydoTextRenderer textRenderer = view.getTextRenderer();

		for (int i = 0; i < vertexReps.size(); i++) {
			PathwayVertexRep currentVertexRep = vertexReps.get(i);
			ALinearizableNode node = null;
			if (currentVertexRep.getType() == EPathwayVertexType.group) {
				PathwayVertexGroupRep groupRep = (PathwayVertexGroupRep) currentVertexRep;
				List<PathwayVertexRep> groupedReps = groupRep.getGroupedVertexReps();
				List<ALinearizableNode> groupedNodes = new ArrayList<ALinearizableNode>();
				createNodesForList(groupedNodes, groupedReps);
				ComplexNode complexNode = new ComplexNode(this, textRenderer, view, lastNodeID++);
				complexNode.setNodes(groupedNodes);
				for (ALinearizableNode groupedNode : groupedNodes) {
					groupedNode.setParentNode(complexNode);
				}
				complexNode.setPathwayVertexRep(currentVertexRep);
				node = complexNode;
			} else if (currentVertexRep.getType() == EPathwayVertexType.compound) {
				CompoundNode compoundNode = new CompoundNode(this, view, lastNodeID++);

				compoundNode.setPathwayVertexRep(currentVertexRep);
				node = compoundNode;

			} else {

				// TODO: Verify that this is also the right approach for
				// enzymes and ortholog
				GeneNode geneNode = new GeneNode(this, textRenderer, view, lastNodeID++);
				int commaIndex = currentVertexRep.getName().indexOf(',');
				if (commaIndex > 0) {
					geneNode.setLabel(currentVertexRep.getName().substring(0, commaIndex));
				} else {
					geneNode.setLabel(currentVertexRep.getName());
				}
				geneNode.setPathwayVertexRep(currentVertexRep);

				node = geneNode;
			}

			nodes.add(node);
		}
	}

	@Override
	protected void renderContent(GL2 gl) {

		renderingStrategy.render(gl, new GLU());
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
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
		// TODO Auto-generated method stub

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
	 * Selects a branch node to be linearized.
	 *
	 * @param node
	 */
	public void selectBranch(ALinearizableNode node) {

		// ALinearizableNode linearizedNode = branchNodesToLinearizedNodesMap.get(node);
		// BranchSummaryNode summaryNode = (BranchSummaryNode) linearizedNodesToIncomingBranchSummaryNodesMap
		// .get(linearizedNode);
		//
		// boolean isIncomingBranch = false;
		// if (summaryNode != null && summaryNode.getBranchNodes().contains(node)) {
		// isIncomingBranch = true;
		// }
		//
		// PathwayVertexRep linearizedVertexRep = linearizedNode.getPathwayVertexRep();
		// PathwayVertexRep branchVertexRep = node.getPathwayVertexRep();
		//
		// DefaultEdge edge = pathway.getEdge(linearizedVertexRep, branchVertexRep);
		// if (edge == null) {
		// edge = pathway.getEdge(branchVertexRep, linearizedVertexRep);
		// }
		//
		// int linearizedNodeIndex = linearizedNodes.indexOf(linearizedNode);
		// List<PathwayVertexRep> newPath = null;
		// List<PathwayVertexRep> branchPath = determineDefiniteUniDirectionalBranchPath(branchVertexRep,
		// linearizedVertexRep, isIncomingBranch);
		//
		// if (isIncomingBranch) {
		// // insert above linearized node
		// Collections.reverse(branchPath);
		// newPath = path.subList(linearizedNodeIndex, path.size());
		//
		// newPath.addAll(0, branchPath);
		//
		// } else {
		// // insert below linearized node
		// newPath = path.subList(0, linearizedNodeIndex + 1);
		// newPath.addAll(branchPath);
		// }
		//
		// setPath(pathway, newPath);
		//
		// broadcastPath();
	}

	/**
	 * Calculates a branch path consisting of {@link PathwayVertexRep} objects for a specified branch node. This path
	 * ends if there is no unambiguous way to continue, the direction of edges changes, the pathway ends, or the
	 * {@link #maxBranchSwitchingPathLength} is reached. The specified <code>PathwayVertexRep</code> that represents the
	 * start of the path is added at the beginning of the path.
	 *
	 * @param branchVertexRep
	 *            The <code>PathwayVertexRep</code> that represents the start of the branch path.
	 * @param linearizedVertexRep
	 *            The <code>PathwayVertexRep</code> of the linearized path this branch belongs to.
	 * @param isIncomingBranchPath
	 *            Determines whether the branch path is incoming or outgoing. This is especially important for
	 *            bidirectional edges.
	 * @return
	 */
	// private List<PathwayVertexRep> determineDefiniteUniDirectionalBranchPath(PathwayVertexRep branchVertexRep,
	// PathwayVertexRep linearizedVertexRep, boolean isIncomingBranchPath) {
	//
	// List<PathwayVertexRep> vertexReps = new ArrayList<PathwayVertexRep>();
	// vertexReps.add(branchVertexRep);
	// DefaultEdge existingEdge = pathway.getEdge(branchVertexRep, linearizedVertexRep);
	// if (existingEdge == null)
	// existingEdge = pathway.getEdge(linearizedVertexRep, branchVertexRep);
	//
	// PathwayVertexRep currentVertexRep = branchVertexRep;
	//
	// for (int i = 0; i < maxBranchSwitchingPathLength; i++) {
	// List<PathwayVertexRep> nextVertices = null;
	// if (isIncomingBranchPath) {
	// nextVertices = Graphs.predecessorListOf(pathway, currentVertexRep);
	// } else {
	// nextVertices = Graphs.successorListOf(pathway, currentVertexRep);
	// }
	//
	// if (nextVertices.size() == 0 || nextVertices.size() > 1) {
	// return vertexReps;
	// } else {
	// currentVertexRep = nextVertices.get(0);
	// vertexReps.add(currentVertexRep);
	// }
	//
	// }
	//
	// return vertexReps;
	// }

	/**
	 * @param currentExpandedBranchNode
	 *            setter, see {@link #expandedBranchSummaryNode}
	 */
	public void setExpandedBranchSummaryNode(BranchSummaryNode expandedBranchSummaryNode) {
		this.expandedBranchSummaryNode = expandedBranchSummaryNode;
	}

	/**
	 * @return the expandedBranchSummaryNode, see {@link #expandedBranchSummaryNode}
	 */
	public BranchSummaryNode getExpandedBranchSummaryNode() {
		return expandedBranchSummaryNode;
	}

	/**
	 * @return the pathNodes, see {@link #pathNodes}
	 */
	public List<ALinearizableNode> getPathNodes() {
		return pathNodes;
	}

	/**
	 * @param renderingStrategy
	 *            setter, see {@link #renderingStrategy}
	 */
	public void setRenderingStrategy(IPathwayPathRenderingStrategy renderingStrategy) {
		this.renderingStrategy = renderingStrategy;
	}

	@Override
	public int getMinHeightPixels() {
		return renderingStrategy.getMinHeightPixels();
	}

	@Override
	public int getMinWidthPixels() {
		return renderingStrategy.getMinWidthPixels();
	}

	/**
	 * @return the view, see {@link #view}
	 */
	public AGLView getView() {
		return view;
	}

	float getX() {
		return x;
	}

	float getY() {
		return y;
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

	private void broadcastPath() {

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
