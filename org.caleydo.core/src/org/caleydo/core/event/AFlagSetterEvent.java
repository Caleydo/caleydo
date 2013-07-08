/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event;

/**
 * Base class for FlagSetter Eventes
 * 
 * @author Alexander Lex
 */
public abstract class AFlagSetterEvent
	extends AEvent {

	private boolean flag;

	public AFlagSetterEvent() {
		flag = false;
	}

	public AFlagSetterEvent(boolean flag) {
		this.flag = flag;
	}

	public final boolean getFlag() {
		return flag;
	}

	@Override
	public boolean checkIntegrity() {
		// nothing to do
		return true;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
}
