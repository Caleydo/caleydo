package org.caleydo.core.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

/**
 * Event that signals that a request to cancel the currently running clusterer was triggered
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class ClustererCanceledEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
