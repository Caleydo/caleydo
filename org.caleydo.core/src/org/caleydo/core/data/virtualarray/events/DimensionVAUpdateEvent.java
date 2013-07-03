/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.virtualarray.events;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Event to signal that a {@link DimensionVirtualArray} has changed. For details see {@link VAUpdateEvent}.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class DimensionVAUpdateEvent
	extends VAUpdateEvent {

	public DimensionVAUpdateEvent() {
		// TODO Auto-generated constructor stub
	}

	public DimensionVAUpdateEvent(String dataDomainID, String perspectiveID, Object sender) {
		setEventSpace(dataDomainID);
		setPerspectiveID(perspectiveID);
		setSender(sender);
	}

}
