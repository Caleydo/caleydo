/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.ui;

import org.caleydo.core.util.color.Color;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IMouseLayer;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.model.ARankColumnModel;

/**
 * a visual glyph for a separator, i.e. a place where to drop a column
 *
 * @author Samuel Gratzl
 *
 */
public class SeparatorUI extends PickableGLElement {
	protected int index;
	protected boolean armed = false;
	private final IMoveHereChecker model;

	public SeparatorUI(IMoveHereChecker model) {
		setzDelta(0.2f);
		this.model = model;
	}

	public SeparatorUI(IMoveHereChecker model, int index) {
		this(model);
		this.index = index;
	}

	/**
	 * @param index
	 *            setter, see {@link index}
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (armed) {
			renderHint(g, w, h);
		}
	}

	protected void renderHint(GLGraphics g, float w, float h) {
		g.color(Color.BLACK);
		if (w > RenderStyle.SEPARATOR_PICK_WIDTH * 0.5f) {
			g.fillRect(0, 0, 5, h);
		} else {
			float tw = 5;
			g.fillRect(-2, 0, tw, h);
		}
		// renderTriangle(g, w);
		// g.fillRect(0, 0, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (getVisibility() != EVisibility.PICKABLE)
			return;
		if (!isDraggingAColumn()) {
			return;
		}
		if (w > RenderStyle.SEPARATOR_PICK_WIDTH * 0.5f) {
			g.color(Color.ORANGE);
			g.fillRect(0, 0, w, h);
			g.color(Color.BLACK);
		} else {
			float tw = RenderStyle.SEPARATOR_PICK_WIDTH;
			g.color(Color.ORANGE);
			g.fillRect(-(tw - w) * 0.5f, 0, tw, h);
			g.color(Color.BLACK);
		}
	}

	protected boolean isDraggingAColumn() {
		IMouseLayer m = context.getMouseLayer();
		return m.hasDraggable(ARankColumnModel.class);
	}

	@Override
	protected void onMouseOver(Pick pick) {
		if (!pick.isAnyDragging())
			return;
		IMouseLayer m = context.getMouseLayer();
		if (!m.hasDraggable(ARankColumnModel.class))
			return;
		Pair<GLElement, ARankColumnModel> info = m.getFirstDraggable(ARankColumnModel.class);
		if (!model.canMoveHere(index, info.getSecond(), RenderStyle.isCloneDragging(pick)))
			return;
		m.setDropable(ARankColumnModel.class, true);
		armed = true;
		repaint();
	}

	@Override
	protected void onMouseOut(Pick pick) {
		if (!armed)
			return;
		IMouseLayer m = context.getMouseLayer();
		m.setDropable(ARankColumnModel.class, false);
		armed = false;
		repaint();
	}

	@Override
	protected void onMouseReleased(Pick pick) {
		if (!armed)
			return;
		IMouseLayer m = context.getMouseLayer();
		m.setDropable(ARankColumnModel.class, false);
		Pair<GLElement, ARankColumnModel> info = m.getFirstDraggable(ARankColumnModel.class);
		m.removeDraggable(info.getFirst());
		m.setDropable(ARankColumnModel.class, false);
		context.getSWTLayer().setCursor(-1);
		armed = false;
		model.moveHere(index, info.getSecond(), RenderStyle.isCloneDragging(pick));
	}



	public interface IMoveHereChecker {
		boolean canMoveHere(int index, ARankColumnModel model, boolean clone);

		void moveHere(int index, ARankColumnModel model, boolean clone);
	}
}
