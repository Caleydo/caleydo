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
package org.caleydo.core.event;

/**
 * <p>
 * General event listener interface for classes that handle events. Inherited classes should usually handle exactly one
 * type of event. Related views or mediators should register one listener to exactly one event within the event system.
 * </p>
 * <p>
 * Optionally, a listener can have an associated event space. As a consequence, it will receive only those events that
 * are of the same event space or have no event space specified.
 * </p>
 * <p>
 * It is also possible to set a listener to receive exclusively events for the designated event space, using the method
 * {@link #setExclusiveEventSpace(String)} instead of {@link #setEventSpace(String)}.
 * </p>
 * 
 * @author Werner Puff
 * @author Alexander Lex
 */
public abstract class AEventListener<T extends IListenerOwner> {

	/** related handling object, usually a view or manager-type class */
	protected T handler = null;

	/** the event space string that decides whether a listener listens for events for this event space */
	protected String eventSpace = null;
	/**
	 * flag determining whether a listener is listening to both it's event space's events and events where no event
	 * space is specified (false), or only to events with the event space specified
	 */
	protected boolean isExclusiveEventSpace = false;

	public AEventListener() {

	}

	public AEventListener(T handler) {
		this.handler = handler;
	}

	/**
	 * Returns the related handler object to this listener.
	 *
	 * @return related handler object
	 */
	public T getHandler() {
		return handler;
	}

	/**
	 * Sets the related handler object to this listener. Usually listeners are created by its handler object.
	 *
	 * @param handler
	 *            related handler object to table.
	 */
	public AEventListener<T> setHandler(T handler) {
		this.handler = handler;
		return this;
	}

	/**
	 * Set the event space - if this is set the listener will receive only events that have this or no event space.
	 * Notice that this has to be set before the listener is registered with the event publisher.
	 * 
	 * @param eventSpace
	 */
	public AEventListener<T> setEventSpace(String eventSpace) {
		this.eventSpace = eventSpace;
		return this;
	}

	/**
	 * Returns the event space or null if none is specified
	 * 
	 * @return
	 */
	public String getEventSpace() {
		return eventSpace;
	}

	/**
	 * Behaves similar to {@link #setEventSpace(String)} in that it set's the event space, however, for setEventSpace,
	 * the listener receives events with no event space specified, while, when using this method, only events specifying
	 * a matching event space are forwarded.
	 * 
	 * @param eventSpace
	 */
	public AEventListener<T> setExclusiveEventSpace(String eventSpace) {
		this.eventSpace = eventSpace;
		isExclusiveEventSpace = true;
		return this;
	}

	/**
	 * Check whether a listener is set to listen exclusively on it's event space (excluding events where the event space
	 * is not set).
	 * 
	 * @return
	 */
	public boolean isExclusiveEventSpace() {
		return isExclusiveEventSpace;
	}

	/**
	 * <p>
	 * Takes care of thread safe execution of events.
	 * </p>
	 * <p>
	 * Filters events based on the events-sender and the listeners related event-handler (the receiver).
	 * Events are only passed to the handleEvent(AEvent) method, if sender and receiver are not the same.
	 * </p>
	 *
	 * @param event
	 *            event object to handle by this listener
	 */
	public void queueEvent(AEvent event) {
		// if (event.getSender() == null) {
		// GeneralManager.get().getLogger().log(
		// new Status(IStatus.WARNING, GeneralManager.PLUGIN_ID, "handling " + this.getClass().getName()
		// + " with sender==null"));
		// }
		if (event.getSender() != this.getHandler()) {
			handler.queueEvent(this, event);
		}
	}

	/**
	 * <p>
	 * Decodes the event and its payload and calls view or management related methods of related views,
	 * mediators or managers.
	 * </p>
	 * <p>
	 * Do not call this method from any other thread than the one where the changes are applied. To submit a
	 * event from a different thread call {@link #queueEvent(AEvent)} instead.
	 * </p>
	 *
	 * @param event
	 *            event object to handle by this listener
	 */
	public abstract void handleEvent(AEvent event);

	/**
	 * Integrity check for listener. Most importantly the existence of a handler is checked. This method can
	 * be overridden, if further checks are necessary.
	 *
	 * @return true if a handler is present else throws exception
	 * @throws NullPointerException
	 *             when handler is null
	 */
	public boolean checkIntegrity() {
		if (handler == null)
			throw new NullPointerException("Handler in " + this + " was null");

		return true;
	}
}
