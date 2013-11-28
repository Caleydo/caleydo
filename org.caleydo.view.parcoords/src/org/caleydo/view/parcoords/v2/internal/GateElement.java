/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.parcoords.v2.internal;

import java.util.List;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.IDoublePredicate;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.AGLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout2;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.parcoords.PCRenderStyle;

/**
 * @author Samuel Gratzl
 *
 */
public class GateElement extends GLElementContainer implements IDoublePredicate, IGLLayout2, ISelectionCallback {
	/**
	 *
	 */
	private static final int GATE_BORDERS = 17;
	private Handle from = new Handle(true);
	private Handle to = new Handle(false);

	/**
	 *
	 */
	public GateElement() {
		this.add(from);
		this.add(to);
		GLButton close = new GLButton();
		close.setCallback(this);
		this.add(close);
		setLayout(this);
	}

	float onDragged(boolean isTop, float value, float dy) {
		float h = getSize().y();
		value += dy / h;
		if (isTop)
			value = Math.max(Math.min(value, to.value),0);
		else
			value = Math.min(Math.max(value, from.value),1);
		relayout();
		return value;
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		findParent(NumericalAxisElement.class).removeGate(this);
	}

	@Override
	public boolean apply(double in) {
		return from.value <= in && in <= to.value;
	}

	@Override
	public boolean apply(Double input) {
		return apply(input.doubleValue());
	}

	@Override
	public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h, IGLLayoutElement parent,
			int deltaTimeMs) {
		children.get(0).setBounds(-60, from.value * h - 22, 77, 22);
		children.get(1).setBounds(-60, to.value * h, 77, 22);
		if (h * (to.value - from.value) > GATE_BORDERS)
			children.get(2).setBounds(w + 2, from.value * h, 13, 13);
		else
			children.get(2).hide();
		return false;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		float from = h * this.from.value;
		float hi = h*this.to.value - from;
		if (hi > GATE_BORDERS) { // render handles
			g.fillImage(PCRenderStyle.GATE_TOP, 0, from, 32, 13);
			g.fillImage(PCRenderStyle.GATE_BODY, 0, from + 13, w, hi - 13 - 3);
			g.fillImage(PCRenderStyle.GATE_BOTTOM, 0, from + hi - 3, w, 3);
		} else {
			g.fillImage(PCRenderStyle.GATE_BODY, 0, from, w, hi);
		}

		super.renderImpl(g, w, h);
	}

	private class Handle extends AGLButton {
		private boolean dragged;
		private float value;
		private final boolean isTop;

		public Handle(boolean isTop) {
			this.isTop = isTop;
			this.value = isTop ? 0 : 1;
		}

		@Override
		protected void onMouseOver(Pick pick) {
			if (!pick.isAnyDragging()) {
				hovered = true;
			}
			super.onMouseOver(pick);
		}

		@Override
		protected void onClicked(Pick pick) {
			if (pick.isAnyDragging())
				return;
			pick.setDoDragging(true);
			dragged = true;
			repaint();
		}

		@Override
		protected void onDragged(Pick pick) {
			if (!dragged)
				return;
			value = GateElement.this.onDragged(isTop, value, pick.getDy());
			repaint();
		}

		@Override
		protected void onMouseReleased(Pick pick) {
			if (dragged) {
				dragged = false;
				repaint();
			}
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (isTop)
				g.fillImage(PCRenderStyle.GATE_MENUE, 0, h, w, -h);
			else
				g.fillImage(PCRenderStyle.GATE_MENUE, 0, 0, w, h);

			String v = findParent(NumericalAxisElement.class).getRawValue(value);
			g.textColor(Color.WHITE).drawText(v, 17, 3, w - 18, h - 8).textColor(Color.BLACK);

			if (hovered)
				hoverEffect.render(g, w, h, this);
			if (armed)
				armedEffect.render(g, w, h, this);
		}

	}
}
