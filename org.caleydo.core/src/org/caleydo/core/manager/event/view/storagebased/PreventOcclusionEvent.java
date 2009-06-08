package org.caleydo.core.manager.event.view.storagebased;

import org.caleydo.core.manager.event.AFlagSetterEvent;

/**
 * Event that signals that the parallel coordinates should change their occlusion prevention state.
 * 
 * @author Alexander Lex
 */
public class PreventOcclusionEvent
	extends AFlagSetterEvent {

	/**
	 * True for flag means that the parallel coordinates should enable occlusion prevention, flase disable
	 * 
	 * @param bFlag
	 */
	public PreventOcclusionEvent(boolean bFlag) {
		super(bFlag);
	}

}
