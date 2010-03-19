package org.caleydo.core.manager.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.AEvent;

/**
 * Event that signals that a given set needs to be clustered.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class ClusterSetEvent
	extends AEvent {

	private ISet set ;

	/**
	 * default no-arg constructor
	 */
	public ClusterSetEvent() {
		// nothing to initialize here
	}

	public ClusterSetEvent(ISet set) {
		this.set = set;
	}

	public ISet setSet() {
		return set;
	}
	
	public ISet getSet() {
		return set;
	}

	@Override
	public boolean checkIntegrity() {
		if (set == null)
			return false;
		return true;
	}

}
