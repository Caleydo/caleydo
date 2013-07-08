/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.parcoords.listener;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

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
