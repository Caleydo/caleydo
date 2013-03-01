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
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.VertexRepBasedContextMenuItem;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.ShowPortalNodesEvent;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.pathway.GLPathway;

/**
 * Renderer that shows the alternative entrances
 *
 * @author Christian Partl
 *
 */
public class ContextualPathsRenderer extends ALayoutRenderer implements IPathwayRepresentation, IListenerOwner {

	protected final String ALTERNATIVES_EVENTSPACE = GeneralManager.get().getEventPublisher().createUniqueEventSpace();

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

	/**
	 * Context menu items that shall be displayed when right-clicking on a path node.
	 */
	protected List<VertexRepBasedContextMenuItem> nodeContextMenuItems = new ArrayList<>();

	private final EventListenerManager listeners = EventListenerManagers.wrap(this);

	private boolean pathwayViewInitialized = false;

	/**
	 * The queue which holds the events
	 */
	private BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue = new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();

	public ContextualPathsRenderer(AGLView view, String eventSpace, PathwayGraph pathway,
			List<TablePerspective> tablePerspectives) {
		this.view = view;
		this.eventSpace = eventSpace;
		this.pathway = pathway;
		layout = new LayoutManager(new ViewFrustum(), view.getPixelGLConverter());
		layout.setUseDisplayLists(true);
		Column baseColumn = new Column();
		pathRow = new Row();
		ElementLayout pathwayTextureLayout = new ElementLayout();
		pathwayTextureLayout.setPixelSizeY(100);

		// This should probably be a renderer in the final version.
		pathwayView = (GLPathway) GeneralManager
				.get()
				.getViewManager()
				.createGLView(GLPathway.class, view.getParentGLCanvas(), view.getParentComposite(),
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1, -1, 1));

		if (tablePerspectives.size() > 0) {
			TablePerspective tablePerspective = tablePerspectives.get(0);
			if (!(tablePerspective instanceof PathwayTablePerspective)) {
				throw new IllegalArgumentException(
						"The provided table perspective must be of type PathwayTablePerspective.");
			}

			pathwayView.setRemoteRenderingGLView((IGLRemoteRenderingView) view);
			pathwayView.setDataDomain(tablePerspective.getDataDomain());
			pathwayView.setTablePerspective(tablePerspective);
		}
		pathwayView.setPathwayPathEventSpace(eventSpace);
		pathwayView.initialize();

		ViewLayoutRenderer viewRenderer = new ViewLayoutRenderer(pathwayView);
		baseColumn.setBottomUp(false);
		baseColumn.add(pathwayTextureLayout);
		baseColumn.add(pathRow);
		pathwayTextureLayout.setRenderer(viewRenderer);
		layout.setBaseElementLayout(baseColumn);

	}

	@Override
	protected void renderContent(GL2 gl) {
		if (!pathwayViewInitialized) {
			pathwayView.initRemote(gl, view, view.getGLMouseListener());
			pathwayViewInitialized = true;
		}
		layout.render(gl);
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

	private APathwayPathRenderer addAlternative(List<List<PathwayVertexRep>> pathSegments) {
		VerticalPathRenderer renderer = new VerticalPathRenderer(view, tablePerspectives);

		renderer.setUpdateStrategy(new FixedPathUpdateStrategy(renderer, eventSpace, isPathSelectionMode));
		renderer.setTablePerspectives(tablePerspectives);
		renderer.setPathway(pathway);

		renderer.setSizeConfig(PathSizeConfiguration.COMPACT);
		for (VertexRepBasedContextMenuItem item : nodeContextMenuItems) {
			renderer.addVertexRepBasedContextMenuItem(item);
		}

		ElementLayout layout = new ElementLayout();
		layout.setDynamicSizeUnitsX(1);
		layout.setRenderer(renderer);
		pathRow.add(layout);

		renderer.init();
		renderer.setPath(pathSegments);
		renderers.put(renderer, layout);
		return renderer;
	}

	@Override
	public void registerEventListeners() {
		listeners.register(this, eventSpace);

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
		if (isDisplayListDirty()) {
			layout.updateLayout();
		}
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
	 * Removes all paths except the one that shows the currently selected path.
	 */
	protected void removeContextPaths() {

		ElementLayout selectedPathLayout = renderers.get(selectedPathRenderer);
		for (ElementLayout layout : renderers.values()) {
			if (layout != selectedPathLayout) {
				pathRow.remove(layout);
			}
		}

		for (APathwayPathRenderer renderer : renderers.keySet()) {
			if (renderer != selectedPathRenderer)
				renderer.destroy(view.getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2());
		}
		renderers.clear();
		if (selectedPathRenderer != null) {
			renderers.put(selectedPathRenderer, selectedPathLayout);
		}
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onShowPortalNodes(ShowPortalNodesEvent event) {
		Set<PathwayVertexRep> vertexReps = PathwayManager.get().getEquivalentVertexRepsInPathway(event.getVertexRep(),
				pathway);
		removeContextPaths();

		for (PathwayVertexRep vertexRep : vertexReps) {
			List<PathwayVertexRep> segment = PathwayManager.get().determineDirectionalPath(vertexRep, false, 5);
			segment.remove(0);
			Collections.reverse(segment);
			segment.addAll(PathwayManager.get().determineDirectionalPath(vertexRep, true, 5));
			List<List<PathwayVertexRep>> pathSegments = new ArrayList<>(1);
			pathSegments.add(segment);
			addAlternative(pathSegments);
		}
		setDisplayListDirty();
		layout.updateLayout();
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onEnablePathSelection(EnablePathSelectionEvent event) {
		isPathSelectionMode = event.isPathSelectionMode();
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onSelectedPathChanged(PathwayPathSelectionEvent event) {
		if (!isPathFromPathway(event.getPathSegments(), pathway)) {
			selectedPathRenderer = null;
			return;
		}

		List<List<PathwayVertexRep>> selectedPathSegments = event.getPathSegmentsAsVertexList();

		boolean isSelectedPathShown = false;

		for (APathwayPathRenderer renderer : renderers.keySet()) {
			isSelectedPathShown = renderer.isPathShown(selectedPathSegments);
			if (isSelectedPathShown) {
				selectedPathRenderer = renderer;
				return;
			}
		}

		APathwayPathRenderer pathRendererWithMostEqualNodes = null;
		int maxEqualVertices = 0;
		for (APathwayPathRenderer renderer : renderers.keySet()) {
			int numEqualVertices = renderer.getNumEqualVertices(selectedPathSegments);
			if (maxEqualVertices < numEqualVertices) {
				pathRendererWithMostEqualNodes = renderer;
				maxEqualVertices = numEqualVertices;
			}
		}
		if (pathRendererWithMostEqualNodes == null) {
			selectedPathRenderer = addAlternative(selectedPathSegments);
		} else {
			pathRendererWithMostEqualNodes.setPath(selectedPathSegments);
		}
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
		if (isDisplayListDirty)
			return true;
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

}
