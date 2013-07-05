/**
 * 
 */
package org.caleydo.view.pathway.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.view.pathway.GLPathway;

/**
 * Event that signals the pathway view to clear the on-node mapping.
 * 
 * @author Christian Partl
 * 
 */
public class ClearMappingEvent
	extends AEvent {

	/**
	 * Pathway view that should clear its mapping.
	 */
	private GLPathway receiver;

	public ClearMappingEvent(GLPathway receiver) {
		this.receiver = receiver;
	}

	@Override
	public boolean checkIntegrity() {
		return receiver != null;
	}

	/**
	 * @param receiver setter, see {@link #receiver}
	 */
	public void setReceiver(GLPathway receiver) {
		this.receiver = receiver;
	}

	/**
	 * @return the receiver, see {@link #receiver}
	 */
	public GLPathway getReceiver() {
		return receiver;
	}

}
