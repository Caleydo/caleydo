package org.caleydo.core.manager.event.view;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Removes all dynamically created selection types. These types are marked as "managed".
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class RemoveManagedSelectionTypesEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}
}
