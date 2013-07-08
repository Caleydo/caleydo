/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

/**
 * Removes all dynamically created selection types. These types are marked as "managed".
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class RemoveManagedSelectionTypesEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}
}
