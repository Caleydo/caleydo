package org.caleydo.core.event.view.radial;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.event.AEvent;

/**
 * This event signals that the color mode in RadialHierarchy shall be changed, i.e. the partial discs are
 * drawn using a different color mode (e.g. Rainbow, Expression).
 * 
 * @author Christian Partl
 */
@XmlRootElement
@XmlType
public class ChangeColorModeEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
