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
package org.caleydo.core.util.clusterer.algorithm;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.data.ClustererCanceledEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.ClustererCanceledListener;
import org.caleydo.core.util.clusterer.IClusterer;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.collection.Pair;

/**
 * Abstract base class for clusterers that handles external events
 *
 * @author Alexander Lex
 */
public abstract class AClusterer
	implements IClusterer {

	private BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue;
	private ClustererCanceledListener clustererCanceledListener;

	protected boolean isClusteringCanceled = false;

	// variables needed for correct visualization of cluster progress bar
	protected int iProgressBarMultiplier;
	protected int iProgressBarOffsetValue;

	protected VirtualArray recordVA;
	protected VirtualArray dimensionVA;

	protected ClusterConfiguration clusterState;

	public AClusterer() {
		queue = new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();

	}

	public void setClusterState(ClusterConfiguration clusterState) {
		this.clusterState = clusterState;
		this.recordVA = clusterState.getSourceRecordPerspective().getVirtualArray();
		this.dimensionVA = clusterState.getSourceDimensionPerspective().getVirtualArray();
	}

	/**
	 * Call this when the object is ready for the garbage collector
	 */
	public void destroy() {
		unregisterEventListeners();
	}

	@Override
	public void cancel() {
		isClusteringCanceled = true;
	}

	@Override
	public void registerEventListeners() {
		clustererCanceledListener = new ClustererCanceledListener();
		clustererCanceledListener.setHandler(this);
		GeneralManager.get().getEventPublisher()
			.addListener(ClustererCanceledEvent.class, clustererCanceledListener);

	}

	@Override
	public void unregisterEventListeners() {

		if (clustererCanceledListener != null) {
			GeneralManager.get().getEventPublisher().removeListener(clustererCanceledListener);
			clustererCanceledListener = null;
		}
	}

	/**
	 * This method should be called every display cycle when it is save to change the state of the object. It
	 * processes all the previously submitted events.
	 */
	public final void processEvents() {
		Pair<AEventListener<? extends IListenerOwner>, AEvent> pair;
		while (queue.peek() != null) {
			pair = queue.poll();
			pair.getFirst().handleEvent(pair.getSecond());
		}
	}

	@Override
	public void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		queue.add(new Pair<AEventListener<? extends IListenerOwner>, AEvent>(listener, event));
	}
}
