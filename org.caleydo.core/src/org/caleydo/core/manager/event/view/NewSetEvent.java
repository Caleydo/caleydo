package org.caleydo.core.manager.event.view;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.AEvent;

/**
 * This event signals that a new set which it also contains is available. Listeners are supposed to swapt
 * their current set with the set provided.
 * 
 * @author Alexander Lex
 */
public class NewSetEvent
	extends AEvent {

	ISet set = null;

	/**
	 * Sets the new set for the event
	 * 
	 * @param set
	 *            the set to replace the old one
	 */
	public void setSet(ISet set) {
		this.set = set;
	}

	/**
	 * Returns the new set of the event
	 * 
	 * @return the set to replace the old one
	 */
	public ISet getSet() {
		return set;
	}

	@Override
	public boolean checkIntegrity() {
		if (set == null)
			throw new NullPointerException("set was null");
		return true;
	}

}
