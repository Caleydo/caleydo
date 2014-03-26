/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.dnd;

import org.caleydo.core.view.opengl.layout2.GLElement;

/**
 * factory for creating {@link GLElement} for a given {@link IDragInfo} elem
 *
 * @author Samuel Gratzl
 *
 */
public interface IRemoteDragInfoUICreator {
	/**
	 * create a visual representation for a {@link IDragInfo}
	 *
	 * @param info
	 * @return
	 */
	GLElement createUI(IDragInfo info);
}
