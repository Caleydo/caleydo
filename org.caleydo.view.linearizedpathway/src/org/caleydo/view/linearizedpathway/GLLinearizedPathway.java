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
package org.caleydo.view.linearizedpathway;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.SetMinViewSizeEvent;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IMultiDataContainerBasedView;
import org.caleydo.core.view.listener.AddDataContainersEvent;
import org.caleydo.core.view.listener.AddDataContainersListener;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.connectionline.ClosedArrowRenderer;
import org.caleydo.core.view.opengl.util.connectionline.ConnectionLineRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineCrossingRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineEndArrowRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineEndStaticLineRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineLabelRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.edge.EPathwayReactionEdgeType;
import org.caleydo.datadomain.pathway.graph.item.edge.EPathwayRelationEdgeSubType;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayReactionEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayRelationEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGroupRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.PathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.linearizedpathway.listener.LinearizePathwayPathEventListener;
import org.caleydo.view.linearizedpathway.mappeddataview.MappedDataRenderer;
import org.caleydo.view.linearizedpathway.node.ALinearizableNode;
import org.caleydo.view.linearizedpathway.node.ANode;
import org.caleydo.view.linearizedpathway.node.BranchSummaryNode;
import org.caleydo.view.linearizedpathway.node.ComplexNode;
import org.caleydo.view.linearizedpathway.node.CompoundNode;
import org.caleydo.view.linearizedpathway.node.GeneNode;
import org.caleydo.view.linearizedpathway.renderstyle.TemplateRenderStyle;
import org.caleydo.view.pathway.event.LinearizedPathwayPathEvent;
import org.caleydo.view.pathway.event.ShowBubbleSetForPathwayVertexRepsEvent;
import org.eclipse.swt.widgets.Composite;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

/**
 * Main view class for the linearized pathway view.
 * 
 * @author Christian Partl
 * @author Alexander Lex
 */

public class GLLinearizedPathway extends AGLView implements IMultiDataContainerBasedView {

	public final static int DEFAULT_DATA_ROW_HEIGHT_PIXELS = 60;
	public final static int BRANCH_COLUMN_WIDTH_PIXELS = 200;
	public final static int PATHWAY_COLUMN_WIDTH_PIXELS = 150;
	public final static int MIN_NODE_DISTANCE_PIXELS = 70;
	public final static int TOP_SPACING_PIXELS = 60;
	public final static int BOTTOM_SPACING_PIXELS = 60;
	public final static int PREVIEW_NODE_DATA_ROW_HEIGHT_PIXELS = 40;
	public final static int BRANCH_SUMMARY_NODE_TO_LINEARIZED_NODE_VERTICAL_DISTANCE_PIXELS = 20;
	public final static int EXPANDED_BRANCH_NODE_SPACING_PIXELS = 20;
	public final static int EXPANDED_BRANCH_NODE_WIDTH_PIXELS = 150;
	public final static int SPACING_PIXELS = 2;
	public final static int BRANCH_AREA_SIDE_SPACING_PIXELS = 8;

	public final static int DEFAULT_MAX_BRANCH_SWITCHING_PATH_LENGTH = 5;

	public final static String VIEW_TYPE = "org.caleydo.view.linearizedpathway";

	private TemplateRenderStyle renderStyle;

	private int lastNodeId = 0;

	/**
	 * The pathway of the linearized path.
	 */
	private PathwayGraph pathway;

	/**
	 * The path of the pathway that is currently linearized.
	 */
	private List<PathwayVertexRep> path;

	/**
	 * The list of nodes that are rendered in a linearized way.
	 */
	private List<ALinearizableNode> linearizedNodes = new ArrayList<ALinearizableNode>();

	/**
	 * List of nodes that can currently be displayed in branches.
	 */
	private List<ANode> branchNodes = new ArrayList<ANode>();

	/**
	 * Map that associates the linearized nodes with their incoming branch
	 * summary nodes.
	 */
	private Map<ANode, ANode> linearizedNodesToIncomingBranchSummaryNodesMap = new HashMap<ANode, ANode>();

	/**
	 * Map that associates the linearized nodes with their outgoing branch
	 * summary nodes.
	 */
	private Map<ANode, ANode> linearizedNodesToOutgoingBranchSummaryNodesMap = new HashMap<ANode, ANode>();

	/**
	 * Map that associates every node in a branch with a linearized node.
	 */
	private Map<ANode, ALinearizableNode> branchNodesToLinearizedNodesMap = new HashMap<ANode, ALinearizableNode>();

	/**
	 * Map that associates each linearized node with the indices of the
	 * corresponding {@link PathwayVertexRep} objects in the {@link #path}.
	 */
	// private Map<ANode, List<Integer>>
	// linearizedNodesToPathwayVertexRepIndicesMap = new HashMap<ANode,
	// List<Integer>>();

	/**
	 * The number of rows in which data values are shown.
	 */
	private int numDataRows = 0;

	/**
	 * All genetic data domains.
	 */
	private List<GeneticDataDomain> geneticDataDomains = new ArrayList<GeneticDataDomain>();

	/**
	 * The pathway datadomain.
	 */
	private PathwayDataDomain pathwayDataDomain;

	/**
	 * The branch node that is currently expanded to show the possible branches.
	 */
	private BranchSummaryNode expandedBranchSummaryNode = null;

	private boolean isLayoutDirty = true;

	/**
	 * The renderer for the experimental data of the nodes in the linearized
	 * pathways.
	 */
	private MappedDataRenderer mappedDataRenderer;

	/**
	 * The current height for all data rows.
	 */
	private float dataRowHeight;

	/**
	 * The maximum number of nodes that are added at once to the linearized
	 * pathway when switching branches.
	 */
	private int maxBranchSwitchingPathLength = DEFAULT_MAX_BRANCH_SWITCHING_PATH_LENGTH;

	private LinearizePathwayPathEventListener linearizePathwayPathEventListener;
	private AddDataContainersListener addDataContainersListener;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLLinearizedPathway(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);

		viewType = GLLinearizedPathway.VIEW_TYPE;
		viewLabel = "Linearized Pathway";

		List<IDataDomain> dataDomains = DataDomainManager.get().getDataDomainsByType(
				"org.caleydo.datadomain.genetic");
		for (IDataDomain dataDomain : dataDomains) {
			geneticDataDomains.add((GeneticDataDomain) dataDomain);
		}
		pathwayDataDomain = (PathwayDataDomain) DataDomainManager.get()
				.getDataDomainByType("org.caleydo.datadomain.pathway");
		mappedDataRenderer = new MappedDataRenderer(this);

	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		renderStyle = new TemplateRenderStyle(viewFrustum);
		textRenderer = new CaleydoTextRenderer(24);

		super.renderStyle = renderStyle;
		detailLevel = EDetailLevel.HIGH;

		path = new ArrayList<PathwayVertexRep>();

		for (PathwayGraph graph : PathwayManager.get().getAllItems()) {
			if (graph.getType() == PathwayDatabaseType.KEGG
					&& graph.getTitle().startsWith("Glioma")) {
				pathway = graph;
				break;
			}
		}

		PathwayVertexRep currentVertex = null;
		for (PathwayVertexRep vertex : pathway.vertexSet()) {
			currentVertex = vertex;
			break;
		}

		// float currentPosition = 7.5f;

		for (int i = 0; i < 6; i++) {

			path.add(currentVertex);

			for (DefaultEdge edge : pathway.edgesOf(currentVertex)) {
				// PathwayVertexRep v1 = currentGraph.getEdgeSource(edge);
				PathwayVertexRep v2 = pathway.getEdgeTarget(edge);

				currentVertex = v2;
			}
		}

		setPath(pathway, path);

		// mappedDataRenderer.init(gl);
		// createNodes();

	}

	private void createNodes() {
		linearizedNodes.clear();
		numDataRows = 0;
		branchNodes.clear();
		branchNodesToLinearizedNodesMap.clear();
		linearizedNodesToIncomingBranchSummaryNodesMap.clear();
		linearizedNodesToOutgoingBranchSummaryNodesMap.clear();
		// linearizedNodesToPathwayVertexRepIndicesMap.clear();

		createNodesForList(linearizedNodes, path);

		// determine complex nodes first
		// List<ComplexNode> complexNodes = new ArrayList<ComplexNode>();
		// ComplexNode complexNode = null;
		// for (int i = 0; i < path.size(); i++) {
		// PathwayVertexRep currentVertexRep = path.get(i);
		// if (i + 1 < path.size()) {
		// PathwayVertexRep nextVertexRep = path.get(i + 1);
		// if (pathway.getEdge(currentVertexRep, nextVertexRep) == null
		// && pathway.getEdge(nextVertexRep, currentVertexRep) == null) {
		// if (complexNode == null) {
		// // nodeRenderers.add(complexNodeRenderer);
		// complexNode = new ComplexNode(pixelGLConverter, textRenderer,
		// this, lastNodeId++);
		// complexNode.addVertexRep(currentVertexRep);
		// // vertexRepToNodeMap.put(currentVertexRep,
		// // complexNode);
		// complexNodes.add(complexNode);
		// }
		//
		// complexNode.addVertexRep(nextVertexRep);
		// // vertexRepToNodeMap.put(nextVertexRep, complexNode);
		// } else {
		// if (complexNode != null) {
		// complexNode = null;
		// }
		// }
		// }
		// }

		// createNodesForList(linearizedNodes, path, true, complexNodes, true);

		// Create branch nodes
		for (int i = 0; i < linearizedNodes.size(); i++) {
			ALinearizableNode currentNode = linearizedNodes.get(i);
			PathwayVertexRep currentVertexRep = currentNode.getPathwayVertexRep();
			DefaultEdge prevEdge = null;
			DefaultEdge nextEdge = null;

			if (i > 0) {
				ALinearizableNode prevNode = linearizedNodes.get(i - 1);
				prevEdge = pathway.getEdge(prevNode.getPathwayVertexRep(),
						currentVertexRep);
				if (prevEdge == null)
					prevEdge = pathway.getEdge(currentVertexRep,
							prevNode.getPathwayVertexRep());
			}
			if (i != linearizedNodes.size() - 1) {
				ALinearizableNode nextNode = linearizedNodes.get(i + 1);
				nextEdge = pathway.getEdge(nextNode.getPathwayVertexRep(),
						currentVertexRep);
				if (nextEdge == null)
					nextEdge = pathway.getEdge(currentVertexRep,
							nextNode.getPathwayVertexRep());
			}

			Set<DefaultEdge> edges = pathway.edgesOf(currentVertexRep);

			BranchSummaryNode incomingNode = new BranchSummaryNode(this, lastNodeId++,
					currentNode);
			BranchSummaryNode outgoingNode = new BranchSummaryNode(this, lastNodeId++,
					currentNode);
			List<PathwayVertexRep> sourceVertexReps = new ArrayList<PathwayVertexRep>();
			List<PathwayVertexRep> targetVertexReps = new ArrayList<PathwayVertexRep>();

			for (DefaultEdge edge : edges) {
				if ((edge != prevEdge) && (edge != nextEdge)) {
					if (pathway.getEdgeTarget(edge) == currentVertexRep) {
						sourceVertexReps.add(pathway.getEdgeSource(edge));

					} else {
						targetVertexReps.add(pathway.getEdgeTarget(edge));
					}
				}
			}

			if (sourceVertexReps.size() > 0) {
				List<ALinearizableNode> sourceNodes = new ArrayList<ALinearizableNode>();

				createNodesForList(sourceNodes, sourceVertexReps);
				incomingNode.setBranchNodes(sourceNodes);
				linearizedNodesToIncomingBranchSummaryNodesMap.put(currentNode,
						incomingNode);
				branchNodes.add(incomingNode);
				branchNodes.addAll(sourceNodes);
				for (ANode node : sourceNodes) {
					((ALinearizableNode) node).setPreviewMode(true);
					branchNodesToLinearizedNodesMap.put(node, currentNode);
				}
			}

			if (targetVertexReps.size() > 0) {
				List<ALinearizableNode> targetNodes = new ArrayList<ALinearizableNode>();
				createNodesForList(targetNodes, targetVertexReps);

				outgoingNode.setBranchNodes(targetNodes);
				linearizedNodesToOutgoingBranchSummaryNodesMap.put(currentNode,
						outgoingNode);
				branchNodes.add(outgoingNode);
				branchNodes.addAll(targetNodes);
				for (ANode node : targetNodes) {
					((ALinearizableNode) node).setPreviewMode(true);
					branchNodesToLinearizedNodesMap.put(node, currentNode);
				}
			}

		}

	}

	private void createNodesForList(List<ALinearizableNode> nodes,
			List<PathwayVertexRep> vertexReps) {

		for (int i = 0; i < vertexReps.size(); i++) {
			PathwayVertexRep currentVertexRep = vertexReps.get(i);
			ALinearizableNode node = null;
			if (currentVertexRep.getType() == EPathwayVertexType.group) {
				PathwayVertexGroupRep groupRep = (PathwayVertexGroupRep) currentVertexRep;
				List<PathwayVertexRep> groupedReps = groupRep.getGroupedVertexReps();
				List<ALinearizableNode> groupedNodes = new ArrayList<ALinearizableNode>();
				createNodesForList(groupedNodes, groupedReps);
				ComplexNode complexNode = new ComplexNode(pixelGLConverter, textRenderer,
						this, lastNodeId++);
				complexNode.setNodes(groupedNodes);
				for (ALinearizableNode groupedNode : groupedNodes) {
					groupedNode.setParentNode(complexNode);
				}
				complexNode.setPathwayVertexRep(currentVertexRep);
				node = complexNode;
			} else if (currentVertexRep.getType() == EPathwayVertexType.compound) {
				CompoundNode compoundNode = new CompoundNode(pixelGLConverter, this,
						lastNodeId++);

				compoundNode.setPathwayVertexRep(currentVertexRep);
				node = compoundNode;

			} else {

				// TODO: Verify that this is also the right approach for
				// enzymes and ortholog
				GeneNode geneNode = new GeneNode(pixelGLConverter, textRenderer, this,
						lastNodeId++);
				int commaIndex = currentVertexRep.getName().indexOf(',');
				if (commaIndex > 0) {
					geneNode.setCaption(currentVertexRep.getName().substring(0,
							commaIndex));
				} else {
					geneNode.setCaption(currentVertexRep.getName());
				}
				geneNode.setPathwayVertexRep(currentVertexRep);

				node = geneNode;
			}

			nodes.add(node);
			setNumberOfMappedValues(node);
		}
	}

	/**
	 * Determines the {@link ComplexNode}s for the specified
	 * {@link PathwayVertexRep}s.
	 * 
	 * @param vertexReps
	 * @return
	 */
	// private List<ComplexNode> createComplexBranchNodes(List<PathwayVertexRep>
	// vertexReps) {
	//
	// List<PathwayVertexRep> vertexRepsLeft = new ArrayList<PathwayVertexRep>(
	// vertexReps);
	//
	// // Detect complex nodes by comparing their edges
	// List<ComplexNode> complexNodes = new ArrayList<ComplexNode>();
	// while (vertexRepsLeft.size() > 0) {
	//
	// PathwayVertexRep vertexRep = vertexRepsLeft.get(0);
	// ComplexNode complexNode = null;
	// for (PathwayVertexRep vRep : vertexRepsLeft) {
	// if (vertexRep != vRep) {
	// List<PathwayVertexRep> edgeSources1 = new ArrayList<PathwayVertexRep>();
	// List<PathwayVertexRep> edgeTargets1 = new ArrayList<PathwayVertexRep>();
	// List<PathwayVertexRep> edgeSources2 = new ArrayList<PathwayVertexRep>();
	// List<PathwayVertexRep> edgeTargets2 = new ArrayList<PathwayVertexRep>();
	// getEdgeEnds(edgeSources1, edgeTargets1, vertexRep);
	// getEdgeEnds(edgeSources2, edgeTargets2, vRep);
	//
	// if ((edgeSources1.size() == edgeSources2.size())
	// && (edgeTargets1.size() == edgeTargets2.size())
	// && (edgeTargets1.containsAll(edgeTargets2))
	// && (edgeSources1.containsAll(edgeSources2))) {
	// if (complexNode == null) {
	// complexNode = new ComplexNode(pixelGLConverter, textRenderer,
	// this, lastNodeId++);
	// complexNode.addVertexRep(vertexRep);
	// complexNodes.add(complexNode);
	// }
	// complexNode.addVertexRep(vRep);
	// }
	// }
	// }
	// if (complexNode == null)
	// break;
	// for (PathwayVertexRep vRep : complexNode.getVertexReps()) {
	// vertexRepsLeft.remove(vRep);
	// }
	// }
	//
	// return complexNodes;
	// }
	//
	// private void createNodesForList(List<ANode> nodeList,
	// List<PathwayVertexRep> vertexReps, boolean affectsDataRows,
	// List<ComplexNode> complexNodes,
	// boolean fillLinearizedNodesToPathwayVertexRepMap) {
	// // ANode prevNode = null;
	// // for (int i = 0; i < vertexReps.size(); i++) {
	// //
	// // PathwayVertexRep currentVertexRep = vertexReps.get(i);
	// //
	// // ANode node = null;
	// // for (ComplexNode complexNode : complexNodes) {
	// // List<PathwayVertexRep> vReps = complexNode.getVertexReps();
	// // for (PathwayVertexRep vRep : vReps) {
	// // if (vRep == currentVertexRep) {
	// // node = complexNode;
	// // break;
	// // }
	// // }
	// // }
	// // if (node != null) {
	// // if (prevNode != node) {
	// // nodeList.add(node);
	// // }
	// // } else {
	// // if (currentVertexRep.getType() == EPathwayVertexType.compound) {
	// // CompoundNode compoundNode = new CompoundNode(pixelGLConverter, this,
	// // lastNodeId++);
	// //
	// // compoundNode.setPathwayVertexRep(currentVertexRep);
	// // node = compoundNode;
	// //
	// // } else {
	// //
	// // // TODO: Verify that this is also the right approach for
	// // // enzymes and ortholog
	// // GeneNode geneNode = new GeneNode(pixelGLConverter, textRenderer,
	// // this, lastNodeId++);
	// // int commaIndex = currentVertexRep.getName().indexOf(',');
	// // if (commaIndex > 0) {
	// // geneNode.setCaption(currentVertexRep.getName().substring(0,
	// // commaIndex));
	// // } else {
	// // geneNode.setCaption(currentVertexRep.getName());
	// // }
	// // geneNode.setPathwayVertexRep(currentVertexRep);
	// //
	// // node = geneNode;
	// // }
	// // nodeList.add(node);
	// // // vertexRepToNodeMap.put(currentVertexRep, node);
	// // }
	// // // int numMappedValues =
	// // determineNumberOfMappedValues(currentVertexRep);
	// // // node.setNumAssociatedRows(node.getNumAssociatedRows() +
	// // numMappedValues);
	// //
	// // // if (fillLinearizedNodesToPathwayVertexRepMap) {
	// // // List<Integer> indices =
	// // // linearizedNodesToPathwayVertexRepIndicesMap
	// // // .get(node);
	// // // if (indices == null) {
	// // // indices = new ArrayList<Integer>();
	// // // linearizedNodesToPathwayVertexRepIndicesMap.put(node, indices);
	// // // }
	// // // indices.add(i);
	// // // }
	// //
	// // // if (affectsDataRows) {
	// // // numDataRows += numMappedValues;
	// // // }
	// //
	// // prevNode = node;
	// // }
	// }

	private int setNumberOfMappedValues(ALinearizableNode node) {
		int numMappedValues = 0;

		if (node instanceof ComplexNode) {
			ComplexNode complexNode = (ComplexNode) node;

			for (ALinearizableNode groupedNode : complexNode.getNodes()) {
				numMappedValues += setNumberOfMappedValues(groupedNode);
			}
		} else {
			numMappedValues = node.getPathwayVertexRep().getDavidIDs().size();
		}
		node.setNumAssociatedRows(numMappedValues);

		return numMappedValues;

		// List<PathwayVertex> vertices = vertexRep.getPathwayVertices();
		//
		// if (vertices == null)
		// return 0;
		//
		// Set<Integer> allIDs = new HashSet<Integer>();
		//
		// for (PathwayVertex vertex : vertices) {
		// Integer davidId =
		// PathwayItemManager.get().getDavidIdByPathwayVertex(vertex);
		//
		// for (GeneticDataDomain dataDomain : geneticDataDomains) {
		// Set<Integer> ids = dataDomain.getGeneIDMappingManager().getIDAsSet(
		// pathwayDataDomain.getDavidIDType(), dataDomain.getGeneIDType(),
		// davidId);
		//
		// // TODO: This is only true if the davidID maps to one id of the
		// // genetic
		// // datadomain. However, matching multiple ids from different
		// // genetic
		// // datadomains is difficult.
		// if (ids != null && !ids.isEmpty()) {
		// allIDs.add(davidId);
		// }
		// }
		// }
		//
		// return allIDs.size();
	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				glParentView.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		init(gl);
	}

	@Override
	public void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);
		display(gl);
		if (busyState != EBusyState.OFF) {
			renderBusyMode(gl);
		}

	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
	}

	@Override
	public void display(GL2 gl) {

		if (isDisplayListDirty) {
			buildDisplayList(gl, displayListIndex);
			isDisplayListDirty = false;
		}
		isLayoutDirty = false;
		gl.glCallList(displayListIndex);

		checkForHits(gl);
	}

	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {

		dataRowHeight = pixelGLConverter
				.getGLHeightForPixelHeight(DEFAULT_DATA_ROW_HEIGHT_PIXELS);

		gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);

		float branchColumnWidth = pixelGLConverter
				.getGLWidthForPixelWidth(BRANCH_COLUMN_WIDTH_PIXELS);
		float pathwayColumnWidth = pixelGLConverter
				.getGLWidthForPixelWidth(PATHWAY_COLUMN_WIDTH_PIXELS);

		GLU glu = new GLU();

		List<AnchorNodeSpacing> anchorNodeSpacings = calcAnchorNodeSpacings();
		// float dataRowHeight = Float.MAX_VALUE;
		Vec3f currentPosition = new Vec3f(branchColumnWidth + pathwayColumnWidth / 2.0f,
				viewFrustum.getHeight(), 0.2f);
		float pathwayHeight = 0;

		for (AnchorNodeSpacing spacing : anchorNodeSpacings) {

			float currentAnchorNodeSpacing = spacing.getCurrentAnchorNodeSpacing();
			// if(Float.isNaN(currentAnchorNodeSpacing))
			float yStep = (Float.isNaN(currentAnchorNodeSpacing) ? viewFrustum
					.getHeight() : spacing.getCurrentAnchorNodeSpacing())
					/ ((float) spacing.getNodesInbetween().size() + 1);

			for (int i = 0; i < spacing.getNodesInbetween().size(); i++) {
				ANode node = spacing.getNodesInbetween().get(i);
				node.setPosition(new Vec3f(currentPosition.x(), currentPosition.y()
						- (i + 1) * yStep, currentPosition.z()));
				node.render(gl, glu);
				// if (expandedBranchSummaryNode == null)
				renderBranchNodes(gl, glu, node);
			}

			currentPosition.setY(currentPosition.y()
					- spacing.getCurrentAnchorNodeSpacing());

			ANode endAnchorNode = spacing.getEndNode();
			if (endAnchorNode != null) {
				endAnchorNode.setPosition(new Vec3f(currentPosition));
				endAnchorNode.render(gl, glu);
				// if (expandedBranchSummaryNode == null)
				renderBranchNodes(gl, glu, endAnchorNode);
			}

			ANode startAnchorNode = spacing.getStartNode();

			int numSpacingAnchorNodeRows = 0;
			if (startAnchorNode != null)
				numSpacingAnchorNodeRows += startAnchorNode.getNumAssociatedRows();
			if (endAnchorNode != null)
				numSpacingAnchorNodeRows += endAnchorNode.getNumAssociatedRows();

			// float currentDataRowHeight =
			// spacing.getCurrentAnchorNodeSpacing()
			// / ((float) numSpacingAnchorNodeRows / 2.0f);

			// if (currentDataRowHeight < dataRowHeight)
			// dataRowHeight = currentDataRowHeight;

			pathwayHeight += spacing.getCurrentAnchorNodeSpacing();
		}

		if (expandedBranchSummaryNode != null) {
			renderBranchSummaryNode(gl, glu, expandedBranchSummaryNode);
			float coverWidth = pixelGLConverter
					.getGLWidthForPixelWidth(PATHWAY_COLUMN_WIDTH_PIXELS
							+ BRANCH_COLUMN_WIDTH_PIXELS);
			gl.glColor4f(1, 1, 1, 0.9f);

			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(0, 0, 0.1f);
			gl.glVertex3f(coverWidth, 0, 0.1f);
			gl.glVertex3f(coverWidth, viewFrustum.getHeight(), 0.1f);
			gl.glVertex3f(0, viewFrustum.getHeight(), 0.1f);
			gl.glEnd();
		}

		int minViewHeightPixels = pixelGLConverter
				.getPixelHeightForGLHeight(pathwayHeight);

		if (minViewHeightPixels > parentGLCanvas.getHeight()) {
			SetMinViewSizeEvent event = new SetMinViewSizeEvent();
			event.setMinViewSize(parentGLCanvas.getBounds().width, minViewHeightPixels);
			event.setView(this);
			eventPublisher.triggerEvent(event);
			setDisplayListDirty();
		}

		float dataRowPositionX = branchColumnWidth + pathwayColumnWidth;

		float topSpacing = pixelGLConverter.getGLWidthForPixelWidth(TOP_SPACING_PIXELS);
		gl.glPushMatrix();

		gl.glTranslatef(dataRowPositionX, topSpacing, 0);
		// TODO do this only when necessary - cause re-initialization
		mappedDataRenderer.setLinearizedNodes(linearizedNodes);

		mappedDataRenderer.setGeometry(viewFrustum.getWidth() - dataRowPositionX
				- topSpacing, viewFrustum.getHeight() - 2 * topSpacing, dataRowPositionX,
				topSpacing, dataRowHeight);

		// mappedDataRenderer.render(gl);
		gl.glPopMatrix();

		renderEdgesOfLinearizedNodes(gl);

		gl.glEndList();

		// gl.glMatrixMode(GL2.GL_MODELVIEW);
		// gl.glPushMatrix();
		// gl.glTranslatef(2, 2, 2);
		// pixelGLConverter.getGLWidthForCurrentGLTransform(gl);
		// gl.glPopMatrix();

	}

	/**
	 * Renders the branch nodes for a specified linearized node. The position of
	 * this node has to be set beforehand.
	 * 
	 * @param node
	 */
	private void renderBranchNodes(GL2 gl, GLU glu, ANode node) {

		// float branchNodePositionX = pixelGLConverter
		// .getGLWidthForPixelWidth(BRANCH_COLUMN_WIDTH_PIXELS) / 2.0f;
		// float spacing =
		// pixelGLConverter.getGLWidthForPixelWidth(SPACING_PIXELS);
		// float verticalBranchNodeSpacing =
		// pixelGLConverter.getGLHeightForPixelHeight(20);
		//
		// Vec3f nodePosition = node.getPosition();
		ANode incomingNode = linearizedNodesToIncomingBranchSummaryNodesMap.get(node);
		if ((incomingNode != null) && (incomingNode != expandedBranchSummaryNode)) {

			renderBranchSummaryNode(gl, glu, (BranchSummaryNode) incomingNode);

			ConnectionLineRenderer connectionLineRenderer = new ConnectionLineRenderer();
			List<Vec3f> linePoints = new ArrayList<Vec3f>();
			Vec3f sourcePosition = incomingNode.getRightConnectionPoint();
			Vec3f targetPosition = node.getLeftConnectionPoint();
			sourcePosition.setZ(0);
			targetPosition.setZ(0);
			linePoints.add(sourcePosition);
			linePoints.add(targetPosition);

			LineEndArrowRenderer lineEndArrowRenderer = createDefaultLineEndArrowRenderer();
			connectionLineRenderer.addAttributeRenderer(lineEndArrowRenderer);

			connectionLineRenderer.renderLine(gl, linePoints);
		}

		ANode outgoingNode = linearizedNodesToOutgoingBranchSummaryNodesMap.get(node);
		if ((outgoingNode != null) && (outgoingNode != expandedBranchSummaryNode)) {

			renderBranchSummaryNode(gl, glu, (BranchSummaryNode) outgoingNode);

			ConnectionLineRenderer connectionLineRenderer = new ConnectionLineRenderer();
			List<Vec3f> linePoints = new ArrayList<Vec3f>();

			Vec3f sourcePosition = node.getLeftConnectionPoint();
			Vec3f targetPosition = outgoingNode.getRightConnectionPoint();
			sourcePosition.setZ(0);
			targetPosition.setZ(0);
			linePoints.add(sourcePosition);
			linePoints.add(targetPosition);

			LineEndArrowRenderer lineEndArrowRenderer = createDefaultLineEndArrowRenderer();
			connectionLineRenderer.addAttributeRenderer(lineEndArrowRenderer);

			connectionLineRenderer.renderLine(gl, linePoints);
		}
	}

	private void renderBranchSummaryNode(GL2 gl, GLU glu, BranchSummaryNode summaryNode) {
		boolean isIncomingNode = linearizedNodesToIncomingBranchSummaryNodesMap
				.get(summaryNode.getAssociatedLinearizedNode()) == summaryNode;
		ALinearizableNode linearizedNode = summaryNode.getAssociatedLinearizedNode();
		Vec3f linearizedNodePosition = linearizedNode.getPosition();

		float sideSpacing = pixelGLConverter
				.getGLHeightForPixelHeight(BRANCH_AREA_SIDE_SPACING_PIXELS);
		float branchSummaryNodeToLinearizedNodeDistance = pixelGLConverter
				.getGLHeightForPixelHeight(BRANCH_SUMMARY_NODE_TO_LINEARIZED_NODE_VERTICAL_DISTANCE_PIXELS);
		float width = summaryNode.getWidth();
		float titleAreaHeight = pixelGLConverter.getGLHeightForPixelHeight(summaryNode
				.getTitleAreaHeightPixels());

		float nodePositionY = linearizedNodePosition.y()
				+ (isIncomingNode ? branchSummaryNodeToLinearizedNodeDistance
						: -branchSummaryNodeToLinearizedNodeDistance)
				- (summaryNode.getHeight() / 2.0f) + titleAreaHeight / 2.0f;

		summaryNode.setPosition(new Vec3f(sideSpacing + width / 2.0f, nodePositionY,
				(summaryNode.isCollapsed() ? 0 : 0.2f)));

		summaryNode.render(gl, glu);

		if (!summaryNode.isCollapsed()) {
			List<ALinearizableNode> branchNodes = summaryNode.getBranchNodes();
			for (ALinearizableNode node : branchNodes) {
				renderEdge(gl, node, linearizedNode, node.getRightConnectionPoint(),
						linearizedNode.getLeftConnectionPoint(), 0.2f, false);
			}
		}

		float bottomPositionY = nodePositionY - (summaryNode.getHeight() / 2.0f);

		if (viewFrustum.getBottom() > bottomPositionY) {
			int additionalHeight = pixelGLConverter.getPixelHeightForGLHeight(viewFrustum
					.getBottom() - bottomPositionY);
			SetMinViewSizeEvent event = new SetMinViewSizeEvent();
			event.setMinViewSize(parentGLCanvas.getBounds().width,
					parentGLCanvas.getBounds().height + additionalHeight);
			event.setView(this);
			eventPublisher.triggerEvent(event);
			setDisplayListDirty();
		}
	}

	/**
	 * Renders all the nodes of the {@link #expandedBranchSummaryNode}. Note,
	 * that the associated linearized node has to be positioned beforehand.
	 * 
	 * @param gl
	 * @param glu
	 * @param linearizedNode
	 */
	// private void renderExpandedBranchNode(GL2 gl, GLU glu) {
	// ANode linearizedNode =
	// expandedBranchSummaryNode.getAssociatedLinearizedNode();
	//
	// // Vec3f branchSummaryNodePosition =
	// // expandedBranchSummaryNode.getPosition();
	// // float branchSummaryNodeTopY = branchSummaryNodePosition.y()
	// // +
	// // pixelGLConverter.getGLHeightForPixelHeight(expandedBranchSummaryNode
	// // .getHeightPixels()) / 2.0f;
	// // float branchSummaryNodeMinHeight = pixelGLConverter
	// // .getGLHeightForPixelHeight(expandedBranchSummaryNode
	// // .getMinRequiredHeightPixels());
	// // float spacing =
	// // pixelGLConverter.getGLWidthForPixelWidth(SPACING_PIXELS);
	// //
	// // List<ANode> branchNodes = expandedBranchSummaryNode.getBranchNodes();
	// // // float previewDataRowHeight =
	// // //
	// //
	// pixelGLConverter.getGLHeightForPixelHeight(PREVIEW_NODE_DATA_ROW_HEIGHT_PIXELS);
	// // float nodeSpacing = pixelGLConverter
	// // .getGLHeightForPixelHeight(EXPANDED_BRANCH_NODE_SPACING_PIXELS);
	// // float branchNodePositionX = pixelGLConverter
	// // .getGLWidthForPixelWidth(BRANCH_COLUMN_WIDTH_PIXELS) / 2.0f;
	// //
	// // float totalHeight = 0;
	// //
	// // for (ANode node : branchNodes) {
	// // totalHeight += pixelGLConverter.getGLHeightForPixelHeight(node
	// // .getMinRequiredHeightPixels());
	// // node.setHeightPixels(node.getMinRequiredHeightPixels());
	// // node.setWidthPixels(node.getMinRequiredWidthPixels());
	// // }
	// //
	// // totalHeight += nodeSpacing * (branchNodes.size() - 1);
	// //
	// // float topPositionY = branchSummaryNodeTopY -
	// // branchSummaryNodeMinHeight - 4
	// // * spacing;
	// // if (topPositionY - totalHeight < viewFrustum.getBottom()) {
	// // topPositionY += (viewFrustum.getBottom() - (topPositionY -
	// // totalHeight));
	// // }
	// // if (topPositionY > viewFrustum.getTop() - branchSummaryNodeMinHeight)
	// // {
	// // topPositionY -= (topPositionY - (viewFrustum.getTop() -
	// // branchSummaryNodeMinHeight));
	// // }
	//
	// // float nodeWidth = pixelGLConverter
	// // .getGLWidthForPixelWidth(expandedBranchSummaryNode.getWidthPixels());
	// // float currentHeight = pixelGLConverter
	// //
	// .getGLHeightForPixelHeight(expandedBranchSummaryNode.getHeightPixels());
	// // if (branchSummaryNodeMinHeight + 0.0001f > currentHeight ||
	// // isLayoutDirty) {
	// //
	// // expandedBranchSummaryNode.setHeightPixels(expandedBranchSummaryNode
	// // .getMinRequiredHeightPixels()
	// // + pixelGLConverter.getPixelHeightForGLHeight(totalHeight)
	// // + 7
	// // * SPACING_PIXELS);
	// // expandedBranchSummaryNode.setPosition(new Vec3f(4 * spacing +
	// // nodeWidth
	// // / 2.0f, topPositionY + branchSummaryNodeMinHeight + 4 * spacing
	// // - (totalHeight + branchSummaryNodeMinHeight + 7 * spacing) / 2.0f,
	// // 0.15f));
	// // }
	// // expandedBranchSummaryNode.render(gl, glu);
	// //
	// // float currentPositionY = topPositionY;
	// // // System.out.println(branchSummaryNodeTopY + "," +
	// // // branchSummaryNodeMinHeight + ","
	// // // + topPositionY);
	// //
	//
	// // gl.glPopName();
	//
	// }

	/**
	 * Calculates the spacings between all anchor nodes (nodes with mapped data)
	 * of the path.
	 * 
	 * @return
	 */
	private List<AnchorNodeSpacing> calcAnchorNodeSpacings() {

		List<AnchorNodeSpacing> anchorNodeSpacings = new ArrayList<AnchorNodeSpacing>();
		List<AnchorNodeSpacing> anchorNodeSpacingsWithTooFewSpace = new ArrayList<AnchorNodeSpacing>();
		List<AnchorNodeSpacing> anchorNodeSpacingsWithEnoughSpace = new ArrayList<AnchorNodeSpacing>();

		// float minDataRowHeight = pixelGLConverter
		// .getGLHeightForPixelHeight(MIN_DATA_ROW_HEIGHT_PIXELS);
		float minNodeDistance = pixelGLConverter
				.getGLHeightForPixelHeight(MIN_NODE_DISTANCE_PIXELS);
		float topSpacing = pixelGLConverter.getGLHeightForPixelHeight(TOP_SPACING_PIXELS);
		float bottomSpacing = pixelGLConverter
				.getGLHeightForPixelHeight(BOTTOM_SPACING_PIXELS);

		// float dataRowHeight = (viewFrustum.getHeight() - bottomSpacing -
		// topSpacing)
		// / (float) numDataRows;
		//
		// if (dataRowHeight < minDataRowHeight)
		// dataRowHeight = minDataRowHeight;

		List<ANode> unmappedNodes = new ArrayList<ANode>();
		ANode currentAnchorNode = null;

		// Calculate spacings according to a regular row distribution with the
		// current dataRowHeight
		for (int i = 0; i < linearizedNodes.size(); i++) {

			ANode node = linearizedNodes.get(i);
			int numAssociatedRows = node.getNumAssociatedRows();

			if (numAssociatedRows == 0) {
				unmappedNodes.add(node);

			} else {
				AnchorNodeSpacing anchorNodeSpacing = createAnchorNodeSpacing(
						currentAnchorNode, node, unmappedNodes, minNodeDistance,
						currentAnchorNode == null, false);

				anchorNodeSpacings.add(anchorNodeSpacing);

				unmappedNodes = new ArrayList<ANode>();
				currentAnchorNode = node;

				if (anchorNodeSpacing.getMinAnchorNodeSpacing() > anchorNodeSpacing
						.getCurrentAnchorNodeSpacing())
					anchorNodeSpacingsWithTooFewSpace.add(anchorNodeSpacing);
				else
					anchorNodeSpacingsWithEnoughSpace.add(anchorNodeSpacing);
			}

			if (i == linearizedNodes.size() - 1) {
				AnchorNodeSpacing anchorNodeSpacing = createAnchorNodeSpacing(
						currentAnchorNode, null, unmappedNodes, minNodeDistance,
						currentAnchorNode == null, true);
				anchorNodeSpacings.add(anchorNodeSpacing);
				if (anchorNodeSpacing.getMinAnchorNodeSpacing() > anchorNodeSpacing
						.getCurrentAnchorNodeSpacing())
					anchorNodeSpacingsWithTooFewSpace.add(anchorNodeSpacing);
				else
					anchorNodeSpacingsWithEnoughSpace.add(anchorNodeSpacing);
			}
		}

		// Reduce space for spacings with enough space to grant spacings with
		// too few space more space
		for (AnchorNodeSpacing spacingWithTooFewSpace : anchorNodeSpacingsWithTooFewSpace) {
			float additionallyRequiredSpace = spacingWithTooFewSpace
					.getMinAnchorNodeSpacing()
					- spacingWithTooFewSpace.getCurrentAnchorNodeSpacing();

			while ((additionallyRequiredSpace > 0)
					&& (anchorNodeSpacingsWithEnoughSpace.size() > 0)) {

				float maxReducableSpace = Float.MAX_VALUE;

				for (AnchorNodeSpacing spacing : anchorNodeSpacingsWithEnoughSpace) {
					float reducableSpace = spacing.getCurrentAnchorNodeSpacing()
							- spacing.getMinAnchorNodeSpacing();
					if (reducableSpace < maxReducableSpace)
						maxReducableSpace = reducableSpace;
				}

				float spaceToReduce = Math.min(maxReducableSpace,
						additionallyRequiredSpace
								/ (float) anchorNodeSpacingsWithEnoughSpace.size());

				List<AnchorNodeSpacing> spacingsWithEnoughSpaceCopy = new ArrayList<AnchorNodeSpacing>(
						anchorNodeSpacingsWithEnoughSpace);

				for (AnchorNodeSpacing spacingWithEnoughSpace : spacingsWithEnoughSpaceCopy) {
					float newSpacing = spacingWithEnoughSpace
							.getCurrentAnchorNodeSpacing() - spaceToReduce;
					spacingWithEnoughSpace.setCurrentAnchorNodeSpacing(newSpacing);
					if (newSpacing <= spacingWithEnoughSpace.getMinAnchorNodeSpacing())
						anchorNodeSpacingsWithEnoughSpace.remove(spacingWithEnoughSpace);
					additionallyRequiredSpace -= spaceToReduce;
				}
			}

			spacingWithTooFewSpace.setCurrentAnchorNodeSpacing(spacingWithTooFewSpace
					.getMinAnchorNodeSpacing());
		}

		return anchorNodeSpacings;
	}

	private AnchorNodeSpacing createAnchorNodeSpacing(ANode startAnchorNode,
			ANode endAnchorNode, List<ANode> nodesInbetween, float minNodeDistance,
			boolean isFirstSpacing, boolean isLastSpacing) {

		AnchorNodeSpacing anchorNodeSpacing = new AnchorNodeSpacing();
		anchorNodeSpacing.setStartNode(startAnchorNode);
		anchorNodeSpacing.setEndNode(endAnchorNode);
		anchorNodeSpacing.setNodesInbetween(nodesInbetween);

		int numSpacingAnchorNodeRows = 0;
		if (startAnchorNode != null)
			numSpacingAnchorNodeRows += startAnchorNode.getNumAssociatedRows();
		if (endAnchorNode != null)
			numSpacingAnchorNodeRows += endAnchorNode.getNumAssociatedRows();

		float additionalSpacing = 0;
		if (isFirstSpacing)
			additionalSpacing += pixelGLConverter
					.getGLHeightForPixelHeight(TOP_SPACING_PIXELS);
		if (isLastSpacing)
			additionalSpacing += pixelGLConverter
					.getGLHeightForPixelHeight(BOTTOM_SPACING_PIXELS);

		anchorNodeSpacing.setMinAnchorNodeSpacing(Math.max(dataRowHeight
				* ((float) numSpacingAnchorNodeRows) / 2.0f + additionalSpacing,
				minNodeDistance * (float) (nodesInbetween.size() + 1)));
		anchorNodeSpacing.setCurrentAnchorNodeSpacing(dataRowHeight
				* ((float) numSpacingAnchorNodeRows) / 2.0f + additionalSpacing);

		return anchorNodeSpacing;
	}

	private void renderEdgesOfLinearizedNodes(GL2 gl) {
		for (int i = 0; i < linearizedNodes.size() - 1; i++) {
			ALinearizableNode node1 = linearizedNodes.get(i);
			ALinearizableNode node2 = linearizedNodes.get(i + 1);
			renderEdge(gl, node1, node2, node1.getBottomConnectionPoint(),
					node2.getTopConnectionPoint(), 0.2f, true);
		}
	}

	private void renderEdge(GL2 gl, ALinearizableNode node1, ALinearizableNode node2,
			Vec3f node1ConnectionPoint, Vec3f node2ConnectionPoint, float zCoordinate,
			boolean isVerticalConnection) {

		PathwayVertexRep vertexRep1 = node1.getPathwayVertexRep();
		PathwayVertexRep vertexRep2 = node2.getPathwayVertexRep();

		DefaultEdge edge = pathway.getEdge(vertexRep1, vertexRep2);
		if (edge == null) {
			edge = pathway.getEdge(vertexRep2, vertexRep1);
			if (edge == null)
				return;
		}

		ConnectionLineRenderer connectionRenderer = new ConnectionLineRenderer();
		List<Vec3f> linePoints = new ArrayList<Vec3f>();

		boolean isNode1Target = pathway.getEdgeTarget(edge) == vertexRep1;

		Vec3f sourceConnectionPoint = (isNode1Target) ? node2ConnectionPoint
				: node1ConnectionPoint;
		Vec3f targetConnectionPoint = (isNode1Target) ? node1ConnectionPoint
				: node2ConnectionPoint;

		sourceConnectionPoint.setZ(zCoordinate);
		targetConnectionPoint.setZ(zCoordinate);

		linePoints.add(sourceConnectionPoint);
		linePoints.add(targetConnectionPoint);

		if (edge instanceof PathwayReactionEdgeRep) {
			// TODO: This is just a default edge. Is this right?
			PathwayReactionEdgeRep reactionEdge = (PathwayReactionEdgeRep) edge;

			ClosedArrowRenderer arrowRenderer = new ClosedArrowRenderer(pixelGLConverter);
			LineEndArrowRenderer lineEndArrowRenderer = new LineEndArrowRenderer(false,
					arrowRenderer);

			connectionRenderer.addAttributeRenderer(lineEndArrowRenderer);

			if (reactionEdge.getType() == EPathwayReactionEdgeType.reversible) {
				arrowRenderer = new ClosedArrowRenderer(pixelGLConverter);
				lineEndArrowRenderer = new LineEndArrowRenderer(true, arrowRenderer);
				connectionRenderer.addAttributeRenderer(lineEndArrowRenderer);
			}

			// linePoints.add(nodeRenderer1.getBottomConnectionPoint());
			// linePoints.add(nodeRenderer2.getTopConnectionPoint());

		} else {
			if (edge instanceof PathwayRelationEdgeRep) {
				PathwayRelationEdgeRep relationEdgeRep = (PathwayRelationEdgeRep) edge;

				ArrayList<EPathwayRelationEdgeSubType> subtypes = relationEdgeRep
						.getRelationSubTypes();
				float spacing = pixelGLConverter.getGLHeightForPixelHeight(3);

				for (EPathwayRelationEdgeSubType subtype : subtypes) {
					switch (subtype) {
					case compound:
						// TODO:
						break;
					case hidden_compound:
						// TODO:
						break;
					case activation:
						connectionRenderer
								.addAttributeRenderer(createDefaultLineEndArrowRenderer());
						break;
					case inhibition:
						connectionRenderer
								.addAttributeRenderer(createDefaultLineEndStaticLineRenderer(isVerticalConnection));
						if (isVerticalConnection) {
							targetConnectionPoint.setY(targetConnectionPoint.y()
									+ ((isNode1Target) ? -spacing : spacing));
						} else {
							targetConnectionPoint.setX(targetConnectionPoint.x()
									+ ((isNode1Target) ? spacing : -spacing));
						}
						break;
					case expression:
						connectionRenderer
								.addAttributeRenderer(createDefaultLineEndArrowRenderer());
						if (vertexRep1.getType() == EPathwayVertexType.gene
								&& vertexRep1.getType() == EPathwayVertexType.gene) {
							connectionRenderer
									.addAttributeRenderer(createDefaultLabelOnLineRenderer("e"));
						}
						break;
					case repression:
						connectionRenderer
								.addAttributeRenderer(createDefaultLineEndArrowRenderer());
						connectionRenderer
								.addAttributeRenderer(createDefaultLineEndStaticLineRenderer(isVerticalConnection));
						targetConnectionPoint.setY(targetConnectionPoint.y()
								+ ((isNode1Target) ? -spacing : spacing));
						break;
					case indirect_effect:
						connectionRenderer
								.addAttributeRenderer(createDefaultLineEndArrowRenderer());
						connectionRenderer.setLineStippled(true);
						break;
					case state_change:
						connectionRenderer.setLineStippled(true);
						break;
					case binding_association:
						// Nothing to do
						break;
					case dissociation:
						connectionRenderer
								.addAttributeRenderer(createDefaultOrthogonalLineCrossingRenderer());
						break;
					case missing_interaction:
						connectionRenderer
								.addAttributeRenderer(createDefaultLineCrossingRenderer());
						break;
					case phosphorylation:
						connectionRenderer
								.addAttributeRenderer(createDefaultLabelAboveLineRenderer(EPathwayRelationEdgeSubType.phosphorylation
										.getSymbol()));
						break;
					case dephosphorylation:
						connectionRenderer
								.addAttributeRenderer(createDefaultLabelAboveLineRenderer(EPathwayRelationEdgeSubType.dephosphorylation
										.getSymbol()));
						break;
					case glycosylation:
						connectionRenderer
								.addAttributeRenderer(createDefaultLabelAboveLineRenderer(EPathwayRelationEdgeSubType.glycosylation
										.getSymbol()));
						break;
					case ubiquitination:
						connectionRenderer
								.addAttributeRenderer(createDefaultLabelAboveLineRenderer(EPathwayRelationEdgeSubType.ubiquitination
										.getSymbol()));
						break;
					case methylation:
						connectionRenderer
								.addAttributeRenderer(createDefaultLabelAboveLineRenderer(EPathwayRelationEdgeSubType.methylation
										.getSymbol()));
						break;
					}
				}
			}
		}

		connectionRenderer.renderLine(gl, linePoints);
	}

	private LineEndArrowRenderer createDefaultLineEndArrowRenderer() {
		ClosedArrowRenderer arrowRenderer = new ClosedArrowRenderer(pixelGLConverter);
		return new LineEndArrowRenderer(false, arrowRenderer);
	}

	private LineEndStaticLineRenderer createDefaultLineEndStaticLineRenderer(
			boolean isHorizontalLine) {
		LineEndStaticLineRenderer lineEndRenderer = new LineEndStaticLineRenderer(false,
				pixelGLConverter);
		lineEndRenderer.setHorizontalLine(isHorizontalLine);
		return lineEndRenderer;
	}

	private LineLabelRenderer createDefaultLabelOnLineRenderer(String text) {
		LineLabelRenderer lineLabelRenderer = new LineLabelRenderer(0.5f,
				pixelGLConverter, text, textRenderer);
		lineLabelRenderer.setLineOffsetPixels(0);
		return lineLabelRenderer;
	}

	private LineLabelRenderer createDefaultLabelAboveLineRenderer(String text) {
		LineLabelRenderer lineLabelRenderer = new LineLabelRenderer(0.66f,
				pixelGLConverter, text, textRenderer);
		lineLabelRenderer.setLineOffsetPixels(5);
		return lineLabelRenderer;
	}

	private LineCrossingRenderer createDefaultOrthogonalLineCrossingRenderer() {
		LineCrossingRenderer lineCrossingRenderer = new LineCrossingRenderer(0.5f,
				pixelGLConverter);
		lineCrossingRenderer.setCrossingAngle(90);
		return lineCrossingRenderer;
	}

	private LineCrossingRenderer createDefaultLineCrossingRenderer() {
		LineCrossingRenderer lineCrossingRenderer = new LineCrossingRenderer(0.5f,
				pixelGLConverter);
		lineCrossingRenderer.setCrossingAngle(45);
		return lineCrossingRenderer;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedLinearizedPathwayView serializedForm = new SerializedLinearizedPathwayView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString() {
		return "TODO: ADD INFO THAT APPEARS IN THE LOG";
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		linearizePathwayPathEventListener = new LinearizePathwayPathEventListener();
		linearizePathwayPathEventListener.setHandler(this);
		eventPublisher.addListener(LinearizedPathwayPathEvent.class,
				linearizePathwayPathEventListener);

		addDataContainersListener = new AddDataContainersListener();
		addDataContainersListener.setHandler(this);
		eventPublisher.addListener(AddDataContainersEvent.class,
				addDataContainersListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (linearizePathwayPathEventListener != null) {
			eventPublisher.removeListener(linearizePathwayPathEventListener);
			linearizePathwayPathEventListener = null;
		}

		if (addDataContainersListener != null) {
			eventPublisher.removeListener(addDataContainersListener);
			addDataContainersListener = null;
		}
	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Sets a new path to be linearized.
	 * 
	 * @param pathway
	 *            The pathway the path corresponds to.
	 * @param path
	 *            List of {@link PathwayVertexRep}s that represents a path. If
	 *            multiple <code>PathwayVertexRep</code>s represent a complex
	 *            node, they must occur in a sequence.
	 */
	public void setPath(PathwayGraph pathway, List<PathwayVertexRep> path) {
		this.pathway = pathway;
		this.path = path;

		expandedBranchSummaryNode = null;
		for (ANode node : linearizedNodes) {
			node.unregisterPickingListeners();
		}
		for (ANode node : branchNodes) {
			node.unregisterPickingListeners();
		}

		SetMinViewSizeEvent event = new SetMinViewSizeEvent();
		event.setMinViewSize(0, 0);
		event.setView(this);
		eventPublisher.triggerEvent(event);

		createNodes();
		setDisplayListDirty();
	}

	/**
	 * @param currentExpandedBranchNode
	 *            setter, see {@link #expandedBranchSummaryNode}
	 */
	public void setExpandedBranchSummaryNode(BranchSummaryNode expandedBranchSummaryNode) {
		this.expandedBranchSummaryNode = expandedBranchSummaryNode;
	}

	/**
	 * @return the expandedBranchSummaryNode, see
	 *         {@link #expandedBranchSummaryNode}
	 */
	public BranchSummaryNode getExpandedBranchSummaryNode() {
		return expandedBranchSummaryNode;
	}

	/**
	 * @return the currentExpandedBranchNode, see
	 *         {@link #expandedBranchSummaryNode}
	 */
	public BranchSummaryNode getCurrentExpandedBranchNode() {
		return expandedBranchSummaryNode;
	}

	/**
	 * Selects a branch node to be linearized.
	 * 
	 * @param node
	 */
	public void selectBranch(ALinearizableNode node) {
		ALinearizableNode linearizedNode = branchNodesToLinearizedNodesMap.get(node);

		PathwayVertexRep linearizedVertexRep = linearizedNode.getPathwayVertexRep();
		PathwayVertexRep branchVertexRep = node.getPathwayVertexRep();

		DefaultEdge edge = pathway.getEdge(linearizedVertexRep, branchVertexRep);
		if (edge == null) {
			edge = pathway.getEdge(branchVertexRep, linearizedVertexRep);
		}

		int linearizedNodeIndex = linearizedNodes.indexOf(linearizedNode);
		// List<Integer> indices = linearizedNodesToPathwayVertexRepIndicesMap
		// .get(linearizedNode);
		List<PathwayVertexRep> newPath = null;
		List<PathwayVertexRep> branchPath = determineDefiniteUniDirectionalBranchPath(
				branchVertexRep, linearizedVertexRep);

		if (pathway.getEdgeSource(edge) == branchVertexRep) {
			// insert above linearized node
			Collections.reverse(branchPath);
			newPath = path.subList(linearizedNodeIndex, path.size());

			newPath.addAll(0, branchPath);

		} else {
			// insert below linearized node
			newPath = path.subList(0, linearizedNodeIndex + 1);
			newPath.addAll(branchPath);
		}

		setPath(pathway, newPath);

		ShowBubbleSetForPathwayVertexRepsEvent event = new ShowBubbleSetForPathwayVertexRepsEvent(
				new ArrayList<PathwayVertexRep>(newPath));
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	/**
	 * Calculates a branch path consisting of {@link PathwayVertexRep} objects
	 * for a specified branch node. This path ends if there is no unambiguous
	 * way to continue, the direction of edges changes, the pathway ends, or the
	 * {@link #maxBranchSwitchingPathLength} is reached. The specified
	 * <code>PathwayVertexRep</code> that represents the start of the path is
	 * added at the beginning of the path.
	 * 
	 * @param branchVertexRep
	 *            The <code>PathwayVertexRep</code> that represents the start of
	 *            the branch path.
	 * @param linearizedVertexRep
	 *            The <code>PathwayVertexRep</code> of the linearized path this
	 *            branch belongs to.
	 * @return
	 */
	private List<PathwayVertexRep> determineDefiniteUniDirectionalBranchPath(
			PathwayVertexRep branchVertexRep, PathwayVertexRep linearizedVertexRep) {

		List<PathwayVertexRep> vertexReps = new ArrayList<PathwayVertexRep>();
		vertexReps.add(branchVertexRep);
		DefaultEdge existingEdge = pathway.getEdge(branchVertexRep, linearizedVertexRep);
		if (existingEdge == null)
			existingEdge = pathway.getEdge(linearizedVertexRep, branchVertexRep);

		boolean isIncomingBranchPath = false;

		if (pathway.getEdgeSource(existingEdge) == branchVertexRep) {
			isIncomingBranchPath = true;
		}
		PathwayVertexRep currentVertexRep = branchVertexRep;

		for (int i = 0; i < maxBranchSwitchingPathLength; i++) {

			// List<DefaultEdge> edges = new ArrayList<DefaultEdge>(
			// pathway.edgesOf(currentVertexRep));
			// edges.remove(existingEdge);
			List<PathwayVertexRep> nextVertices = null;
			if (isIncomingBranchPath) {
				nextVertices = Graphs.predecessorListOf(pathway, currentVertexRep);
			} else {
				nextVertices = Graphs.successorListOf(pathway, currentVertexRep);
			}

			if (nextVertices.size() == 0 || nextVertices.size() > 1) {
				return vertexReps;
			} else {
				currentVertexRep = nextVertices.get(0);
				vertexReps.add(currentVertexRep);

				// List<PathwayVertexRep> vertices = new
				// ArrayList<PathwayVertexRep>();
				// for (DefaultEdge edge : edges) {
				// PathwayVertexRep source = pathway.getEdgeSource(edge);
				// PathwayVertexRep target = pathway.getEdgeTarget(edge);
				//
				// if ((target != currentVertexRep && isIncomingBranchPath)
				// || (source != currentVertexRep && !isIncomingBranchPath)) {
				// return vertexReps;
				// }
				// vertices.add((target == currentVertexRep) ? source : target);
				// }
				//
				// if (edges.size() == 1) {
				// existingEdge = edges.get(0);
				// currentVertexRep = vertices.get(0);
				// vertexReps.add(currentVertexRep);
				// } else {
				// if (isComplexNode(vertices)) {
				// vertexReps.addAll(vertices);
				// // It is ok to continue with only one vertexRep from the
				// // complex node.
				//
				// existingEdge = pathway.getEdge(currentVertexRep,
				// vertices.get(0));
				// if (existingEdge == null)
				// existingEdge = pathway.getEdge(vertices.get(0),
				// currentVertexRep);
				// currentVertexRep = vertices.get(0);
				//
				// } else {
				// return vertexReps;
				// }
				// }
			}

		}

		return vertexReps;
	}

	/**
	 * Determines whether the specified list of {@link PathwayVertexRep}s
	 * represents one complex node.
	 * 
	 * @param vertexReps
	 * @return
	 */
	// private boolean isComplexNode(List<PathwayVertexRep> vertexReps) {
	//
	// // Detect complex nodes by comparing the sources and targets of their
	// // edges
	// for (PathwayVertexRep vertexRep : vertexReps) {
	// for (PathwayVertexRep vRep : vertexReps) {
	// if (vertexRep != vRep) {
	//
	// List<PathwayVertexRep> edgeSources1 = new ArrayList<PathwayVertexRep>();
	// List<PathwayVertexRep> edgeTargets1 = new ArrayList<PathwayVertexRep>();
	//
	// List<PathwayVertexRep> edgeSources2 = new ArrayList<PathwayVertexRep>();
	// List<PathwayVertexRep> edgeTargets2 = new ArrayList<PathwayVertexRep>();
	//
	// getEdgeEnds(edgeSources1, edgeTargets1, vertexRep);
	// getEdgeEnds(edgeSources2, edgeTargets2, vRep);
	//
	// if ((edgeSources1.size() != edgeSources2.size())
	// || (edgeTargets1.size() != edgeTargets2.size())
	// || !(edgeTargets1.containsAll(edgeTargets2))
	// || !(edgeSources1.containsAll(edgeSources2))) {
	// return false;
	// }
	// }
	// }
	// }
	// return true;
	// }

	/**
	 * Determines the edge ends of a specified {@link PathwayVertexRep} and
	 * fills the specified lists (source, target) accordingly.
	 * 
	 * @param edges
	 * @param edgeSources
	 * @param edgeTargets
	 * @param vertexRep
	 */
	// private void getEdgeEnds(List<PathwayVertexRep> edgeSources,
	// List<PathwayVertexRep> edgeTargets, PathwayVertexRep vertexRep) {
	// Set<DefaultEdge> edges = pathway.edgesOf(vertexRep);
	// for (DefaultEdge edge : edges) {
	// PathwayVertexRep target = pathway.getEdgeTarget(edge);
	// if (target == vertexRep) {
	// edgeSources.add(pathway.getEdgeSource(edge));
	// } else {
	// edgeTargets.add(target);
	// }
	// }
	// }

	/**
	 * @return the linearizedNodes, see {@link #linearizedNodes}
	 */
	public List<ALinearizableNode> getLinearizedNodes() {
		return linearizedNodes;
	}

	/**
	 * Removes the specified linearized node from the path if it is at the start
	 * or the end of the path.
	 * 
	 * @param node
	 */
	public void removeLinearizedNode(ANode node) {
		// List<Integer> indices =
		// linearizedNodesToPathwayVertexRepIndicesMap.get(node);
		int linearizedNodeIndex = linearizedNodes.indexOf(node);

		if (linearizedNodeIndex == 0) {
			// for (int i = 0; i < indices.size(); i++) {
			path.remove(0);
			// }
		} else if (linearizedNodeIndex == path.size() - 1) {
			// for (int i = 0; i < indices.size(); i++) {
			path.remove(path.size() - 1);
			// }

		} else {
			return;
		}

		setPath(pathway, path);

		ShowBubbleSetForPathwayVertexRepsEvent event = new ShowBubbleSetForPathwayVertexRepsEvent(
				new ArrayList<PathwayVertexRep>(path));
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		isLayoutDirty = true;
	}

	/**
	 * @param dataRowHeight
	 *            setter, see {@link #dataRowHeight}
	 */
	public void setDataRowHeight(float dataRowHeight) {
		this.dataRowHeight = dataRowHeight;
	}

	/**
	 * @return the dataRowHeight, see {@link #dataRowHeight}
	 */
	public float getDataRowHeight() {
		return dataRowHeight;
	}

	@Override
	public void addDataContainer(DataContainer newDataContainer) {
		mappedDataRenderer.addDataContainer(newDataContainer);

	}

	@Override
	public void addDataContainers(List<DataContainer> newDataContainers) {
		mappedDataRenderer.addDataContainers(newDataContainers);
	}

	@Override
	public List<DataContainer> getDataContainers() {
		if (mappedDataRenderer == null)
			return null;
		return mappedDataRenderer.getDataContainers();
	}

	@Override
	public boolean isDataView() {
		return true;
	}

}
