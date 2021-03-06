/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.graph.DataDomainGraph;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.events.DimensionVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.event.data.NewDataDomainEvent;
import org.caleydo.core.event.data.NewDataDomainLoadedEvent;
import org.caleydo.core.event.data.RemoveDataDomainEvent;
import org.caleydo.core.event.view.MinSizeAppliedEvent;
import org.caleydo.core.event.view.NewViewEvent;
import org.caleydo.core.event.view.SetMinViewSizeEvent;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.event.view.ViewClosedEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.base.IDefaultLabelHolder;
import org.caleydo.core.util.base.ILabelHolder;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.RCPViewInitializationData;
import org.caleydo.core.view.RCPViewManager;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.dvi.event.ApplySpecificGraphLayoutEvent;
import org.caleydo.view.dvi.event.CreateAndRenameTablePerspectiveEvent;
import org.caleydo.view.dvi.event.CreateTablePerspectiveBeforeAddingEvent;
import org.caleydo.view.dvi.event.CreateViewFromTablePerspectiveEvent;
import org.caleydo.view.dvi.event.OpenViewEvent;
import org.caleydo.view.dvi.event.RenameLabelHolderEvent;
import org.caleydo.view.dvi.event.ShowDataConnectionsEvent;
import org.caleydo.view.dvi.event.ShowViewWithoutDataEvent;
import org.caleydo.view.dvi.layout.AGraphLayout;
import org.caleydo.view.dvi.layout.TwoLayeredGraphLayout;
import org.caleydo.view.dvi.layout.edge.rendering.AEdgeRenderer;
import org.caleydo.view.dvi.listener.ApplySpecificGraphLayoutEventListener;
import org.caleydo.view.dvi.listener.CreateAndAddTablePerspectiveEventListener;
import org.caleydo.view.dvi.listener.CreateAndRenameTablePerspectiveEventListener;
import org.caleydo.view.dvi.listener.CreateViewFromTablePerspectiveEventListener;
import org.caleydo.view.dvi.listener.DataDomainChangedListener;
import org.caleydo.view.dvi.listener.DataDomainEventListener;
import org.caleydo.view.dvi.listener.DimensionVAUpdateEventListener;
import org.caleydo.view.dvi.listener.GLDVIKeyListener;
import org.caleydo.view.dvi.listener.MinSizeAppliedEventListener;
import org.caleydo.view.dvi.listener.NewViewEventListener;
import org.caleydo.view.dvi.listener.OpenViewEventListener;
import org.caleydo.view.dvi.listener.RecordVAUpdateEventListener;
import org.caleydo.view.dvi.listener.RenameLabelHolderEventListener;
import org.caleydo.view.dvi.listener.ShowDataConnectionsEventListener;
import org.caleydo.view.dvi.listener.ShowViewWithoutDataEventListener;
import org.caleydo.view.dvi.listener.TablePerspectivesCangedListener;
import org.caleydo.view.dvi.listener.ViewClosedEventListener;
import org.caleydo.view.dvi.node.ADataNode;
import org.caleydo.view.dvi.node.IDVINode;
import org.caleydo.view.dvi.node.MultiTablePerspectiveViewNode;
import org.caleydo.view.dvi.node.NodeCreator;
import org.caleydo.view.dvi.node.ViewNode;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * This class is responsible for providing an overview of all loaded datasets and opened views and their relationships.
 *
 * @author Christian Partl
 */
public class GLDataViewIntegrator extends AGLView implements IViewCommandHandler {

	public static String VIEW_TYPE = "org.caleydo.view.dvi";

	public static String VIEW_NAME = "Data-View Integrator";

	public final static int BOUNDS_SPACING_PIXELS = 10;

	private GLDVIKeyListener glKeyListener;
	private boolean useDetailLevel = false;

	private Graph dataGraph;
	private AGraphLayout graphLayout;
	private int maxNodeWidthPixels;
	private int maxNodeHeightPixels;
	private DragAndDropController dragAndDropController;
	private boolean applyAutomaticLayout;
	private int lastNodeID = 0;
	private Set<ADataNode> dataNodes;
	private Set<ViewNode> viewNodes;
	private Map<IDataDomain, Set<ViewNode>> viewNodesOfDataDomains;
	private Map<IDataDomain, ADataNode> dataNodesOfDataDomains;
	private ConnectionBandRenderer connectionBandRenderer;

	private int maxDataAmount = Integer.MIN_VALUE;

	private final EventListenerManager listeners = EventListenerManagers.wrap(this);

	private IDVINode currentMouseOverNode;

	private NodeCreator nodeCreator;

	private int minViewHeightPixels;
	private int minViewWidthPixels;

	private boolean isMinSizeApplied = false;
	private boolean waitForMinSizeApplication = false;
	private boolean isRendered = false;
	private boolean showDataConnections = false;

	/**
	 * Constructor.
	 */
	public GLDataViewIntegrator(IGLCanvas glCanvas, ViewFrustum viewFrustum) {

		super(glCanvas, viewFrustum, VIEW_TYPE, VIEW_NAME);

		connectionBandRenderer = new ConnectionBandRenderer();

		glKeyListener = new GLDVIKeyListener();
		// graphLayout = new ForceDirectedGraphLayout(this, dataGraph);
		dragAndDropController = new DragAndDropController(this);
		dataNodes = new HashSet<ADataNode>();
		viewNodes = new HashSet<ViewNode>();
		viewNodesOfDataDomains = new HashMap<IDataDomain, Set<ViewNode>>();
		dataNodesOfDataDomains = new HashMap<IDataDomain, ADataNode>();
		nodeCreator = new NodeCreator();
	}

	@Override
	public void init(GL2 gl) {

		displayListIndex = gl.glGenLists(1);

		// Register keyboard listener to GL2 canvas
		final Composite parentComposite = parentGLCanvas.asComposite();
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite.addKeyListener(glKeyListener);
			}
		});

		textRenderer = new CaleydoTextRenderer(24);
		dataGraph = new Graph();
		graphLayout = new TwoLayeredGraphLayout(this, dataGraph);

		DataDomainGraph dataDomainGraph = DataDomainManager.get().getDataDomainGraph();

		for (IDataDomain dataDomain : dataDomainGraph.getDataDomains()) {
			addDataDomain(dataDomain);
		}
		
		for (IView view : ViewManager.get().getAllViews()) {
			addView(view);
		}

		applyAutomaticLayout = true;

		addTypePickingTooltipListener(new IPickingLabelProvider() {

			@Override
			public String getLabel(Pick pick) {
				Edge e = dataGraph.getEdge(pick.getObjectID());
				if (e == null)
					return null;
				IDVINode node1 = e.getNode1();
				IDVINode node2 = e.getNode2();

				MultiTablePerspectiveViewNode viewNode = null;
				ADataNode dataNode = null;

				if (node1 instanceof MultiTablePerspectiveViewNode) {
					viewNode = (MultiTablePerspectiveViewNode) node1;
				} else if (node1 instanceof ADataNode) {
					dataNode = (ADataNode) node1;
				}

				if (node2 instanceof MultiTablePerspectiveViewNode) {
					viewNode = (MultiTablePerspectiveViewNode) node2;
				} else if (node2 instanceof ADataNode) {
					dataNode = (ADataNode) node2;
				}

				if (viewNode == null || dataNode == null)
					return null;

				List<TablePerspective> viewTablePerspectives = viewNode.getTablePerspectives();
				List<TablePerspective> dataTablePerspectives = dataNode.getTablePerspectives();
				boolean isGenericBand = true;
				for (TablePerspective vtp : viewTablePerspectives) {
					if (dataTablePerspectives.contains(vtp)) {
						isGenericBand = false;
						break;
					}
				}
				if (isGenericBand) {
					return viewNode.getLabel() + " currently displays data from the " + dataNode.getLabel()
							+ " dataset, but uses a stratification of a different dataset.";
				}
				return null;
			}
		}, PickingType.EDGE_BAND.name());
	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView, final GLMouseListener glMouseListener) {
		setMouseListener(glMouseListener);
		init(gl);
		pixelGLConverter = glParentView.getPixelGLConverter();
	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
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

		if (!isRendered) {
			maxNodeWidthPixels = Integer.MIN_VALUE;
			maxNodeHeightPixels = Integer.MIN_VALUE;

			for (IDVINode node : dataGraph.getNodes()) {
				node.recalculateNodeSize();
				if (node.getHeightPixels() > maxNodeHeightPixels)
					maxNodeHeightPixels = node.getHeightPixels();

				if (node.getWidthPixels() > maxNodeWidthPixels)
					maxNodeWidthPixels = node.getWidthPixels();
			}
		}

		if (isDisplayListDirty && !(!isMinSizeApplied && waitForMinSizeApplication)) {
			buildDisplayList(gl, displayListIndex);
			isDisplayListDirty = false;
			waitForMinSizeApplication = false;

			// update all IMultiTablePerspecitveBasedViews to get orderings of
			// tableperspectives right, as this is only possible after the nodes
			// have been layouted once.
			if (!isRendered) {
				for (ViewNode viewNode : viewNodes) {
					if (viewNode instanceof MultiTablePerspectiveViewNode) {
						viewNode.update();
					}
				}
			}
		}
		gl.glCallList(displayListIndex);

		if (!lazyMode)
			checkForHits(gl);

		dragAndDropController.handleDragging(gl, glMouseListener);

		isRendered = true;
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

		Rectangle2D drawingArea = calculateGraphDrawingArea();

		if (applyAutomaticLayout) {
			for (IDVINode node : dataGraph.getNodes()) {
				node.setCustomPosition(false);
			}
			for (Edge edge : dataGraph.getAllEdges()) {
				AEdgeRenderer edgeRenderer = graphLayout.getLayoutSpecificEdgeRenderer(edge);
				edge.setEdgeRenderer(edgeRenderer);
			}
			// graphLayout.setGraph(dataGraph);

			graphLayout.clearNodePositions();
			graphLayout.layout(drawingArea);
			updateMinWindowSize(true);
		}

		for (IDVINode node : dataGraph.getNodes()) {
			// Point2D position = graphLayout.getNodePosition(node);

			node.render(gl);

			// float relativePosX = (float) position.getX() / drawingAreaWidth;
			// float relativePosY = (float) position.getY() / drawingAreaHeight;
			// relativeNodePositions.put(node, new Pair<Float,
			// Float>(relativePosX,
			// relativePosY));
		}

		renderEdges(gl);

		gl.glEndList();

		// gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		// gl.glPushMatrix();
		// gl.glTranslatef(2, 2, 2);
		// pixelGLConverter.getGLWidthForCurrentGLTransform(gl);
		// gl.glPopMatrix();

		applyAutomaticLayout = false;

	}

	public Rectangle2D calculateGraphDrawingArea() {
		int drawingAreaWidth = pixelGLConverter.getPixelWidthForGLWidth(viewFrustum.getWidth()) - 2
				* BOUNDS_SPACING_PIXELS;
		int drawingAreaHeight = pixelGLConverter.getPixelHeightForGLHeight(viewFrustum.getHeight()) - 2
				* BOUNDS_SPACING_PIXELS;

		Rectangle2D drawingArea = new Rectangle();

		drawingArea.setFrame(BOUNDS_SPACING_PIXELS, BOUNDS_SPACING_PIXELS, drawingAreaWidth, drawingAreaHeight);

		return drawingArea;
	}

	public void updateMinWindowSize(boolean waitForMinSizeApplication) {

		// if (!dragAndDropController.isDragging()) {
		int minWidth = graphLayout.getMinWidthPixels() + 2 * BOUNDS_SPACING_PIXELS;
		int minHeight = graphLayout.getMinHeightPixels() + 2 * BOUNDS_SPACING_PIXELS;

		if (minWidth > minViewWidthPixels + 2 || minWidth < minViewWidthPixels - 2
				|| minHeight > minViewHeightPixels + 2 || minHeight < minViewHeightPixels - 2) {

			minViewWidthPixels = minWidth;
			minViewHeightPixels = minHeight;

			this.waitForMinSizeApplication = waitForMinSizeApplication;
			isMinSizeApplied = false;

			SetMinViewSizeEvent event = new SetMinViewSizeEvent(this);
			event.setMinViewSize(minViewWidthPixels, minViewHeightPixels);
			EventPublisher.trigger(event);
			// parentComposite.getParent();
		}
		// }
	}

	private void renderEdges(GL2 gl) {

		calcMaxDataAmount();

		connectionBandRenderer.init(gl);

		List<Edge> bandConnectedNodes = new ArrayList<Edge>();

		for (Edge edge : dataGraph.getAllEdges()) {

			// Works because there are no edges between view nodes
			if ((edge.getNode1() instanceof ViewNode) || (edge.getNode2() instanceof ViewNode)) {
				// Render later transparent in foreground
				bandConnectedNodes.add(edge);
			} else {
				renderEdge(gl, edge, connectionBandRenderer);
			}
		}

		for (Edge edge : bandConnectedNodes) {
			renderEdge(gl, edge, connectionBandRenderer);
		}

	}

	private void renderEdge(GL2 gl, Edge edge, ConnectionBandRenderer connectionBandRenderer) {
		boolean highlight = false;
		if (edge.getNode1() == currentMouseOverNode || edge.getNode2() == currentMouseOverNode) {
			highlight = true;
		}
		edge.getEdgeRenderer().renderEdge(gl, connectionBandRenderer, highlight);
	}

	private void calcMaxDataAmount() {
		for (ADataNode dataNode : dataNodes) {
			if (maxDataAmount < dataNode.getDataDomain().getDataAmount())
				maxDataAmount = dataNode.getDataDomain().getDataAmount();
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return new SerializedDVIView(this);
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		NewViewEventListener newViewEventListener = new NewViewEventListener();
		newViewEventListener.setHandler(this);
		listeners.register(NewViewEvent.class, newViewEventListener);

		ViewClosedEventListener viewClosedEventListener = new ViewClosedEventListener();
		viewClosedEventListener.setHandler(this);
		listeners.register(ViewClosedEvent.class, viewClosedEventListener);

		TablePerspectivesCangedListener tablePerspectivesChangedListener = new TablePerspectivesCangedListener();
		tablePerspectivesChangedListener.setHandler(this);
		listeners.register(TablePerspectivesChangedEvent.class, tablePerspectivesChangedListener);

		DataDomainChangedListener dataDomainChangedListener = new DataDomainChangedListener();
		dataDomainChangedListener.setHandler(this);
		listeners.register(DataDomainUpdateEvent.class, dataDomainChangedListener);

		DataDomainEventListener newDataDomainEventListener = new DataDomainEventListener();
		newDataDomainEventListener.setHandler(this);
		listeners.register(NewDataDomainEvent.class, newDataDomainEventListener);
		listeners.register(NewDataDomainLoadedEvent.class, newDataDomainEventListener);
		listeners.register(RemoveDataDomainEvent.class, newDataDomainEventListener);

		OpenViewEventListener openViewEventListener = new OpenViewEventListener();
		openViewEventListener.setHandler(this);
		listeners.register(OpenViewEvent.class, openViewEventListener);

		CreateViewFromTablePerspectiveEventListener createViewFromTablePerspectiveEventListener = new CreateViewFromTablePerspectiveEventListener();
		createViewFromTablePerspectiveEventListener.setHandler(this);
		listeners.register(CreateViewFromTablePerspectiveEvent.class, createViewFromTablePerspectiveEventListener);

		ApplySpecificGraphLayoutEventListener applySpecificGraphLayoutEventListener = new ApplySpecificGraphLayoutEventListener();
		applySpecificGraphLayoutEventListener.setHandler(this);
		listeners.register(ApplySpecificGraphLayoutEvent.class, applySpecificGraphLayoutEventListener);

		MinSizeAppliedEventListener minSizeAppliedEventListener = new MinSizeAppliedEventListener();
		minSizeAppliedEventListener.setHandler(this);
		listeners.register(MinSizeAppliedEvent.class, minSizeAppliedEventListener);

		ShowDataConnectionsEventListener showDataConnectionsEventListener = new ShowDataConnectionsEventListener();
		showDataConnectionsEventListener.setHandler(this);
		listeners.register(ShowDataConnectionsEvent.class, showDataConnectionsEventListener);

		RecordVAUpdateEventListener recordVAUpdateEventListener = new RecordVAUpdateEventListener();
		recordVAUpdateEventListener.setHandler(this);
		listeners.register(RecordVAUpdateEvent.class, recordVAUpdateEventListener);

		DimensionVAUpdateEventListener dimensionVAUpdateEventListener = new DimensionVAUpdateEventListener();
		dimensionVAUpdateEventListener.setHandler(this);
		listeners.register(DimensionVAUpdateEvent.class, dimensionVAUpdateEventListener);

		ShowViewWithoutDataEventListener showViewWithoutDataEventListener = new ShowViewWithoutDataEventListener();
		showViewWithoutDataEventListener.setHandler(this);
		listeners.register(ShowViewWithoutDataEvent.class, showViewWithoutDataEventListener);

		RenameLabelHolderEventListener renameLabelHolderEventListener = new RenameLabelHolderEventListener();
		renameLabelHolderEventListener.setHandler(this);
		listeners.register(RenameLabelHolderEvent.class, renameLabelHolderEventListener);

		CreateAndAddTablePerspectiveEventListener createAndAddTablePerspectiveEventListener = new CreateAndAddTablePerspectiveEventListener();
		createAndAddTablePerspectiveEventListener.setHandler(this);
		listeners.register(CreateTablePerspectiveBeforeAddingEvent.class, createAndAddTablePerspectiveEventListener);

		CreateAndRenameTablePerspectiveEventListener createAndRenameTablePerspectiveEventListener = new CreateAndRenameTablePerspectiveEventListener();
		createAndRenameTablePerspectiveEventListener.setHandler(this);
		listeners.register(CreateAndRenameTablePerspectiveEvent.class, createAndRenameTablePerspectiveEventListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		listeners.unregisterAll();
	}

	@Override
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	public void setApplyAutomaticLayout(boolean applyAutomaticLayout) {
		this.applyAutomaticLayout = applyAutomaticLayout;

		if (applyAutomaticLayout) {
			minViewWidthPixels = 10;
			minViewHeightPixels = 10;

			isMinSizeApplied = false;
			waitForMinSizeApplication = true;
			

			EventPublisher eventPublisher = EventPublisher.INSTANCE;
			SetMinViewSizeEvent event = new SetMinViewSizeEvent(this);
			event.setMinViewSize(minViewWidthPixels, minViewHeightPixels);
			eventPublisher.triggerEvent(event);
		}
	}

	public void addView(IView view) {

		for (ViewNode node : viewNodes) {
			if (node != null && node.getRepresentedView() == view)
				return;
		}

		if (!(view instanceof AGLView && ((AGLView) view).isRenderedRemote()) && view.isDataView()) {

			ViewNode node = nodeCreator.createViewNode(graphLayout, this, dragAndDropController, lastNodeID++, view);
			dataGraph.addNode(node);
			viewNodes.add(node);
			Set<IDataDomain> dataDomains = view.getDataDomains();
			if (dataDomains != null && !dataDomains.isEmpty()) {
				for (IDataDomain dataDomain : dataDomains) {
					Set<ViewNode> viewNodes = viewNodesOfDataDomains.get(dataDomain);
					if (viewNodes == null) {
						viewNodes = new HashSet<ViewNode>();
					}
					viewNodes.add(node);
					viewNodesOfDataDomains.put(dataDomain, viewNodes);
					ADataNode dataNode = dataNodesOfDataDomains.get(dataDomain);
					if (dataNode != null) {
						Edge edge = dataGraph.addEdge(dataNode, node);
						AEdgeRenderer edgeRenderer = graphLayout.getLayoutSpecificEdgeRenderer(edge);
						edge.setEdgeRenderer(edgeRenderer);
						dataNode.update();
					}
				}
			}
			for (ADataNode dataNode : dataNodes) {
				dataNode.update();
			}

			applyAutomaticLayout = true;
			setDisplayListDirty();
		}
	}

	public void removeView(IView view) {

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
					if (viewNodes.isEmpty())
						viewNodesOfDataDomains.remove(dataDomain);
				}
			}
		}

		dataGraph.removeNode(viewNode);
		viewNodes.remove(viewNode);
		if (dragAndDropController.getDropArea() == viewNode)
			dragAndDropController.setDropArea(null);
		graphLayout.cleanupNode(viewNode);
		viewNode.destroy();

		if (viewNode == currentMouseOverNode)
			currentMouseOverNode = null;

		for (ADataNode dataNode : dataNodes) {
			dataNode.update();
		}

		// applyAutomaticLayout = true;
		setDisplayListDirty();
	}

	public void updateView(AView view) {

		if (view instanceof AGLView && ((AGLView) view).isRenderedRemote())
			return;

		ViewNode viewNode = null;
		for (ViewNode node : viewNodes) {
			if (node.getRepresentedView() == view) {
				viewNode = node;
				break;
			}
		}

		if (viewNode == null)
			return;

		Set<IDataDomain> dataDomainsOfView = view.getDataDomains();
		if (dataDomainsOfView != null) {
			viewNode.update();
			// Update data nodes, which might hide tableperspectives now missing in the
			for (ADataNode dataNode : dataNodes) {
				dataNode.update();
			}
			updateGraphEdgesOfViewNode(viewNode);
		}
	}

	public void updateGraphEdgesOfViewNode(ViewNode viewNode) {
		Set<IDataDomain> dataDomainsOfView = viewNode.getDataDomains();

		for (IDataDomain dataDomain : dataNodesOfDataDomains.keySet()) {

			Set<ViewNode> viewNodes = viewNodesOfDataDomains.get(dataDomain);
			if (dataDomainsOfView.contains(dataDomain)) {
				if (viewNodes == null) {
					viewNodes = new HashSet<ViewNode>();
				}
				viewNodes.add(viewNode);
				viewNodesOfDataDomains.put(dataDomain, viewNodes);
				ADataNode dataNode = dataNodesOfDataDomains.get(dataDomain);
				if (dataNode != null) {
					Edge edge = dataGraph.addEdge(dataNode, viewNode);
					AEdgeRenderer edgeRenderer = graphLayout.getLayoutSpecificEdgeRenderer(edge);
					edge.setEdgeRenderer(edgeRenderer);
				}
			} else {
				if (viewNodes != null) {
					viewNodes.remove(viewNode);
					if (viewNodes.isEmpty()) // clean empty
						viewNodesOfDataDomains.remove(dataDomain);
				}
				ADataNode dataNode = dataNodesOfDataDomains.get(dataDomain);
				if (dataNode != null) {
					dataGraph.removeEdge(dataNode, viewNode);
				}
			}
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
			dataNode = nodeCreator.createDataNode(graphLayout, this, dragAndDropController, lastNodeID++, dataDomain);
			if (dataNode == null)
				return;
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
					Edge edge = dataGraph.addEdge(dataNode, node);
					AEdgeRenderer edgeRenderer = graphLayout.getLayoutSpecificEdgeRenderer(edge);
					edge.setEdgeRenderer(edgeRenderer);
					nodeAdded = true;
					break;
				}
			}
			if (!nodeAdded) {
				ADataNode node = nodeCreator.createDataNode(graphLayout, this, dragAndDropController, lastNodeID++,
						neighborDataDomain);
				if (node == null)
					return;
				dataGraph.addNode(node);
				dataNodes.add(node);
				dataNodesOfDataDomains.put(node.getDataDomain(), node);
				Edge edge = dataGraph.addEdge(dataNode, node);
				AEdgeRenderer edgeRenderer = graphLayout.getLayoutSpecificEdgeRenderer(edge);
				edge.setEdgeRenderer(edgeRenderer);
			}
		}

		applyAutomaticLayout = true;
		setDisplayListDirty();
	}

	public void removeDataDomain(String dataDomainID) {
		ADataNode node = null;
		IDataDomain toremove = null;
		for (IDataDomain dataDomain : dataNodesOfDataDomains.keySet()) {
			if (dataDomain.getDataDomainID().equals(dataDomainID)) {
				toremove = dataDomain;
				node = dataNodesOfDataDomains.remove(dataDomain);
				break;
			}
		}
		if (node == null)
			return;
		node.destroy();

		if (currentMouseOverNode == node) {
			currentMouseOverNode = null;
		}

		dataGraph.removeNode(node);
		dataNodes.remove(node);
		viewNodesOfDataDomains.remove(toremove);

		applyAutomaticLayout = true;
		setDisplayListDirty();
	}

	// public TextureManager getTextureManager() {
	// return textureManager;
	// }

	public int getMaxDataAmount() {
		return maxDataAmount;
	}

	public String getEdgeLabel(ADataNode node1, ADataNode node2) {

		DataDomainGraph dataDomainGraph = DataDomainManager.get().getDataDomainGraph();

		Set<org.caleydo.core.data.datadomain.graph.Edge> edges = dataDomainGraph.getEdges(node1.getDataDomain(),
				node2.getDataDomain());

		StringBuffer stringBuffer = new StringBuffer();

		Iterator<org.caleydo.core.data.datadomain.graph.Edge> iterator = edges.iterator();
		while (iterator.hasNext()) {
			org.caleydo.core.data.datadomain.graph.Edge e = iterator.next();
			IDCategory category = e.getIdCategory();
			if (category != null) {
				stringBuffer.append(e.getIdCategory().getCategoryName());
			} else {
				stringBuffer.append("Unknown Mapping");
			}
			if (iterator.hasNext()) {
				stringBuffer.append(", ");
			}
		}

		return stringBuffer.toString();
	}

	public void openView(final IView view) {
		
		final CaleydoRCPViewPart viewPart = ViewManager.get().getViewPartFromView(view);

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {

				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(viewPart);
				// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				// .activate(viewPart);
				// viewPart.setFocus();
				// viewPart.getSWTComposite().setFocus();

			}
		});

	}

	public void createViewWithoutData(final String viewID) {

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {

				try {
					if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewID);
					}
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void createView(final String viewID, final IDataDomain dataDomain, final TablePerspective tablePerspective) {

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {

				try {

					String secondaryID = UUID.randomUUID().toString();
					RCPViewInitializationData rcpViewInitData = new RCPViewInitializationData();
					rcpViewInitData.setDataDomainID(dataDomain.getDataDomainID());
					rcpViewInitData.setTablePerspective(tablePerspective);
					RCPViewManager.get().addRCPView(secondaryID, rcpViewInitData);

					if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
								.showView(viewID, secondaryID, IWorkbenchPage.VIEW_ACTIVATE);

					}
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void setCurrentMouseOverNode(IDVINode currentMouseOverNode) {
		this.currentMouseOverNode = currentMouseOverNode;
	}

	public IDVINode getCurrentMouseOverNode() {
		return currentMouseOverNode;
	}

	public void applyGraphLayout(Class<? extends AGraphLayout> graphLayoutClass) {

		try {
			graphLayout = graphLayoutClass.getConstructor(GLDataViewIntegrator.class, Graph.class).newInstance(this,
					dataGraph);

			for (IDVINode node : dataGraph.getNodes()) {
				node.setGraphLayout(graphLayout);
			}
		} catch (Exception e) {
			Logger.log(new Status(IStatus.ERROR, this.toString(), "Failed to create Graph Layout", e));
		}

		setApplyAutomaticLayout(true);
		setDisplayListDirty();
	}

	public boolean isMinSizeApplied() {
		return isMinSizeApplied;
	}

	public void setMinSizeApplied(boolean isMinSizeApplied) {
		this.isMinSizeApplied = isMinSizeApplied;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);

		if (!waitForMinSizeApplication && isRendered) {

			Rectangle2D drawingArea = calculateGraphDrawingArea();

			graphLayout.applyIncrementalLayout(drawingArea);

			// for (IDVINode node : dataGraph.getNodes()) {
			// Pair<Float, Float> relativePosition =
			// relativeNodePositions.get(node);
			// graphLayout.setNodePosition(node,
			// new Point2D.Double(
			// relativePosition.getFirst() * drawingAreaWidth,
			// relativePosition.getSecond() * drawingAreaHeight));
			// }

			SetMinViewSizeEvent event = new SetMinViewSizeEvent(this);
			event.setMinViewSize(minViewWidthPixels, minViewHeightPixels);
			EventPublisher.trigger(event);
		}
	}

	public ADataNode getDataNode(IDataDomain dataDomain) {
		return dataNodesOfDataDomains.get(dataDomain);
	}

	public Set<ViewNode> getViewNodes() {
		return viewNodes;
	}

	public AGraphLayout getGraphLayout() {
		return graphLayout;
	}

	public Collection<IDVINode> getAllNodes() {
		return dataGraph.getNodes();
	}

	public void showDataConnections(boolean showDataConnections) {
		this.showDataConnections = showDataConnections;
		setDisplayListDirty();
	}

	public boolean isShowDataConnections() {
		return showDataConnections;
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		gl.glDeleteLists(displayListIndex, 1);
		// TODO: delete layout managers
	}

	/**
	 * Opens an input dialog in order to rename the specified {@link ILabelHolder}.
	 *
	 * @param labelHolder
	 */
	public void renameLabelHolder(final ILabelHolder labelHolder) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {

				IInputValidator validator = new IInputValidator() {
					@Override
					public String isValid(String newText) {
						if (newText.equalsIgnoreCase(""))
							return "Please enter a name.";
						else
							return null;
					}
				};

				InputDialog dialog = new InputDialog(new Shell(), "Rename " + labelHolder.getProviderName(), "Name",
						labelHolder.getLabel(), validator);

				if (dialog.open() == Window.OK) {
					if (labelHolder instanceof IDefaultLabelHolder) {
						((IDefaultLabelHolder) labelHolder).setLabel(dialog.getValue(), false);
					} else {
						labelHolder.setLabel(dialog.getValue());
					}
					for (IDVINode node : dataGraph.getNodes()) {
						node.update();
					}
					setDisplayListDirty();
				}
			}
		});
	}

	public boolean isTablePerspectiveShownByView(TablePerspective tablePerspective) {
		for (ViewNode viewNode : viewNodes) {
			if (viewNode.getTablePerspectives().contains(tablePerspective))
				return true;
		}
		return false;
	}

}
