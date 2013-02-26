/**
 * 
 */
package org.caleydo.datadomain.pathway.listener;

import org.caleydo.core.event.AEvent;

/**
 * Event that signals to clear a selected path.
 * 
 * @author Christian Partl
 * 
 */
public class ClearPathEvent extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
