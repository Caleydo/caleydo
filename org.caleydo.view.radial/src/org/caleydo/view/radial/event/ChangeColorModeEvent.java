/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial.event;

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
