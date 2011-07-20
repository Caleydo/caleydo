package org.caleydo.view.datagraph;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.DataDomainGraph;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.event.data.DimensionGroupsChangedEvent;
import org.caleydo.core.manager.event.data.NewDataDomainEvent;
import org.caleydo.core.manager.event.view.DataDomainsChangedEvent;
import org.caleydo.core.manager.event.view.NewViewEvent;
import org.caleydo.core.manager.event.view.ViewClosedEvent;
import org.caleydo.core.manager.picking.PickingMode;
import org.caleydo.core.manager.picking.PickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.datagraph.bandlayout.AConnectionBandCreator;
import org.caleydo.view.datagraph.bandlayout.BandInfo;
import org.caleydo.view.datagraph.bandlayout.ConnectionBandCreatorFactory;
import org.caleydo.view.datagraph.listener.DataDomainsChangedEventListener;
import org.caleydo.view.datagraph.listener.DimensionGroupsChangedEventListener;
import org.caleydo.view.datagraph.listener.GLDataGraphKeyListener;
import org.caleydo.view.datagraph.listener.NewDataDomainEventListener;
import org.caleydo.view.datagraph.listener.NewViewEventListener;
import org.caleydo.view.datagraph.listener.ViewClosedEventListener;
import org.eclipse.swt.widgets.Composite;

/**
 * This class is responsible for rendering the radial hierarchy and receiving
 * user events and events from other views.
 * 
 * @author Christian Partl
 */
public class GLDataGraph extends AGLView implements IViewCommandHandler {

	public final static String VIEW_TYPE = "org.caleydo.view.datagraph";

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
	private Map<IDataDomain, DataNode> dataNodesOfDataDomains;
	private ConnectionBandRenderer connectionBandRenderer;

	private NewViewEventListener newViewEventListener;
	private NewDataDomainEventListener newDataDomainEventListener;
	private ViewClosedEventListener viewClosedEventListener;
	private DataDomainsChangedEventListener dataDomainsChangedEventListener;
	private DimensionGroupsChangedEventListener dimensionGroupsChangedEventListener;

	/**
	 * Constructor.
	 */
	public GLDataGraph(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);

		connectionBandRenderer = new ConnectionBandRenderer();
		viewType = GLDataGraph.VIEW_TYPE;
		glKeyListener = new GLDataGraphKeyListener();
		dataGraph = new Graph<IDataGraphNode>();
		graphLayout = new ForceDirectedGraphLayout();
		relativeNodePositions = new HashMap<IDataGraphNode, Pair<Float, Float>>();
		dragAndDropController = new DragAndDropController(this);
		dataNodes = new HashSet<DataNode>();
		viewNodes = new HashSet<ViewNode>();
		viewNodesOfDataDomains = new HashMap<IDataDomain, Set<ViewNode>>();
		dataNodesOfDataDomains = new HashMap<IDataDomain, DataNode>();

		DataDomainGraph dataDomainGraph = DataDomainManager.get()
				.getDataDomainGraph();

		for (IDataDomain dataDomain : dataDomainGraph.getDataDomains()) {
//			DataNode dataNode = new DataNode(graphLayout, this,
//					dragAndDropController, lastNodeID++, dataDomain);
//			boolean nodeAdded = false;
//			for (DataNode node : dataNodes) {
//				if (node.getDataDomain() == dataDomain) {
//					dataNode = node;
//					nodeAdded = true;
//					break;
//				}
//			}
//			if (!nodeAdded) {
//				dataGraph.addNode(dataNode);
//				dataNodes.add(dataNode);
//				dataNodesOfDataDomains.put(dataNode.getDataDomain(), dataNode);
//			}
//
//			Set<IDataDomain> neighbors = dataDomainGraph
//					.getNeighboursOf(dataDomain);
//
//			for (IDataDomain neighborDataDomain : neighbors) {
//				nodeAdded = false;
//				for (DataNode node : dataNodes) {
//					if (node.getDataDomain() == neighborDataDomain) {
//						dataGraph.addEdge(dataNode, node);
//						nodeAdded = true;
//						break;
//					}
//				}
//				if (!nodeAdded) {
//					DataNode node = new DataNode(graphLayout, this,
//							dragAndDropController, lastNodeID++,
//							neighborDataDomain);
//					dataGraph.addNode(node);
//					dataNodes.add(node);
//					dataNodesOfDataDomains.put(node.getDataDomain(), node);
//					dataGraph.addEdge(dataNode, node);
//				}
//			}
			addDataDomain(dataDomain);
		}

		// Set<String> allowedViewTypes = new HashSet<String>();
		// // TODO: Maybe add to AView isMetaView() instead?
		// allowedViewTypes.add("org.caleydo.view.parcoords");
		// allowedViewTypes.add("org.caleydo.view.heatmap");
		// allowedViewTypes.add("org.caleydo.view.heatmap.hierarchical");
		// allowedViewTypes.add("org.caleydo.view.visbricks");
		// allowedViewTypes.add("org.caleydo.view.scatterplot");
		// allowedViewTypes.add("org.caleydo.view.tabular");
		// allowedViewTypes.add("org.caleydo.view.bucket");

		Collection<AGLView> views = GeneralManager.get()
				.getViewGLCanvasManager().getAllGLViews();

		for (AGLView view : views) {
			addView(view);
		}

		// for (DataNode dataNode : dataNodes) {
		// Set<ViewNode> viewNodes = viewNodesOfDataDomains.get(dataNode
		// .getDataDomain());
		// if (viewNodes != null) {
		// for (ViewNode viewNode : viewNodes) {
		// dataGraph.addEdge(dataNode, viewNode);
		// }
		// }
		// }

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
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite.addKeyListener(glKeyListener);
			}
		});

		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite.addKeyListener(glKeyListener);
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
			graphLayout.clearNodePositions();
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

		for (Pair<IDataGraphNode, IDataGraphNode> edge : dataGraph
				.getAllEdges()) {

			// Works because there are no edges between view nodes
			if ((edge.getFirst() instanceof ViewNode)
					|| (edge.getSecond() instanceof ViewNode)) {
				renderConnectionBands(gl, edge.getFirst(), edge.getSecond());
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

	private void renderConnectionBands(GL2 gl, IDataGraphNode node1,
			IDataGraphNode node2) {

		AConnectionBandCreator bandCreator = ConnectionBandCreatorFactory
				.getConnectionBandCreator(node1, node2, pixelGLConverter);

		for (List<Pair<Point2D, Point2D>> anchorPoints : bandCreator
				.calcConnectionBands()) {
			connectionBandRenderer.init(gl);
			connectionBandRenderer.renderComplexBand(gl, anchorPoints, false,
					new float[] { 0, 0, 0 }, 0.2f);
		}

		// Set<ADimensionGroupData> dimensionGroups1 =
		// node1.getDimensionGroups();
		// Set<ADimensionGroupData> dimensionGroups2 =
		// node2.getDimensionGroups();
		// List<Pair<Point2D, Point2D>> anchorPoints1Group = new
		// ArrayList<Pair<Point2D, Point2D>>();
		// List<Pair<Point2D, Point2D>> anchorPoints2Group = new
		// ArrayList<Pair<Point2D, Point2D>>();
		//
		// if (dimensionGroups1 != null && !dimensionGroups1.isEmpty()
		// && dimensionGroups2 != null && !dimensionGroups2.isEmpty()) {
		// for (ADimensionGroupData dimGroupData1 : dimensionGroups1) {
		// for (ADimensionGroupData dimGroupData2 : dimensionGroups2) {
		// if (dimGroupData1.getID() == dimGroupData2.getID()) {
		// renderBand(
		// gl,
		// node1.getBottomDimensionGroupAnchorPoints(dimGroupData1),
		// node2.getBottomDimensionGroupAnchorPoints(dimGroupData2),
		// -0.2f, -0.2f, false, false);
		// }
		// }
		// }
		//
		// } else {
		//
		// Point2D position1 = node1.getPosition();
		// Point2D position2 = node2.getPosition();
		//
		// float deltaX = (float) (position1.getX() - position2.getX());
		// float deltaY = (float) (position1.getY() - position2.getY());
		//
		// Pair<Point2D, Point2D> anchorPoints1;
		// Pair<Point2D, Point2D> anchorPoints2;
		//
		// float offset1 = 0;
		// boolean isOffsetHorizontal = false;
		//
		// if (deltaX < 0) {
		// if (deltaY < 0) {
		// float spacingX = (float) ((position2.getX() - node2
		// .getWidth() / 2.0f) - (position1.getX() + node1
		// .getWidth() / 2.0f));
		// float spacingY = (float) ((position2.getY() - node2
		// .getHeight() / 2.0f) - (position1.getY() + node1
		// .getHeight() / 2.0f));
		// if (spacingX > spacingY) {
		// anchorPoints1 = node1.getRightAnchorPoints();
		// anchorPoints2 = node2.getLeftAnchorPoints();
		// offset1 = 0.3f * spacingX;
		// isOffsetHorizontal = true;
		// } else {
		// anchorPoints1 = node1.getTopAnchorPoints();
		// anchorPoints2 = node2.getBottomAnchorPoints();
		// offset1 = 0.3f * spacingY;
		// isOffsetHorizontal = false;
		// }
		// } else {
		// float spacingX = (float) ((position2.getX() - node2
		// .getWidth() / 2.0f) - (position1.getX() + node1
		// .getWidth() / 2.0f));
		// float spacingY = (float) ((position1.getY() - node1
		// .getHeight() / 2.0f) - (position2.getY() + node2
		// .getHeight() / 2.0f));
		// if (spacingX > spacingY) {
		// anchorPoints1 = node1.getRightAnchorPoints();
		// anchorPoints2 = node2.getLeftAnchorPoints();
		// offset1 = 0.3f * (spacingX);
		// isOffsetHorizontal = true;
		// } else {
		// anchorPoints1 = node1.getBottomAnchorPoints();
		// anchorPoints2 = node2.getTopAnchorPoints();
		// offset1 = -0.3f * spacingY;
		// isOffsetHorizontal = false;
		// }
		// }
		// } else {
		// if (deltaY < 0) {
		// float spacingX = (float) ((position1.getX() - node1
		// .getWidth() / 2.0f) - (position2.getX() + node2
		// .getWidth() / 2.0f));
		// float spacingY = (float) ((position2.getY() - node2
		// .getHeight() / 2.0f) - (position1.getY() + node1
		// .getHeight() / 2.0f));
		// if (spacingX > spacingY) {
		// anchorPoints1 = node1.getLeftAnchorPoints();
		// anchorPoints2 = node2.getRightAnchorPoints();
		// offset1 = -0.3f * (spacingX);
		// isOffsetHorizontal = true;
		// } else {
		// anchorPoints1 = node1.getTopAnchorPoints();
		// anchorPoints2 = node2.getBottomAnchorPoints();
		// offset1 = 0.3f * spacingY;
		// isOffsetHorizontal = false;
		// }
		// } else {
		// float spacingX = (float) ((position1.getX() - node1
		// .getWidth() / 2.0f) - (position2.getX() + node2
		// .getWidth() / 2.0f));
		// float spacingY = (float) ((position1.getY() - node1
		// .getHeight() / 2.0f) - (position2.getY() + node2
		// .getHeight() / 2.0f));
		// if (spacingX > spacingY) {
		// anchorPoints1 = node1.getLeftAnchorPoints();
		// anchorPoints2 = node2.getRightAnchorPoints();
		// offset1 = -0.3f * (spacingX);
		// isOffsetHorizontal = true;
		// } else {
		// anchorPoints1 = node1.getBottomAnchorPoints();
		// anchorPoints2 = node2.getTopAnchorPoints();
		// offset1 = -0.3f * spacingY;
		// isOffsetHorizontal = false;
		// }
		// }
		// }

	}

	private void renderBand(GL2 gl, BandInfo bandInfo) {
		Pair<Point2D, Point2D> anchorPoints1 = bandInfo.getAnchorPoints1();
		Pair<Point2D, Point2D> anchorPoints2 = bandInfo.getAnchorPoints2();

		connectionBandRenderer.init(gl);
		float[] side1AnchorPos1 = new float[] {
				(float) anchorPoints1.getFirst().getX(),
				(float) anchorPoints1.getFirst().getY() };
		float[] side1AnchorPos2 = new float[] {
				(float) anchorPoints1.getSecond().getX(),
				(float) anchorPoints1.getSecond().getY() };

		float[] side2AnchorPos1 = new float[] {
				(float) anchorPoints2.getFirst().getX(),
				(float) anchorPoints2.getFirst().getY() };
		float[] side2AnchorPos2 = new float[] {
				(float) anchorPoints2.getSecond().getX(),
				(float) anchorPoints2.getSecond().getY() };

		float[] offset1AnchorPos1 = new float[] {
				side1AnchorPos1[0]
						+ (bandInfo.isOffset1Horizontal() ? (bandInfo
								.getOffset1()) : 0),
				side1AnchorPos1[1]
						+ (bandInfo.isOffset1Horizontal() ? 0 : bandInfo
								.getOffset1()) };
		float[] offset1AnchorPos2 = new float[] {
				side1AnchorPos2[0]
						+ (bandInfo.isOffset1Horizontal() ? (bandInfo
								.getOffset1()) : 0),
				side1AnchorPos2[1]
						+ (bandInfo.isOffset1Horizontal() ? 0 : bandInfo
								.getOffset1()) };

		float[] offset2AnchorPos1 = new float[] {
				side2AnchorPos1[0]
						+ (bandInfo.isOffset2Horizontal() ? (bandInfo
								.getOffset1()) : 0),
				side2AnchorPos1[1]
						+ (bandInfo.isOffset2Horizontal() ? 0 : bandInfo
								.getOffset1()) };
		float[] offset2AnchorPos2 = new float[] {
				side2AnchorPos2[0]
						+ (bandInfo.isOffset2Horizontal() ? (bandInfo
								.getOffset1()) : 0),
				side2AnchorPos2[1]
						+ (bandInfo.isOffset2Horizontal() ? 0 : bandInfo
								.getOffset1()) };

		Pair<float[], float[]> pair1 = new Pair<float[], float[]>(
				side1AnchorPos1, side1AnchorPos2);
		Pair<float[], float[]> pair2 = new Pair<float[], float[]>(
				offset1AnchorPos1, offset1AnchorPos2);
		Pair<float[], float[]> pair3 = new Pair<float[], float[]>(
				offset2AnchorPos1, offset2AnchorPos2);
		Pair<float[], float[]> pair4 = new Pair<float[], float[]>(
				side2AnchorPos1, side2AnchorPos2);

		List<Pair<float[], float[]>> anchorPoints = new ArrayList<Pair<float[], float[]>>();
		anchorPoints.add(pair1);
		anchorPoints.add(pair2);
		anchorPoints.add(pair3);
		anchorPoints.add(pair4);

		// connectionBandRenderer.renderStraightBand(gl, leftTopPos,
		// leftBottomPos, rightTopPos, rightBottomPos, false, 0, 0, false,
		// new float[] { 0, 0, 0, 1 }, 1f);

		// connectionBandRenderer.renderSingleBand(gl, side1AnchorPos1,
		// side1AnchorPos2, side2AnchorPos1, side2AnchorPos2, false,
		// bandInfo.getOffset1(), bandInfo.getOffset2(), new float[] { 0,
		// 0, 0 }, 0.2f, bandInfo.isOffset1Horizontal(),
		// bandInfo.isOffset2Horizontal());
	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	@Override
	protected void handlePickingEvents(PickingType pickingType,
			PickingMode pickingMode, int externalID, Pick pick) {
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

		newViewEventListener = new NewViewEventListener();
		newViewEventListener.setHandler(this);
		eventPublisher.addListener(NewViewEvent.class, newViewEventListener);

		viewClosedEventListener = new ViewClosedEventListener();
		viewClosedEventListener.setHandler(this);
		eventPublisher.addListener(ViewClosedEvent.class,
				viewClosedEventListener);

		dataDomainsChangedEventListener = new DataDomainsChangedEventListener();
		dataDomainsChangedEventListener.setHandler(this);
		eventPublisher.addListener(DataDomainsChangedEvent.class,
				dataDomainsChangedEventListener);

		dimensionGroupsChangedEventListener = new DimensionGroupsChangedEventListener();
		dimensionGroupsChangedEventListener.setHandler(this);
		eventPublisher.addListener(DimensionGroupsChangedEvent.class,
				dimensionGroupsChangedEventListener);

		newDataDomainEventListener = new NewDataDomainEventListener();
		newDataDomainEventListener.setHandler(this);
		eventPublisher.addListener(NewDataDomainEvent.class,
				newDataDomainEventListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (newViewEventListener != null) {
			eventPublisher.removeListener(newViewEventListener);
			newViewEventListener = null;
		}

		if (viewClosedEventListener != null) {
			eventPublisher.removeListener(viewClosedEventListener);
			viewClosedEventListener = null;
		}

		if (dataDomainsChangedEventListener != null) {
			eventPublisher.removeListener(dataDomainsChangedEventListener);
			dataDomainsChangedEventListener = null;
		}

		if (dimensionGroupsChangedEventListener != null) {
			eventPublisher.removeListener(dimensionGroupsChangedEventListener);
			dimensionGroupsChangedEventListener = null;
		}

		if (newDataDomainEventListener != null) {
			eventPublisher.removeListener(newDataDomainEventListener);
			newDataDomainEventListener = null;
		}
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

	public void addView(AGLView view) {
		if (!view.isRenderedRemote() && view.isDataView()) {
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
					DataNode dataNode = dataNodesOfDataDomains.get(dataDomain);
					if (dataNode != null) {
						dataGraph.addEdge(dataNode, node);
					}
				}
			}
			applyAutomaticLayout = true;
			setDisplayListDirty();
		}
	}

	public void removeView(AGLView view) {

		ViewNode viewNode = null;
		for (ViewNode node : viewNodes) {
			if (node.getRepresentedView() == view) {
				viewNode = node;
				break;
			}
		}

		if (viewNode == null)
			return;

		Set<IDataDomain> dataDomains = viewNode.getDataDomains();

		if (dataDomains != null) {
			for (IDataDomain dataDomain : dataDomains) {
				Set<ViewNode> viewNodes = viewNodesOfDataDomains
						.get(dataDomain);
				if (viewNodes != null) {
					viewNodes.remove(viewNode);
				}
			}
		}

		dataGraph.removeNode(viewNode);
		viewNodes.remove(viewNode);
		viewNode.destroy();
		applyAutomaticLayout = true;
		setDisplayListDirty();
	}

	public void updateView(AGLView view) {
		removeView(view);
		addView(view);
	}

	public void updateDataDomain(IDataDomain dataDomain) {
		if (dataNodesOfDataDomains.get(dataDomain) != null) {
			setDisplayListDirty();
		}
	}

	public void addDataDomain(IDataDomain dataDomain) {
		DataNode dataNode = null;
		boolean nodeAdded = false;
		for (DataNode node : dataNodes) {
			if (node.getDataDomain() == dataDomain) {
				dataNode = node;
				nodeAdded = true;
				break;
			}
		}
		if (!nodeAdded) {
			dataNode = new DataNode(graphLayout, this, dragAndDropController,
					lastNodeID++, dataDomain);
			dataGraph.addNode(dataNode);
			dataNodes.add(dataNode);
			dataNodesOfDataDomains.put(dataNode.getDataDomain(), dataNode);
		}

		DataDomainGraph dataDomainGraph = DataDomainManager.get()
				.getDataDomainGraph();

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
						dragAndDropController, lastNodeID++, neighborDataDomain);
				dataGraph.addNode(node);
				dataNodes.add(node);
				dataNodesOfDataDomains.put(node.getDataDomain(), node);
				dataGraph.addEdge(dataNode, node);
			}
		}
		
		applyAutomaticLayout = true;
		setDisplayListDirty();
	}

}
