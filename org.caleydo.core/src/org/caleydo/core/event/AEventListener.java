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
 * General event listener interface for classes that handle events. Inherited classes should usually handle
 * exactly one type of event. Related views or mediators should register one listener to exactly one event
 * within the event system.
 * </p>
 * <p>
 * Optionally, a listener can have a dataDomainID table. As a consequence, it will receive only those events
 * that are of the same dataDomainID or have no dataDomainID specified.
 * </p>
 * <p>
 * It is also possible to set a listener to receive exclusively events for the designated dataDomain, using
 * the method {@link #setExclusiveDataDomainType(String)} instead of {@link #setDataDomainID(String)}.
 * </p>
 *
 * @author Werner Puff
 * @author Alexander Lex
 */
public abstract class AEventListener<T extends IListenerOwner> {

	/** related handling object, usually a view or manager-type class */
	protected T handler = null;

	/** the dataDomainID string that decides whether a listener listens for events for this data domain */
	protected String dataDomainID = null;
	/**
	 * flag determining whether a listener is listening to both it's dataDomain events and events where no
	 * dataDomain is specified (false), or only to events with the dataDomain specified
	 */
	protected boolean isExclusiveDataDomain = false;

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
	 * Set the dataDomainID - if this is set the listener will receive only events that have this or no
	 * dataDomainID. Notice that this has to be set before the listener is registered with the event
	 * publisher.
	 *
	 * @param dataDomainID
	 */
	public AEventListener<T> setDataDomainID(String dataDomainID) {
		this.dataDomainID = dataDomainID;
		return this;
	}

	/**
	 * Returns the dataDomainType or null if none is specified
	 *
	 * @return
	 */
	public String getDataDomainID() {
		return dataDomainID;
	}

	/**
	 * Behaves similar to {@link #setDataDomainID(String)} in that it set's the dataDomainID, however, for
	 * setDataDomainID, the listener receives events with not dataDomain specified, while, when using this
	 * method, only events specifying a matching dataDomain are forwarded.
	 *
	 * @param dataDomainID
	 */
	public AEventListener<T> setExclusiveDataDomainID(String dataDomainID) {
		this.dataDomainID = dataDomainID;
		isExclusiveDataDomain = true;
		return this;
	}

	/**
	 * Check whether a listener is set to listen exclusively on it's datadomain (excluding events where the
	 * datadomain is not set).
	 *
	 * @return
	 */
	public boolean isExclusiveDataDomain() {
		return isExclusiveDataDomain;
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
