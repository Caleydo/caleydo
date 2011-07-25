package org.caleydo.core.manager.event.view.dimensionbased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * This event signals that a current selection should be applied to the virtual array, i.e. the deselected
 * elements should be removed.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class ApplyCurrentSelectionToVirtualArrayEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
