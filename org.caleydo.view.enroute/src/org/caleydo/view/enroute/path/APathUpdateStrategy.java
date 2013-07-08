/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.path;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.PathwayMappingEvent;
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;
import org.caleydo.view.pathway.event.SampleMappingModeEvent;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.GraphPathImpl;

/**
 * Strategy that defines how a path reacts to path selections.
 *
 * @author Christian Partl
 *
 */
public abstract class APathUpdateStrategy implements IListenerOwner {

	protected final APathwayPathRenderer renderer;
	protected String pathwayPathEventSpace;

	private final EventListenerManager listeners = EventListenerManagers.wrap(this);

	/**
	 * The queue which holds the events
	 */
	private BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue = new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();

	public APathUpdateStrategy(APathwayPathRenderer renderer, String pathwayPathEventSpace) {
		this.renderer = renderer;
		this.pathwayPathEventSpace = pathwayPathEventSpace;
	}

	@Override
	public final synchronized void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		queue.add(new Pair<AEventListener<? extends IListenerOwner>, AEvent>(listener, event));
	}

	/**
	 * This method should be called every display cycle when it is save to change the state of the object. It processes
	 * all the previously submitted events.
	 */
	public final void processEvents() {
		Pair<AEventListener<? extends IListenerOwner>, AEvent> pair;
		while (queue.peek() != null) {
			pair = queue.poll();
			pair.getFirst().handleEvent(pair.getSecond());
		}
	}

	@Override
	public void registerEventListeners() {
		listeners.register(this, pathwayPathEventSpace);
	}

	@Override
	public void unregisterEventListeners() {
		listeners.unregisterAll();
	}

	/**
	 * @param pathwayPathEventSpace
	 *            setter, see {@link pathwayPathEventSpace}
	 */
	public void setPathwayPathEventSpace(String pathwayPathEventSpace) {
		this.pathwayPathEventSpace = pathwayPathEventSpace;
	}

	protected void triggerPathUpdate(List<List<PathwayVertexRep>> pathSegments) {
		List<PathwayPath> segments = new ArrayList<>(pathSegments.size());
		for (List<PathwayVertexRep> segment : pathSegments) {
			PathwayVertexRep startVertexRep = segment.get(0);
			PathwayVertexRep endVertexRep = segment.get(segment.size() - 1);
			List<DefaultEdge> edges = new ArrayList<DefaultEdge>();
			PathwayGraph pathway = startVertexRep.getPathway();

			for (int i = 0; i < segment.size() - 1; i++) {
				PathwayVertexRep currentVertexRep = segment.get(i);
				PathwayVertexRep nextVertexRep = segment.get(i + 1);

				DefaultEdge edge = pathway.getEdge(currentVertexRep, nextVertexRep);
				if (edge == null)
					edge = pathway.getEdge(nextVertexRep, currentVertexRep);
				edges.add(edge);
			}
			GraphPath<PathwayVertexRep, DefaultEdge> graphPath = new GraphPathImpl<PathwayVertexRep, DefaultEdge>(
					pathway, startVertexRep, endVertexRep, edges, edges.size());

			segments.add(new PathwayPath(graphPath));
		}

		PathwayPathSelectionEvent event = new PathwayPathSelectionEvent();
		event.setEventSpace(pathwayPathEventSpace);
		event.setPathSegments(segments);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	@ListenTo
	public void onAddTablePerspectives(AddTablePerspectivesEvent event) {
		renderer.addTablePerspectives(event.getTablePerspectives());
	}


	@ListenTo
	public void onRemoveTablePerspectives(RemoveTablePerspectiveEvent event) {
		renderer.tablePerspectives.remove(event.getTablePerspective());
		renderer.setLayoutDirty(true);
	}

	@ListenTo
	public void onSampleMappingModeChanged(SampleMappingModeEvent event) {
		renderer.sampleMappingMode = event.getSampleMappingMode();
		renderer.setLayoutDirty(true);
	}

	@ListenTo
	public void onPathwayMappingChanged(PathwayMappingEvent event) {
		renderer.mappedPerspective = event.getTablePerspective();
		renderer.setLayoutDirty(true);
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public abstract void onEnablePathSelection(EnablePathSelectionEvent event);

	@ListenTo(restrictExclusiveToEventSpace = true)
	public abstract void onSelectedPathChanged(PathwayPathSelectionEvent event);

	/**
	 * Triggers events to indicate a path update
	 */
	public abstract void triggerPathUpdate();

	/**
	 * This method shall be called, when all nodes of the associated {@link APathwayPathRenderer} are created.
	 */
	public abstract void nodesCreated();

	/**
	 * Tells whether a change to the path that was triggered by the path is permitted or not.
	 *
	 * @param newPath
	 * @return
	 */
	public abstract boolean isPathChangePermitted(List<List<PathwayVertexRep>> newPath);

}
