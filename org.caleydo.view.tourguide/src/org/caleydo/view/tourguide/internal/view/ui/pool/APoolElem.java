/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui.pool;

import java.awt.Color;

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
		context.setCursor(SWT.CURSOR_HAND);
		repaint();
	}

	@Override
	protected void onMouseOut(Pick pick) {
		if (armed) {
			armed = false;
			context.setCursor(-1);
			repaint();
		}
	}
}
