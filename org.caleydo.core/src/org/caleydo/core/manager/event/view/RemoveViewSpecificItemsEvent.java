package org.caleydo.core.manager.event.view;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event to signal that all view specific toolbar-items should be removed.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class RemoveViewSpecificItemsEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
