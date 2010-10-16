package org.caleydo.core.manager.event.view.treemap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event for zoom out.
 * @author Michael Lafer
 *
 */

@XmlRootElement
@XmlType
public class ZoomOutEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}