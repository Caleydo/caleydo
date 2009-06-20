package org.caleydo.core.manager.event.view.radial;

import org.caleydo.core.manager.event.AEvent;

/**
 * This event signals that the color mode in RadialHierarchy shall be changed, i.e. the partial discs are
 * drawn using a different color mode (e.g. Rainbow, Expression).
 * 
 * @author Christian Partl
 */
public class ChangeColorModeEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
