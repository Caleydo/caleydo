/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import gleem.linalg.Vec2f;

import java.awt.Dimension;
import java.awt.Point;

/**
 * @author Samuel Gratzl
 *
 */
public interface IGLMouseListener {
	void mousePressed(IMouseEvent mouseEvent);

	void mouseMoved(IMouseEvent mouseEvent);

	void mouseClicked(IMouseEvent mouseEvent);

	void mouseReleased(IMouseEvent mouseEvent);

	void mouseDragged(IMouseEvent mouseEvent);

	void mouseWheelMoved(IMouseEvent mouseEvent);

	void mouseEntered(IMouseEvent mouseEvent);

	void mouseExited(IMouseEvent mouseEvent);

	public interface IMouseEvent {
		Vec2f getPoint(Units unit);

		Vec2f getDIPPoint();

		Point getRAWPoint();

		int getClickCount();

		int getWheelRotation();

		int getButton();

		boolean isButtonDown(int button);

		@Deprecated
		Dimension getParentSize();

		boolean isShiftDown();

		boolean isAltDown();

		boolean isCtrlDown();
	}
}
