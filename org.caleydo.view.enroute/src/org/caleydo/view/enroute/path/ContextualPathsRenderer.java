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
package org.caleydo.view.enroute.path;

import java.awt.geom.Rectangle2D;
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
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.IVertexRepBasedEventFactory;
import org.caleydo.datadomain.pathway.VertexRepBasedContextMenuItem;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.PathwayMappingEvent;
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.ShowNodeContextEvent;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.enroute.event.ShowPathEvent;
import org.caleydo.view.pathway.ESampleMappingMode;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.PathwayTextureCreator;
import org.caleydo.view.pathway.event.SampleMappingModeEvent;

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
	protected GLPathway pathwayView;
	protected boolean isPathSelectionMode = false;
	protected APathwayPathRenderer selectedPathRenderer;
	private BranchPathEventSpaceListener branchPathEventSpaceListener = new BranchPathEventSpaceListener();
	private VertexRepComparator comparator = new VertexRepComparator();
	private List<List<PathwayVertexRep>> selectedPathSegments = new ArrayList<>();
	private ESampleMappingMode sampleMappingMode;
	private TablePerspective mappedPerspective;
	protected final boolean showThumbnail;

	/**
	 * Context menu items that shall be displayed when right-clicking on a path node.
	 */
	protected List<VertexRepBasedContextMenuItem> nodeContextMenuItems = new ArrayList<>();

	/**
	 * Events that shall be triggered when selecting a path node.
	 */
	protected List<Pair<IVertexRepBasedEventFactory, PickingMode>> nodeEvents = new ArrayList<>();

	private final EventListenerManager listeners = EventListenerManagers.wrap(this);

	private boolean pathwayViewInitialized = false;

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
			PathwayTextureCreator creator = new PathwayTextureCreator();
			pathwayView = (GLPathway) creator.createRemoteView(view, tablePerspectives, eventSpace);

			ElementLayout pathwayTextureLayout = new ElementLayout();
			pathwayTextureLayout.setPixelSizeY(pathwayView.getMinPixelHeight());

			ViewLayoutRenderer viewRenderer = new ViewLayoutRenderer(pathwayView);
			baseColumn.add(pathwayTextureLayout);
			pathwayTextureLayout.setRenderer(viewRenderer);
		}
		baseColumn.add(pathRow);
		layout.setBaseElementLayout(baseColumn);

	}

	@Override
	protected void renderContent(GL2 gl) {
		if (!pathwayViewInitialized && showThumbnail) {
			pathwayView.initRemote(gl, view, view.getGLMouseListener());
			pathwayViewInitialized = true;
		}
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
	public Rectangle2D getVertexRepBounds(PathwayVertexRep vertexRep) {
		if (selectedPathRenderer != null) {
			Rectangle2D bounds = getAbsolutePosition(selectedPathRenderer.getVertexRepBounds(vertexRep),
					renderers.get(selectedPathRenderer));
			if (bounds != null)
				return bounds;
		}
		for (Entry<APathwayPathRenderer, ElementLayout> entry : renderers.entrySet()) {
			APathwayPathRenderer renderer = entry.getKey();
			ElementLayout layout = entry.getValue();
			Rectangle2D bounds = getAbsolutePosition(renderer.getVertexRepBounds(vertexRep), layout);
			if (bounds != null)
				return bounds;
		}
		return null;
	}

	protected Rectangle2D getAbsolutePosition(Rectangle2D rect, ElementLayout layout) {
		if (rect == null || layout == null)
			return null;
		return new Rectangle2D.Float(layout.getTranslateX() + (float) rect.getMinX(), y - layout.getTranslateY()
				- layout.getSizeScaledY() + (float) rect.getMinY(), (float) rect.getWidth(), (float) rect.getHeight());
	}

	@Override
	public List<Rectangle2D> getVertexRepsBounds(PathwayVertexRep vertexRep) {
		List<Rectangle2D> allBounds = new ArrayList<>();

		for (Entry<APathwayPathRenderer, ElementLayout> entry : renderers.entrySet()) {
			APathwayPathRenderer renderer = entry.getKey();
			ElementLayout layout = entry.getValue();
			for (Rectangle2D bounds : renderer.getVertexRepsBounds(vertexRep)) {
				Rectangle2D absoluteBounds = getAbsolutePosition(bounds, layout);
				if (absoluteBounds != null)
					allBounds.add(absoluteBounds);
			}
		}
		return allBounds;
	}

	@Override
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

	private APathwayPathRenderer addPath(List<List<PathwayVertexRep>> pathSegments) {
		VerticalPathRenderer renderer = new VerticalPathRenderer(view, tablePerspectives);

		renderer.setUpdateStrategy(new FixedPathUpdateStrategy(renderer, eventSpace, isPathSelectionMode, this,
				selectedPathSegments));
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

		for (Pair<IVertexRepBasedEventFactory, PickingMode> eventPair : nodeEvents) {
			renderer.addVertexRepBasedSelectionEvent(eventPair.getFirst(), eventPair.getSecond());
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
		renderersToRemove.remove(selectedPathRenderer);

		for (PathwayVertexRep vertexRep : vertexReps) {
			boolean createNewPath = true;
			for (APathwayPathRenderer renderer : renderers.keySet()) {
				if (PathUtil.containsVertexRep(renderer.pathSegments, vertexRep)) {
					renderersToRemove.remove(renderer);
					createNewPath = false;
					break;
				}
			}
			if (createNewPath) {
				// List<PathwayVertexRep> segment = PathwayManager.get().determineDirectionalPath(vertexRep, false, 5);
				List<PathwayVertexRep> segment = PathwayManager.get().determineDirectionalPath(vertexRep, false, 4,
						comparator);
				segment.remove(0);
				Collections.reverse(segment);
				segment.addAll(PathwayManager.get().determineDirectionalPath(vertexRep, true, 4, comparator));
				// segment.addAll(PathwayManager.get().determineDirectionalPath(vertexRep, true, 5));
				List<List<PathwayVertexRep>> pathSegments = new ArrayList<>(1);
				pathSegments.add(segment);
				addPath(pathSegments);
			}
		}
		for (APathwayPathRenderer renderer : renderersToRemove) {
			removePath(renderer);
		}

		// setDisplayListDirty(true);
		layout.updateLayout();
		// for (APathwayPathRenderer renderer : renderers.keySet()) {
		// renderer.updateLayout();
		// }
		triggerMinSizeUpdate();
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onAddTablePerspectives(AddTablePerspectivesEvent event) {
		tablePerspectives.addAll(event.getTablePerspectives());
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
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onSelectedPathChanged(PathwayPathSelectionEvent event) {
		selectedPathSegments = event.getPathSegmentsAsVertexList();
		if (!isPathFromPathway(event.getPathSegments(), pathway)) {
			selectedPathRenderer = null;
			return;
		}

		List<List<PathwayVertexRep>> selectedPathSegments = event.getPathSegmentsAsVertexList();

		boolean isSelectedPathShown = false;
		if (selectedPathRenderer != null) {
			isSelectedPathShown = PathUtil
					.isPathShown(selectedPathRenderer.pathSegments, selectedPathSegments, pathway);
			if (isSelectedPathShown)
				return;
		}

		for (APathwayPathRenderer renderer : renderers.keySet()) {
			isSelectedPathShown = PathUtil.isPathShown(renderer.pathSegments, selectedPathSegments, pathway);
			if (isSelectedPathShown) {
				selectedPathRenderer = renderer;
				return;
			}
		}

		APathwayPathRenderer pathRendererWithMostEqualNodes = null;
		int maxEqualVertices = 0;
		int selectedPathRendererEqualVertices = 0;
		for (APathwayPathRenderer renderer : renderers.keySet()) {
			int numEqualVertices = PathUtil.getNumEqualVertices(renderer.pathSegments, selectedPathSegments);
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

	private boolean isPathFromPathway(List<PathwayPath> pathSegments, PathwayGraph pathway) {
		for (PathwayPath path : pathSegments) {
			if (path.getPathway() == pathway) {
				return true;
			}
		}
		return false;
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
				isPathShown = PathUtil.isPathShown(renderer.pathSegments, event.getPathSegments(), pathway);
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
		return (showThumbnail ? (pathwayView.getMinPixelHeight()) : 1) + maxMinPixelHeight + 1;
	}

	@Override
	public int getMinWidthPixels() {
		int totalWidth = 0;
		for (APathwayPathRenderer renderer : renderers.keySet()) {
			totalWidth += renderer.getMinWidthPixels();
		}
		// 2 * (65 + 50)
		return Math.max(Math.max(showThumbnail ? (pathwayView.getMinPixelWidth()) : 2 * (65 + 50), 2 * (65 + 50)),
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

	@Override
	public void addVertexRepBasedSelectionEvent(IVertexRepBasedEventFactory eventFactory, PickingMode pickingMode) {
		nodeEvents.add(new Pair<IVertexRepBasedEventFactory, PickingMode>(eventFactory, pickingMode));
		for (APathwayPathRenderer renderer : renderers.keySet()) {
			renderer.addVertexRepBasedSelectionEvent(eventFactory, pickingMode);
		}

	}

}
