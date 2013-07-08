/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial;

/**
 * Interface for creating element representations of selected partial discs for
 * drawing connection lines.
 * 
 * @author Christian Partl
 */
public interface IElementRepCreator {

	/**
	 * Creates an element representation with the specified parameters.
	 * 
	 * @param partialDisc
	 *            Partial disc an element representation shall be created for.
	 * @param viewID
	 *            View ID of the radial hierarchy.
	 */
	public void createElementRep(PartialDisc partialDisc, int viewIDfloat,
			float fHierarchyCenterX, float fHierarchyCenterY, float fHierarchyCenterZ);
}
