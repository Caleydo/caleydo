package org.caleydo.core.manager.event.view.treemap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event for zoom in.
 * @author Michael Lafer
 *
 */

@XmlRootElement
@XmlType
public class ZoomInEvent 
extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
