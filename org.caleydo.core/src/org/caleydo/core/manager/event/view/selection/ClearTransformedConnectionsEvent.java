package org.caleydo.core.manager.event.view.selection;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event to signal that all transformed selection points should be deleted. The source selections are
 * untouched. This event is used when the selections are unchanged, but actions like animations change the
 * resulting screen coordinates of connection line points.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class ClearTransformedConnectionsEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
