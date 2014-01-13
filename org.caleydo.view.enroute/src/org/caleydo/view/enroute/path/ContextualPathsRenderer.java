/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.view.MinSizeUpdateEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.LayoutRendererAdapter;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.IVertexRepSelectionListener;
import org.caleydo.datadomain.pathway.VertexRepBasedContextMenuItem;
import org.caleydo.datadomain.pathway.graph.PathSegment;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.ESampleMappingMode;
import org.caleydo.datadomain.pathway.listener.EnableFreePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.PathwayMappingEvent;
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.SampleMappingModeEvent;
import org.caleydo.datadomain.pathway.listener.ShowNodeContextEvent;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.enroute.event.ShowPathEvent;
import org.caleydo.view.pathway.v2.ui.PathwayElement;
import org.caleydo.view.pathway.v2.ui.PathwayTextureRepresentation;
import org.caleydo.view.pathway.v2.ui.augmentation.path.MultiplePathsAugmentation;
import org.caleydo.view.pathway.v2.ui.augmentation.path.MergedPathSegmentsAugmentation;

/**
 * Renderer that shows the alternative entrances
 *
 * @author Christian Partl
 *
 */
public class ContextualPathsRenderer extends ALayoutRenderer implements IPathwayRepresentation, IListenerOwner {

	protected final String BRANCH_PATH_EVENTSPACE = GeneralManager.get().getEventPublisher().createUniqueEventSpace();
	// protected final int PATHWAY_TEXTURE_HEIGHT = 100;

	protected LayoutManager layout;
	protected PathwayGraph pathway;
	protected String eventSpace;
	protected Row pathRow;
	protected Map<APathwayPathRenderer, ElementLayout> renderers = new LinkedHashMap<>();
	protected AGLView view;
	protected List<TablePerspective> tablePerspectives = new ArrayList<>();
	// protected GLPathway pathwayView;
	protected PathwayTextureRepresentation pathwayThumbnail;
	protected boolean isPathSelectionMode = false;
	protected boolean isFreePathSelectionMode = false;
	protected APathwayPathRenderer selectedPathRenderer;
	private BranchPathEventSpaceListener branchPathEventSpaceListener = new BranchPathEventSpaceListener();
	private VertexRepComparator comparator = new VertexRepComparator();
	private PathwayPath selectedPath = new PathwayPath();
	private ESampleMappingMode sampleMappingMode;
	private TablePerspective mappedPerspective;
	protected final boolean showThumbnail;

	private boolean isControlKeyPressed = false;
	private boolean isShiftKeyPressed = false;

	protected MultiplePathsAugmentation contextPathsAugmentation;
	protected MergedPathSegmentsAugmentation selectedPathAugmentation;

	/**
	 * Context menu items that shall be displayed when right-clicking on a path node.
	 */
	protected List<VertexRepBasedContextMenuItem> nodeContextMenuItems = new ArrayList<>();

	/**
	 * Events that shall be triggered when selecting a path node.
	 */
	// protected List<Pair<IVertexRepBasedEventFactory, PickingMode>> nodeEvents = new ArrayList<>();

	protected List<IVertexRepSelectionListener> vertexListeners = new ArrayList<>();

	private final EventListenerManager listeners = EventListenerManagers.wrap(this);

	// private boolean pathwayViewInitialized = false;
	private KeyListener keyListener = new KeyListener();

	/**
	 * The queue which holds the events
	 */
	private BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue = new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();

	public ContextualPathsRenderer(AGLView view, String eventSpace, PathwayGraph pathway,
			List<TablePerspective> tablePerspectives, boolean showThumbnail) {
		this.showThumbnail = showThumbnail;
		this.view = view;
		this.eventSpace = eventSpace;
		this.pathway = pathway;
		this.tablePerspectives = tablePerspectives;
		layout = new LayoutManager(new ViewFrustum(), view.getPixelGLConverter());
		layout.setUseDisplayLists(true);
		Column baseColumn = new Column();
		pathRow = new Row();
		baseColumn.setBottomUp(false);

		if (showThumbnail) {

			PathwayElement pathwayElement = new PathwayElement(eventSpace);
			pathwayThumbnail = new PathwayTextureRepresentation(pathway);

			pathwayElement.setPathwayRepresentation(pathwayThumbnail);

			contextPathsAugmentation = new MultiplePathsAugmentation(pathwayThumbnail);
			pathwayElement.addForegroundAugmentation(contextPathsAugmentation);

			selectedPathAugmentation = new MergedPathSegmentsAugmentation(pathwayThumbnail);
			selectedPathAugmentation.setColor(SelectionType.SELECTION.getColor());
			pathwayElement.addForegroundAugmentation(selectedPathAugmentation);

			LayoutRendererAdapter wrappingLayoutRenderer = new LayoutRendererAdapter(view,
					ResourceLocators.DATA_CLASSLOADER, pathwayElement, eventSpace);

			pathwayThumbnail.setWrappingLayoutRenderer(wrappingLayoutRenderer);
			pathwayThumbnail.setMinHeight(150);
			pathwayThumbnail.setMinHeight(150);

			ElementLayout pathwayThumbnailLayout = new ElementLayout();
			pathwayThumbnailLayout.setPixelSizeY((int) pathwayThumbnail.getMinHeight());
			baseColumn.add(pathwayThumbnailLayout);
			pathwayThumbnailLayout.setRenderer(wrappingLayoutRenderer);

			// PathwayTextureCreator creator = new PathwayTextureCreator();
			// pathwayView = (GLPathway) creator.create(view, pathway, tablePerspectives, null, eventSpace);
			//
			// ElementLayout pathwayTextureLayout = new ElementLayout();
			// pathwayTextureLayout.setPixelSizeY(pathwayView.getMinPixelHeight());
			//
			// ViewLayoutRenderer viewRenderer = new ViewLayoutRenderer(pathwayView);
			// baseColumn.add(pathwayTextureLayout);
			// pathwayTextureLayout.setRenderer(viewRenderer);
		}
		baseColumn.add(pathRow);
		layout.setBaseElementLayout(baseColumn);
		view.getParentGLCanvas().addKeyListener(keyListener);
	}

	@Override
	protected void renderContent(GL2 gl) {
		// if (!pathwayViewInitialized && showThumbnail) {
		// pathwayView.initRemote(gl, view, view.getGLMouseListener());
		// pathwayViewInitialized = true;
		// }
		layout.render(gl);

		// gl.glBegin(GL.GL_LINES);
		// gl.glVertex3f(0, 0, 0);
		// gl.glVertex3f(x, y, 0);
		// gl.glEnd();
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

	@Override
	public PathwayGraph getPathway() {
		return pathway;
	}

	@Override
	public List<PathwayGraph> getPathways() {
		List<PathwayGraph> pathways = new ArrayList<>(1);
		if (pathway != null)
			pathways.add(pathway);
		return pathways;
	}

	@Override
	public Rect getVertexRepBounds(PathwayVertexRep vertexRep) {
		if (selectedPathRenderer != null) {
			Rect bounds = getAbsolutePosition(selectedPathRenderer.getVertexRepBounds(vertexRep),
					renderers.get(selectedPathRenderer));
			if (bounds != null)
				return bounds;
		}
		for (Entry<APathwayPathRenderer, ElementLayout> entry : renderers.entrySet()) {
			APathwayPathRenderer renderer = entry.getKey();
			ElementLayout layout = entry.getValue();
			Rect bounds = getAbsolutePosition(renderer.getVertexRepBounds(vertexRep), layout);
			if (bounds != null)
				return bounds;
		}
		return null;
	}

	protected Rect getAbsolutePosition(Rect rect, ElementLayout layout) {
		if (rect == null || layout == null)
			return null;
		return new Rect(layout.getTranslateX() + rect.x(), y - layout.getTranslateY() - layout.getSizeScaledY()
				+ rect.y(), rect.width(), rect.height());
	}

	@Override
	public List<Rect> getVertexRepsBounds(PathwayVertexRep vertexRep) {
		List<Rect> allBounds = new ArrayList<>();

		for (Entry<APathwayPathRenderer, ElementLayout> entry : renderers.entrySet()) {
			APathwayPathRenderer renderer = entry.getKey();
			ElementLayout layout = entry.getValue();
			for (Rect bounds : renderer.getVertexRepsBounds(vertexRep)) {
				Rect absoluteBounds = getAbsolutePosition(bounds, layout);
				if (absoluteBounds != null)
					allBounds.add(absoluteBounds);
			}
		}
		return allBounds;
	}

	// @Override
	public void addVertexRepBasedContextMenuItem(VertexRepBasedContextMenuItem item) {
		nodeContextMenuItems.add(item);
		for (APathwayPathRenderer renderer : renderers.keySet()) {
			renderer.addVertexRepBasedContextMenuItem(item);
		}
	}

	/**
	 * Method that initializes the {@link ContextualPathsRenderer}. Shall be called once prior use.
	 */
	public void init() {
		registerEventListeners();
	}

	private APathwayPathRenderer addPath(PathwayPath pathSegments) {
		VerticalPathRenderer renderer = new VerticalPathRenderer(view, tablePerspectives);

		renderer.setUpdateStrategy(new FixedPathUpdateStrategy(renderer, eventSpace, isPathSelectionMode,
				isFreePathSelectionMode, this, selectedPath));
		renderer.pathwayPathEventSpace = eventSpace;
		// renderer.setTablePerspectives(tablePerspectives);
		renderer.setPathway(pathway);
		renderer.setBranchPathExtractionEventSpace(BRANCH_PATH_EVENTSPACE);
		renderer.setAllowBranchPathExtraction(true);
		renderer.setSampleMappingMode(sampleMappingMode);
		renderer.setMappedPerspective(mappedPerspective);

		renderer.setSizeConfig(PathSizeConfiguration.COMPACT);
		for (VertexRepBasedContextMenuItem item : nodeContextMenuItems) {
			renderer.addVertexRepBasedContextMenuItem(item);
		}

		// for (Pair<IVertexRepBasedEventFactory, PickingMode> eventPair : nodeEvents) {
		// renderer.addVertexRepBasedSelectionEvent(eventPair.getFirst(), eventPair.getSecond());
		// }
		for (IVertexRepSelectionListener listener : vertexListeners) {
			renderer.addVertexRepSelectionListener(listener);
		}

		ElementLayout layout = new ElementLayout();
		layout.setRenderer(renderer);
		pathRow.add(layout);

		renderer.init();
		renderer.setPath(pathSegments);
		layout.setDynamicSizeUnitsX(renderer.getMinWidthPixels());
		renderers.put(renderer, layout);
		return renderer;
	}

	@Override
	public void registerEventListeners() {
		listeners.register(this, eventSpace);
		listeners.register(branchPathEventSpaceListener, BRANCH_PATH_EVENTSPACE);
	}

	@Override
	public void unregisterEventListeners() {
		listeners.unregisterAll();

	}

	@Override
	public final synchronized void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		queue.add(new Pair<AEventListener<? extends IListenerOwner>, AEvent>(listener, event));
	}

	/**
	 * This method should be called every display cycle when it is save to change the state of the object. It processes
	 * all the previously submitted events.
	 */
	protected final void processEvents() {
		Pair<AEventListener<? extends IListenerOwner>, AEvent> pair;
		while (queue.peek() != null) {
			pair = queue.poll();
			pair.getFirst().handleEvent(pair.getSecond());
		}
	}

	@Override
	protected void prepare() {
		processEvents();
		// if (isDisplayListDirty()) {
		// layout.updateLayout();
		// }
	}

	/**
	 * @return the tablePerspectives, see {@link #tablePerspectives}
	 */
	public List<TablePerspective> getTablePerspectives() {
		return tablePerspectives;
	}

	/**
	 * @param tablePerspectives
	 *            setter, see {@link tablePerspectives}
	 */
	public void setTablePerspectives(List<TablePerspective> tablePerspectives) {
		this.tablePerspectives = tablePerspectives;
	}

	/**
	 * Removes the specified pathrenderer.
	 */
	protected void removePath(APathwayPathRenderer renderer) {
		pathRow.remove(renderers.get(renderer));
		renderers.remove(renderer);
		renderer.destroy(view.getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2());
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onShowNodeContext(ShowNodeContextEvent event) {
		if (event.getVertexRep().getType() == EPathwayVertexType.map)
			return;
		Set<PathwayVertexRep> vertexReps = PathwayManager.get().getEquivalentVertexRepsInPathway(event.getVertexRep(),
				pathway);
		if (event.getVertexRep().getPathway() == pathway) {
			vertexReps.add(event.getVertexRep());
		}

		Set<APathwayPathRenderer> renderersToRemove = new HashSet<>(renderers.keySet());
		for (PathwayVertexRep vertexRep : PathwayPath.flattenSegments(selectedPath)) {
			for (APathwayPathRenderer renderer : renderers.keySet()) {
				if (PathwayPath.containsVertexRep(renderer.pathSegments, vertexRep)) {
					renderersToRemove.remove(renderer);
				}
			}
		}

		for (PathwayVertexRep vertexRep : vertexReps) {
			boolean createNewPath = true;
			for (APathwayPathRenderer renderer : renderers.keySet()) {
				if (PathwayPath.containsVertexRep(renderer.pathSegments, vertexRep)) {
					renderersToRemove.remove(renderer);
					createNewPath = false;
					break;
				}
			}
			if (createNewPath) {
				// List<PathwayVertexRep> segment = PathwayManager.get().determineDirectionalPath(vertexRep, false, 5);
				PathSegment segment = new PathSegment(PathwayManager.get().determineDirectionalPath(vertexRep, false,
						4, comparator));
				segment.remove(0);
				Collections.reverse(segment);
				segment.addAll(PathwayManager.get().determineDirectionalPath(vertexRep, true, 4, comparator));
				// segment.addAll(PathwayManager.get().determineDirectionalPath(vertexRep, true, 5));
				PathwayPath pathSegments = new PathwayPath(1);
				pathSegments.add(segment);
				addPath(pathSegments);
			}
		}
		for (APathwayPathRenderer renderer : renderersToRemove) {
			removePath(renderer);
		}

		contextPathsChanged();
		// setDisplayListDirty(true);
		layout.updateLayout();
		// for (APathwayPathRenderer renderer : renderers.keySet()) {
		// renderer.updateLayout();
		// }
		triggerMinSizeUpdate();
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onAddTablePerspectives(AddTablePerspectivesEvent event) {
		for (TablePerspective tablePerspective : event.getTablePerspectives()) {
			if (!(tablePerspective.getDataDomain() instanceof GeneticDataDomain))
				continue;
			if (this.tablePerspectives.contains(tablePerspective))
				continue;

			this.tablePerspectives.add(tablePerspective);
		}
		// isPathSelectionMode = event.isPathSelectionMode();
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onRemoveTablePerspective(RemoveTablePerspectiveEvent event) {
		tablePerspectives.remove(event.getTablePerspective());
		// isPathSelectionMode = event.isPathSelectionMode();
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onEnablePathSelection(EnablePathSelectionEvent event) {
		isPathSelectionMode = event.isPathSelectionMode();
		if (isPathSelectionMode)
			isFreePathSelectionMode = false;
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onEnableFreePathSelection(EnableFreePathSelectionEvent event) {
		isFreePathSelectionMode = event.isEnabled();
		if (isFreePathSelectionMode)
			isPathSelectionMode = false;
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onSelectedPathChanged(PathwayPathSelectionEvent event) {
		selectedPath = event.getPath();
		// if (!selectedPath.hasPathway(pathway)) {
		// selectedPathRenderer = null;
		// return;
		// }
		if (selectedPathAugmentation != null) {
			selectedPathAugmentation.setPath(selectedPath);
		}

		PathwayPath selectedPathSegments = event.getPath();

		// if (isFreePathSelectionMode) {
		List<PathwayVertexRep> selectedPathVertexReps = PathwayPath.flattenSegments(selectedPathSegments);
		boolean allVerticesShown = true;
		for (PathwayVertexRep vertexRep : selectedPathVertexReps) {
			if (vertexRep.getPathway() == pathway) {
				boolean vertexShown = false;
				for (APathwayPathRenderer renderer : renderers.keySet()) {
					List<PathwayVertexRep> currentPathVertexReps = PathwayPath.flattenSegments(renderer.pathSegments);
					if (currentPathVertexReps.contains(vertexRep)) {
						vertexShown = true;
						break;
					}
				}
				if (!vertexShown) {
					allVerticesShown = false;
					break;
				}
			}
		}
		if (allVerticesShown)
			return;
		// } else {
		// boolean isSelectedPathShown = false;
		// if (selectedPathRenderer != null) {
		// isSelectedPathShown = PathUtil.isPathShown(selectedPathRenderer.pathSegments, selectedPathSegments,
		// pathway);
		// if (isSelectedPathShown)
		// return;
		// }
		//
		// for (APathwayPathRenderer renderer : renderers.keySet()) {
		// isSelectedPathShown = PathUtil.isPathShown(renderer.pathSegments, selectedPathSegments, pathway);
		// if (isSelectedPathShown) {
		// selectedPathRenderer = renderer;
		// return;
		// }
		// }
		// }

		APathwayPathRenderer pathRendererWithMostEqualNodes = null;
		int maxEqualVertices = 0;
		int selectedPathRendererEqualVertices = 0;
		for (APathwayPathRenderer renderer : renderers.keySet()) {
			int numEqualVertices = PathwayPath.getNumEqualVertices(renderer.pathSegments, selectedPathSegments);
			if (maxEqualVertices < numEqualVertices) {
				pathRendererWithMostEqualNodes = renderer;
				maxEqualVertices = numEqualVertices;
			}
			if (renderer == selectedPathRenderer) {
				selectedPathRendererEqualVertices = numEqualVertices;
			}
		}
		if (pathRendererWithMostEqualNodes == null) {
			selectedPathRenderer = addPath(selectedPathSegments);
		} else {
			// We want the selected path renderer to stay the same if possible.
			if (selectedPathRendererEqualVertices >= maxEqualVertices) {
				pathRendererWithMostEqualNodes = selectedPathRenderer;
			}
			pathRendererWithMostEqualNodes.setPath(selectedPathSegments);
		}

		triggerMinSizeUpdate();

	}

	@ListenTo
	public void onSampleMappingModeChanged(SampleMappingModeEvent event) {
		sampleMappingMode = event.getSampleMappingMode();
	}

	@ListenTo
	public void onPathwayMappingChanged(PathwayMappingEvent event) {
		mappedPerspective = event.getTablePerspective();
	}

	@ListenTo
	public void onMinSizeUpdate(MinSizeUpdateEvent event) {
		ElementLayout layout = renderers.get(event.getMinSizeObject());
		if (layout != null) {
			APathwayPathRenderer renderer = (APathwayPathRenderer) event.getMinSizeObject();
			layout.setDynamicSizeUnitsX(renderer.getMinWidthPixels());
		}
	}

	private void triggerMinSizeUpdate() {
		MinSizeUpdateEvent e = new MinSizeUpdateEvent(this, getMinWidthPixels(), getMinHeightPixels());
		e.setEventSpace(eventSpace);
		EventPublisher.INSTANCE.triggerEvent(e);
	}

	@Override
	public boolean isDisplayListDirty() {
		// if (super.isDisplayListDirty())
		// return true;
		for (APathwayPathRenderer renderer : renderers.keySet()) {
			if (renderer.isDisplayListDirty())
				return true;
		}
		return false;
	}

	@Override
	public void setLimits(float x, float y) {
		super.setLimits(x, y);
		ViewFrustum viewFrustum = new ViewFrustum();
		viewFrustum.setLeft(0);
		viewFrustum.setBottom(0);
		viewFrustum.setRight(x);
		viewFrustum.setTop(y);
		layout.setViewFrustum(viewFrustum);
	}

	/**
	 * @param eventSpace
	 *            setter, see {@link eventSpace}
	 */
	public void setEventSpace(String eventSpace) {
		this.eventSpace = eventSpace;
	}

	/**
	 * @return the eventSpace, see {@link #eventSpace}
	 */
	public String getEventSpace() {
		return eventSpace;
	}

	/**
	 * @return the selectedPathRenderer, see {@link #selectedPathRenderer}
	 */
	public APathwayPathRenderer getSelectedPathRenderer() {
		return selectedPathRenderer;
	}

	private class BranchPathEventSpaceListener {

		@ListenTo(restrictExclusiveToEventSpace = true)
		protected void onShowBranchPath(ShowPathEvent event) {
			boolean isPathShown = false;
			for (APathwayPathRenderer renderer : renderers.keySet()) {
				isPathShown = PathwayPath.isPathShown(renderer.pathSegments, event.getPathSegments(), pathway);
				if (renderer.expandedBranchSummaryNode != null) {
					renderer.expandedBranchSummaryNode.setCollapsed(true);
					renderer.setExpandedBranchSummaryNode(null);
				}
			}
			if (!isPathShown)
				addPath(event.getPathSegments());
		}
	}

	@Override
	public int getMinHeightPixels() {
		int maxMinPixelHeight = 0;
		for (APathwayPathRenderer renderer : renderers.keySet()) {
			int minPixelHeight = renderer.getMinHeightPixels();
			if (minPixelHeight > maxMinPixelHeight) {
				maxMinPixelHeight = minPixelHeight;
			}
		}
		return (showThumbnail ? ((int) pathwayThumbnail.getMinHeight()) : 1) + maxMinPixelHeight + 5;
	}

	@Override
	public int getMinWidthPixels() {
		int totalWidth = 0;
		for (APathwayPathRenderer renderer : renderers.keySet()) {
			totalWidth += renderer.getMinWidthPixels();
		}
		// 2 * (65 + 50)
		return Math.max(
				Math.max(showThumbnail ? ((int) pathwayThumbnail.getMinHeight()) : 2 * (65 + 50), 2 * (65 + 50)),
				totalWidth);
	}

	protected class VertexRepComparator implements Comparator<PathwayVertexRep> {

		@Override
		public int compare(PathwayVertexRep o1, PathwayVertexRep o2) {
			float sumStdDev1 = 0;
			float sumStdDev2 = 0;
			for (TablePerspective tablePerspective : tablePerspectives) {
				sumStdDev1 += o1.calcAverage(tablePerspective).getStandardDeviation();
				sumStdDev2 += o2.calcAverage(tablePerspective).getStandardDeviation();
			}
			if (sumStdDev1 > sumStdDev2)
				return 1;
			if (sumStdDev1 < sumStdDev2)
				return -1;
			return 0;
		}
	}

	// @Override
	// public void addVertexRepBasedSelectionEvent(IVertexRepBasedEventFactory eventFactory, PickingMode pickingMode) {
	// nodeEvents.add(new Pair<IVertexRepBasedEventFactory, PickingMode>(eventFactory, pickingMode));
	// for (APathwayPathRenderer renderer : renderers.keySet()) {
	// renderer.addVertexRepBasedSelectionEvent(eventFactory, pickingMode);
	// }
	//
	// }

	private class KeyListener implements IGLKeyListener {

		@Override
		public void keyPressed(IKeyEvent e) {
			isControlKeyPressed = e.isControlDown();
			isShiftKeyPressed = e.isShiftDown();
		}

		@Override
		public void keyReleased(IKeyEvent e) {
			isControlKeyPressed = e.isControlDown();
			isShiftKeyPressed = e.isShiftDown();
		}

	}

	@Override
	public void destroy(GL2 gl) {
		super.destroy(gl);
		layout.destroy(gl);
		view.getParentGLCanvas().removeKeyListener(keyListener);
	}

	/**
	 * @return the isControlKeyPressed, see {@link #isControlKeyPressed}
	 */
	public boolean isControlKeyPressed() {
		return isControlKeyPressed;
	}

	/**
	 * @return the isShiftKeyPressed, see {@link #isShiftKeyPressed}
	 */
	public boolean isShiftKeyPressed() {
		return isShiftKeyPressed;
	}

	public void contextPathsChanged() {
		// List<List<PathwayVertexRep>> contextPaths = new ArrayList<>();
		if (contextPathsAugmentation != null) {
			contextPathsAugmentation.clearPaths();
			for (APathwayPathRenderer renderer : renderers.keySet()) {
				contextPathsAugmentation.addPath(renderer.getPathSegments());
				// List<PathwayVertexRep> flattenedPath = PathwayPath.flattenSegments(renderer.getPathSegments());
				// if (flattenedPath != null && !flattenedPath.isEmpty()) {
				// contextPaths.add(flattenedPath);
				// }
			}
			// pathwayView.setContextPaths(contextPaths);
		}
	}

	@Override
	public void addVertexRepSelectionListener(IVertexRepSelectionListener listener) {
		vertexListeners.add(listener);
	}

	@Override
	public Rect getPathwayBounds() {
		return new Rect(0, 0, view.getPixelGLConverter().getPixelWidthForGLWidth(elementLayout.getSizeScaledX()), view
				.getPixelGLConverter().getPixelHeightForGLHeight(elementLayout.getSizeScaledY()));
	}

	@Override
	public GLElement asGLElement() {
		return null;
	}

	@Override
	public AGLView asAGLView() {
		return null;
	}

	@Override
	public ALayoutRenderer asLayoutRenderer() {
		return this;
	}

	@Override
	public float getMinWidth() {
		return getMinWidthPixels();
	}

	@Override
	public float getMinHeight() {
		return getMinHeightPixels();
	}

}
