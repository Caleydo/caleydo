package org.caleydo.view.linearizedpathway;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2;
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
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.PathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.linearizedpathway.renderstyle.TemplateRenderStyle;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.jgrapht.graph.DefaultEdge;

/**
 * 
 * 
 * @author Christian
 */

public class GLLinearizedPathway extends AGLView {

	public final static int MIN_DATA_ROW_HEIGHT_PIXELS = 60;
	public final static int BRANCH_COLUMN_WIDTH_PIXELS = 200;
	public final static int PATHWAY_COLUMN_WIDTH_PIXELS = 150;
	public final static int MIN_NODE_DISTANCE_PIXELS = 70;
	public final static int TOP_SPACING_PIXELS = 60;
	public final static int BOTTOM_SPACING_PIXELS = 60;

	public final static String VIEW_TYPE = "org.caleydo.view.linearizedpathway";

	private TemplateRenderStyle renderStyle;

	/**
	 * The pathway of the linearized path.
	 */
	private PathwayGraph pathway;

	/**
	 * The path of the pathway that is currently linearized.
	 */
	private List<PathwayVertexRep> path;

	/**
	 * The list of nodes that are rendered.
	 */
	private List<ANodeRenderer> nodeRenderers = new ArrayList<ANodeRenderer>();

	/**
	 * Map that associates all {@link PathwayVertexRep} objects from the path
	 * with a node renderer.
	 */
	private Map<PathwayVertexRep, ANodeRenderer> vertexRepToNodeRendererMap = new HashMap<PathwayVertexRep, ANodeRenderer>();

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

		createNodeRenderers();

	}

	private void createNodeRenderers() {

		vertexRepToNodeRendererMap.clear();
		nodeRenderers.clear();
		numDataRows = 0;

		// determine complex nodes first
		ComplexNodeRenderer complexNodeRenderer = null;
		for (int i = 0; i < path.size(); i++) {
			PathwayVertexRep currentVertexRep = path.get(i);
			if (i + 1 < path.size()) {
				PathwayVertexRep nextVertexRep = path.get(i + 1);
				if (pathway.getEdge(currentVertexRep, nextVertexRep) == null
						&& pathway.getEdge(nextVertexRep, currentVertexRep) == null) {
					if (complexNodeRenderer == null) {
						// nodeRenderers.add(complexNodeRenderer);
						complexNodeRenderer = new ComplexNodeRenderer(pixelGLConverter,
								textRenderer);
						complexNodeRenderer.addVertexRep(currentVertexRep);
						vertexRepToNodeRendererMap.put(currentVertexRep,
								complexNodeRenderer);
					}

					complexNodeRenderer.addVertexRep(nextVertexRep);
					vertexRepToNodeRendererMap.put(nextVertexRep, complexNodeRenderer);
				} else {
					if (complexNodeRenderer != null) {
						complexNodeRenderer = null;
					}
				}
			}
		}

		ANodeRenderer prevNodeRenderer = null;
		for (PathwayVertexRep currentVertexRep : path) {

			ANodeRenderer nodeRenderer = vertexRepToNodeRendererMap.get(currentVertexRep);
			if (nodeRenderer != null) {
				if (prevNodeRenderer != nodeRenderer) {
					nodeRenderers.add(nodeRenderer);
				}
			} else {
				if (currentVertexRep.getType() == EPathwayVertexType.compound) {
					CompoundNodeRenderer compoundNodeRenderer = new CompoundNodeRenderer(
							pixelGLConverter);

					compoundNodeRenderer.setPathwayVertexRep(currentVertexRep);
					compoundNodeRenderer.setHeightPixels(20);
					compoundNodeRenderer.setWidthPixels(20);
					nodeRenderer = compoundNodeRenderer;

				} else {

					// TODO: Verify that this also the right approach for
					// enzymes and ortholog
					GeneNodeRenderer geneNodeRenderer = new GeneNodeRenderer(
							pixelGLConverter, textRenderer);
					int commaIndex = currentVertexRep.getName().indexOf(',');
					if (commaIndex > 0) {
						geneNodeRenderer.setCaption(currentVertexRep.getName().substring(
								0, commaIndex));
					} else {
						geneNodeRenderer.setCaption(currentVertexRep.getName());
					}
					geneNodeRenderer.setPathwayVertexRep(currentVertexRep);

					nodeRenderer = geneNodeRenderer;
				}
				nodeRenderers.add(nodeRenderer);
				vertexRepToNodeRendererMap.put(currentVertexRep, nodeRenderer);
			}
			int numMappedValues = determineNumberOfMappedValues(currentVertexRep);
			nodeRenderer.setNumAssociatedRows(nodeRenderer.getNumAssociatedRows()
					+ numMappedValues);

			numDataRows += numMappedValues;

			prevNodeRenderer = nodeRenderer;
		}

	}

	private int determineNumberOfMappedValues(PathwayVertexRep vertexRep) {
		List<PathwayVertex> vertices = vertexRep.getPathwayVertices();
		if (vertices == null)
			return 0;

		int numMappedGenes = 0;

		for (PathwayVertex vertex : vertices) {
			int davidId = PathwayItemManager.get().getDavidIdByPathwayVertex(vertex);

			for (GeneticDataDomain dataDomain : geneticDataDomains) {
				Set<Integer> ids = dataDomain.getGeneIDMappingManager().getIDAsSet(
						pathwayDataDomain.getDavidIDType(), dataDomain.getGeneIDType(),
						davidId);

				if (ids != null && !ids.isEmpty()) {
					numMappedGenes++;
					// TODO: or is it this way?
					// numMappedGenes+= ids.size();
					break;
				}
			}
		}

		return numMappedGenes;
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

		float branchColumnWidth = pixelGLConverter
				.getGLWidthForPixelWidth(BRANCH_COLUMN_WIDTH_PIXELS);
		float pathwayColumnWidth = pixelGLConverter
				.getGLWidthForPixelWidth(PATHWAY_COLUMN_WIDTH_PIXELS);

		GLU glu = new GLU();

		List<AnchorNodeSpacing> anchorNodeSpacings = calcAnchorNodeSpacings();
		float dataRowHeight = Float.MAX_VALUE;
		Vec3f currentPosition = new Vec3f(branchColumnWidth + pathwayColumnWidth / 2.0f,
				viewFrustum.getHeight(), 0);
		float pathwayHeight = 0;

		for (AnchorNodeSpacing spacing : anchorNodeSpacings) {

			float yStep = spacing.getCurrentAnchorNodeSpacing()
					/ ((float) spacing.getNodesInbetween().size() + 1);
			for (int i = 0; i < spacing.getNodesInbetween().size(); i++) {
				ANodeRenderer nodeRenderer = spacing.getNodesInbetween().get(i);
				nodeRenderer.setPosition(new Vec3f(currentPosition.x(), currentPosition
						.y() - (i + 1) * yStep, currentPosition.z()));
				nodeRenderer.render(gl, glu);
			}

			currentPosition.setY(currentPosition.y()
					- spacing.getCurrentAnchorNodeSpacing());

			ANodeRenderer endAnchorNode = spacing.getEndNode();
			if (endAnchorNode != null) {
				endAnchorNode.setPosition(new Vec3f(currentPosition));
				endAnchorNode.render(gl, glu);
			}

			ANodeRenderer startAnchorNode = spacing.getStartNode();

			int numSpacingAnchorNodeRows = 0;
			if (startAnchorNode != null)
				numSpacingAnchorNodeRows += startAnchorNode.getNumAssociatedRows();
			if (endAnchorNode != null)
				numSpacingAnchorNodeRows += endAnchorNode.getNumAssociatedRows();

			float currentDataRowHeight = spacing.getCurrentAnchorNodeSpacing()
					/ ((float) numSpacingAnchorNodeRows / 2.0f);

			if (currentDataRowHeight < dataRowHeight)
				dataRowHeight = currentDataRowHeight;

			pathwayHeight += spacing.getCurrentAnchorNodeSpacing();
		}

		int minViewHeightPixels = pixelGLConverter
				.getPixelHeightForGLHeight(pathwayHeight);
		if (minViewHeightPixels > parentGLCanvas.getHeight()) {
			SetMinViewSizeEvent event = new SetMinViewSizeEvent();
			event.setMinViewSize(parentGLCanvas.getBounds().width, minViewHeightPixels);
			event.setView(this);
			eventPublisher.triggerEvent(event);
		}

		float dataRowPositionX = branchColumnWidth + pathwayColumnWidth;

		for (ANodeRenderer nodeRenderer : nodeRenderers) {
			for (int i = 0; i < nodeRenderer.getNumAssociatedRows(); i++) {

				Vec3f nodePosition = nodeRenderer.getPosition();

				float rowPositionY = nodePosition.y()
						+ (nodeRenderer.getNumAssociatedRows() * dataRowHeight / 2.0f)
						- i * dataRowHeight;

				gl.glBegin(GL2.GL_LINE_LOOP);
				gl.glVertex3f(dataRowPositionX, rowPositionY - dataRowHeight, 0);
				gl.glVertex3f(viewFrustum.getWidth(), rowPositionY - dataRowHeight, 0);
				gl.glVertex3f(viewFrustum.getWidth(), rowPositionY, 0);
				gl.glVertex3f(dataRowPositionX, rowPositionY, 0);
				gl.glEnd();
			}
		}

		renderEdges(gl);

		checkForHits(gl);
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

		float minDataRowHeight = pixelGLConverter
				.getGLHeightForPixelHeight(MIN_DATA_ROW_HEIGHT_PIXELS);
		float minNodeDistance = pixelGLConverter
				.getGLHeightForPixelHeight(MIN_NODE_DISTANCE_PIXELS);
		float topSpacing = pixelGLConverter.getGLHeightForPixelHeight(TOP_SPACING_PIXELS);
		float bottomSpacing = pixelGLConverter
				.getGLHeightForPixelHeight(BOTTOM_SPACING_PIXELS);

		float dataRowHeight = (viewFrustum.getHeight() - bottomSpacing - topSpacing)
				/ (float) numDataRows;

		if (dataRowHeight < minDataRowHeight)
			dataRowHeight = minDataRowHeight;

		List<ANodeRenderer> unmappedNodeRenderers = new ArrayList<ANodeRenderer>();
		ANodeRenderer currentAnchorNode = null;

		// Calculate spacings according to a regular row distribution with the
		// current dataRowHeight
		for (int i = 0; i < nodeRenderers.size(); i++) {

			ANodeRenderer nodeRenderer = nodeRenderers.get(i);
			int numAssociatedRows = nodeRenderer.getNumAssociatedRows();

			if (numAssociatedRows == 0) {
				unmappedNodeRenderers.add(nodeRenderer);

			} else {
				AnchorNodeSpacing anchorNodeSpacing = createAnchorNodeSpacing(
						currentAnchorNode, nodeRenderer, unmappedNodeRenderers,
						minDataRowHeight, minNodeDistance, dataRowHeight,
						currentAnchorNode == null, false);

				anchorNodeSpacings.add(anchorNodeSpacing);

				unmappedNodeRenderers = new ArrayList<ANodeRenderer>();
				currentAnchorNode = nodeRenderer;

				if (anchorNodeSpacing.getMinAnchorNodeSpacing() > anchorNodeSpacing
						.getCurrentAnchorNodeSpacing())
					anchorNodeSpacingsWithTooFewSpace.add(anchorNodeSpacing);
				else
					anchorNodeSpacingsWithEnoughSpace.add(anchorNodeSpacing);
			}

			if (i == nodeRenderers.size() - 1) {
				AnchorNodeSpacing anchorNodeSpacing = createAnchorNodeSpacing(
						currentAnchorNode, null, unmappedNodeRenderers, minDataRowHeight,
						minNodeDistance, dataRowHeight, currentAnchorNode == null, true);
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

	private AnchorNodeSpacing createAnchorNodeSpacing(ANodeRenderer startAnchorNode,
			ANodeRenderer endAnchorNode, List<ANodeRenderer> nodesInbetween,
			float minDataRowHeight, float minNodeDistance, float dataRowHeight,
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

		anchorNodeSpacing.setMinAnchorNodeSpacing(Math.max(minDataRowHeight
				* ((float) numSpacingAnchorNodeRows) / 2.0f + additionalSpacing,
				minNodeDistance * (float) (nodesInbetween.size() + 1)));
		anchorNodeSpacing.setCurrentAnchorNodeSpacing(dataRowHeight
				* ((float) numSpacingAnchorNodeRows) / 2.0f + additionalSpacing);

		return anchorNodeSpacing;
	}

	private void renderEdges(GL2 gl) {
		for (int i = 0; i < nodeRenderers.size() - 1; i++) {
			ANodeRenderer nodeRenderer1 = nodeRenderers.get(i);
			ANodeRenderer nodeRenderer2 = nodeRenderers.get(i + 1);
			renderEdge(gl, nodeRenderer1, nodeRenderer2);
		}
	}

	private void renderEdge(GL2 gl, ANodeRenderer nodeRenderer1,
			ANodeRenderer nodeRenderer2) {

		PathwayVertexRep vertexRep1 = nodeRenderer1.getPathwayVertexRep();
		PathwayVertexRep vertexRep2 = nodeRenderer2.getPathwayVertexRep();

		DefaultEdge edge = pathway.getEdge(vertexRep1, vertexRep2);
		if (edge == null) {
			edge = pathway.getEdge(vertexRep2, vertexRep1);
			if (edge == null)
				return;
		}

		ConnectionLineRenderer connectionRenderer = new ConnectionLineRenderer();
		List<Vec3f> linePoints = new ArrayList<Vec3f>();

		boolean isNode1Target = pathway.getEdgeTarget(edge) == vertexRep1;

		Vec3f sourceConnectionPoint = (isNode1Target) ? nodeRenderer2
				.getTopConnectionPoint() : nodeRenderer1.getBottomConnectionPoint();
		Vec3f targetConnectionPoint = (isNode1Target) ? nodeRenderer1
				.getBottomConnectionPoint() : nodeRenderer2.getTopConnectionPoint();

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

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setPath(PathwayGraph pathway, List<PathwayVertexRep> path) {
		this.pathway = pathway;
		this.path = path;
		// TODO: initialize nodes etc.
	}

}
