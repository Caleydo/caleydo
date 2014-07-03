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
 * Reset the remote renderer
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class ResetViewEvent
	extends AEvent {

	/**
	 * 
	 */
	public ResetViewEvent() {
	}
	
	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
