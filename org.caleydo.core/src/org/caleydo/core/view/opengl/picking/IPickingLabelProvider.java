/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.picking;

/**
 * Provides a label that depends on a {@link Pick}.
 * 
 * @author Christian Partl
 * 
 */
public interface IPickingLabelProvider {
	/**
	 * Returns a label depending on the provided {@link Pick}.
	 *
	 * @param pick
	 * @return
	 */
	public String getLabel(Pick pick);

}
