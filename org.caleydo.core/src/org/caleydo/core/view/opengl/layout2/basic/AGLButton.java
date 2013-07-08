/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.basic;

import org.caleydo.core.util.color.Color;

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
