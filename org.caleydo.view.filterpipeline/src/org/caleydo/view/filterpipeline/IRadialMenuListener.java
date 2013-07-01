/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.filterpipeline;

/**
 * 
 * @author Thomas Geymayer
 * 
 */
public interface IRadialMenuListener {
	/**
	 * Called if an entry is selected and the left mouse button is released
	 * 
	 * @param externalId
	 * @param selection
	 */
	public void handleRadialMenuSelection(int externalId, int selection);

	/**
	 * Called while an entry is selected and the left mouse button is hold
	 * pressed
	 * 
	 * @param externalId
	 * @param selection
	 */
	public void handleRadialMenuHover(int externalId, int selection);
}
