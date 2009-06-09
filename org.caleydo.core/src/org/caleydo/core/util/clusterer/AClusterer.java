package org.caleydo.core.util.clusterer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.ClusterProgressEvent;
import org.caleydo.core.manager.event.data.ClustererCanceledEvent;
import org.caleydo.core.manager.general.GeneralManager;
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

	protected int iProgressBarMultiplier;
	protected int iProgressBarOffsetValue;

	public AClusterer() {
		queue = new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();
		clustererCanceledListener = new ClustererCanceledListener();
		clustererCanceledListener.setHandler(this);
		GeneralManager.get().getEventPublisher().addListener(ClustererCanceledEvent.class,
			clustererCanceledListener);
	}

	/**
	 * Call this when the object is ready for the garbage collector
	 */
	public void destroy() {
		if (clustererCanceledListener != null) {
			GeneralManager.get().getEventPublisher().removeListener(clustererCanceledListener);
			clustererCanceledListener = null;
		}
	}

	@Override
	public void cancel() {
		bClusteringCanceled = true;
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
