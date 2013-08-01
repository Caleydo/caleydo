/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event;

/**
 * a event that has an dedicated receiver
 *
 * @author Samuel Gratzl
 *
 */
public abstract class ADirectedEvent extends AEvent {
	private Object receiver;

	/**
	 * chaining supporting receiver setter
	 *
	 * @param receiver
	 * @return
	 */
	public ADirectedEvent to(Object receiver) {
		this.receiver = receiver;
		return this;
	}

	/**
	 * determines whether the receiver is equal to the given object
	 *
	 * @param obj
	 * @return
	 */
	public boolean sentTo(Object obj) {
		return receiver == obj;
	}

	/**
	 * @return the receiver, see {@link #receiver}
	 */
	public Object getReceiver() {
		return receiver;
	}

	/**
	 * dummy implementation
	 */
	@Override
	public boolean checkIntegrity() {
		return true;
	}
}
