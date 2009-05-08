package org.caleydo.core.manager.event;

import java.util.Date;

import org.caleydo.core.manager.IEventPublisher;

/**
 * Basic class for all types of caleydo events. Events are the communication transport objects between the
 * subsystems of the application. Events must be xml serializeable with JAXB for network transmission. Events
 * are distributed by an {@link IEventPublisher}
 * 
 * @author Werner Puff
 */
public abstract class AEvent {

	/** timestamp of the event-creation */
	Date creationTime;

	/** sender object of the event */
	Object Sender;

	/**
	 * Basic constructor
	 */
	protected AEvent() {
		creationTime = new Date();
	}

	/**
	 * Gets the creation time of this event
	 * 
	 * @return creation time
	 */
	public Date getCreationTime() {
		return creationTime;
	}

	/**
	 * Sets the creation time of this event. This method should be used with caution, as the creation time of
	 * events is usually set only on creation of the event.
	 * 
	 * @param creationTime
	 *            new creation time of the event
	 */
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	/**
	 * Returns the object that created the event, or null if no sender was set
	 * 
	 * @return the sender or null
	 */
	public Object getSender() {
		return Sender;
	}

	/**
	 * The sender is the object that creates the event. This is not necessary but avoids circular updates,
	 * i.e. you will not get any events you send out with a set sender back.
	 * 
	 * @param sender
	 *            the object creating and triggering the event
	 */
	public void setSender(Object sender) {
		Sender = sender;
	}

}
