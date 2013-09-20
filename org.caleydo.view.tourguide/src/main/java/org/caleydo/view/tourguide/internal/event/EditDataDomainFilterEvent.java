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
	private int minSize;

	public EditDataDomainFilterEvent() {

	}

	public EditDataDomainFilterEvent(Object payload, int minSize) {
		this.payload = payload;
		this.minSize = minSize;
	}

	/**
	 * @return the minSize, see {@link #minSize}
	 */
	public int getMinSize() {
		return minSize;
	}

	/**
	 * @return the payload, see {@link #payload}
	 */
	public Object getPayload() {
		return payload;
	}

	/**
	 * by convention if the payload == null
	 * 
	 * @return
	 */
	public boolean isStartEditing() {
		return payload == null;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}

