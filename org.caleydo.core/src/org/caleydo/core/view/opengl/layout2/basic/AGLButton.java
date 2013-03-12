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
package org.caleydo.core.view.opengl.layout2.basic;

import java.awt.Color;

import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * a simple basic widget for a button with a lot of basic effects
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AGLButton extends PickableGLElement {
	/**
	 * is mouse over
	 */
	protected boolean hovered = false;
	/**
	 * is mouse down
	 */
	protected boolean armed = false;

	/**
	 * effect to render when the mouse is over the component
	 */
	protected IGLRenderer hoverEffect = GLRenderers.drawRoundedRect(Color.DARK_GRAY);
	/**
	 * effect to render when the mouse is down
	 */
	protected IGLRenderer armedEffect = GLRenderers.fillRoundedRect(new Color(1, 1, 1, 0.3f));


	public AGLButton() {

	}

	/**
	 * @param armedEffect
	 *            setter, see {@link armedEffect}
	 */
	public AGLButton setArmedEffect(IGLRenderer armedEffect) {
		if (armedEffect == null)
			armedEffect = GLRenderers.DUMMY;
		if (this.armedEffect.equals(armedEffect))
			return this;
		this.armedEffect = armedEffect;
		if (armed)
			repaint();
		return this;
	}

	/**
	 * @param hoverEffect
	 *            setter, see {@link hoverEffect}
	 */
	public AGLButton setHoverEffect(IGLRenderer hoverEffect) {
		if (hoverEffect == null)
			hoverEffect = GLRenderers.DUMMY;
		if (this.hoverEffect.equals(hoverEffect))
			return this;
		this.hoverEffect = hoverEffect;
		if (hovered)
			repaint();
		return this;
	}

	@Override
	protected void onMouseOver(Pick pick) {
		if (pick.isAnyDragging())
			return;
		hovered = true;
		repaint();
	}

	@Override
	protected void onMouseOut(Pick pick) {
		if (!hovered)
			return;
		armed = false;
		hovered = false;
		repaint();
	}
}
