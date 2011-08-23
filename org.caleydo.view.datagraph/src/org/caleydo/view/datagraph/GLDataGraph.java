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

import org.caleydo.core.data.datadomain.DataDomainGraph;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.ADimensionGroupData;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.data.DimensionGroupsChangedEvent;
import org.caleydo.core.manager.event.data.NewDataDomainEvent;
import org.caleydo.core.manager.event.view.DataDomainsChangedEvent;
import org.caleydo.core.manager.event.view.NewViewEvent;
import org.caleydo.core.manager.event.view.ViewClosedEvent;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.datagraph.bandlayout.AEdgeBandRenderer;
import org.caleydo.view.datagraph.bandlayout.BandInfo;
import org.caleydo.view.datagraph.bandlayout.ConnectionBandCreatorFactory;
import org.caleydo.view.datagraph.bandlayout.SimpleEdgeRoutingStrategy;
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
			// DataNode dataNode = new DataNode(graphLayout, this,
			// dragAndDropController, lastNodeID++, dataDomain);
			// boolean nodeAdded = false;
			// for (DataNode node : dataNodes) {
			// if (node.getDataDomain() == dataDomain) {
			// dataNode = node;
			// nodeAdded = true;
			// break;
			// }
			// }
			// if (!nodeAdded) {
			// dataGraph.addNode(dataNode);
			// dataNodes.add(dataNode);
			// dataNodesOfDataDomains.put(dataNode.getDataDomain(), dataNode);
			// }
			//
			// Set<IDataDomain> neighbors = dataDomainGraph
			// .getNeighboursOf(dataDomain);
			//
			// for (IDataDomain neighborDataDomain : neighbors) {
			// nodeAdded = false;
			// for (DataNode node : dataNodes) {
			// if (node.getDataDomain() == neighborDataDomain) {
			// dataGraph.addEdge(dataNode, node);
			// nodeAdded = true;
			// break;
			// }
			// }
			// if (!nodeAdded) {
			// DataNode node = new DataNode(graphLayout, this,
			// dragAndDropController, lastNodeID++,
			// neighborDataDomain);
			// dataGraph.addNode(node);
			// dataNodes.add(node);
			// dataNodesOfDataDomains.put(node.getDataDomain(), node);
			// dataGraph.addEdge(dataNode, node);
			// }
			// }
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

		Collection<AGLView> views = GeneralManager.get().getViewManager()
				.getAllGLViews();

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

		// renderTestCurve(gl);

		gl.glEndList();

	}

//	private void renderTestCurve(GL2 gl) {
//
//		Point2D v0 = new Point2D.Double(0.5, 0.5);
//		Point2D v1 = new Point2D.Double(1, 0.5);
//		Point2D v2 = new Point2D.Double(2, 3);
//		Point2D v3 = new Point2D.Double(2.5, 1);
//
//		computeOneSpline(v0, v0, v1, v2, gl);
//		computeOneSpline(v0, v1, v2, v3, gl);
//		computeOneSpline(v1, v2, v3, v3, gl);
//
//		gl.glPointSize(5);
//		gl.glColor3f(1, 0, 0);
//		gl.glBegin(GL2.GL_POINTS);
//		gl.glVertex2d(v0.getX(), v0.getY());
//		gl.glVertex2d(v1.getX(), v1.getY());
//		gl.glVertex2d(v2.getX(), v2.getY());
//		gl.glVertex2d(v3.getX(), v3.getY());
//		gl.glEnd();
//
//	}

//	private void computeOneSpline(Point2D v0, Point2D v1, Point2D v2,
//			Point2D v3, GL2 gl) {
//
//		// double ctrlx1 = (-Math.abs(v0.getX() - v1.getX()) / 4) + v1.getX()
//		// + (Math.abs(v2.getX() - v1.getX()) / 4);
//		// double ctrly1 = (-Math.abs(v0.getY() - v1.getY()) / 4) + v1.getY()
//		// + (Math.abs(v2.getY() - v1.getY()) / 4);
//		// double ctrlx2 = (-Math.abs(v1.getX() - v2.getX()) / 4) + v2.getX()
//		// + (Math.abs(v3.getX() - v2.getX()) / 4);
//		// double ctrly2 = (-Math.abs(v1.getY() - v2.getY()) / 4) + v2.getY()
//		// + (Math.abs(v3.getY() - v2.getY()) / 4);
//
//		// double dx = Math.abs(v1.getX() - v2.getX())
//		// * Math.abs(v1.getX() - v2.getX())
//		// + Math.abs(v1.getY() - v2.getY())
//		// * Math.abs(v1.getY() - v2.getY());
//		// double dy = dx;
//		//
//		// double ctrlx1 = (-v0.getX() / dx) + v1.getX() + v2.getX() / dx;
//		// double ctrly1 = (-v0.getY() / dy) + v1.getY() + v2.getY() / dy;
//		//
//		// double ctrlx2 = (v1.getX() / dx) + v2.getX() - v3.getX() / dx;
//		// double ctrly2 = (v1.getY() / dy) + v2.getY() - v3.getY() / dy;
//
//		double k = 12;
//		double ctrlx1 = (-v0.getX() / k) + v1.getX() + v2.getX() / k;
//		double ctrly1 = (-v0.getY() / k) + v1.getY() + v2.getY() / k;
//
//		double ctrlx2 = (v1.getX() / k) + v2.getX() - v3.getX() / k;
//		double ctrly2 = (v1.getY() / k) + v2.getY() - v3.getY() / k;
//
//		ArrayList<Vec3f> inputPoints = new ArrayList<Vec3f>();
//		inputPoints.add(new Vec3f((float) v1.getX(), (float) v1.getY(), 0));
//		inputPoints.add(new Vec3f((float) ctrlx1, (float) ctrly1, 0));
//		inputPoints.add(new Vec3f((float) ctrlx2, (float) ctrly2, 0));
//		inputPoints.add(new Vec3f((float) v2.getX(), (float) v2.getY(), 0));
//
//		NURBSCurve nurb = new NURBSCurve(inputPoints, 10);
//		ArrayList<Vec3f> outputPoints = nurb.getCurvePoints();
//
//		// Band border
//		gl.glLineWidth(1);
//		gl.glBegin(GL2.GL_POINTS);
//		for (int i = 0; i < outputPoints.size(); i++) {
//			gl.glVertex3f(outputPoints.get(i).x(), outputPoints.get(i).y(),
//					outputPoints.get(i).z());
//		}
//		gl.glEnd();
//
//		renderBand(gl, outputPoints, 30);
//
//		// gl.glPointSize(5);
//		// gl.glColor3f(0, 0, 1);
//		// gl.glBegin(GL2.GL_POINTS);
//		// gl.glVertex2d(ctrlx1, ctrly1);
//		// gl.glVertex2d(ctrlx2, ctrly2);
//		// gl.glEnd();
//		// curve.setCurve(v1.getX(), v1.getY(), ctrlx1, ctrly1, ctrlx2, ctrly2,
//		// v2.getX(), v2.getY());
//	}

	
	private void renderEdges(GL2 gl) {

		List<Pair<IDataGraphNode, IDataGraphNode>> bandConnectedNodes = new ArrayList<Pair<IDataGraphNode, IDataGraphNode>>();

		for (Pair<IDataGraphNode, IDataGraphNode> edge : dataGraph
				.getAllEdges()) {

			// Works because there are no edges between view nodes
			if ((edge.getFirst() instanceof ViewNode)
					|| (edge.getSecond() instanceof ViewNode)) {
				// Render later transparent in foreground
				bandConnectedNodes.add(edge);
			} else {

				gl.glPushAttrib(GL2.GL_LINE_BIT | GL2.GL_COLOR_BUFFER_BIT);
				gl.glColor3f(0, 0, 0);
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

		for (Pair<IDataGraphNode, IDataGraphNode> edge : bandConnectedNodes) {
			renderConnectionBands(gl, edge.getFirst(), edge.getSecond());
		}

	}

	private void renderConnectionBands(GL2 gl, IDataGraphNode node1,
			IDataGraphNode node2) {

		AEdgeBandRenderer bandCreator = ConnectionBandCreatorFactory
				.getConnectionBandCreator(node1, node2, pixelGLConverter, viewFrustum);

		bandCreator.renderEdgeBand(gl, new SimpleEdgeRoutingStrategy(dataGraph));
//		for (List<Pair<Point2D, Point2D>> anchorPoints : bandCreator
//				.calcConnectionBands()) {
//			connectionBandRenderer.init(gl);
//			connectionBandRenderer.renderComplexBand(gl, anchorPoints, false,
//					new float[] { 0, 0, 0 }, 0.2f);
//		}

//		List<ADimensionGroupData> dimensionGroups1 = node1.getDimensionGroups();
//		List<ADimensionGroupData> dimensionGroups2 = node2.getDimensionGroups();
//
//		Point2D node1BundlingPoint = new Point2D.Double(node1.getPosition()
//				.getX(), node1.getPosition().getY() - node1.getHeight() / 2.0f
//				- 0.2f);
//		Point2D node2BundlingPoint = new Point2D.Double(node2.getPosition()
//				.getX(), node2.getPosition().getY() - node2.getHeight() / 2.0f
//				- 0.2f);
//
//		if (dimensionGroups1 != null && !dimensionGroups1.isEmpty()
//				&& dimensionGroups2 != null && !dimensionGroups2.isEmpty()) {
//
//			for (ADimensionGroupData dimGroupData1 : dimensionGroups1) {
//				for (ADimensionGroupData dimGroupData2 : dimensionGroups2) {
//					if (dimGroupData1.getID() == dimGroupData2.getID()) {
//
//					}
//				}
//			}
//		}
//
//		gl.glPointSize(5);
//		gl.glColor3f(1, 0, 0);
//		gl.glBegin(GL2.GL_POINTS);
//		gl.glVertex2d(node1BundlingPoint.getX(), node1BundlingPoint.getY());
//		gl.glVertex2d(node2BundlingPoint.getX(), node2BundlingPoint.getY());
//		gl.glEnd();
//
//		ArrayList<Point2D> edgePoints = new ArrayList<Point2D>();
//		edgePoints.add(node1BundlingPoint);
//		edgePoints.add(node2BundlingPoint);
//
//		createEdge(gl, edgePoints);
//
//		edgePoints.add(edgePoints.get(edgePoints.size() - 1));
//		edgePoints.add(0, edgePoints.get(0));
//		
//		connectionBandRenderer.init(gl);
//
//		for (int i = 0; i < edgePoints.size() - 3; i++) {
//			connectionBandRenderer.renderInterpolatedBand(gl, edgePoints, 30, pixelGLConverter);
////			computeOneSpline(edgePoints.get(i), edgePoints.get(i + 1),
////					edgePoints.get(i + 2), edgePoints.get(i + 3), gl);
//		}

		

	}

	private void createEdge(GL2 gl, List<Point2D> edgePoints) {

		if (edgePoints == null || edgePoints.size() < 2)
			return;

		Map<Point2D, IDataGraphNode> pointsOnBoundingBoxes = new HashMap<Point2D, IDataGraphNode>();

		for (int i = 1; i < edgePoints.size(); i++) {
			Point2D point1 = edgePoints.get(i - 1);
			Point2D point2 = edgePoints.get(i);

			for (IDataGraphNode node : dataGraph.getNodes()) {
				Rectangle2D box = node.getBoundingBox();
				int code1 = box.outcode(point1);
				int code2 = box.outcode(point2);

				boolean isPoint1OnBoundingBox = (pointsOnBoundingBoxes
						.get(point1) == node);
				boolean isPoint2OnBoundingBox = (pointsOnBoundingBoxes
						.get(point2) == node);

				if (((code1 & code2) != 0)
						|| (code1 == 0 && !isPoint1OnBoundingBox)
						|| (code2 == 0 && !isPoint2OnBoundingBox)
						|| (isPoint1OnBoundingBox && isPoint2OnBoundingBox)) {
					continue;
				}

				Point2D intersection1 = (isPoint1OnBoundingBox) ? (point1)
						: (calcIntersectionPoint(point1, point2, box, code1));
				Point2D intersection2 = (isPoint2OnBoundingBox) ? (point2)
						: (calcIntersectionPoint(point2, point1, box, code2));

				if (intersection1 != null && intersection2 != null) {

					if (intersection1.getX() == intersection2.getX()
							&& intersection1.getY() == intersection2.getY()) {
						continue;
					}

					Point2D[] corners = new Point2D[4];
					// corners[0] = new Point2D.Double(box.getMinX() - 0.001,
					// box.getMinY() - 0.001);
					// corners[1] = new Point2D.Double(box.getMaxX() + 0.001,
					// box.getMinY() - 0.001);
					// corners[2] = new Point2D.Double(box.getMaxX() + 0.001,
					// box.getMaxY() + 0.001);
					// corners[3] = new Point2D.Double(box.getMinX() - 0.001,
					// box.getMaxY() + 0.001);
					corners[0] = new Point2D.Double(box.getMinX(),
							box.getMinY());
					corners[1] = new Point2D.Double(box.getMaxX(),
							box.getMinY());
					corners[2] = new Point2D.Double(box.getMaxX(),
							box.getMaxY());
					corners[3] = new Point2D.Double(box.getMinX(),
							box.getMaxY());

					double minDistance = Double.MAX_VALUE;
					Point2D bendPoint = null;

					for (int j = 0; j < 4; j++) {
						if ((corners[j].getX() == point1.getX()
								&& corners[j].getY() == point1.getY() && isPoint1OnBoundingBox)
								|| (corners[j].getX() == point2.getX() && corners[j]
										.getY() == point2.getY())
								&& isPoint2OnBoundingBox) {
							continue;
						}
						double currentSummedDistance = intersection1
								.distanceSq(corners[j])
								+ intersection2.distanceSq(corners[j]);
						if (currentSummedDistance < minDistance) {
							minDistance = currentSummedDistance;
							bendPoint = corners[j];

						}
					}
					if (bendPoint == null) {
						System.out.println("null");
					}

					boolean isPointAlreadyAdded = false;

					for (Point2D point : edgePoints) {
						if (point.getX() == bendPoint.getX()
								&& point.getY() == bendPoint.getY()) {
							isPointAlreadyAdded = true;
							break;
						}
					}

					if (isPointAlreadyAdded) {
						continue;
					}

					edgePoints.add(i, bendPoint);
					pointsOnBoundingBoxes.put(bendPoint, node);
					i--;

					// gl.glPointSize(5);
					// gl.glColor3f(0, 0, 1);
					// gl.glBegin(GL2.GL_POINTS);
					// gl.glVertex2d(intersection1.getX(),
					// intersection1.getY());
					// gl.glVertex2d(intersection2.getX(),
					// intersection2.getY());
					// gl.glEnd();
					//
					gl.glLineWidth(1);
					gl.glColor3f(0, 0, 1);
					gl.glBegin(GL2.GL_LINE_LOOP);
					gl.glVertex2d(box.getMinX(), box.getMinY());
					gl.glVertex2d(box.getMaxX(), box.getMinY());
					gl.glVertex2d(box.getMaxX(), box.getMaxY());
					gl.glVertex2d(box.getMinX(), box.getMaxY());
					gl.glEnd();

					break;
				}
			}
		}

		for (int step = edgePoints.size() - 2; step >= 2; step--) {

			for (int i = 0; i + step < edgePoints.size(); i++) {
				Point2D point1 = edgePoints.get(i);
				Point2D point2 = edgePoints.get(i + step);

				boolean hasIntersection = false;

				for (IDataGraphNode node : dataGraph.getNodes()) {
					Rectangle2D box = node.getBoundingBox();
					int code1 = box.outcode(point1);
					int code2 = box.outcode(point2);

					boolean isPoint1OnBoundingBox = (pointsOnBoundingBoxes
							.get(point1) == node);
					boolean isPoint2OnBoundingBox = (pointsOnBoundingBoxes
							.get(point2) == node);

					if ((code1 & code2) != 0) {
						continue;
					}

					if ((code1 == 0 && !isPoint1OnBoundingBox)
							|| (code2 == 0 && !isPoint2OnBoundingBox)
							|| (isPoint1OnBoundingBox && isPoint2OnBoundingBox)) {
						hasIntersection = true;
						break;
					}

					Point2D intersection1 = (isPoint1OnBoundingBox) ? (point1)
							: (calcIntersectionPoint(point1, point2, box, code1));
					Point2D intersection2 = (isPoint2OnBoundingBox) ? (point2)
							: (calcIntersectionPoint(point2, point1, box, code2));

					if (intersection1 == null || intersection2 == null) {
						continue;
					}

					if (intersection1.distance(intersection2) < 0.000001
							&& (isPoint1OnBoundingBox || isPoint2OnBoundingBox)) {
						continue;
					}

					hasIntersection = true;
					break;
				}

				if (!hasIntersection) {
					for (int j = i + 1; j < i + step; j++) {
						edgePoints.remove(i+1);
					}
					step = edgePoints.size() - 2;
					break;
				}
			}
		}

		// gl.glColor3f(1, 0, 1);
		// gl.glBegin(GL2.GL_LINE_STRIP);
		// for (Point2D point : edgePoints) {
		// gl.glVertex2d(point.getX(), point.getY());
		// }
		// gl.glEnd();
	}

	private Point2D calcIntersectionPoint(Point2D point1, Point2D point2,
			Rectangle2D rect, int code1) {

		double k = 0;

		if (point1.getX() != point2.getX()) {
			k = (point2.getY() - point1.getY())
					/ (point2.getX() - point1.getX());
		}

		if ((code1 & Rectangle2D.OUT_LEFT) != 0) {
			double y = point1.getY() + ((rect.getMinX() - point1.getX()) * k);

			if (y <= rect.getMaxY() && y >= rect.getMinY())
				return new Point2D.Double(rect.getMinX(), y);
		}

		if ((code1 & Rectangle2D.OUT_RIGHT) != 0) {
			double y = point1.getY() + ((rect.getMaxX() - point1.getX()) * k);
			if (y <= rect.getMaxY() && y >= rect.getMinY())
				return new Point2D.Double(rect.getMaxX(), y);
		}

		if ((code1 & Rectangle2D.OUT_TOP) != 0) {
			double x = point1.getX();
			if (k != 0) {
				x += (rect.getMinY() - point1.getY()) / k;
			}

			if (x <= rect.getMaxX() && x >= rect.getMinX())
				return new Point2D.Double(x, rect.getMinY());
		}

		if ((code1 & Rectangle2D.OUT_BOTTOM) != 0) {
			double x = point1.getX();
			if (k != 0) {
				x += (rect.getMaxY() - point1.getY()) / k;
			}

			if (x <= rect.getMaxX() && x >= rect.getMinX())
				return new Point2D.Double(x, rect.getMaxY());
		}

		return null;
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
		DataNode dataNode = dataNodesOfDataDomains.get(dataDomain);
		if (dataNode != null) {
			dataNode.update();
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
