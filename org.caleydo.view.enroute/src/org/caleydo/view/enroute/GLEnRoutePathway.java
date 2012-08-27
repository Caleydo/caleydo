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
package org.caleydo.view.enroute;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.SetMinViewSizeEvent;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedMultiTablePerspectiveBasedView;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.AddTablePerspectivesListener;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveListener;
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
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.edge.EPathwayReactionEdgeType;
import org.caleydo.datadomain.pathway.graph.item.edge.EPathwayRelationEdgeSubType;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayReactionEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayRelationEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGroupRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.enroute.event.RemoveEnRouteNodeEvent;
import org.caleydo.view.enroute.listener.EnRoutePathEventListener;
import org.caleydo.view.enroute.listener.RemoveEnRouteNodeEventListener;
import org.caleydo.view.enroute.mappeddataview.MappedDataRenderer;
import org.caleydo.view.enroute.node.ALinearizableNode;
import org.caleydo.view.enroute.node.ANode;
import org.caleydo.view.enroute.node.BranchSummaryNode;
import org.caleydo.view.enroute.node.ComplexNode;
import org.caleydo.view.enroute.node.CompoundNode;
import org.caleydo.view.enroute.node.GeneNode;
import org.caleydo.view.pathway.event.EnRoutePathEvent;
import org.eclipse.swt.widgets.Composite;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.GraphPathImpl;

/**
 * Main view class for the linearized pathway view.
 * 
 * @author Christian Partl
 * @author Alexander Lex
 */

public class GLEnRoutePathway extends AGLView implements IMultiTablePerspectiveBasedView,
		IEventBasedSelectionManagerUser {

	public static String VIEW_TYPE = "org.caleydo.view.enroute";
	public static String VIEW_NAME = "enRoute";

	public final static int DEFAULT_DATA_ROW_HEIGHT_PIXELS = 60;
	public final static int BRANCH_COLUMN_WIDTH_PIXELS = 100;
	public final static int PATHWAY_COLUMN_WIDTH_PIXELS = 150;
	public final static int DATA_COLUMN_WIDTH_PIXELS = 350;
	public final static int MIN_NODE_SPACING_PIXELS = 50;
	public final static int TOP_SPACING_PIXELS = 60;
	public final static int TOP_SPACING_MAPPED_DATA = 10;
	public final static int SIDE_SPACING_MAPPED_DATA = 10;
	public final static int BOTTOM_SPACING_PIXELS = 60;
	public final static int PREVIEW_NODE_DATA_ROW_HEIGHT_PIXELS = 40;
	public final static int BRANCH_SUMMARY_NODE_TO_LINEARIZED_NODE_VERTICAL_DISTANCE_PIXELS = 20;
	public final static int EXPANDED_BRANCH_NODE_SPACING_PIXELS = 20;
	public final static int EXPANDED_BRANCH_NODE_WIDTH_PIXELS = 150;
	public final static int SPACING_PIXELS = 2;
	public final static int BRANCH_AREA_SIDE_SPACING_PIXELS = 8;

	public final static int DEFAULT_MAX_BRANCH_SWITCHING_PATH_LENGTH = 5;

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
	 * The {@link IDataDomain}s for which data is displayed in this view.
	 */
	private Set<IDataDomain> dataDomains = new HashSet<IDataDomain>();

	/**
	 * The branch node that is currently expanded to show the possible branches.
	 */
	private BranchSummaryNode expandedBranchSummaryNode = null;

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

	/**
	 * Determines whether the layout needs to be updated. This is a more severe
	 * update than only the display list update.
	 */
	private boolean isLayoutDirty = true;

	private EventBasedSelectionManager geneSelectionManager;
	private EventBasedSelectionManager metaboliteSelectionManager;

	private EnRoutePathEventListener linearizePathwayPathEventListener;
	private AddTablePerspectivesListener addTablePerspectivesListener;
	private RemoveEnRouteNodeEventListener removeLinearizedNodeEventListener;
	private RemoveTablePerspectiveListener removeTablePerspectiveListener;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLEnRoutePathway(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		mappedDataRenderer = new MappedDataRenderer(this);

		geneSelectionManager = new EventBasedSelectionManager(this,
				IDType.getIDType("DAVID"));
		geneSelectionManager.registerEventListeners();

		metaboliteSelectionManager = new EventBasedSelectionManager(this,
				IDType.getIDType("METABOLITE"));
		metaboliteSelectionManager.registerEventListeners();

	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		textRenderer = new CaleydoTextRenderer(24);

		super.renderStyle = renderStyle;
		detailLevel = EDetailLevel.HIGH;

		path = new ArrayList<PathwayVertexRep>();
		// Create sample path
		for (PathwayGraph graph : PathwayManager.get().getAllItems()) {
			if (graph.getType() == EPathwayDatabaseType.KEGG
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

		for (int i = 0; i < 6; i++) {

			path.add(currentVertex);

			for (DefaultEdge edge : pathway.edgesOf(currentVertex)) {
				PathwayVertexRep v2 = pathway.getEdgeTarget(edge);

				currentVertex = v2;
			}
		}

		setPath(pathway, path);

		setMappedDataRendererGeometry();

		// mappedDataRenderer.init(gl);
		// createNodes();

	}

	private void createNodes() {
		linearizedNodes.clear();
		branchNodes.clear();
		branchNodesToLinearizedNodesMap.clear();
		linearizedNodesToIncomingBranchSummaryNodesMap.clear();
		linearizedNodesToOutgoingBranchSummaryNodesMap.clear();

		createNodesForList(linearizedNodes, path);

		// Create branch nodes
		for (int i = 0; i < linearizedNodes.size(); i++) {
			ALinearizableNode currentNode = linearizedNodes.get(i);
			PathwayVertexRep currentVertexRep = currentNode.getPathwayVertexRep();
			PathwayVertexRep prevVertexRep = null;
			PathwayVertexRep nextVertexRep = null;

			if (i > 0) {
				ALinearizableNode prevNode = linearizedNodes.get(i - 1);
				prevVertexRep = prevNode.getPathwayVertexRep();
			}
			if (i != linearizedNodes.size() - 1) {
				ALinearizableNode nextNode = linearizedNodes.get(i + 1);
				nextVertexRep = nextNode.getPathwayVertexRep();
			}

			BranchSummaryNode incomingNode = new BranchSummaryNode(this, lastNodeId++,
					currentNode);
			BranchSummaryNode outgoingNode = new BranchSummaryNode(this, lastNodeId++,
					currentNode);
			List<PathwayVertexRep> sourceVertexReps = Graphs.predecessorListOf(pathway,
					currentVertexRep);
			sourceVertexReps.remove(prevVertexRep);
			List<PathwayVertexRep> targetVertexReps = Graphs.successorListOf(pathway,
					currentVertexRep);
			targetVertexReps.remove(nextVertexRep);

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
					geneNode.setLabel(currentVertexRep.getName().substring(0, commaIndex));
				} else {
					geneNode.setLabel(currentVertexRep.getName());
				}
				geneNode.setPathwayVertexRep(currentVertexRep);

				node = geneNode;
			}

			nodes.add(node);
			setMappedDavidIds(node);
		}
	}

	private List<Integer> setMappedDavidIds(ALinearizableNode node) {
		List<Integer> mappedDavidIds = new ArrayList<Integer>();

		if (node instanceof ComplexNode) {
			ComplexNode complexNode = (ComplexNode) node;

			for (ALinearizableNode groupedNode : complexNode.getNodes()) {
				mappedDavidIds.addAll(setMappedDavidIds(groupedNode));
			}
		} else {
			// TODO: This is only true if the davidID maps to one id of the
			// genetic
			for (Integer davidID : node.getPathwayVertexRep().getDavidIDs()) {
				if (doesDavidMapToData(davidID))
					mappedDavidIds.add(davidID);
			}
		}
		node.setDavidIDs(mappedDavidIds);

		return mappedDavidIds;
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

		// setMappedDataRendererGeometry();
		if (isDisplayListDirty) {
			buildDisplayList(gl, displayListIndex);
			isDisplayListDirty = false;
			isLayoutDirty = false;
		}
		gl.glCallList(displayListIndex);

		checkForHits(gl);
	}

	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {

		dataRowHeight = pixelGLConverter
				.getGLHeightForPixelHeight(DEFAULT_DATA_ROW_HEIGHT_PIXELS);
		setMappedDataRendererGeometry();

		gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);

		float branchColumnWidth = pixelGLConverter
				.getGLWidthForPixelWidth(BRANCH_COLUMN_WIDTH_PIXELS);
		float pathwayColumnWidth = pixelGLConverter
				.getGLWidthForPixelWidth(PATHWAY_COLUMN_WIDTH_PIXELS);

		GLU glu = new GLU();

		List<AnchorNodeSpacing> anchorNodeSpacings = calcAnchorNodeSpacings();

		Vec3f currentPosition = new Vec3f(branchColumnWidth + pathwayColumnWidth / 2.0f,
				viewFrustum.getHeight(), 0.2f);
		float pathwayHeight = 0;
		float minNodeSpacing = pixelGLConverter
				.getGLHeightForPixelHeight(MIN_NODE_SPACING_PIXELS);

		for (AnchorNodeSpacing spacing : anchorNodeSpacings) {

			float currentAnchorNodeSpacing = spacing.getCurrentAnchorNodeSpacing();

			float nodeSpacing = (Float.isNaN(currentAnchorNodeSpacing) ? minNodeSpacing
					: (currentAnchorNodeSpacing - spacing.getTotalNodeHeight())
							/ ((float) spacing.getNodesInbetween().size() + 1));
			ANode startAnchorNode = spacing.getStartNode();

			float currentInbetweenNodePositionY = currentPosition.y()
					- ((startAnchorNode != null) ? startAnchorNode.getHeight() / 2.0f : 0);

			for (int i = 0; i < spacing.getNodesInbetween().size(); i++) {
				ANode node = spacing.getNodesInbetween().get(i);

				node.setPosition(new Vec3f(currentPosition.x(),
						currentInbetweenNodePositionY - nodeSpacing - node.getHeight()
								/ 2.0f, currentPosition.z()));
				node.render(gl, glu);
				currentInbetweenNodePositionY -= (nodeSpacing + node.getHeight());

				renderBranchNodes(gl, glu, node);
			}

			currentPosition.setY(currentPosition.y()
					- spacing.getCurrentAnchorNodeSpacing());

			ANode endAnchorNode = spacing.getEndNode();
			if (endAnchorNode != null) {
				endAnchorNode.setPosition(new Vec3f(currentPosition));
				endAnchorNode.render(gl, glu);
				renderBranchNodes(gl, glu, endAnchorNode);
			}

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
			setMinSize(minViewHeightPixels);
		}

		float dataRowPositionX = branchColumnWidth + pathwayColumnWidth;

		float topSpacing = pixelGLConverter
				.getGLWidthForPixelWidth(TOP_SPACING_MAPPED_DATA);
		gl.glPushMatrix();

		// TODO do this only when necessary - cause re-initialization
		if (isLayoutDirty) {
			mappedDataRenderer.setLinearizedNodes(linearizedNodes);
		}
		gl.glTranslatef(dataRowPositionX, topSpacing, 0);
		mappedDataRenderer.render(gl);
		gl.glPopMatrix();

		renderEdgesOfLinearizedNodes(gl);

		gl.glEndList();

	}

	private void setMinSize(int minHeightPixels) {
		SetMinViewSizeEvent event = new SetMinViewSizeEvent();
		event.setMinViewSize(BRANCH_COLUMN_WIDTH_PIXELS + PATHWAY_COLUMN_WIDTH_PIXELS
				+ DATA_COLUMN_WIDTH_PIXELS, minHeightPixels);
		event.setView(this);
		eventPublisher.triggerEvent(event);
		setDisplayListDirty();
	}

	/**
	 * Calculates the spacings between all anchor nodes (nodes with mapped data)
	 * of the path.
	 * 
	 * @return
	 */
	private List<AnchorNodeSpacing> calcAnchorNodeSpacings() {

		List<AnchorNodeSpacing> anchorNodeSpacings = new ArrayList<AnchorNodeSpacing>();
		List<ANode> unmappedNodes = new ArrayList<ANode>();
		ALinearizableNode currentAnchorNode = null;

		for (int i = 0; i < linearizedNodes.size(); i++) {

			ALinearizableNode node = linearizedNodes.get(i);
			int numAssociatedRows = node.getNumAssociatedRows();

			if (numAssociatedRows == 0) {
				unmappedNodes.add(node);

			} else {
				AnchorNodeSpacing anchorNodeSpacing = createAnchorNodeSpacing(
						currentAnchorNode, node, unmappedNodes,
						currentAnchorNode == null, false);

				anchorNodeSpacings.add(anchorNodeSpacing);

				unmappedNodes = new ArrayList<ANode>();
				currentAnchorNode = node;
			}

			if (i == linearizedNodes.size() - 1) {
				AnchorNodeSpacing anchorNodeSpacing = createAnchorNodeSpacing(
						currentAnchorNode, null, unmappedNodes,
						currentAnchorNode == null, true);
				anchorNodeSpacings.add(anchorNodeSpacing);

			}
		}

		return anchorNodeSpacings;
	}

	private AnchorNodeSpacing createAnchorNodeSpacing(ALinearizableNode startAnchorNode,
			ALinearizableNode endAnchorNode, List<ANode> nodesInbetween,
			boolean isFirstSpacing, boolean isLastSpacing) {

		AnchorNodeSpacing anchorNodeSpacing = new AnchorNodeSpacing();
		anchorNodeSpacing.setStartNode(startAnchorNode);
		anchorNodeSpacing.setEndNode(endAnchorNode);
		anchorNodeSpacing.setNodesInbetween(nodesInbetween);
		anchorNodeSpacing.calcTotalNodeHeight();

		float minNodeSpacing = pixelGLConverter
				.getGLHeightForPixelHeight(MIN_NODE_SPACING_PIXELS);

		int numSpacingAnchorNodeRows = 0;
		if (startAnchorNode != null) {
			numSpacingAnchorNodeRows += startAnchorNode.getNumAssociatedRows();
		}
		if (endAnchorNode != null) {
			numSpacingAnchorNodeRows += endAnchorNode.getNumAssociatedRows();
		}

		float additionalSpacing = 0;
		if (isFirstSpacing)
			additionalSpacing += pixelGLConverter
					.getGLHeightForPixelHeight(TOP_SPACING_PIXELS);
		if (isLastSpacing)
			additionalSpacing += pixelGLConverter
					.getGLHeightForPixelHeight(BOTTOM_SPACING_PIXELS);

		anchorNodeSpacing.setCurrentAnchorNodeSpacing(Math.max(
				dataRowHeight * ((float) numSpacingAnchorNodeRows) / 2.0f
						+ additionalSpacing,
				minNodeSpacing * (float) (nodesInbetween.size() + 1)
						+ anchorNodeSpacing.getTotalNodeHeight()));

		return anchorNodeSpacing;
	}

	/**
	 * Renders the branch nodes for a specified linearized node. The position of
	 * this node has to be set beforehand.
	 * 
	 * @param node
	 */
	private void renderBranchNodes(GL2 gl, GLU glu, ANode node) {

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
			int minViewHeightPixels = pixelGLConverter
					.getPixelHeightForGLHeight(viewFrustum.getBottom() - bottomPositionY)
					+ parentGLCanvas.getHeight();
			setMinSize(minViewHeightPixels);
		}
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
		lineLabelRenderer.setXCentered(true);
		lineLabelRenderer.setYCentered(true);
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
	public ASerializedMultiTablePerspectiveBasedView getSerializableRepresentation() {
		SerializedEnRoutePathwayView serializedForm = new SerializedEnRoutePathwayView();
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

		linearizePathwayPathEventListener = new EnRoutePathEventListener();
		linearizePathwayPathEventListener.setHandler(this);
		eventPublisher.addListener(EnRoutePathEvent.class,
				linearizePathwayPathEventListener);

		addTablePerspectivesListener = new AddTablePerspectivesListener();
		addTablePerspectivesListener.setHandler(this);
		eventPublisher.addListener(AddTablePerspectivesEvent.class,
				addTablePerspectivesListener);

		removeLinearizedNodeEventListener = new RemoveEnRouteNodeEventListener();
		removeLinearizedNodeEventListener.setHandler(this);
		eventPublisher.addListener(RemoveEnRouteNodeEvent.class,
				removeLinearizedNodeEventListener);

		removeTablePerspectiveListener = new RemoveTablePerspectiveListener();
		removeTablePerspectiveListener.setHandler(this);
		eventPublisher.addListener(RemoveTablePerspectiveEvent.class,
				removeTablePerspectiveListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (linearizePathwayPathEventListener != null) {
			eventPublisher.removeListener(linearizePathwayPathEventListener);
			linearizePathwayPathEventListener = null;
		}

		if (addTablePerspectivesListener != null) {
			eventPublisher.removeListener(addTablePerspectivesListener);
			addTablePerspectivesListener = null;
		}

		if (removeLinearizedNodeEventListener != null) {
			eventPublisher.removeListener(removeLinearizedNodeEventListener);
			removeLinearizedNodeEventListener = null;
		}

		if (removeTablePerspectiveListener != null) {
			eventPublisher.removeListener(removeTablePerspectiveListener);
			removeTablePerspectiveListener = null;
		}

		geneSelectionManager.unregisterEventListeners();
		metaboliteSelectionManager.unregisterEventListeners();
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

		setMinSize(0);

		createNodes();
		setLayoutDirty();

	}

	private void setMappedDataRendererGeometry() {

		float topSpacing = pixelGLConverter
				.getGLHeightForPixelHeight(TOP_SPACING_MAPPED_DATA);
		float branchColumnWidth = pixelGLConverter
				.getGLWidthForPixelWidth(BRANCH_COLUMN_WIDTH_PIXELS);
		float pathwayColumnWidth = pixelGLConverter
				.getGLWidthForPixelWidth(PATHWAY_COLUMN_WIDTH_PIXELS);
		float dataRowPositionX = branchColumnWidth + pathwayColumnWidth;

		mappedDataRenderer.setGeometry(viewFrustum.getWidth() - dataRowPositionX
				- topSpacing, viewFrustum.getHeight() - 2 * topSpacing, dataRowPositionX,
				topSpacing, dataRowHeight);

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
		BranchSummaryNode summaryNode = (BranchSummaryNode) linearizedNodesToIncomingBranchSummaryNodesMap
				.get(linearizedNode);

		boolean isIncomingBranch = false;
		if (summaryNode != null && summaryNode.getBranchNodes().contains(node)) {
			isIncomingBranch = true;
		}

		PathwayVertexRep linearizedVertexRep = linearizedNode.getPathwayVertexRep();
		PathwayVertexRep branchVertexRep = node.getPathwayVertexRep();

		DefaultEdge edge = pathway.getEdge(linearizedVertexRep, branchVertexRep);
		if (edge == null) {
			edge = pathway.getEdge(branchVertexRep, linearizedVertexRep);
		}

		int linearizedNodeIndex = linearizedNodes.indexOf(linearizedNode);
		List<PathwayVertexRep> newPath = null;
		List<PathwayVertexRep> branchPath = determineDefiniteUniDirectionalBranchPath(
				branchVertexRep, linearizedVertexRep, isIncomingBranch);

		if (isIncomingBranch) {
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

		broadcastPath();
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
	 * @param isIncomingBranchPath
	 *            Determines whether the branch path is incoming or outgoing.
	 *            This is especially important for bidirectional edges.
	 * @return
	 */
	private List<PathwayVertexRep> determineDefiniteUniDirectionalBranchPath(
			PathwayVertexRep branchVertexRep, PathwayVertexRep linearizedVertexRep,
			boolean isIncomingBranchPath) {

		List<PathwayVertexRep> vertexReps = new ArrayList<PathwayVertexRep>();
		vertexReps.add(branchVertexRep);
		DefaultEdge existingEdge = pathway.getEdge(branchVertexRep, linearizedVertexRep);
		if (existingEdge == null)
			existingEdge = pathway.getEdge(linearizedVertexRep, branchVertexRep);

		PathwayVertexRep currentVertexRep = branchVertexRep;

		for (int i = 0; i < maxBranchSwitchingPathLength; i++) {
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
			}

		}

		return vertexReps;
	}

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
	public void removeLinearizedNode(ALinearizableNode node) {

		int linearizedNodeIndex = linearizedNodes.indexOf(node);

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
					pathway, startVertexRep, endVertexRep, edges, (double) edges.size());

			pathwayPath = new PathwayPath(graphPath);
		}
		EnRoutePathEvent event = new EnRoutePathEvent();
		event.setPath(pathwayPath);
		event.setSender(this);
		eventPublisher.triggerEvent(event);

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		setMappedDataRendererGeometry();
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
	public void addTablePerspective(TablePerspective newTablePerspective) {
		mappedDataRenderer.addTablePerspective(newTablePerspective);
		for (ALinearizableNode node : linearizedNodes) {
			setMappedDavidIds(node);
		}
		for (ANode node : branchNodes) {
			if (node instanceof ALinearizableNode) {
				setMappedDavidIds((ALinearizableNode) node);
				((ALinearizableNode) node).update();
			}
		}
		dataDomains.add(newTablePerspective.getDataDomain());
		setMappedDataRendererGeometry();
		setLayoutDirty();
	}

	@Override
	public void addTablePerspectives(List<TablePerspective> newTablePerspectives) {
		mappedDataRenderer.addTablePerspectives(newTablePerspectives);
		for (ALinearizableNode node : linearizedNodes) {
			setMappedDavidIds(node);
		}
		for (ANode node : branchNodes) {
			if (node instanceof ALinearizableNode) {
				setMappedDavidIds((ALinearizableNode) node);
				((ALinearizableNode) node).update();
			}
		}
		for (TablePerspective tablePerspective : newTablePerspectives) {
			dataDomains.add(tablePerspective.getDataDomain());
		}

		setMappedDataRendererGeometry();

		TablePerspectivesChangedEvent event = new TablePerspectivesChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
		setLayoutDirty();
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {
		if (mappedDataRenderer == null)
			return null;
		return mappedDataRenderer.getTablePerspectives();
	}

	@Override
	public boolean isDataView() {
		return true;
	}

	/**
	 * Returns true if one of the set {@link TablePerspective}s maps to the
	 * davidID provided, else false
	 */
	public boolean doesDavidMapToData(Integer davidID) {
		for (TablePerspective currentTablePerspective : mappedDataRenderer
				.getTablePerspectives()) {
			GeneticDataDomain dataDomain = (GeneticDataDomain) currentTablePerspective
					.getDataDomain();
			Set<Integer> ids = dataDomain.getGeneIDMappingManager().getIDAsSet(
					IDType.getIDType("DAVID"), dataDomain.getGeneIDType(), davidID);
			if (ids != null && ids.size() > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void notifyOfChange() {
		setDisplayListDirty();
	}

	/**
	 * @return the geneSelectionManager, see {@link #geneSelectionManager}
	 */
	public EventBasedSelectionManager getGeneSelectionManager() {
		return geneSelectionManager;
	}

	/**
	 * @return the metaboliteSelectionManager, see
	 *         {@link #metaboliteSelectionManager}
	 */
	public EventBasedSelectionManager getMetaboliteSelectionManager() {
		return metaboliteSelectionManager;
	}

	/**
	 * @return the mappedDataRenderer, see {@link #mappedDataRenderer}
	 */
	public MappedDataRenderer getMappedDataRenderer() {
		return mappedDataRenderer;
	}

	public List<TablePerspective> getResolvedTablePerspectives() {
		return mappedDataRenderer.getResolvedTablePerspectives();
	}

	@Override
	public Set<IDataDomain> getDataDomains() {
		return dataDomains;
	}

	@Override
	public void removeTablePerspective(int tablePerspectiveID) {

		for (TablePerspective tablePerspective : mappedDataRenderer
				.getTablePerspectives()) {
			if (tablePerspective.getID() == tablePerspectiveID) {
				IDataDomain dataDomain = tablePerspective.getDataDomain();
				boolean removeDataDomain = true;
				for (TablePerspective tp : mappedDataRenderer.getTablePerspectives()) {
					if (tp != tablePerspective && tp.getDataDomain() == dataDomain) {
						removeDataDomain = false;
						break;
					}
				}

				if (removeDataDomain) {
					dataDomains.remove(dataDomain);
				}
				break;
			}
		}
		mappedDataRenderer.removeTablePerspective(tablePerspectiveID);

		TablePerspectivesChangedEvent event = new TablePerspectivesChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);

		setLayoutDirty();
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		gl.glDeleteLists(displayListIndex, 1);
		// TODO: Destroy all the layoutManagers

	}

	public void setLayoutDirty() {
		isLayoutDirty = true;
		setDisplayListDirty();
	}

}
