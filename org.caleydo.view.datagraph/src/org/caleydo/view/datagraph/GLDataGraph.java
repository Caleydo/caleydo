package org.caleydo.view.datagraph;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.DataDomainGraph;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.datagraph.listener.GLDataGraphKeyListener;

/**
 * This class is responsible for rendering the radial hierarchy and receiving
 * user events and events from other views.
 * 
 * @author Christian Partl
 */
public class GLDataGraph extends AGLView implements IViewCommandHandler {

	public final static String VIEW_ID = "org.caleydo.view.datagraph";

	public final static int BOUNDS_SPACING_PIXELS = 30;

	private GLDataGraphKeyListener glKeyListener;
	private boolean useDetailLevel = false;

	private Graph<IDataGraphNode> dataGraph;
	private ForceDirectedGraphLayout graphLayout;
	private int maxNodeWidthPixels;
	private int maxNodeHeightPixels;
	private DragAndDropController dragAndDropController;
	private boolean applyAutomaticLayout;
	private Map<IDataGraphNode, Pair<Float, Float>> relativeNodePositions;
	private int lastNodeID = 0;
	private Set<DataNode> dataNodes;
	private Set<ViewNode> viewNodes;
	private Map<IDataDomain, Set<ViewNode>> viewNodesOfDataDomains;
	ConnectionBandRenderer connectionBandRenderer;

	/**
	 * Constructor.
	 */
	public GLDataGraph(GLCaleydoCanvas glCanvas, final ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);

		connectionBandRenderer = new ConnectionBandRenderer();
		viewType = GLDataGraph.VIEW_ID;
		glKeyListener = new GLDataGraphKeyListener();
		dataGraph = new Graph<IDataGraphNode>();
		graphLayout = new ForceDirectedGraphLayout();
		relativeNodePositions = new HashMap<IDataGraphNode, Pair<Float, Float>>();
		dragAndDropController = new DragAndDropController(this);
		dataNodes = new HashSet<DataNode>();
		viewNodes = new HashSet<ViewNode>();
		viewNodesOfDataDomains = new HashMap<IDataDomain, Set<ViewNode>>();

		DataDomainGraph dataDomainGraph = DataDomainManager.get()
				.getDataDomainGraph();

		for (IDataDomain dataDomain : dataDomainGraph.getDataDomains()) {
			DataNode dataNode = new DataNode(graphLayout, this,
					dragAndDropController, lastNodeID++, dataDomain);
			boolean nodeAdded = false;
			for (DataNode node : dataNodes) {
				if (node.getDataDomain() == dataDomain) {
					dataNode = node;
					nodeAdded = true;
					break;
				}
			}
			if (!nodeAdded) {
				dataGraph.addNode(dataNode);
				dataNodes.add(dataNode);
			}

			Set<IDataDomain> neighbors = dataDomainGraph
					.getNeighboursOf(dataDomain);

			for (IDataDomain neighborDataDomain : neighbors) {
				nodeAdded = false;
				for (DataNode node : dataNodes) {
					if (node.getDataDomain() == neighborDataDomain) {
						dataGraph.addEdge(dataNode, node);
						nodeAdded = true;
						break;
					}
				}
				if (!nodeAdded) {
					DataNode node = new DataNode(graphLayout, this,
							dragAndDropController, lastNodeID++,
							neighborDataDomain);
					dataGraph.addNode(node);
					dataNodes.add(node);
					dataGraph.addEdge(dataNode, node);
				}
			}
		}

		Set<String> allowedViewTypes = new HashSet<String>();
		// TODO: Maybe add to AView isMetaView() instead?
		allowedViewTypes.add("org.caleydo.view.parcoords");
		allowedViewTypes.add("org.caleydo.view.heatmap");
		allowedViewTypes.add("org.caleydo.view.heatmap.hierarchical");
		allowedViewTypes.add("org.caleydo.view.visbricks");
		allowedViewTypes.add("org.caleydo.view.scatterplot");
		allowedViewTypes.add("org.caleydo.view.tabular");
		allowedViewTypes.add("org.caleydo.view.bucket");

		Collection<AGLView> views = GeneralManager.get()
				.getViewGLCanvasManager().getAllGLViews();

		for (AGLView view : views) {
			if (!view.isRenderedRemote()
					&& allowedViewTypes.contains(view.getViewType())) {
				ViewNode node = new ViewNode(graphLayout, this,
						dragAndDropController, lastNodeID++, view);
				dataGraph.addNode(node);
				viewNodes.add(node);
				Set<IDataDomain> dataDomains = view.getDataDomains();
				if (dataDomains != null && !dataDomains.isEmpty()) {
					node.setDataDomains(dataDomains);
					for (IDataDomain dataDomain : dataDomains) {
						Set<ViewNode> viewNodes = viewNodesOfDataDomains
								.get(dataDomain);
						if (viewNodes == null) {
							viewNodes = new HashSet<ViewNode>();
						}
						viewNodes.add(node);
						viewNodesOfDataDomains.put(dataDomain, viewNodes);
					}
				}
			}
		}

		for (DataNode dataNode : dataNodes) {
			Set<ViewNode> viewNodes = viewNodesOfDataDomains.get(dataNode
					.getDataDomain());
			if (viewNodes != null) {
				for (ViewNode viewNode : viewNodes) {
					dataGraph.addEdge(dataNode, viewNode);
				}
			}
		}

		// DataNode o2 = new DataNode(graphLayout, this, dragAndDropController,
		// lastNodeID++);
		// DataNode o3 = new DataNode(graphLayout, this, dragAndDropController,
		// lastNodeID++);
		// DataNode o4 = new DataNode(graphLayout, this, dragAndDropController,
		// lastNodeID++);
		// DataNode o5 = new DataNode(graphLayout, this, dragAndDropController,
		// lastNodeID++);
		//
		// dataGraph.addNode(o1);
		// dataGraph.addNode(o2);
		// dataGraph.addNode(o3);
		// dataGraph.addNode(o4);
		// dataGraph.addNode(o5);
		//
		// dataGraph.addEdge(o1, o2);
		// dataGraph.addEdge(o1, o3);
		// dataGraph.addEdge(o3, o4);
		// dataGraph.addEdge(o5, o4);
		// dataGraph.addEdge(o3, o2);

		maxNodeWidthPixels = Integer.MIN_VALUE;
		maxNodeHeightPixels = Integer.MIN_VALUE;

		for (IDataGraphNode node : dataGraph.getNodes()) {
			if (node.getHeightPixels() > maxNodeHeightPixels)
				maxNodeHeightPixels = node.getHeightPixels();

			if (node.getWidthPixels() > maxNodeWidthPixels)
				maxNodeWidthPixels = node.getWidthPixels();
		}

		applyAutomaticLayout = true;
	}

	@Override
	public void init(GL2 gl) {
		textRenderer = new CaleydoTextRenderer(24);
	}

	@Override
	public void initLocal(GL2 gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		// Register keyboard listener to GL2 canvas
		parentGLCanvas.getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
					@Override
					public void run() {
						parentGLCanvas.getParentComposite().addKeyListener(
								glKeyListener);
					}
				});

		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
					@Override
					public void run() {
						glParentView.getParentGLCanvas().getParentComposite()
								.addKeyListener(glKeyListener);
					}
				});

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);

	}

	@Override
	public void setDetailLevel(DetailLevel detailLevel) {
		if (useDetailLevel) {
			super.setDetailLevel(detailLevel);
			// renderStyle.setDetailLevel(detailLevel);
		}

	}

	@Override
	public void displayLocal(GL2 gl) {

		if (!lazyMode)
			pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal) {
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);

		if (!lazyMode)
			checkForHits(gl);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public void displayRemote(GL2 gl) {

		if (bIsDisplayListDirtyRemote) {
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);

	}

	@Override
	public void display(GL2 gl) {
		gl.glCallList(iGLDisplayListToCall);

		dragAndDropController.handleDragging(gl, glMouseListener);

		if (!isRenderedRemote())
			contextMenu.render(gl, this);

	}

	/**
	 * Builds the display list for a given display list index.
	 * 
	 * @param gl
	 *            Instance of GL2.
	 * @param iGLDisplayListIndex
	 *            Index of the display list.
	 */
	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);

		PixelGLConverter pixelGLConverter = parentGLCanvas
				.getPixelGLConverter();

		int drawingAreaWidth = pixelGLConverter
				.getPixelWidthForGLWidth(viewFrustum.getWidth())
				- 2
				* BOUNDS_SPACING_PIXELS - maxNodeWidthPixels;
		int drawingAreaHeight = pixelGLConverter
				.getPixelHeightForGLHeight(viewFrustum.getHeight())
				- 2
				* BOUNDS_SPACING_PIXELS - maxNodeHeightPixels;
		if (applyAutomaticLayout) {
			graphLayout.setGraph(dataGraph);
			Rectangle2D rect = new Rectangle();

			rect.setFrame(BOUNDS_SPACING_PIXELS + (maxNodeWidthPixels / 2.0f),
					BOUNDS_SPACING_PIXELS + (maxNodeHeightPixels / 2.0f),
					drawingAreaWidth, drawingAreaHeight);
			graphLayout.layout(rect);
		} else {
			if (!dragAndDropController.isDragging()) {
				for (IDataGraphNode node : dataGraph.getNodes()) {
					Pair<Float, Float> relativePosition = relativeNodePositions
							.get(node);
					graphLayout.setNodePosition(node, new Point2D.Double(
							relativePosition.getFirst() * drawingAreaWidth,
							relativePosition.getSecond() * drawingAreaHeight));
				}
			}
		}

		for (IDataGraphNode node : dataGraph.getNodes()) {
			Point2D position = graphLayout.getNodePosition(node, true);
			float relativePosX = (float) position.getX() / drawingAreaWidth;
			float relativePosY = (float) position.getY() / drawingAreaHeight;
			relativeNodePositions.put(node, new Pair<Float, Float>(
					relativePosX, relativePosY));

			node.render(gl);

		}
		renderEdges(gl);
		gl.glEndList();

	}

	private void renderEdges(GL2 gl) {

		PixelGLConverter pixelGLConverter = parentGLCanvas
				.getPixelGLConverter();

		for (Pair<IDataGraphNode, IDataGraphNode> edge : dataGraph
				.getAllEdges()) {

			// Works because there are no edges between view nodes
			if ((edge.getFirst() instanceof ViewNode)
					|| (edge.getSecond() instanceof ViewNode)) {
				renderBand(gl, edge.getFirst(), edge.getSecond());
			} else {

				gl.glPushAttrib(GL2.GL_LINE_BIT);
				gl.glLineWidth(2);
				gl.glBegin(GL2.GL_LINES);
				Point2D position1 = graphLayout
						.getNodePosition(edge.getFirst());
				Point2D position2 = graphLayout.getNodePosition(edge
						.getSecond());

				float x1 = pixelGLConverter
						.getGLWidthForPixelWidth((int) position1.getX());
				float x2 = pixelGLConverter
						.getGLWidthForPixelWidth((int) position2.getX());
				float y1 = pixelGLConverter
						.getGLHeightForPixelHeight((int) position1.getY());
				float y2 = pixelGLConverter
						.getGLHeightForPixelHeight((int) position2.getY());

				gl.glVertex3f(x1, y1, -0.5f);
				gl.glVertex3f(x2, y2, -0.5f);
				gl.glEnd();
				gl.glPopAttrib();
			}
		}

	}

	private void renderBand(GL2 gl, IDataGraphNode node1, IDataGraphNode node2) {
		Point2D position1 = node1.getPosition();
		Point2D position2 = node2.getPosition();

		float deltaX = (float) (position1.getX() - position2.getX());
		float deltaY = (float) (position1.getY() - position2.getY());

		Pair<Point2D, Point2D> anchorPoints1;
		Pair<Point2D, Point2D> anchorPoints2;

		if (deltaX < 0) {
			if (deltaY < 0) {
				float spacingX = (float) ((position2.getX() - node2.getWidth() / 2.0f) - (position1
						.getX() + node1.getWidth() / 2.0f));
				float spacingY = (float) ((position2.getY() - node2.getHeight() / 2.0f) - (position1
						.getY() + node1.getHeight() / 2.0f));
				if (spacingX > spacingY) {
					anchorPoints1 = node1.getRightAnchorPoints();
					anchorPoints2 = node2.getLeftAnchorPoints();
				} else {
					anchorPoints1 = node1.getTopAnchorPoints();
					anchorPoints2 = node2.getBottomAnchorPoints();
				}
			} else {
				float spacingX = (float) ((position2.getX() - node2.getWidth() / 2.0f) - (position1
						.getX() + node1.getWidth() / 2.0f));
				float spacingY = (float) ((position1.getY() - node1.getHeight() / 2.0f) - (position2
						.getY() + node2.getHeight() / 2.0f));
				if (spacingX > spacingY) {
					anchorPoints1 = node1.getRightAnchorPoints();
					anchorPoints2 = node2.getLeftAnchorPoints();
				} else {
					anchorPoints1 = node1.getBottomAnchorPoints();
					anchorPoints2 = node2.getTopAnchorPoints();
				}
			}
		} else {
			if (deltaY < 0) {
				float spacingX = (float) ((position1.getX() - node1.getWidth() / 2.0f) - (position2
						.getX() + node2.getWidth() / 2.0f));
				float spacingY = (float) ((position2.getY() - node2.getHeight() / 2.0f) - (position1
						.getY() + node1.getHeight() / 2.0f));
				if (spacingX > spacingY) {
					anchorPoints1 = node1.getLeftAnchorPoints();
					anchorPoints2 = node2.getRightAnchorPoints();
				} else {
					anchorPoints1 = node1.getTopAnchorPoints();
					anchorPoints2 = node2.getBottomAnchorPoints();
				}
			} else {
				float spacingX = (float) ((position1.getX() - node1.getWidth() / 2.0f) - (position2
						.getX() + node2.getWidth() / 2.0f));
				float spacingY = (float) ((position1.getY() - node1.getHeight() / 2.0f) - (position2
						.getY() + node2.getHeight() / 2.0f));
				if (spacingX > spacingY) {
					anchorPoints1 = node1.getLeftAnchorPoints();
					anchorPoints2 = node2.getRightAnchorPoints();
				} else {
					anchorPoints1 = node1.getBottomAnchorPoints();
					anchorPoints2 = node2.getTopAnchorPoints();
				}
			}
		}

		connectionBandRenderer.init(gl);
		float[] leftTopPos = new float[] {
				(float) anchorPoints1.getFirst().getX(),
				(float) anchorPoints1.getFirst().getY() };
		float[] leftBottomPos = new float[] {
				(float) anchorPoints1.getSecond().getX(),
				(float) anchorPoints1.getSecond().getY() };

		float[] rightTopPos = new float[] {
				(float) anchorPoints2.getFirst().getX(),
				(float) anchorPoints2.getFirst().getY() };
		float[] rightBottomPos = new float[] {
				(float) anchorPoints2.getSecond().getX(),
				(float) anchorPoints2.getSecond().getY() };

		connectionBandRenderer.renderStraightBand(gl, leftTopPos,
				leftBottomPos, rightTopPos, rightBottomPos, false, 0, 0, false,
				new float[] { 0, 0, 0, 1 }, 1f);

	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int externalID, Pick pick) {
		if (detailLevel == DetailLevel.VERY_LOW) {
			return;
		}

	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(SelectionType selectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getShortInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearAllSelections() {

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDataGraphView serializedForm = new SerializedDataGraphView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {

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
	public void handleClearSelections() {
	}

	@Override
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	@Override
	public void handleUpdateView() {

		setDisplayListDirty();
	}

	public void setApplyAutomaticLayout(boolean applyAutomaticLayout) {
		this.applyAutomaticLayout = applyAutomaticLayout;
	}

}
