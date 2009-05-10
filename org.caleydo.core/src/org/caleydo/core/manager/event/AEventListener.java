package org.caleydo.core.manager.event;

import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.core.runtime.Status;

/**
 * General event listener interface for classes that handle events.
 * Inherited classes should usually handle exactly one type of event.
 * Related views or mediators should register one listener to exactly one event
 * within the event system. 
 * @author Werner Puff
 */
public abstract class AEventListener<T> {

	/** related handling object, usually a view or manager-type class */
	protected T handler = null;
	
	/**
	 * Returns the related handler object to this listener. 
	 * @return related handler object
	 */
	public T getHandler() {
		return handler;
	}
	
	/**
	 * Sets the related handler object to this listener.
	 * Usually listeners are created by its handler object.
	 * @param handler related handler object to set.
	 */
	public void setHandler(T handler) {
		this.handler = handler;
	}

	/**
	 * Filters events based on the events-sender and the listeners related event-handler (the receiver).
	 * Events are only passed to the handleEvent(AEvent) method, if sender and receiver are not the same.
	 * @param event event object to handle by this listener
	 */
	public void handleEventFiltered(AEvent event) {
		if (event.getSender() == null) {
			GeneralManager.get().getLogger().log(new Status(Status.WARNING, GeneralManager.PLUGIN_ID, 
				"handling " + this.getClass().getName() + " with sender==null"));
		}
		if (event.getSender() != this.getHandler()) {
			handleEvent(event);
		}
	}
	
	/**
	 * Decodes the event and its payload and calls view or management related
	 * methods of related views, mediators or managers.
	 * @param event event object to handle by this listener
	 */
	public abstract void handleEvent(AEvent event);

}
