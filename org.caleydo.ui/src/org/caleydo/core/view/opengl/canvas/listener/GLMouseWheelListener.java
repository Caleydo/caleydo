/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLMouseAdapter;

/**
 * Mouse wheel listener that uses a {@link AGLView} as handler.
 *
 * @author Partl
 */
public class GLMouseWheelListener extends GLMouseAdapter {

	private IMouseWheelHandler handler;

	public GLMouseWheelListener(IMouseWheelHandler handler) {
		this.handler = handler;
	}

	@Override
	public void mouseWheelMoved(IMouseEvent event) {
		handler.handleMouseWheel(event.getWheelRotation(), event.getPoint());
	}
}
