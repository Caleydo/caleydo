/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.virtualarray.events;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.event.AEvent;

/**
 * Event to signal that the virtual array has changed. It carries a {@link VirtualArrayDelta} as payload which
 * adapts the recipients virtual array for example by removing items.
 *
 * @author Alexander Lex
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class VADeltaEvent
	extends AEvent {

	/** delta between old and new selection */
	private VirtualArrayDelta virtualArrayDelta;

	/** additional information about the selection, e.g. to display in the info-box */
	private String info;

	public VirtualArrayDelta getVirtualArrayDelta() {
		return virtualArrayDelta;
	}

	public void setVirtualArrayDelta(VirtualArrayDelta virtualArrayDelta) {
		this.virtualArrayDelta = virtualArrayDelta;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public boolean checkIntegrity() {
		if (virtualArrayDelta == null) {
			throw new IllegalStateException("Integrity check in " + this
				+ "failed - virtualArrayDelta was null");
		}
		return true;
	}

}
