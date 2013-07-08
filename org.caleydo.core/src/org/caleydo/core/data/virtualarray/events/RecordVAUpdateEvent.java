/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.virtualarray.events;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;

/**
 * Event to signal that the virtual array has changed. It carries a {@link VirtualArrayDelta} as payload which
 * adapts the recipients virtual array for example by removing items.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class RecordVAUpdateEvent
	extends VAUpdateEvent {

	public RecordVAUpdateEvent() {

	}

	public RecordVAUpdateEvent(String dataDomainID, String perspectiveID, Object sender) {
		setEventSpace(dataDomainID);
		setPerspectiveID(perspectiveID);
		setSender(sender);
	}

}
