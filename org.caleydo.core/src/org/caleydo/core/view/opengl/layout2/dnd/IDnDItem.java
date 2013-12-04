/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.dnd;

import gleem.linalg.Vec2f;

public interface IDnDItem {
	IDragInfo getInfo();

	EDnDType getType();

	/**
	 * just in drop
	 * 
	 * @return
	 */
	Vec2f getMousePos();
}
