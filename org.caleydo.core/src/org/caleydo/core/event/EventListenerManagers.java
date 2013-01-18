package org.caleydo.core.event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.caleydo.core.util.collection.Pair;
import org.eclipse.swt.widgets.Display;

/**
 * factory class for {@link EventListenerManager} objects
 *
 * @author Samuel Gratzl
 *
 */
public final class EventListenerManagers {
	private EventListenerManagers() {

	}

	public static EventListenerManager wrap(IListenerOwner owner) {
		return new EventListenerManager(owner);
	}
	/**
	 * creates a new {@link EventListenerManager}, which directly executes the queued events
	 *
	 * @return
	 */
	public static EventListenerManager createDirect() {
		return new EventListenerManager(new DirectListenerOwner());
	}

	/**
	 * creates a new {@link EventListenerManager}, which queues the event in the SWT {@link Display} loop
	 *
	 * @return
	 */
	public static EventListenerManager createSWTDirect() {
		return new EventListenerManager(new DirectListenerOwner() {
			@Override
			public void queueEvent(final AEventListener<? extends IListenerOwner> listener, final AEvent event) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						listener.handleEvent(event);
					}
				});
			}
		});
	}

	/**
	 * creates a new {@link QueuedEventListenerManager}, which queues the events and can either be manually processed
	 * using {@link QueuedEventListenerManager#processEvents()} or within an own thread using by using the
	 * {@link QueuedEventListenerManager#run()} method
	 *
	 * @return
	 */
	public static QueuedEventListenerManager createQueued() {
		return new QueuedEventListenerManager();
	}

	public static class QueuedEventListenerManager extends EventListenerManager implements Runnable {
		QueuedEventListenerManager() {
			super(new QueuedListenerOwner());
		}

		/**
		 * processes all currently queued events, non-blocking
		 */
		public void processEvents() {
			BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue = ((QueuedListenerOwner) owner)
					.getQueue();
			Pair<AEventListener<? extends IListenerOwner>, AEvent> p;
			while ((p = queue.poll()) != null) {
				p.getFirst().handleEvent(p.getSecond());
			}
		}

		/**
		 * wait and process events
		 */
		@Override
		public void run() {
			BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue = ((QueuedListenerOwner) owner)
					.getQueue();
			while (!Thread.interrupted()) {
				try {
					Pair<AEventListener<? extends IListenerOwner>, AEvent> eventPair = queue.take();
					eventPair.getFirst().handleEvent(eventPair.getSecond());
				} catch (InterruptedException e) {
					e.printStackTrace();
					break; // stop listening
				}
			}
		}
	}
}

class DirectListenerOwner implements IListenerOwner {
	@Override
	public void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		listener.handleEvent(event);
	}

	@Override
	public void registerEventListeners() {

	}

	@Override
	public void unregisterEventListeners() {

	}
}

class QueuedListenerOwner extends DirectListenerOwner {
	private final BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue = new LinkedBlockingQueue<>();

	@Override
	public void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		queue.offer(new Pair<AEventListener<? extends IListenerOwner>, AEvent>(listener, event));
	}

	public BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> getQueue() {
		return queue;
	}
}