package org.caleydo.core.manager.event;

/**
 * Interface for all instances that have listeners. Used for thread-safe event queuing.
 * 
 * @author Alexander Lex
 */
public interface IListenerOwner {

	/**
	 * Submit an event which is executed by the specified listener once the IListenerOwner thinks it's safe to
	 * do so. This method needs to be implemented using the synchronized keyword.
	 * 
	 * @param listener
	 *            The listener used by the IListenerOwner to listen to the event
	 * @param event
	 *            The event which is to be executed
	 */
	public void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event);
}
