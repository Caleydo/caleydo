/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.dnd;

import org.caleydo.core.view.opengl.layout2.GLElement;

/**
 * a {@link IDragInfo} which can create a representation by its own
 *
 * @author Samuel Gratzl
 *
 */
public interface IUIDragInfo extends IDragInfo {
	/**
	 * create a new representation
	 * 
	 * @return
	 */
	GLElement createUI();
}
