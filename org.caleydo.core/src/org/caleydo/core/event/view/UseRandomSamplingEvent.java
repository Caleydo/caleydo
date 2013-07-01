/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.view;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AFlagSetterEvent;

@XmlRootElement
@XmlType
public class UseRandomSamplingEvent
	extends AFlagSetterEvent {

	public UseRandomSamplingEvent() {
		// nothing to initialize here
	}

	public UseRandomSamplingEvent(boolean flag) {
		super(flag);
	}
}
