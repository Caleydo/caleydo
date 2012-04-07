package org.caleydo.core.event;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Basic class for all types of caleydo events. Events are the communication transport objects between the
 * subsystems of the application. Events must be xml serializeable with JAXB for network transmission. Events
 * are distributed by an {@link EventPublisher}
 * 
 * @author Werner Puff
 */
@XmlRootElement(name = "AEvent")
public abstract class AEvent {

	/** timestamp of the event-creation */
	Date creationTime;

	/** sender object of the event */
	@XmlTransient
	Object Sender;

	protected String dataDomainID = null;

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
	@XmlTransient
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

	/**
	 * Set a dataDomainID string so that only those receivers which either have the same or no registered
	 * domain receive the event
	 * 
	 * @param dataDomainID
	 */
	public void setDataDomainID(String dataDomainID) {
		this.dataDomainID = dataDomainID;
	}

	/**
	 * Get the dataDomainID for the event
	 * 
	 * @return
	 */
	public String getDataDomainID() {

		return dataDomainID;
	}

	/**
	 * Abstract method that has to be implemented by every inheriting view. It is supposed to check the
	 * integrity of the event as soon as it is actually published, thereby avoiding errors due to incorrect
	 * initialization. When additional information can be provided an exception should be thrown inside the
	 * method, else a return false is sufficient, which causes an exception in the {@link EventPublisher}.
	 * 
	 * @return true if everything is correct, else false
	 */
	public abstract boolean checkIntegrity();

}
