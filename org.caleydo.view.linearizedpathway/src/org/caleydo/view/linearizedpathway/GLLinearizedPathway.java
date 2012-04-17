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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.SetMinViewSizeEvent;
import org.caleydo.core.serialize.ASerializedView;
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
import org.caleydo.datadomain.pathway.graph.item.edge.EPathwayRelationEdgeSubType;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayReactionEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayRelationEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
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
import org.caleydo.view.pathway.event.LinearizePathwayPathEvent;
import org.eclipse.swt.widgets.Composite;
import org.jgrapht.graph.DefaultEdge;

/**
 * Main view class for the linearized pathway view.
 * 
 * @author Christian Partl
 * @author Alexander Lex
 */

public class GLLinearizedPathway extends AGLView {

	public final static int DEFAULT_DATA_ROW_HEIGHT_PIXELS = 60;
	public final static int BRANCH_COLUMN_WIDTH_PIXELS = 200;
	public final static int PATHWAY_COLUMN_WIDTH_PIXELS = 150;
	public final static int MIN_NODE_DISTANCE_PIXELS = 70;
	public final static int TOP_SPACING_PIXELS = 60;
	public final static int BOTTOM_SPACING_PIXELS = 60;
	public final static int PREVIEW_NODE_DATA_ROW_HEIGHT_PIXELS = 40;
	public final static int BRANCH_SUMMARY_NODE_DISTANCE_PIXELS = 40;
	public final static int EXPANDED_BRANCH_NODE_SPACING_PIXELS = 20;
	public final static int EXPANDED_BRANCH_NODE_WIDTH_PIXELS = 150;
	public final static int SPACING_PIXELS = 2;

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
	private List<ANode> linearizedNodes = new ArrayList<ANode>();

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
	private Map<ANode, ANode> branchNodesToLinearizedNodesMap = new HashMap<ANode, ANode>();

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
	MappedDataRenderer mappedDataRenderer;

	/**
	 * The current height for all data rows.
	 */
	private float dataRowHeight;

	private LinearizePathwayPathEventListener linearizePathwayPathEventListener;

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

		mappedDataRenderer = new MappedDataRenderer(this);
		mappedDataRenderer.init(gl);
		// createNodes();

	}

	private void createNodes() {
		linearizedNodes.clear();
		numDataRows = 0;
		branchNodes.clear();
		branchNodesToLinearizedNodesMap.clear();
		linearizedNodesToIncomingBranchSummaryNodesMap.clear();
		linearizedNodesToOutgoingBranchSummaryNodesMap.clear();

		// determine complex nodes first
		List<ComplexNode> complexNodes = new ArrayList<ComplexNode>();
		ComplexNode complexNode = null;
		for (int i = 0; i < path.size(); i++) {
			PathwayVertexRep currentVertexRep = path.get(i);
			if (i + 1 < path.size()) {
				PathwayVertexRep nextVertexRep = path.get(i + 1);
				if (pathway.getEdge(currentVertexRep, nextVertexRep) == null
						&& pathway.getEdge(nextVertexRep, currentVertexRep) == null) {
					if (complexNode == null) {
						// nodeRenderers.add(complexNodeRenderer);
						complexNode = new ComplexNode(pixelGLConverter, textRenderer,
								this, lastNodeId++);
						complexNode.addVertexRep(currentVertexRep);
						// vertexRepToNodeMap.put(currentVertexRep,
						// complexNode);
						complexNodes.add(complexNode);
					}

					complexNode.addVertexRep(nextVertexRep);
					// vertexRepToNodeMap.put(nextVertexRep, complexNode);
				} else {
					if (complexNode != null) {
						complexNode = null;
					}
				}
			}
		}

		createNodesForList(linearizedNodes, path, true, complexNodes);

		// Create branch nodes
		for (int i = 0; i < linearizedNodes.size(); i++) {
			ANode currentNode = linearizedNodes.get(i);
			PathwayVertexRep currentVertexRep = currentNode.getPathwayVertexRep();
			DefaultEdge prevEdge = null;
			DefaultEdge nextEdge = null;

			if (i > 0) {
				ANode prevNode = linearizedNodes.get(i - 1);
				prevEdge = pathway.getEdge(prevNode.getPathwayVertexRep(),
						currentVertexRep);
				if (prevEdge == null)
					prevEdge = pathway.getEdge(currentVertexRep,
							prevNode.getPathwayVertexRep());
			}
			if (i != linearizedNodes.size() - 1) {
				ANode nextNode = linearizedNodes.get(i + 1);
				nextEdge = pathway.getEdge(nextNode.getPathwayVertexRep(),
						currentVertexRep);
				if (nextEdge == null)
					nextEdge = pathway.getEdge(currentVertexRep,
							nextNode.getPathwayVertexRep());
			}

			Set<DefaultEdge> edges = pathway.edgesOf(currentNode.getPathwayVertexRep());

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
				List<ANode> sourceNodes = new ArrayList<ANode>();
				complexNodes = createComplexBranchNodes(sourceVertexReps);
				createNodesForList(sourceNodes, sourceVertexReps, false, complexNodes);
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
				List<ANode> targetNodes = new ArrayList<ANode>();
				complexNodes = createComplexBranchNodes(targetVertexReps);
				createNodesForList(targetNodes, targetVertexReps, false, complexNodes);
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

	private List<ComplexNode> createComplexBranchNodes(List<PathwayVertexRep> vertexReps) {

		// Detect complex nodes by comparing their edges
		List<ComplexNode> complexNodes = new ArrayList<ComplexNode>();
		for (PathwayVertexRep vertexRep : vertexReps) {
			ComplexNode complexNode = null;
			for (PathwayVertexRep vRep : vertexReps) {
				if (vertexRep != vRep) {
					Set<DefaultEdge> edges1 = pathway.edgesOf(vertexRep);
					Set<DefaultEdge> edges2 = pathway.edgesOf(vRep);

					if ((edges1.containsAll(edges2)) && (edges1.size() == edges2.size())) {
						if (complexNode == null) {
							complexNode = new ComplexNode(pixelGLConverter, textRenderer,
									this, lastNodeId++);
							complexNode.addVertexRep(vertexRep);
							complexNodes.add(complexNode);
							// vertexRepToNodeMap.put(vertexRep, complexNode);
						}

						complexNode.addVertexRep(vRep);
						// vertexRepToNodeMap.put(vRep, complexNode);
					}
				}
			}
		}

		return complexNodes;
	}

	private void createNodesForList(List<ANode> nodeList,
			List<PathwayVertexRep> vertexReps, boolean affectsDataRows,
			List<ComplexNode> complexNodes) {
		ANode prevNode = null;
		for (PathwayVertexRep currentVertexRep : vertexReps) {

			ANode node = null;
			for (ComplexNode complexNode : complexNodes) {
				List<PathwayVertexRep> vReps = complexNode.getVertexReps();
				for (PathwayVertexRep vRep : vReps) {
					if (vRep == currentVertexRep) {
						node = complexNode;
						break;
					}
				}
			}
			if (node != null) {
				if (prevNode != node) {
					nodeList.add(node);
				}
			} else {
				if (currentVertexRep.getType() == EPathwayVertexType.compound) {
					CompoundNode compoundNode = new CompoundNode(pixelGLConverter, this,
							lastNodeId++);

					compoundNode.setPathwayVertexRep(currentVertexRep);
					compoundNode.setHeightPixels(20);
					compoundNode.setWidthPixels(20);
					node = compoundNode;

				} else {

					// TODO: Verify that this is also the right approach for
					// enzymes and ortholog
					GeneNode geneNode = new GeneNode(pixelGLConverter, textRenderer,
							this, lastNodeId++);
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
				nodeList.add(node);
				// vertexRepToNodeMap.put(currentVertexRep, node);
			}
			int numMappedValues = determineNumberOfMappedValues(currentVertexRep);
			node.setNumAssociatedRows(node.getNumAssociatedRows() + numMappedValues);

			if (affectsDataRows) {
				numDataRows += numMappedValues;
			}

			prevNode = node;
		}
	}

	private int determineNumberOfMappedValues(PathwayVertexRep vertexRep) {
		return vertexRep.getDavidIDs().size();
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
				renderCollapsedBranchNodes(gl, glu, node);
			}

			currentPosition.setY(currentPosition.y()
					- spacing.getCurrentAnchorNodeSpacing());

			ANode endAnchorNode = spacing.getEndNode();
			if (endAnchorNode != null) {
				endAnchorNode.setPosition(new Vec3f(currentPosition));
				endAnchorNode.render(gl, glu);
				// if (expandedBranchSummaryNode == null)
				renderCollapsedBranchNodes(gl, glu, endAnchorNode);
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

		if (expandedBranchSummaryNode != null)
			renderExpandedBranchNode(gl, glu);

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

		mappedDataRenderer.setFrustum(viewFrustum.getWidth() - dataRowPositionX
				- topSpacing, viewFrustum.getHeight() - 2 * topSpacing, dataRowHeight,
				dataRowPositionX, topSpacing);

		mappedDataRenderer.render(gl);
		gl.glPopMatrix();

		renderEdges(gl);

		gl.glEndList();

		// gl.glMatrixMode(GL2.GL_MODELVIEW);
		// gl.glPushMatrix();
		// gl.glTranslatef(2, 2, 2);
		// pixelGLConverter.getGLWidthForCurrentGLTransform(gl);
		// gl.glPopMatrix();

	}

	/**
	 * Renders the collapsed branch nodes for a specified linearized node. The
	 * position of this node has to be set beforehand.
	 * 
	 * @param node
	 */
	private void renderCollapsedBranchNodes(GL2 gl, GLU glu, ANode node) {

		// float branchNodePositionX = pixelGLConverter
		// .getGLWidthForPixelWidth(BRANCH_COLUMN_WIDTH_PIXELS) / 2.0f;
		float spacing = pixelGLConverter.getGLWidthForPixelWidth(SPACING_PIXELS);
		float verticalBranchNodeSpacing = pixelGLConverter.getGLHeightForPixelHeight(20);

		Vec3f nodePosition = node.getPosition();
		ANode collapsedIncomingNode = linearizedNodesToIncomingBranchSummaryNodesMap
				.get(node);
		if ((collapsedIncomingNode != null)
				&& (collapsedIncomingNode != expandedBranchSummaryNode)) {
			float minNodeWidth = pixelGLConverter
					.getGLWidthForPixelWidth(collapsedIncomingNode
							.getMinRequiredWidthPixels());

			collapsedIncomingNode.setWidthPixels(collapsedIncomingNode
					.getMinRequiredWidthPixels());
			collapsedIncomingNode.setHeightPixels(collapsedIncomingNode
					.getMinRequiredHeightPixels());
			collapsedIncomingNode.setPosition(new Vec3f(spacing + minNodeWidth / 2.0f,
					nodePosition.y() + verticalBranchNodeSpacing, 0));
			collapsedIncomingNode.render(gl, glu);

			ConnectionLineRenderer connectionLineRenderer = new ConnectionLineRenderer();
			List<Vec3f> linePoints = new ArrayList<Vec3f>();
			Vec3f sourcePosition = collapsedIncomingNode.getRightConnectionPoint();
			Vec3f targetPosition = node.getLeftConnectionPoint();
			sourcePosition.setZ(0);
			targetPosition.setZ(0);
			linePoints.add(sourcePosition);
			linePoints.add(targetPosition);

			LineEndArrowRenderer lineEndArrowRenderer = createDefaultLineEndArrowRenderer();
			connectionLineRenderer.addAttributeRenderer(lineEndArrowRenderer);

			connectionLineRenderer.renderLine(gl, linePoints);
		}

		ANode collapsedOutgoingNode = linearizedNodesToOutgoingBranchSummaryNodesMap
				.get(node);
		if ((collapsedOutgoingNode != null)
				&& (collapsedOutgoingNode != expandedBranchSummaryNode)) {

			float minNodeWidth = pixelGLConverter
					.getGLWidthForPixelWidth(collapsedOutgoingNode
							.getMinRequiredWidthPixels());

			collapsedOutgoingNode.setWidthPixels(collapsedOutgoingNode
					.getMinRequiredWidthPixels());
			collapsedOutgoingNode.setHeightPixels(collapsedOutgoingNode
					.getMinRequiredHeightPixels());
			collapsedOutgoingNode.setPosition(new Vec3f(spacing + minNodeWidth / 2.0f,
					nodePosition.y() - verticalBranchNodeSpacing, 0));
			collapsedOutgoingNode.render(gl, glu);

			ConnectionLineRenderer connectionLineRenderer = new ConnectionLineRenderer();
			List<Vec3f> linePoints = new ArrayList<Vec3f>();

			Vec3f sourcePosition = node.getLeftConnectionPoint();
			Vec3f targetPosition = collapsedOutgoingNode.getRightConnectionPoint();
			sourcePosition.setZ(0);
			targetPosition.setZ(0);
			linePoints.add(sourcePosition);
			linePoints.add(targetPosition);

			LineEndArrowRenderer lineEndArrowRenderer = createDefaultLineEndArrowRenderer();
			connectionLineRenderer.addAttributeRenderer(lineEndArrowRenderer);

			connectionLineRenderer.renderLine(gl, linePoints);
		}
	}

	/**
	 * Renders all the nodes of the branch summary node that is currently
	 * expanded. Note, that the associated linearized node has to be positioned
	 * beforehand.
	 * 
	 * @param gl
	 * @param glu
	 * @param linearizedNode
	 */
	private void renderExpandedBranchNode(GL2 gl, GLU glu) {
		ANode linearizedNode = expandedBranchSummaryNode.getAssociatedLinearizedNode();
		Vec3f branchSummaryNodePosition = expandedBranchSummaryNode.getPosition();
		float branchSummaryNodeTopY = branchSummaryNodePosition.y()
				+ pixelGLConverter.getGLHeightForPixelHeight(expandedBranchSummaryNode
						.getHeightPixels()) / 2.0f;
		float branchSummaryNodeMinHeight = pixelGLConverter
				.getGLHeightForPixelHeight(expandedBranchSummaryNode
						.getMinRequiredHeightPixels());
		float spacing = pixelGLConverter.getGLWidthForPixelWidth(SPACING_PIXELS);

		List<ANode> branchNodes = expandedBranchSummaryNode.getBranchNodes();
		// float previewDataRowHeight =
		// pixelGLConverter.getGLHeightForPixelHeight(PREVIEW_NODE_DATA_ROW_HEIGHT_PIXELS);
		float nodeSpacing = pixelGLConverter
				.getGLHeightForPixelHeight(EXPANDED_BRANCH_NODE_SPACING_PIXELS);
		float branchNodePositionX = pixelGLConverter
				.getGLWidthForPixelWidth(BRANCH_COLUMN_WIDTH_PIXELS) / 2.0f;

		float totalHeight = 0;

		for (ANode node : branchNodes) {
			totalHeight += pixelGLConverter.getGLHeightForPixelHeight(node
					.getMinRequiredHeightPixels());
			node.setHeightPixels(node.getMinRequiredHeightPixels());
			node.setWidthPixels(node.getMinRequiredWidthPixels());
		}

		totalHeight += nodeSpacing * (branchNodes.size() - 1);

		float topPositionY = branchSummaryNodeTopY - branchSummaryNodeMinHeight - spacing;
		if (topPositionY - totalHeight < viewFrustum.getBottom()) {
			topPositionY += (viewFrustum.getBottom() - (topPositionY - totalHeight));
		}
		if (topPositionY > viewFrustum.getTop() - branchSummaryNodeMinHeight) {
			topPositionY -= (topPositionY - (viewFrustum.getTop() - branchSummaryNodeMinHeight));
		}

		float currentPositionY = topPositionY;

		for (ANode node : branchNodes) {
			float nodeHeight = pixelGLConverter.getGLHeightForPixelHeight(node
					.getHeightPixels());
			node.setPosition(new Vec3f(branchNodePositionX, currentPositionY - nodeHeight
					/ 2.0f, 0.2f));
			node.render(gl, glu);
			currentPositionY -= nodeHeight + nodeSpacing;

			renderEdge(gl, node, linearizedNode, node.getRightConnectionPoint(),
					linearizedNode.getLeftConnectionPoint(), 0.2f);
		}

		float nodeWidth = pixelGLConverter
				.getGLWidthForPixelWidth(expandedBranchSummaryNode.getWidthPixels());
		float currentHeight = pixelGLConverter
				.getGLHeightForPixelHeight(expandedBranchSummaryNode.getHeightPixels());
		if (branchSummaryNodeMinHeight + 0.0001f > currentHeight || isLayoutDirty) {

			expandedBranchSummaryNode.setHeightPixels(expandedBranchSummaryNode
					.getMinRequiredHeightPixels()
					+ pixelGLConverter.getPixelHeightForGLHeight(totalHeight)
					+ 2
					* SPACING_PIXELS);
			expandedBranchSummaryNode.setPosition(new Vec3f(spacing + nodeWidth / 2.0f,
					topPositionY + branchSummaryNodeMinHeight + spacing
							- (totalHeight + branchSummaryNodeMinHeight + 2 * spacing)
							/ 2.0f, 0.15f));
		}
		expandedBranchSummaryNode.render(gl, glu);

		float coverWidth = pixelGLConverter
				.getGLWidthForPixelWidth(PATHWAY_COLUMN_WIDTH_PIXELS
						+ BRANCH_COLUMN_WIDTH_PIXELS);

		// gl.glPushName(pickingManager.getPickingID(getID(), "asd", 0));
		gl.glColor4f(1, 1, 1, 0.9f);

		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0.1f);
		gl.glVertex3f(coverWidth, 0, 0.1f);
		gl.glVertex3f(coverWidth, viewFrustum.getHeight(), 0.1f);
		gl.glVertex3f(0, viewFrustum.getHeight(), 0.1f);
		gl.glEnd();
		// gl.glPopName();

	}

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

	private void renderEdges(GL2 gl) {
		for (int i = 0; i < linearizedNodes.size() - 1; i++) {
			ANode node1 = linearizedNodes.get(i);
			ANode node2 = linearizedNodes.get(i + 1);
			renderEdge(gl, node1, node2, node1.getBottomConnectionPoint(),
					node2.getTopConnectionPoint(), 0.2f);
		}
	}

	private void renderEdge(GL2 gl, ANode node1, ANode node2, Vec3f node1ConnectionPoint,
			Vec3f node2ConnectionPoint, float zCoordinate) {

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
			ClosedArrowRenderer arrowRenderer = new ClosedArrowRenderer(pixelGLConverter);
			LineEndArrowRenderer lineEndArrowRenderer = new LineEndArrowRenderer(false,
					arrowRenderer);

			connectionRenderer.addAttributeRenderer(lineEndArrowRenderer);

			// linePoints.add(nodeRenderer1.getBottomConnectionPoint());
			// linePoints.add(nodeRenderer2.getTopConnectionPoint());

		} else {
			if (edge instanceof PathwayRelationEdgeRep) {
				PathwayRelationEdgeRep relationEdgeRep = (PathwayRelationEdgeRep) edge;

				ArrayList<EPathwayRelationEdgeSubType> subtypes = relationEdgeRep
						.getRelationSubTypes();
				float spacing = pixelGLConverter.getGLHeightForPixelHeight(2);

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
								.addAttributeRenderer(createDefaultLineEndStaticLineRenderer());
						targetConnectionPoint.setY(targetConnectionPoint.y()
								+ ((isNode1Target) ? -spacing : spacing));
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
								.addAttributeRenderer(createDefaultLineEndStaticLineRenderer());
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

	private LineEndStaticLineRenderer createDefaultLineEndStaticLineRenderer() {
		LineEndStaticLineRenderer lineEndRenderer = new LineEndStaticLineRenderer(false,
				pixelGLConverter);
		lineEndRenderer.setHorizontalLine(true);
		return lineEndRenderer;
	}

	private LineLabelRenderer createDefaultLabelOnLineRenderer(String text) {
		LineLabelRenderer lineLabelRenderer = new LineLabelRenderer(0.66f,
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
		eventPublisher.addListener(LinearizePathwayPathEvent.class,
				linearizePathwayPathEventListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (linearizePathwayPathEventListener != null) {
			eventPublisher.removeListener(linearizePathwayPathEventListener);
			linearizePathwayPathEventListener = null;
		}
	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

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
	public void selectBranch(ANode node) {
		ANode linearizedNode = branchNodesToLinearizedNodesMap.get(node);

		PathwayVertexRep linearizedVertexRep = linearizedNode.getPathwayVertexRep();
		PathwayVertexRep branchVertexRep = node.getPathwayVertexRep();

		DefaultEdge edge = pathway.getEdge(linearizedVertexRep, branchVertexRep);
		if (edge == null) {
			edge = pathway.getEdge(branchVertexRep, linearizedVertexRep);
		}

		int linearizedNodeIndex = linearizedNodes.indexOf(linearizedNode);
		List<PathwayVertexRep> newPath = null;
		if (pathway.getEdgeSource(edge) == branchVertexRep) {
			// insert above linearized node
			newPath = path.subList(linearizedNodeIndex, path.size());
			newPath.add(0, branchVertexRep);

		} else {
			// insert below linearized node
			newPath = path.subList(0, linearizedNodeIndex + 1);
			newPath.add(branchVertexRep);
		}
//		LinearizePathwayPathEvent event = new LinearizePathwayPathEvent();
//		event.setPath(newPath);
//		event.setPathway(pathway);
//
//		eventPublisher.triggerEvent(event);

		setPath(pathway, newPath);
	}

	/**
	 * @return the linearizedNodes, see {@link #linearizedNodes}
	 */
	public List<ANode> getLinearizedNodes() {
		return linearizedNodes;
	}

	/**
	 * Removes the specified linearized node from the path if it is at the start
	 * or the end of the path.
	 * 
	 * @param node
	 */
	public void removeLinearizedNode(ANode node) {
		int index = linearizedNodes.indexOf(node);

		if ((index == 0) || (index == linearizedNodes.size() - 1)) {
			path.remove(index);
			setPath(pathway, path);
		}
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

}
