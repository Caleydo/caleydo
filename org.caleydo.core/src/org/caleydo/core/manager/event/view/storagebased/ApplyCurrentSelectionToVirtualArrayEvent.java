package org.caleydo.core.manager.event.view.storagebased;

import org.caleydo.core.manager.event.AEvent;

/**
 * This event signals that a current selection should be applied to the virtual array, i.e. the deselected
 * elements should be removed.
 * 
 * @author Alexander Lex
 */
public class ApplyCurrentSelectionToVirtualArrayEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
