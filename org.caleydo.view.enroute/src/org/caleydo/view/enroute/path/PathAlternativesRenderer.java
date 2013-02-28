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
import java.util.List;
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
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.VertexRepBasedContextMenuItem;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.ShowPortalNodesEvent;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

/**
 * Renderer that shows the alternative entrances
 *
 * @author Christian Partl
 *
 */
public class PathAlternativesRenderer extends ALayoutRenderer implements IPathwayRepresentation, IListenerOwner {

	protected final String ALTERNATIVES_EVENTSPACE = GeneralManager.get().getEventPublisher().createUniqueEventSpace();

	protected LayoutManager layout;
	protected PathwayGraph pathway;
	protected String eventSpace;
	protected Row baseRow;
	protected List<APathwayPathRenderer> renderers = new ArrayList<>();
	protected AGLView view;
	protected List<TablePerspective> tablePerspectives = new ArrayList<>();

	private final EventListenerManager listeners = EventListenerManagers.wrap(this);

	/**
	 * The queue which holds the events
	 */
	private BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue = new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();

	public PathAlternativesRenderer(AGLView view) {
		this.view = view;
		layout = new LayoutManager(new ViewFrustum(), view.getPixelGLConverter());
		baseRow = new Row();
		layout.setBaseElementLayout(baseRow);

	}

	@Override
	protected void renderContent(GL2 gl) {
		layout.render(gl);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
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

		return null;
	}

	@Override
	public List<Rectangle2D> getVertexRepsBounds(PathwayVertexRep vertexRep) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addVertexRepBasedContextMenuItem(VertexRepBasedContextMenuItem item) {
		// TODO Auto-generated method stub

	}

	/**
	 * Method that initializes the {@link PathAlternativesRenderer}. Shall be called once prior use.
	 */
	public void init() {
		registerEventListeners();
	}

	private void addAlternative(List<List<PathwayVertexRep>> pathSegments) {
		VerticalPathRenderer renderer = new VerticalPathRenderer(view, tablePerspectives);
		renderer.setUpdateStrategy(new FixedPathUpdateStrategy(renderer, eventSpace));

		if (tablePerspectives.size() > 0) {
			renderer.setPathway(pathway);
		}

		renderer.setSizeConfig(PathSizeConfiguration.COMPACT);

		ElementLayout layout = new ElementLayout();
		layout.setDynamicSizeUnitsX(1);
		layout.setRenderer(renderer);
		baseRow.add(layout);

		renderer.init();
		renderer.setPath(pathSegments);
		renderers.add(renderer);
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

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onShowPortalNodes(ShowPortalNodesEvent event) {
		Set<PathwayVertexRep> vertexReps = PathwayManager.get().getEquivalentVertexRepsInPathway(event.getVertexRep(),
				pathway);
		baseRow.removeAll();

		for (APathwayPathRenderer renderer : renderers) {
			renderer.destroy(view.getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2());
		}
		renderers.clear();

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

	@Override
	public boolean isDisplayListDirty() {
		if (isDisplayListDirty)
			return true;
		for (APathwayPathRenderer renderer : renderers) {
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
	 * @param pathway
	 *            setter, see {@link pathway}
	 */
	public void setPathway(PathwayGraph pathway) {
		this.pathway = pathway;
	}

}
