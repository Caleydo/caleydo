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

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGroupRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.BranchSummaryNode;
import org.caleydo.view.enroute.path.node.ComplexNode;
import org.caleydo.view.enroute.path.node.CompoundNode;
import org.caleydo.view.enroute.path.node.GeneNode;

/**
 * Renderer that is responsible for rendering a single pathway path.
 *
 * @author Christian Partl
 *
 */
public class PathwayPathRenderer extends ALayoutRenderer implements IEventBasedSelectionManagerUser {

	/**
	 * The pathway graph the rendered path belongs to.
	 */
	private PathwayGraph pathway;

	// /**
	// * The list of {@link PathwayVertexRep}s that represents the path.
	// */
	// private List<PathwayVertexRep> path;

	/**
	 * List of renderable nodes for the path.
	 */
	private List<ALinearizableNode> pathNodes;

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

	private EventBasedSelectionManager geneSelectionManager;
	private EventBasedSelectionManager metaboliteSelectionManager;
	private EventBasedSelectionManager sampleSelectionManager;

	public PathwayPathRenderer(AGLView view, EventBasedSelectionManager geneSelectionManager,
			EventBasedSelectionManager metaboliteSelectionManager, EventBasedSelectionManager sampleSelectionManager,
			List<TablePerspective> tablePerspectives) {
		this.view = view;

		this.geneSelectionManager = geneSelectionManager;
		this.metaboliteSelectionManager = metaboliteSelectionManager;
		this.sampleSelectionManager = sampleSelectionManager;

		this.tablePerspectives = tablePerspectives;

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
		// this.path = path;

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
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

	@Override
	public void destroy(GL2 gl) {
		geneSelectionManager.unregisterEventListeners();
		metaboliteSelectionManager.unregisterEventListeners();
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
}
