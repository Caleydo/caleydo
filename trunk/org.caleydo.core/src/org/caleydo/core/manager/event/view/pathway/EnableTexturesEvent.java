package org.caleydo.core.manager.event.view.pathway;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Events that signals that the pathway textures within pathway views should be enabled.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class EnableTexturesEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
