package org.caleydo.core.manager.event.view.remote;

import org.caleydo.core.manager.event.AEvent;

/**
 * Reset the remote renderer
 * 
 * @author Alexander Lex
 */
public class ResetRemoteRendererEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
