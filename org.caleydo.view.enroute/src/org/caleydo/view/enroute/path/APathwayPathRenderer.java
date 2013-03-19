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

import gleem.linalg.Vec3f;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.MinSizeUpdateEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.IVertexRepBasedEventFactory;
import org.caleydo.datadomain.pathway.VertexRepBasedContextMenuItem;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGroupRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.enroute.event.PathRendererChangedEvent;
import org.caleydo.view.enroute.event.ShowPathEvent;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.ANode;
import org.caleydo.view.enroute.path.node.BranchSummaryNode;
import org.caleydo.view.enroute.path.node.ComplexNode;
import org.caleydo.view.enroute.path.node.CompoundNode;
import org.caleydo.view.enroute.path.node.GeneNode;
import org.caleydo.view.enroute.path.node.mode.ComplexNodeLinearizedMode;
import org.caleydo.view.enroute.path.node.mode.ComplexNodePreviewMode;
import org.caleydo.view.enroute.path.node.mode.CompoundNodeLinearizedMode;
import org.caleydo.view.enroute.path.node.mode.CompoundNodePreviewMode;
import org.caleydo.view.enroute.path.node.mode.GeneNodeLinearizedMode;
import org.caleydo.view.enroute.path.node.mode.GeneNodePreviewMode;
import org.caleydo.view.pathway.ESampleMappingMode;
import org.caleydo.view.pathway.GLPathway;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

/**
 * Renderer that is responsible for rendering a single pathway path.
 *
 * @author Christian Partl
 *
 */
public abstract class APathwayPathRenderer extends ALayoutRenderer implements IEventBasedSelectionManagerUser,
		IPathwayRepresentation {

	protected static final int MAX_BRANCH_SWITCHING_PATH_LENGTH = 5;

	/**
	 * Strategy that defines the update behavior of this path renderer.F
	 */
	protected APathUpdateStrategy updateStrategy;

	/**
	 * The list of path segments that are a list of {@link PathwayVertexRep}s.
	 */
	protected List<List<PathwayVertexRep>> pathSegments = new ArrayList<>();

	/**
	 * List of renderable nodes for the path.
	 */
	protected List<ALinearizableNode> pathNodes = new ArrayList<>();

	/**
	 * View that renders this renderer.
	 */
	protected final AGLView view;

	/**
	 * Table perspectives for node previews.
	 */
	protected List<TablePerspective> tablePerspectives = new ArrayList<>();

	/**
	 * Event space that is used for receiving and sending path events.
	 */
	protected String pathwayPathEventSpace = GLPathway.DEFAULT_PATHWAY_PATH_EVENT_SPACE;

	/**
	 * Minimum width in pixels required by the renderer.
	 */
	protected int minWidthPixels;

	/**
	 * Minimum height in pixels required by the renderer.
	 */
	protected int minHeightPixels;

	/**
	 * If set the path renderer only renders path segments that belong to this pathway.
	 */
	protected PathwayGraph pathway;

	/**
	 * Configuration that determines the size of individual path elements.
	 */
	protected PathSizeConfiguration sizeConfig = PathSizeConfiguration.DEFAULT;

	/**
	 * Context menu items that shall be displayed when right-clicking on any node.
	 */
	protected List<VertexRepBasedContextMenuItem> nodeContextMenuItems = new ArrayList<>();

	/**
	 * Branch summary node that is currently expanded
	 */
	protected BranchSummaryNode expandedBranchSummaryNode;

	/**
	 * Map that associates the linearized nodes with their incoming branch summary nodes.
	 */
	protected Map<ANode, BranchSummaryNode> linearizedNodesToIncomingBranchSummaryNodesMap = new HashMap<>();

	/**
	 * Map that associates the linearized nodes with their outgoing branch summary nodes.
	 */
	protected Map<ANode, BranchSummaryNode> linearizedNodesToOutgoingBranchSummaryNodesMap = new HashMap<>();

	/**
	 * Map that associates every node in a branch with a linearized node.
	 */
	protected Map<ALinearizableNode, ALinearizableNode> branchNodesToLinearizedNodesMap = new HashMap<>();

	/**
	 * Allows to trigger a {@link ShowPathEvent} for branch paths via context menu.
	 */
	protected boolean allowBranchPathExtraction = false;

	/**
	 * Event space that shall be used when triggering a {@link ShowPathEvent}.
	 */
	protected String branchPathExtractionEventSpace;

	/**
	 * Alpha value that shall be used by nodes for rendering.s
	 */
	protected float nodeAlpha = 1f;

	/**
	 * Events that should be triggered when selecting a node.
	 */
	protected Map<PickingMode, List<IVertexRepBasedEventFactory>> nodeEvents = new HashMap<>();

	protected TablePerspective mappedPerspective;

	protected ESampleMappingMode sampleMappingMode;

	protected int layoutDisplayListIndex = -1;
	private boolean isLayoutDirty = true;

	protected EventBasedSelectionManager geneSelectionManager;
	protected EventBasedSelectionManager metaboliteSelectionManager;
	protected EventBasedSelectionManager sampleSelectionManager;
	protected EventBasedSelectionManager vertexSelectionManager;

	protected PixelGLConverter pixelGLConverter;
	protected CaleydoTextRenderer textRenderer;

	public APathwayPathRenderer(AGLView view, List<TablePerspective> tablePerspectives) {
		this.view = view;
		addTablePerspectives(tablePerspectives);
		this.pixelGLConverter = view.getPixelGLConverter();
		this.textRenderer = view.getTextRenderer();

		vertexSelectionManager = new EventBasedSelectionManager(this, IDType.getIDType(EGeneIDTypes.PATHWAY_VERTEX_REP
				.name()));
		vertexSelectionManager.registerEventListeners();
		geneSelectionManager = new EventBasedSelectionManager(this, IDType.getIDType("DAVID"));
		geneSelectionManager.registerEventListeners();

		metaboliteSelectionManager = new EventBasedSelectionManager(this, IDType.getIDType("METABOLITE"));
		metaboliteSelectionManager.registerEventListeners();

		List<GeneticDataDomain> dataDomains = DataDomainManager.get().getDataDomainsByType(GeneticDataDomain.class);
		if (dataDomains.size() != 0) {
			IDType sampleIDType = dataDomains.get(0).getSampleIDType().getIDCategory().getPrimaryMappingType();
			sampleSelectionManager = new EventBasedSelectionManager(this, sampleIDType);
			sampleSelectionManager.registerEventListeners();
		}
	}

	/**
	 * Method that initializes the {@link APathwayPathRenderer}. Shall be called once prior use.
	 */
	public void init() {
		updateStrategy.registerEventListeners();
	}

	/**
	 * Sets a new path to be linearized.
	 *
	 * @param pathSegments
	 *            List of path segments that are a List of {@link PathwayVertexRep}s. The last node of segment n and the
	 *            first node of segment n+1 must be equivalent (i.e. they must refer to the same {@link PathwayVertex}
	 *            objects).
	 */
	public void setPath(List<List<PathwayVertexRep>> pathSegments) {
		this.pathSegments = pathSegments;

		createNodes(pathSegments);
		updateStrategy.nodesCreated();

		PathRendererChangedEvent event = new PathRendererChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);

		updateLayout();

	}

	/**
	 * Updates the layout of the path. This method should be called everytime something changes in the layout of the
	 * path.
	 */
	protected abstract void updateLayout();

	@Override
	protected abstract void renderContent(GL2 gl);

	@Override
	protected abstract boolean permitsWrappingDisplayLists();

	protected void createNodes(List<List<PathwayVertexRep>> pathSegments) {

		destroyNodes();
		pathNodes.clear();
		for (List<PathwayVertexRep> vertexReps : pathSegments) {
			List<ALinearizableNode> currentNodes = new ArrayList<>();
			if (pathway == null || (vertexReps.size() > 0 && vertexReps.get(0).getPathway() == pathway)) {
				createNodesForList(currentNodes, vertexReps);
				appendNodes(pathNodes, currentNodes);
			}
		}

		expandedBranchSummaryNode = null;

		branchNodesToLinearizedNodesMap.clear();
		linearizedNodesToIncomingBranchSummaryNodesMap.clear();
		linearizedNodesToOutgoingBranchSummaryNodesMap.clear();

		// Create branch nodes
		for (int i = 0; i < pathNodes.size(); i++) {

			ALinearizableNode currentNode = pathNodes.get(i);
			BranchSummaryNode incomingNode = new BranchSummaryNode(view, currentNode, this);
			incomingNode.init();
			BranchSummaryNode outgoingNode = new BranchSummaryNode(view, currentNode, this);
			outgoingNode.init();
			List<ALinearizableNode> sourceNodes = new ArrayList<ALinearizableNode>();
			List<ALinearizableNode> targetNodes = new ArrayList<ALinearizableNode>();

			for (PathwayVertexRep currentVertexRep : currentNode.getVertexReps()) {
				PathwayVertexRep prevVertexRep = null;
				PathwayVertexRep nextVertexRep = null;
				PathwayGraph pathway = currentVertexRep.getPathway();

				if (i > 0) {
					ALinearizableNode prevNode = pathNodes.get(i - 1);
					for (PathwayVertexRep prevVR : prevNode.getVertexReps()) {
						if (prevVR.getPathway() == pathway) {
							DefaultEdge edge = pathway.getEdge(prevVR, currentVertexRep);
							if (edge != null) {
								prevVertexRep = prevVR;
							}
						}
					}
				}
				if (i != pathNodes.size() - 1) {
					ALinearizableNode nextNode = pathNodes.get(i + 1);
					for (PathwayVertexRep nextVR : nextNode.getVertexReps()) {
						if (nextVR.getPathway() == pathway) {
							DefaultEdge edge = pathway.getEdge(currentVertexRep, nextVR);
							if (edge != null) {
								nextVertexRep = nextVR;
							}
						}
					}
				}

				List<PathwayVertexRep> sourceVertexReps = Graphs.predecessorListOf(pathway, currentVertexRep);
				sourceVertexReps.remove(prevVertexRep);
				List<PathwayVertexRep> targetVertexReps = Graphs.successorListOf(pathway, currentVertexRep);
				targetVertexReps.remove(nextVertexRep);

				if (sourceVertexReps.size() > 0) {
					createNodesForList(sourceNodes, sourceVertexReps);
				}

				if (targetVertexReps.size() > 0) {
					createNodesForList(targetNodes, targetVertexReps);
				}
			}

			if (sourceNodes.size() > 0) {
				incomingNode.setBranchNodes(sourceNodes);
				linearizedNodesToIncomingBranchSummaryNodesMap.put(currentNode, incomingNode);
				for (ALinearizableNode node : sourceNodes) {
					setPreviewMode(node);
					branchNodesToLinearizedNodesMap.put(node, currentNode);
				}
			}

			if (targetNodes.size() > 0) {
				outgoingNode.setBranchNodes(targetNodes);
				linearizedNodesToOutgoingBranchSummaryNodesMap.put(currentNode, outgoingNode);
				for (ALinearizableNode node : targetNodes) {
					setPreviewMode(node);
					branchNodesToLinearizedNodesMap.put(node, currentNode);
				}
			}
		}
	}

	public void setPreviewMode(ALinearizableNode node) {
		if (node instanceof ComplexNode) {
			node.setMode(new ComplexNodePreviewMode(view, this));
		} else if (node instanceof CompoundNode) {
			node.setMode(new CompoundNodePreviewMode(view, this));
		} else {
			node.setMode(new GeneNodePreviewMode(view, this));
		}
	}

	/**
	 * Destroys all nodes.
	 */
	protected void destroyNodes() {
		for (ANode node : pathNodes) {
			node.destroy();
		}
		for (ALinearizableNode node : branchNodesToLinearizedNodesMap.keySet()) {
			node.destroy();
		}
		for (BranchSummaryNode node : linearizedNodesToIncomingBranchSummaryNodesMap.values()) {
			node.destroy();
		}
		for (BranchSummaryNode node : linearizedNodesToOutgoingBranchSummaryNodesMap.values()) {
			node.destroy();
		}
	}

	/**
	 * Merges the last node of pathNodes with the first node of nodesToAppend and adds the remaining nodesToAppend to
	 * pathNodes.
	 *
	 * @param pathNodes
	 * @param nodesToAppend
	 */
	protected void appendNodes(List<ALinearizableNode> pathNodes, List<ALinearizableNode> nodesToAppend) {
		if (pathNodes.size() <= 0) {
			pathNodes.addAll(nodesToAppend);
		} else {
			if (nodesToAppend.size() > 0) {
				ALinearizableNode lastNodeOfPath = pathNodes.get(pathNodes.size() - 1);
				ALinearizableNode firstNodeOfNodesToAppend = nodesToAppend.get(0);
				if (mergeNodes(lastNodeOfPath, firstNodeOfNodesToAppend)) {
					nodesToAppend.remove(0);
					firstNodeOfNodesToAppend.destroy();
				}
				pathNodes.addAll(nodesToAppend);
			}
		}
	}

	/**
	 * Merges node1 with node2, i.e., the {@link PathwayVertexRep}s from node2 are added to node1, if they are
	 * equivalent.
	 *
	 * @param node1
	 * @param node2
	 * @return True, if the nodes were merged, false otherwise.
	 */
	protected boolean mergeNodes(ALinearizableNode node1, ALinearizableNode node2) {
		if (node1.getMappedDavidIDs().size() == node2.getMappedDavidIDs().size()
				&& node1.getMappedDavidIDs().containsAll(node2.getMappedDavidIDs())) {

			for (PathwayVertexRep vertexRep : node2.getVertexReps()) {
				node1.addPathwayVertexRep(vertexRep);
			}
			if (node1 instanceof ComplexNode) {
				List<ALinearizableNode> nodesOfNode1 = ((ComplexNode) node1).getNodes();
				for (ALinearizableNode node1Child : nodesOfNode1) {
					List<ALinearizableNode> nodesOfNode2 = ((ComplexNode) node2).getNodes();
					for (ALinearizableNode node2Child : nodesOfNode2) {
						if (node1Child.getMappedDavidIDs().size() == node2Child.getMappedDavidIDs().size()
								&& node1Child.getMappedDavidIDs().containsAll(node2Child.getMappedDavidIDs())) {
							mergeNodes(node1Child, node2Child);
						}
					}
				}
			}
			return true;
		}
		return false;
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
				ComplexNode complexNode = new ComplexNode(this, textRenderer, view, new ComplexNodeLinearizedMode(view,
						this));
				complexNode.setNodes(groupedNodes);
				for (ALinearizableNode groupedNode : groupedNodes) {
					groupedNode.setParentNode(complexNode);
				}
				complexNode.addPathwayVertexRep(currentVertexRep);
				complexNode.init();
				node = complexNode;
			} else if (currentVertexRep.getType() == EPathwayVertexType.compound) {
				CompoundNode compoundNode = new CompoundNode(this, view, new CompoundNodeLinearizedMode(view, this));

				compoundNode.addPathwayVertexRep(currentVertexRep);
				compoundNode.init();
				node = compoundNode;

			} else {

				// TODO: Verify that this is also the right approach for
				// enzymes and ortholog
				GeneNode geneNode = new GeneNode(this, textRenderer, view, new GeneNodeLinearizedMode(view, this));
				geneNode.setLabel(currentVertexRep.getShortName());
				geneNode.addPathwayVertexRep(currentVertexRep);
				geneNode.init();
				node = geneNode;
			}
			node.addPickingListener(new NodeContextMenuPickingListener(node));
			nodes.add(node);
		}

	}

	@Override
	public void destroy(GL2 gl) {
		destroyNodes();
		geneSelectionManager.unregisterEventListeners();
		metaboliteSelectionManager.unregisterEventListeners();
		sampleSelectionManager.unregisterEventListeners();
		vertexSelectionManager.unregisterEventListeners();
		updateStrategy.unregisterEventListeners();
		super.destroy(gl);
	}

	@Override
	public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {

	}

	/**
	 * @return the rowSelectionManager, see {@link #rowSelectionManager}
	 */
	public EventBasedSelectionManager getGeneSelectionManager() {
		return geneSelectionManager;
	}

	/**
	 * @return the metaboliteSelectionManager, see {@link #metaboliteSelectionManager}
	 */
	public EventBasedSelectionManager getMetaboliteSelectionManager() {
		return metaboliteSelectionManager;
	}

	/**
	 * @return the sampleSelectionManager, see {@link #sampleSelectionManager}
	 */
	public EventBasedSelectionManager getSampleSelectionManager() {
		return sampleSelectionManager;
	}

	/**
	 * @param tablePerspectives
	 *            setter, see {@link tablePerspectives}
	 */
	public void setTablePerspectives(List<TablePerspective> tablePerspectives) {
		this.tablePerspectives = tablePerspectives;
	}

	public void addTablePerspectives(List<TablePerspective> tablePerspectives) {
		for (TablePerspective tablePerspective : tablePerspectives) {
			if (!(tablePerspective.getDataDomain() instanceof GeneticDataDomain))
				continue;
			if (this.tablePerspectives.contains(tablePerspective))
				continue;

			this.tablePerspectives.add(tablePerspective);
		}
		setLayoutDirty(true);
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

	@Override
	protected void prepare() {
		updateStrategy.processEvents();
		// if (isDisplayListDirty()) {
		// updateLayout();
		// }
	}

	@Override
	public void setLimits(float x, float y) {
		super.setLimits(x, y);
		updateLayout();
	}

	/**
	 * Selects a branch node to be linearized.
	 *
	 * @param node
	 */
	public void selectBranch(ALinearizableNode node) {

		List<List<PathwayVertexRep>> newPathSegments = getBranchPath(node);

		if (updateStrategy.isPathChangePermitted(newPathSegments)) {
			setExpandedBranchSummaryNode(null);
			setPath(newPathSegments);
			updateStrategy.triggerPathUpdate();
			// setDisplayListDirty(true);
		}
	}

	/**
	 * Determines the path consisting of the original path up to the node where the branching occurrs, and the branch.
	 *
	 * @param branchNode
	 *            The branch node.
	 * @return
	 */
	protected List<List<PathwayVertexRep>> getBranchPath(ALinearizableNode branchNode) {
		ALinearizableNode linearizedNode = branchNodesToLinearizedNodesMap.get(branchNode);
		BranchSummaryNode summaryNode = linearizedNodesToIncomingBranchSummaryNodesMap.get(linearizedNode);

		boolean isIncomingBranch = false;
		if (summaryNode != null && summaryNode.getBranchNodes().contains(branchNode)) {
			isIncomingBranch = true;
		}

		// A branch node should only have one vertex rep.
		PathwayVertexRep branchVertexRep = branchNode.getPrimaryPathwayVertexRep();
		PathwayGraph pathway = branchVertexRep.getPathway();
		PathwayVertexRep linearizedVertexRep = null;
		for (PathwayVertexRep vertexRep : linearizedNode.getVertexReps()) {
			DefaultEdge edge = null;
			if (isIncomingBranch) {
				edge = pathway.getEdge(branchVertexRep, vertexRep);
			} else {
				edge = pathway.getEdge(vertexRep, branchVertexRep);
			}
			if (edge != null) {
				linearizedVertexRep = vertexRep;
			}
		}

		Pair<Integer, Integer> indexPair = determinePathSegmentAndIndexOfPathNode(linearizedNode, linearizedVertexRep);
		int segmentIndex = indexPair.getFirst();
		int vertexRepIndex = indexPair.getSecond();

		List<PathwayVertexRep> newSegment = null;
		List<List<PathwayVertexRep>> newPathSegments = new ArrayList<>();
		List<PathwayVertexRep> branchPath = PathwayManager.get().determineDirectionalPath(branchVertexRep,
				!isIncomingBranch, MAX_BRANCH_SWITCHING_PATH_LENGTH);

		if (isIncomingBranch) {
			// insert above linearized node
			Collections.reverse(branchPath);
			newSegment = new ArrayList<>(pathSegments.get(segmentIndex).subList(vertexRepIndex,
					pathSegments.get(segmentIndex).size()));
			newSegment.addAll(0, branchPath);
			newPathSegments.add(newSegment);
			if (segmentIndex + 1 < pathSegments.size())
				newPathSegments.addAll(new ArrayList<>(pathSegments.subList(segmentIndex + 1, pathSegments.size())));

		} else {
			// insert below linearized node

			newSegment = new ArrayList<>(pathSegments.get(segmentIndex).subList(0, vertexRepIndex + 1));
			newSegment.addAll(branchPath);
			if (segmentIndex > 0)
				newPathSegments.addAll(new ArrayList<>(pathSegments.subList(0, segmentIndex)));
			newPathSegments.add(newSegment);
		}

		return newPathSegments;
	}

	/**
	 * @param currentExpandedBranchNode
	 *            setter, see {@link #expandedBranchSummaryNode}
	 */
	public void setExpandedBranchSummaryNode(BranchSummaryNode expandedBranchSummaryNode) {
		if (this.expandedBranchSummaryNode != expandedBranchSummaryNode) {
			this.expandedBranchSummaryNode = expandedBranchSummaryNode;
			// for (ALinearizableNode node : pathNodes) {
			// node.setPickable(expandedBranchSummaryNode == null);
			// }

			PathRendererChangedEvent event = new PathRendererChangedEvent(this);
			event.setSender(this);
			GeneralManager.get().getEventPublisher().triggerEvent(event);
			updateLayout();
			// setDisplayListDirty(true);
		}
	}

	/**
	 * @return the expandedBranchSummaryNode, see {@link #expandedBranchSummaryNode}
	 */
	public BranchSummaryNode getExpandedBranchSummaryNode() {
		return expandedBranchSummaryNode;
	}

	/**
	 * Removes the specified node from the path if it is at the start or the end of the path.
	 *
	 * @param node
	 */
	public void removeNodeFromPath(ALinearizableNode node) {

		// Create deep copy of pathSegments
		List<List<PathwayVertexRep>> segments = new ArrayList<>(pathSegments.size());
		for (List<PathwayVertexRep> segment : pathSegments) {
			segments.add(new ArrayList<>(segment));
		}

		if (isFirstNode(node)) {
			List<PathwayVertexRep> segment = segments.get(0);
			segment.remove(0);
			if (segment.size() == 0) {
				if (segments.size() > 1) {
					List<PathwayVertexRep> nextSegment = segments.get(1);
					nextSegment.remove(0);
				}
				segments.remove(segment);
			}
		} else if (isLastNode(node)) {
			List<PathwayVertexRep> segment = segments.get(segments.size() - 1);
			segment.remove(segment.size() - 1);
			if (segment.size() == 0) {
				if (segments.size() > 1) {
					List<PathwayVertexRep> prevSegment = segments.get(segments.size() - 2);
					prevSegment.remove(prevSegment.size() - 1);
				}
				segments.remove(segment);
			}

		} else {
			return;
		}

		if (updateStrategy.isPathChangePermitted(segments)) {
			setPath(segments);
			updateStrategy.triggerPathUpdate();
		}
	}

	/**
	 * Determines, whether the specified node is the first node of the whole path. Note, that if this path renderer only
	 * displays segments of a certain pathway, the first rendered node might not be the first node in the path.
	 *
	 * @param node
	 * @return
	 */
	public boolean isFirstNode(ALinearizableNode node) {
		if (pathway == null) {
			return pathNodes.get(0) == node;
		} else {
			List<PathwayVertexRep> firstSegment = pathSegments.get(0);
			for (PathwayVertexRep vertexRep : node.getVertexReps()) {
				if (firstSegment.get(0) == vertexRep) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Determines, whether the specified node is the last node of the whole path. Note, that if this path renderer only
	 * displays segments of a certain pathway, the last rendered node might not be the last node in the path.
	 *
	 * @param node
	 * @return
	 */
	public boolean isLastNode(ALinearizableNode node) {
		if (pathway == null) {
			return pathNodes.get(pathNodes.size() - 1) == node;
		} else {
			List<PathwayVertexRep> firstSegment = pathSegments.get(pathSegments.size() - 1);
			for (PathwayVertexRep vertexRep : node.getVertexReps()) {
				if (firstSegment.get(firstSegment.size() - 1) == vertexRep) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Determines the indices of the path segment (first) and the index of the vertexRep (second) within a segment a
	 * path node belongs to.
	 *
	 * @param node
	 * @param vertexRep
	 * @return
	 */
	protected Pair<Integer, Integer> determinePathSegmentAndIndexOfPathNode(ALinearizableNode node,
			PathwayVertexRep vertexRep) {
		int linearizedNodeIndex = pathNodes.indexOf(node);
		int correspondingIndex = 0;
		for (int i = 0; i < pathSegments.size(); i++) {
			List<PathwayVertexRep> segment = pathSegments.get(i);
			if (pathway == null || (segment.size() > 0 && segment.get(0).getPathway() == pathway)) {
				for (int j = 0; j < segment.size(); j++) {
					PathwayVertexRep currentVertexRep = segment.get(j);
					if (correspondingIndex == linearizedNodeIndex && vertexRep == currentVertexRep) {
						return new Pair<Integer, Integer>(i, j);
					}
					correspondingIndex++;
				}
				// Decrement corresponding index, because a single node refers to two vertexReps at the beginning and
				// the
				// end of a segment
				correspondingIndex--;
			}
		}
		return null;
	}

	@Override
	public int getMinWidthPixels() {
		return minWidthPixels;
	}

	@Override
	public int getMinHeightPixels() {
		return minHeightPixels;
	}

	/**
	 * @param pixelGLConverter
	 *            setter, see {@link pixelGLConverter}
	 */
	public void setPixelGLConverter(PixelGLConverter pixelGLConverter) {
		this.pixelGLConverter = pixelGLConverter;
	}

	/**
	 * @param textRenderer
	 *            setter, see {@link textRenderer}
	 */
	public void setTextRenderer(CaleydoTextRenderer textRenderer) {
		this.textRenderer = textRenderer;
	}

	/**
	 * @param pathway
	 *            setter, see {@link pathway}
	 */
	public void setPathway(PathwayGraph pathway) {
		this.pathway = pathway;
	}

	/**
	 * @return the sizeConfig, see {@link #sizeConfig}
	 */
	public PathSizeConfiguration getSizeConfig() {
		return sizeConfig;
	}

	/**
	 * @param sizeConfig
	 *            setter, see {@link sizeConfig}
	 */
	public void setSizeConfig(PathSizeConfiguration sizeConfig) {
		this.sizeConfig = sizeConfig;
		if (pathSegments != null && !pathSegments.isEmpty())
			updateLayout();
	}

	@Override
	public PathwayGraph getPathway() {
		if (pathway != null)
			return pathway;
		if (pathSegments != null && pathSegments.size() > 0) {
			return pathSegments.get(pathSegments.size() - 1).get(0).getPathway();
		}
		return null;
	}

	@Override
	public List<PathwayGraph> getPathways() {
		List<PathwayGraph> pathways = new ArrayList<>();
		if (pathway != null) {
			pathways.add(pathway);
		} else if (pathSegments != null) {
			for (List<PathwayVertexRep> segment : pathSegments) {
				pathways.add(segment.get(0).getPathway());
			}
			// we do not want duplicates, but the general order should be preserved.
			pathways = new ArrayList<>(new LinkedHashSet<PathwayGraph>(pathways));
		}

		return pathways;
	}

	@Override
	public Rectangle2D getVertexRepBounds(PathwayVertexRep vertexRep) {
		for (ALinearizableNode node : pathNodes) {
			for (PathwayVertexRep vertexRepOfNode : node.getVertexReps()) {
				if (vertexRepOfNode == vertexRep) {
					Rectangle2D bounds = getLeftTopAlignedNodeBounds(node);
					if (bounds != null)
						return bounds;
				}
			}
		}
		return null;
	}

	protected Rectangle2D getLeftTopAlignedNodeBounds(ALinearizableNode node) {
		Vec3f glNodePosition = node.getPosition();
		if (glNodePosition == null)
			return null;

		int posX = pixelGLConverter.getPixelWidthForGLWidth(glNodePosition.x()) - (int) (node.getWidthPixels() / 2.0f);
		int posY = pixelGLConverter.getPixelHeightForGLHeight(y - glNodePosition.y())
				- (int) (node.getHeightPixels() / 2.0f);

		return new Rectangle2D.Float(posX, posY, node.getWidthPixels(), node.getHeightPixels());
	}

	@Override
	public List<Rectangle2D> getVertexRepsBounds(PathwayVertexRep vertexRep) {
		List<Rectangle2D> boundsList = new ArrayList<>();

		for (ALinearizableNode node : pathNodes) {
			for (PathwayVertexRep vertexRepOfNode : node.getVertexReps()) {
				if (vertexRepOfNode == vertexRep) {
					Rectangle2D bounds = getLeftTopAlignedNodeBounds(node);
					if (bounds != null)
						boundsList.add(bounds);
				}
			}
		}
		return boundsList;
	}

	@Override
	public synchronized void addVertexRepBasedContextMenuItem(VertexRepBasedContextMenuItem item) {
		nodeContextMenuItems.add(item);
	}

	/**
	 * Sets the update strategy of this path. Note, that a strategy can only be set once.
	 *
	 * @param updateStrategy
	 *            setter, see {@link updateStrategy}
	 */
	public void setUpdateStrategy(APathUpdateStrategy updateStrategy) {
		if (this.updateStrategy != null) {
			return;
		}
		this.updateStrategy = updateStrategy;
	}

	/**
	 * @return the updateStrategy, see {@link #updateStrategy}
	 */
	public APathUpdateStrategy getUpdateStrategy() {
		return updateStrategy;
	}

	protected class NodeContextMenuPickingListener extends APickingListener {
		private ALinearizableNode node;

		public NodeContextMenuPickingListener(ALinearizableNode node) {
			this.node = node;
		}

		@Override
		protected void clicked(Pick pick) {
			if (!node.isPickable() || branchNodesToLinearizedNodesMap.keySet().contains(node))
				return;
			triggerEvents(pick.getPickingMode());
		}

		@Override
		protected void mouseOver(Pick pick) {
			if (!node.isPickable())
				return;
			triggerEvents(pick.getPickingMode());
		}

		@Override
		protected void mouseOut(Pick pick) {
			if (!node.isPickable())
				return;
			triggerEvents(pick.getPickingMode());
		}

		@Override
		protected void rightClicked(Pick pick) {
			if (!node.isPickable())
				return;
			triggerEvents(pick.getPickingMode());
			addContextMenuItems(nodeContextMenuItems);
			if (allowBranchPathExtraction && branchNodesToLinearizedNodesMap.keySet().contains(node)) {
				ShowPathEvent event = new ShowPathEvent(getBranchPath(node));
				event.setEventSpace(branchPathExtractionEventSpace);
				view.getContextMenuCreator().addContextMenuItem(new GenericContextMenuItem("Show Branch Path", event));
			}
		}

		private void triggerEvents(PickingMode pickingMode) {
			List<IVertexRepBasedEventFactory> factories = nodeEvents.get(pickingMode);
			if (factories != null) {
				for (IVertexRepBasedEventFactory factory : factories) {
					factory.triggerEvent(node.getPrimaryPathwayVertexRep());
				}
			}
		}

		private void addContextMenuItems(List<VertexRepBasedContextMenuItem> items) {
			for (VertexRepBasedContextMenuItem item : items) {
				// Only use primary vertex rep
				item.setVertexRep(node.getPrimaryPathwayVertexRep());
				view.getContextMenuCreator().addContextMenuItem(item);
			}
		}
	}

	@Override
	public void addVertexRepBasedSelectionEvent(IVertexRepBasedEventFactory eventFactory, PickingMode pickingMode) {
		List<IVertexRepBasedEventFactory> factories = nodeEvents.get(pickingMode);
		if (factories == null) {
			factories = new ArrayList<>();
			nodeEvents.put(pickingMode, factories);
		}
		factories.add(eventFactory);
	}

	/**
	 * @param allowBranchPathExtraction
	 *            setter, see {@link allowBranchPathExtraction}
	 */
	public void setAllowBranchPathExtraction(boolean allowBranchPathExtraction) {
		this.allowBranchPathExtraction = allowBranchPathExtraction;
	}

	/**
	 * @param branchPathExtractionEventSpace
	 *            setter, see {@link branchPathExtractionEventSpace}
	 */
	public void setBranchPathExtractionEventSpace(String branchPathExtractionEventSpace) {
		this.branchPathExtractionEventSpace = branchPathExtractionEventSpace;
	}

	/**
	 * @param minHeightPixels
	 *            setter, see {@link minHeightPixels}
	 */
	public void setMinHeightPixels(int minHeightPixels) {
		if (minHeightPixels == this.minHeightPixels)
			return;
		this.minHeightPixels = minHeightPixels;
		MinSizeUpdateEvent event = new MinSizeUpdateEvent(this, minHeightPixels, minWidthPixels);
		event.setEventSpace(pathwayPathEventSpace);
		EventPublisher.INSTANCE.triggerEvent(event);
	}

	/**
	 * @param minWidthPixels
	 *            setter, see {@link minWidthPixels}
	 */
	public void setMinWidthPixels(int minWidthPixels) {
		if (minWidthPixels == this.minWidthPixels)
			return;
		this.minWidthPixels = minWidthPixels;
		MinSizeUpdateEvent event = new MinSizeUpdateEvent(this, minHeightPixels, minWidthPixels);
		event.setEventSpace(pathwayPathEventSpace);
		EventPublisher.INSTANCE.triggerEvent(event);
	}

	/**
	 * @return the nodeAlpha, see {@link #nodeAlpha}
	 */
	public float getNodeAlpha() {
		return nodeAlpha;
	}

	/**
	 * @param isLayoutDirty
	 *            setter, see {@link isLayoutDirty}
	 */
	public void setLayoutDirty(boolean isLayoutDirty) {
		this.isLayoutDirty = isLayoutDirty;
	}

	/**
	 * @return the isLayoutDirty, see {@link #isLayoutDirty}
	 */
	public boolean isLayoutDirty() {
		return isLayoutDirty;
	}

	/**
	 * @return the vertexSelectionManager, see {@link #vertexSelectionManager}
	 */
	public EventBasedSelectionManager getVertexSelectionManager() {
		return vertexSelectionManager;
	}

	/**
	 * @return the sampleMappingMode, see {@link #sampleMappingMode}
	 */
	public ESampleMappingMode getSampleMappingMode() {
		return sampleMappingMode;
	}

	/**
	 * @return the mappedPerspective, see {@link #mappedPerspective}
	 */
	public TablePerspective getMappedPerspective() {
		return mappedPerspective;
	}

	/**
	 * @param mappedPerspective
	 *            setter, see {@link mappedPerspective}
	 */
	public void setMappedPerspective(TablePerspective mappedPerspective) {
		this.mappedPerspective = mappedPerspective;
	}

	/**
	 * @param sampleMappingMode
	 *            setter, see {@link sampleMappingMode}
	 */
	public void setSampleMappingMode(ESampleMappingMode sampleMappingMode) {
		this.sampleMappingMode = sampleMappingMode;
	}
}
