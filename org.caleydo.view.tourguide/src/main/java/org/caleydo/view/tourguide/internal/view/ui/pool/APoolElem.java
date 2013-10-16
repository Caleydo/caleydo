/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui.pool;


import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.picking.Pick;
import org.eclipse.swt.SWT;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class APoolElem extends PickableGLElement {
	protected boolean armed = false;


	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(getBackgroundColor());
		g.fillRoundedRect(0, 0, w, h, 10);
		g.color(armed ? Color.BLACK : Color.GRAY);
		g.drawRoundedRect(0, 0, w, h, 10);
	}

	protected abstract Color getBackgroundColor();

	@Override
	protected void onMouseOver(Pick pick) {
		if (pick.isAnyDragging())
			return;
		armed = true;
		context.getSWTLayer().setCursor(SWT.CURSOR_HAND);
		repaint();
	}

	@Override
	protected void onMouseOut(Pick pick) {
		if (armed) {
			armed = false;
			context.getSWTLayer().setCursor(-1);
			repaint();
		}
	}
}
