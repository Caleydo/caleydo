package org.caleydo.core.util.clusterer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.ClustererCanceledEvent;
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

	protected boolean bClusteringCanceled = false;

	// variables needed for correct visualization of cluster progress bar
	protected int iProgressBarMultiplier;
	protected int iProgressBarOffsetValue;

	protected RecordVirtualArray recordVA;
	protected DimensionVirtualArray dimensionVA;

	protected ClusterState clusterState;

	public AClusterer() {
		queue = new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();

	}

	public void setClusterState(ClusterState clusterState) {
		this.clusterState = clusterState;
		this.recordVA = clusterState.getRecordPerspective().getVA();
		this.dimensionVA = clusterState.getDimensionPerspective().getVA();
	}

	/**
	 * Call this when the object is ready for the garbage collector
	 */
	public void destroy() {
		unregisterEventListeners();
	}

	@Override
	public void cancel() {
		bClusteringCanceled = true;
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
