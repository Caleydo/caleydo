/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.dnd;

import gleem.linalg.Vec2f;

public interface IDnDItem {
	/**
	 * returns the transfered information
	 *
	 * @return
	 */
	IDragInfo getInfo();

	/**
	 * return the current {@link EDnDType}, i.e. the current drag action
	 *
	 * @return
	 */
	EDnDType getType();

	/**
	 * just rare cases, which is the current mouse pos, e.g. when dropping where it will be dropped in absolute mouse
	 * coordinates
	 *
	 * @return
	 */
	Vec2f getMousePos();

	/**
	 * whether the item is internally just dragged (within same view) or coming from the outside
	 *
	 * @return
	 */
	boolean isInternal();
}
