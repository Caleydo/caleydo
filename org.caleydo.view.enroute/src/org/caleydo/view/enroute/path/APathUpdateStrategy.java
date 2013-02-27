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
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;
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

	@ListenTo(restrictExclusiveToEventSpace = true)
	public abstract void onEnablePathSelection(EnablePathSelectionEvent event);

	@ListenTo(restrictExclusiveToEventSpace = true)
	public abstract void onSelectedPathChanged(PathwayPathSelectionEvent event);

	public abstract void triggerPathUpdate();

	public abstract void nodesCreated();

}
