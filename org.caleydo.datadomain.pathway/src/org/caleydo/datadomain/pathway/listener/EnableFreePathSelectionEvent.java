/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.pathway.listener;

import org.caleydo.core.event.AEvent;

/**
 * Event to signal whether free path selection is enabled.
 *
 * @author Christian Partl
 *
 */
public class EnableFreePathSelectionEvent extends AEvent {

	private boolean isEnabled;

	/**
	 *
	 */
	public EnableFreePathSelectionEvent(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @param isEnabled
	 *            setter, see {@link isEnabled}
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * @return the isEnabled, see {@link #isEnabled}
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

}
