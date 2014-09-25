/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.view;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

/**
 * Event to signal that all views should be reset
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class ResetAllViewsEvent
	extends AEvent {
	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}
}
