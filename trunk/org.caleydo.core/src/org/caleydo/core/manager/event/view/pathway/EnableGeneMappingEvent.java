package org.caleydo.core.manager.event.view.pathway;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Events that signals that gene mapping within pathway views should be enabled.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class EnableGeneMappingEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
