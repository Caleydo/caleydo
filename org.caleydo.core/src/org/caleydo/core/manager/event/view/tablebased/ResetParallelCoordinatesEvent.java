package org.caleydo.core.manager.event.view.tablebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event to signal that the parallel coordinates should be reset
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class ResetParallelCoordinatesEvent
	extends AEvent {
	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}
}
