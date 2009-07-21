package org.caleydo.core.manager.event.view.remote;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event to signal that a pathway should be loaded.
 * 
 * @author Werner Puff
 */
@XmlRootElement(name = "loadPathwayEvent")
@XmlType(name = "LoadPathwayEvent")
public class LoadPathwayEvent
	extends AEvent {

	/** id of the pathway to load */
	private int pathwayID = -1;

	/**
	 * gets the id of the pathway to load
	 * 
	 * @return pathway-id to load
	 */
	public int getPathwayID() {
		return pathwayID;
	}

	/**
	 * sets the id of the pathway to load
	 * 
	 * @param pathwayID
	 *            pathway-id to load
	 */
	public void setPathwayID(int pathwayID) {
		this.pathwayID = pathwayID;
	}

	@Override
	public boolean checkIntegrity() {
		if (pathwayID == -1)
			throw new IllegalStateException("pathwayID was not set");
		return true;
	}

}
