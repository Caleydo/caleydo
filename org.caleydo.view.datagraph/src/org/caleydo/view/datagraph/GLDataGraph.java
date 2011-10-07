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

import org.caleydo.core.data.container.TableBasedDimensionGroupData;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainGraph;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.event.data.DimensionGroupsChangedEvent;
import org.caleydo.core.event.data.NewDataDomainEvent;
import org.caleydo.core.event.view.DataDomainsChangedEvent;
import org.caleydo.core.event.view.NewViewEvent;
import org.caleydo.core.event.view.ViewClosedEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.ARcpGLViewPart;
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
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.datagraph.bandlayout.EdgeBandRenderer;
import org.caleydo.view.datagraph.bandlayout.IEdgeRoutingStrategy;
import org.caleydo.view.datagraph.bandlayout.SimpleEdgeRoutingStrategy;
import org.caleydo.view.datagraph.event.AddDataContainerEvent;
import org.caleydo.view.datagraph.event.OpenViewEvent;
import org.caleydo.view.datagraph.listener.AddDataContainerEventListener;
import org.caleydo.view.datagraph.listener.DataDomainsChangedEventListener;
import org.caleydo.view.datagraph.listener.DimensionGroupsChangedEventListener;
import org.caleydo.view.datagraph.listener.GLDataGraphKeyListener;
import org.caleydo.view.datagraph.listener.NewDataDomainEventListener;
import org.caleydo.view.datagraph.listener.NewViewEventListener;
import org.caleydo.view.datagraph.listener.OpenViewEventListener;
import org.caleydo.view.datagraph.listener.ViewClosedEventListener;
import org.caleydo.view.datagraph.node.ADataNode;
import org.caleydo.view.datagraph.node.IDataGraphNode;
import org.caleydo.view.datagraph.node.NodeCreator;
import org.caleydo.view.datagraph.node.ViewNode;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

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
	private Set<ADataNode> dataNodes;
	private Set<ViewNode> viewNodes;
	private Map<IDataDomain, Set<ViewNode>> viewNodesOfDataDomains;
	private Map<IDataDomain, ADataNode> dataNodesOfDataDomains;
	private ConnectionBandRenderer connectionBandRenderer;

	private int maxDataAmount = Integer.MIN_VALUE;

	private NewViewEventListener newViewEventListener;
	private NewDataDomainEventListener newDataDomainEventListener;
	private ViewClosedEventListener viewClosedEventListener;
	private DataDomainsChangedEventListener dataDomainsChangedEventListener;
	private DimensionGroupsChangedEventListener dimensionGroupsChangedEventListener;
	private AddDataContainerEventListener addDataContainerEventListener;
	private OpenViewEventListener openViewEventListener;

	private NodeCreator nodeCreator;

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
		dataNodes = new HashSet<ADataNode>();
		viewNodes = new HashSet<ViewNode>();
		viewNodesOfDataDomains = new HashMap<IDataDomain, Set<ViewNode>>();
		dataNodesOfDataDomains = new HashMap<IDataDomain, ADataNode>();
		nodeCreator = new NodeCreator();

		DataDomainGraph dataDomainGraph = DataDomainManager.get().getDataDomainGraph();

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

		Collection<AGLView> views = GeneralManager.get().getViewManager().getAllGLViews();

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

	}

	@Override
	public void init(GL2 gl) {

		displayListIndex = gl.glGenLists(1);

		// Register keyboard listener to GL2 canvas
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite.addKeyListener(glKeyListener);
			}
		});

		textRenderer = new CaleydoTextRenderer(24);

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
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {
		this.glMouseListener = glMouseListener;
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
		gl.glCallList(displayListIndex);

		dragAndDropController.handleDragging(gl, glMouseListener);

		if (!lazyMode)
			checkForHits(gl);
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

		int drawingAreaWidth = pixelGLConverter.getPixelWidthForGLWidth(viewFrustum
				.getWidth()) - 2 * BOUNDS_SPACING_PIXELS - maxNodeWidthPixels;
		int drawingAreaHeight = pixelGLConverter.getPixelHeightForGLHeight(viewFrustum
				.getHeight()) - 2 * BOUNDS_SPACING_PIXELS - maxNodeHeightPixels;
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
					Pair<Float, Float> relativePosition = relativeNodePositions.get(node);
					graphLayout.setNodePosition(node,
							new Point2D.Double(relativePosition.getFirst()
									* drawingAreaWidth, relativePosition.getSecond()
									* drawingAreaHeight));
				}
			}
		}

		for (IDataGraphNode node : dataGraph.getNodes()) {
			Point2D position = graphLayout.getNodePosition(node, true);
			float relativePosX = (float) position.getX() / drawingAreaWidth;
			float relativePosY = (float) position.getY() / drawingAreaHeight;
			relativeNodePositions.put(node, new Pair<Float, Float>(relativePosX,
					relativePosY));

			node.render(gl);

		}
		renderEdges(gl);

		gl.glEndList();

	}

	private void renderEdges(GL2 gl) {

		List<Pair<IDataGraphNode, IDataGraphNode>> bandConnectedNodes = new ArrayList<Pair<IDataGraphNode, IDataGraphNode>>();

		for (Pair<IDataGraphNode, IDataGraphNode> edge : dataGraph.getAllEdges()) {

			// Works because there are no edges between view nodes
			if ((edge.getFirst() instanceof ViewNode)
					|| (edge.getSecond() instanceof ViewNode)) {
				// Render later transparent in foreground
				bandConnectedNodes.add(edge);
			} else {

				gl.glPushAttrib(GL2.GL_LINE_BIT | GL2.GL_COLOR_BUFFER_BIT);
				gl.glColor3f(0, 0, 0);
				gl.glLineWidth(2);
				gl.glEnable(GL2.GL_LINE_STIPPLE);
				gl.glLineStipple(3, (short) 127);

				// gl.glBegin(GL2.GL_LINES);
				Point2D position1 = edge.getFirst().getPosition();
				Point2D position2 = edge.getSecond().getPosition();

				List<Point2D> edgePoints = new ArrayList<Point2D>();
				edgePoints.add(position1);
				edgePoints.add(position2);

				IEdgeRoutingStrategy routingStrategy = new SimpleEdgeRoutingStrategy(
						dataGraph);
				routingStrategy.createEdge(edgePoints);

				edgePoints.add(0, position1);
				edgePoints.add(position2);

				connectionBandRenderer.init(gl);
				gl.glPushMatrix();
				gl.glTranslatef(0, 0, -0.1f);
				connectionBandRenderer.renderInterpolatedCurve(gl, edgePoints);
				gl.glPopMatrix();
				//
				// float x1 = pixelGLConverter
				// .getGLWidthForPixelWidth((int) position1.getX());
				// float x2 = pixelGLConverter
				// .getGLWidthForPixelWidth((int) position2.getX());
				// float y1 = pixelGLConverter
				// .getGLHeightForPixelHeight((int) position1.getY());
				// float y2 = pixelGLConverter
				// .getGLHeightForPixelHeight((int) position2.getY());
				//
				// gl.glVertex3f(x1, y1, -0.5f);
				// gl.glVertex3f(x2, y2, -0.5f);
				// gl.glEnd();
				gl.glPopAttrib();
			}
		}

		calcMaxDataAmount();

		for (Pair<IDataGraphNode, IDataGraphNode> edge : bandConnectedNodes) {
			renderConnectionBands(gl, edge.getFirst(), edge.getSecond());
		}

	}

	private void renderConnectionBands(GL2 gl, IDataGraphNode node1, IDataGraphNode node2) {

		EdgeBandRenderer bandRenderer = new EdgeBandRenderer(node1, node2,
				pixelGLConverter, viewFrustum, maxDataAmount);

		bandRenderer.renderEdgeBand(gl, new SimpleEdgeRoutingStrategy(dataGraph));
	}

	private void calcMaxDataAmount() {
		for (ADataNode dataNode : dataNodes) {
			if (maxDataAmount < dataNode.getDataDomain().getDataAmount())
				maxDataAmount = dataNode.getDataDomain().getDataAmount();
		}
	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int externalID, Pick pick) {
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
		eventPublisher.addListener(ViewClosedEvent.class, viewClosedEventListener);

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
		eventPublisher.addListener(NewDataDomainEvent.class, newDataDomainEventListener);

		addDataContainerEventListener = new AddDataContainerEventListener();
		addDataContainerEventListener.setHandler(this);
		eventPublisher.addListener(AddDataContainerEvent.class,
				addDataContainerEventListener);

		openViewEventListener = new OpenViewEventListener();
		openViewEventListener.setHandler(this);
		eventPublisher.addListener(OpenViewEvent.class, openViewEventListener);
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

		if (addDataContainerEventListener != null) {
			eventPublisher.removeListener(addDataContainerEventListener);
			addDataContainerEventListener = null;
		}

		if (openViewEventListener != null) {
			eventPublisher.removeListener(openViewEventListener);
			openViewEventListener = null;
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

			// IExtensionRegistry registry = Platform.getExtensionRegistry();
			// IExtensionPoint point = registry
			// .getExtensionPoint("org.eclipse.ui.views");
			// IExtension[] extensions = point.getExtensions();
			// String viewID = view.getViewType();
			// String viewName = viewID;
			// String iconPath = null;
			// boolean viewNameObtained = false;
			//
			// for (IExtension extension : extensions) {
			// IConfigurationElement[] elements = extension
			// .getConfigurationElements();
			// for (IConfigurationElement element : elements) {
			// if (element.getAttribute("id").equals(viewID)) {
			// viewName = element.getAttribute("name");
			// iconPath = element.getAttribute("icon");
			// viewNameObtained = true;
			// break;
			//
			// }
			// }
			// if (viewNameObtained) {
			// break;
			// }
			// }
			//
			// if (iconPath.equals("")) {
			// iconPath = null;
			// }
			// if (iconPath != null) {
			// ClassLoader classLoader = view.getClass().getClassLoader();
			// URL url = classLoader.getResource(iconPath);
			// try {
			// url = FileLocator.resolve(url);
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// iconPath = new File(url.getFile()).getAbsolutePath();
			// }

			ViewNode node = nodeCreator.createViewNode(graphLayout, this,
					dragAndDropController, lastNodeID++, view);
			dataGraph.addNode(node);
			viewNodes.add(node);
			Set<IDataDomain> dataDomains = view.getDataDomains();
			if (dataDomains != null && !dataDomains.isEmpty()) {
				node.setDataDomains(dataDomains);
				for (IDataDomain dataDomain : dataDomains) {
					Set<ViewNode> viewNodes = viewNodesOfDataDomains.get(dataDomain);
					if (viewNodes == null) {
						viewNodes = new HashSet<ViewNode>();
					}
					viewNodes.add(node);
					viewNodesOfDataDomains.put(dataDomain, viewNodes);
					ADataNode dataNode = dataNodesOfDataDomains.get(dataDomain);
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
				Set<ViewNode> viewNodes = viewNodesOfDataDomains.get(dataDomain);
				if (viewNodes != null) {
					viewNodes.remove(viewNode);
				}
			}
		}

		dataGraph.removeNode(viewNode);
		viewNodes.remove(viewNode);
		viewNode.destroy();
		// applyAutomaticLayout = true;
		setDisplayListDirty();
	}

	public void updateView(AGLView view) {

		ViewNode viewNode = null;
		for (ViewNode node : viewNodes) {
			if (node.getRepresentedView() == view) {
				viewNode = node;
				break;
			}
		}

		Set<IDataDomain> dataDomains = view.getDataDomains();
		if (dataDomains != null && !dataDomains.isEmpty()) {
			viewNode.setDataDomains(dataDomains);
			for (IDataDomain dataDomain : dataDomains) {
				Set<ViewNode> viewNodes = viewNodesOfDataDomains.get(dataDomain);
				if (viewNodes == null) {
					viewNodes = new HashSet<ViewNode>();
				}
				viewNodes.add(viewNode);
				viewNodesOfDataDomains.put(dataDomain, viewNodes);
				ADataNode dataNode = dataNodesOfDataDomains.get(dataDomain);
				if (dataNode != null) {
					dataGraph.addEdge(dataNode, viewNode);
				}
			}

			viewNode.update();
		}
	}

	public void updateDataDomain(IDataDomain dataDomain) {
		ADataNode dataNode = dataNodesOfDataDomains.get(dataDomain);
		if (dataNode != null) {
			dataNode.update();
			setDisplayListDirty();
		}
	}

	public void addDataDomain(IDataDomain dataDomain) {
		ADataNode dataNode = null;
		boolean nodeAdded = false;
		for (ADataNode node : dataNodes) {
			if (node.getDataDomain() == dataDomain) {
				dataNode = node;
				nodeAdded = true;
				break;
			}
		}
		if (!nodeAdded) {
			dataNode = nodeCreator.createDataNode(graphLayout, this,
					dragAndDropController, lastNodeID++, dataDomain);
			dataGraph.addNode(dataNode);
			dataNodes.add(dataNode);
			dataNodesOfDataDomains.put(dataNode.getDataDomain(), dataNode);
		}

		DataDomainGraph dataDomainGraph = DataDomainManager.get().getDataDomainGraph();

		Set<IDataDomain> neighbors = dataDomainGraph.getNeighboursOf(dataDomain);

		for (IDataDomain neighborDataDomain : neighbors) {
			nodeAdded = false;
			for (ADataNode node : dataNodes) {
				if (node.getDataDomain() == neighborDataDomain) {
					dataGraph.addEdge(dataNode, node);
					nodeAdded = true;
					break;
				}
			}
			if (!nodeAdded) {
				ADataNode node = nodeCreator.createDataNode(graphLayout, this,
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

	public TextureManager getTextureManager() {
		return textureManager;
	}

	public int getMaxDataAmount() {
		return maxDataAmount;
	}

	public void createDataContainer(ATableBasedDataDomain dataDomain,
			String recordPerspectiveID, String dimensionPerspectiveID) {

		TableBasedDimensionGroupData data = new TableBasedDimensionGroupData(dataDomain,
				dataDomain.getTable().getRecordPerspective(recordPerspectiveID),
				dataDomain.getTable().getDimensionPerspective(dimensionPerspectiveID));
		dataDomain.addDimensionGroup(data);

		// FIXME: Create proper DimensionGroup
		// FakeDimensionGroupData dimensionGroup = new
		// FakeDimensionGroupData(0);
		// dimensionGroup.setDataDomain(dataDomain);
		// dimensionGroup.setDimensionPerspectiveID(dimensionPerspectiveID);
		// dimensionGroup.setRecordPerspectiveID(recordPerspectiveID);
		// dataDomain.addDimensionGroup(dimensionGroup);
	}

	public void openView(AGLView view) {
		final ARcpGLViewPart viewPart = GeneralManager.get().getViewManager()
				.getViewPartFromView(view);

		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.activate(viewPart);
			}
		});

	}
}
