package org.caleydo.core.manager.event.view.storagebased;

import org.caleydo.core.manager.event.AEvent;

/**
 * This event signals a view that a change has occured in some part that affects the display of the view,
 * outside of other update events such as {@link SelectionUpdate}. An example is a change in color mapping.
 * 
 * @author Werner Puff
 */
public class RedrawViewEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}
}
