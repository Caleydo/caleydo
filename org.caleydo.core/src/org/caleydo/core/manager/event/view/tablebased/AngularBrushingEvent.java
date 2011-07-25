package org.caleydo.core.manager.event.view.tablebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that signals that angular brushing should be activated
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class AngularBrushingEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
