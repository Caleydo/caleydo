package org.caleydo.core.manager.event;

/**
 * General event listener interface for classes that handle events.
 * Inherited classes should usually handle exactly one type of event.
 * Related views or mediators should register one listener to exactly one event
 * within the event system. 
 * @author Werner Puff
 */
public interface IEventListener {

	/**
	 * Decodes the event and its payload and calls view or management related
	 * methods of related views, mediators or managers.
	 * @param event event object to handle by this listener
	 */
	public void handleEvent(AEvent event);

}
