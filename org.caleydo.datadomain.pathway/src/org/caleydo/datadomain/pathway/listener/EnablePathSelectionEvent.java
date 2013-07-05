/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 * 
 */
package org.caleydo.datadomain.pathway.listener;

import org.caleydo.core.event.AEvent;

/**
 * Event that tells whether path selection mode should be enabled.
 * 
 * @author Christian Partl
 * 
 */
public class EnablePathSelectionEvent extends AEvent {

	/**
	 * Determines whether the path selection mode should be enabled in a
	 * {@link GLPathway} view.
	 */
	private boolean isPathSelectionMode = false;


	public EnablePathSelectionEvent(boolean isPathSelectionMode) {
		this.isPathSelectionMode = isPathSelectionMode;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @param isPathSelectionMode
	 *            setter, see {@link #isPathSelectionMode}
	 */
	public void setPathSelectionMode(boolean isPathSelectionMode) {
		this.isPathSelectionMode = isPathSelectionMode;
	}

	/**
	 * @return the isPathSelectionMode, see {@link #isPathSelectionMode}
	 */
	public boolean isPathSelectionMode() {
		return isPathSelectionMode;
	}

}
