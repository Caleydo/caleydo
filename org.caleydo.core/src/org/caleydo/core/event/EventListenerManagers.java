package org.caleydo.core.event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
	 * creates a new {@link EventListenerManager}, which queues the event in the SWT {@link Display} loop
	 *
	 * @return
	 */
	public static EventListenerManager createSynchronizedDirect() {
		return new EventListenerManager(new DirectListenerOwner() {
			@Override
			public synchronized void queueEvent(final AEventListener<? extends IListenerOwner> listener,
					final AEvent event) {
				super.queueEvent(listener, event);
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

	public static final class QueuedEventListenerManager extends EventListenerManager implements Runnable {
		QueuedEventListenerManager() {
			super(new QueuedListenerOwner());
		}

		/**
		 * processes all currently queued events, non-blocking
		 */
		public void processEvents() {
			BlockingQueue<Runnable> queue = getQueue();
			Runnable p;
			while ((p = queue.poll()) != null) {
				p.run();
			}
		}

		private BlockingQueue<Runnable> getQueue() {
			BlockingQueue<Runnable> queue = ((QueuedListenerOwner) owner).getQueue();
			return queue;
		}

		public void asyncExec(Runnable run) {
			getQueue().offer(run);
		}

		/**
		 * wait and process events
		 */
		@Override
		public void run() {
			BlockingQueue<Runnable> queue = getQueue();
			while (!Thread.interrupted()) {
				try {
					queue.take().run();
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
	private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

	@Override
	public void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		queue.offer(new EventExecuteRunner(listener, event));
	}

	public BlockingQueue<Runnable> getQueue() {
		return queue;
	}
}

class EventExecuteRunner implements Runnable {
	private final AEventListener<? extends IListenerOwner> handler;
	private final AEvent event;

	public EventExecuteRunner(AEventListener<? extends IListenerOwner> handler, AEvent event) {
		super();
		this.handler = handler;
		this.event = event;
	}

	@Override
	public void run() {
		handler.handleEvent(event);
	}
}