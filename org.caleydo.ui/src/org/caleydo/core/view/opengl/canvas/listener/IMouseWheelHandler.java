/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.listener;

import gleem.linalg.Vec2f;

public interface IMouseWheelHandler {

	public void handleMouseWheel(int wheelAmount, Vec2f wheelPosition);

}
