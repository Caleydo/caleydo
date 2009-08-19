package org.caleydo.core.manager.event.view.radial;

import org.caleydo.core.manager.event.AEvent;

/**
 * This event signals that a detail view of the specified element shall be displayed in radial hierarchy.
 * 
 * @author Christian Partl
 */
public class DetailOutsideEvent
	extends AEvent {

	private int elementID;
	
	public DetailOutsideEvent() {
		elementID = -1;
	}

	@Override
	public boolean checkIntegrity() {
		if (elementID == -1)
			throw new IllegalStateException("iMaxDisplayedHierarchyDepth was not set");
		return true;
	}

	/**
	 * @return Element the detail view shall be displayed for.
	 */
	public int getElementID() {
		return elementID;
	}

	/**
	 * Sets the element the detail view shall be displayed for.
	 * 
	 * @param elementID
	 *            Element the detail view shall be displayed for.
	 */
	public void setElementID(int elementID) {
		this.elementID = elementID;
	}

}
