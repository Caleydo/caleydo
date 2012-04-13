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
package org.caleydo.core.net;

import java.util.Collection;
import java.util.HashSet;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.IListenerOwner;

/**
 * <p>
 * Sends all events received by one {@link EventPublisher) to another {@link EventPublisher}.
 * <p>
 * <p>
 * Therefore instances of this class are registered to an {@link EventPublisher} as listener while the target-
 * {@link EventPublisher} is set as a property. Filter setting can be set for bridging only remote or local
 * events, e.g. to avoid re-sending events on remote machines again into the network.
 * </p>
 * <p>
 * The implementation does not queue any events for thread-safety, all events are bridged immediately.
 * </p>
 * 
 * @author Werner Puff
 */
public class EventFilterBridge
	extends AEventListener<EventFilterBridge>
	implements IListenerOwner {

	/** human readable name of this object */
	private String name;

	/** {@link EventPublisher} to bridge incoming events to */
	private EventPublisher targetEventPublisher = null;

	/** <code>true</code> when local events should be bridged, <code>false</code> otherwise */
	private boolean bridgeLocalEvents;

	/** <code>true</code> when remote events should be bridged, <code>false</code> otherwise */
	private boolean bridgeRemoteEvents;

	/** list of event sender to block */
	private Collection<Object> blockedSender;

	public EventFilterBridge() {
		name = "no name";
		targetEventPublisher = null;
		bridgeLocalEvents = false;
		bridgeRemoteEvents = false;
		blockedSender = new HashSet<Object>();
	}

	/**
	 * Bridges an event by calling {@link EventPublisher.triggerEvent(Event)} of the target-
	 * {@link EventPublisher}
	 * 
	 * @param event
	 *            event to bridge
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (!blockedSender.contains(event.getSender())) {
			if (event.getSender() instanceof NetworkEventReceiver && bridgeRemoteEvents) {
				targetEventPublisher.triggerEvent(event);
			}
			else if (!(event.getSender() instanceof NetworkEventReceiver) && bridgeLocalEvents) {
				targetEventPublisher.triggerEvent(event);
			}
		}
	}

	/**
	 * Bridges the given event by calling the {@link handleEvent(AEvent)}
	 * 
	 * @param event
	 *            event to bridge
	 */
	@Override
	public void queueEvent(AEvent event) {
		handleEvent(event);
	}

	/**
	 * <p>
	 * Not supported.
	 * </p>
	 * <p>
	 * When an {@link EventPublisher} tries to queue an event its directly passed to the target
	 * {@link EventPublisher}.
	 * </p>
	 */
	@Override
	public void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		throw new UnsupportedOperationException(
			"queueEvent(AEventListener, AEvent) is not supported, no thread safety within EventFilterBridge.");
	}

	/**
	 * <p>
	 * Integrity check for the EventFilterBridge.
	 * </p>
	 * <p>
	 * The check of the listener owner is disabled for this class. EventFitlerBridge-classes trigger events on
	 * the target {@link EventPublisher}
	 * </p>
	 */
	@Override
	public boolean checkIntegrity() {
		return true;
	}

	public EventPublisher getTargetEventPublisher() {
		return targetEventPublisher;
	}

	public void setTargetEventPublisher(EventPublisher targetEventPublisher) {
		this.targetEventPublisher = targetEventPublisher;
	}

	public boolean isBridgeLocalEvents() {
		return bridgeLocalEvents;
	}

	public void setBridgeLocalEvents(boolean bridgeLocalEvents) {
		this.bridgeLocalEvents = bridgeLocalEvents;
	}

	public boolean isBridgeRemoteEvents() {
		return bridgeRemoteEvents;
	}

	public void setBridgeRemoteEvents(boolean bridgeRemoteEvents) {
		this.bridgeRemoteEvents = bridgeRemoteEvents;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Object> getBlockedSender() {
		return blockedSender;
	}

	/**
	 * Adds a sender to the list of blocked senders. Events with have a sender contained in this list are not
	 * dispatched by this bridge.
	 * 
	 * @param sender
	 */
	public void addBlockedSender(Object sender) {
		blockedSender.add(sender);
	}

	public void setBlockedSender(Collection<Object> blockedSender) {
		this.blockedSender = blockedSender;
	}

	@Override
	public void registerEventListeners() {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterEventListeners() {
		// TODO Auto-generated method stub

	}

}
