package org.caleydo.core.manager.event.view.radial;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * This event signals that the one step forth shall be taken in the navigation history of RadialHierarchy.
 * 
 * @author Christian Partl
 */
@XmlRootElement
@XmlType
public class GoForthInHistoryEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
