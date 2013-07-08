/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.event;

import org.caleydo.core.event.ADirectedEvent;

/**
 * event for handling the request and response of editing a data domain
 * 
 * @author Samuel Gratzl
 * 
 */
public class EditDataDomainFilterEvent extends ADirectedEvent {

	private Object payload;

	public EditDataDomainFilterEvent() {

	}

	public EditDataDomainFilterEvent(Object payload) {
		this.payload = payload;
	}

	/**
	 * @return the payload, see {@link #payload}
	 */
	public Object getPayload() {
		return payload;
	}

	public boolean isStartEditing() {
		return payload == null;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}

