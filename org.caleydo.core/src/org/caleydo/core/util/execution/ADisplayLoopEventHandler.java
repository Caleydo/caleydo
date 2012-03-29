package org.caleydo.core.util.execution;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.util.collection.Pair;

public abstract class ADisplayLoopEventHandler
	implements Runnable, IListenerOwner {

	/** The queue which holds the events */
	private BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue;

	public ADisplayLoopEventHandler() {
		queue = new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();
	}

	/**
	 * This method should be called every display cycle when it is save to change the state of the object. It
	 * processes all the previously submitted events.
	 */
	protected final void processEvents() {
		Pair<AEventListener<? extends IListenerOwner>, AEvent> pair;
		while (queue.peek() != null) {
			pair = queue.poll();
			pair.getFirst().handleEvent(pair.getSecond());
		}
	}

	@Override
	public synchronized void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		queue.add(new Pair<AEventListener<? extends IListenerOwner>, AEvent>(listener, event));
	}

}
