/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui.pool;

import gleem.linalg.Vec2f;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IMouseLayer;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.model.ARankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class ColumnPoolElem extends APoolElem {
	private final ARankColumnModel model;
	private boolean isDragging;

	public ColumnPoolElem(ARankColumnModel model) {
		this.model = model;
		setLayoutData(model);
		setTooltip(model.getTitle());
	}

	@Override
	protected Color getBackgroundColor() {
		return model.getBgColor();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(getBackgroundColor());
		g.fillRoundedRect(0, 0, w, h, 5);
		model.getHeaderRenderer().render(g, w - 6, h - 6, this);
		g.color(armed ? Color.BLACK : Color.GRAY);
		g.drawRoundedRect(0, 0, w, h, 5);
	}

	@Override
	protected void onClicked(Pick pick) {
		if (armed && !pick.isAnyDragging()) {
			pick.setDoDragging(true);
			onDragColumn(pick);
		}
	}

	@Override
	protected void onDoubleClicked(Pick pick) {
		// TODO: add back to tour guide
	}

	@Override
	protected void onMouseReleased(Pick pick) {
		if (pick.isDoDragging())
			onDropColumn(pick);
	}

	private void onDropColumn(Pick pick) {
		IMouseLayer l = context.getMouseLayer();
		if (this.isDragging) {
			if (!l.isDropable(this.model)) {
				l.removeDraggable(this.model);
			}
			this.isDragging = false;
			context.getSWTLayer().setCursor(-1);
			return;
		}
	}

	private void onDragColumn(Pick pick) {
		IMouseLayer l = context.getMouseLayer();
		GLElement elem = new DraggedScoreHeaderItem();
		elem.setSize(getSize().x(), getSize().y());
		Vec2f loc = toRelative(pick.getPickedPoint());
		elem.setLocation(-loc.x(), -loc.y());
		isDragging = true;
		l.addDraggable(elem, this.model);
	}

	class DraggedScoreHeaderItem extends GLElement {
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (ColumnPoolElem.this.getParent() != null)
				ColumnPoolElem.this.renderImpl(g, w, h);
		}
	}

}
