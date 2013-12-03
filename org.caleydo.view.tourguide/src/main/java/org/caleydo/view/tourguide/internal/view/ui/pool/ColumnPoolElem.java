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
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDnDItem;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragEvent;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragGLSource;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragInfo;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.ColumnDragInfo;

/**
 * @author Samuel Gratzl
 *
 */
public class ColumnPoolElem extends APoolElem implements IDragGLSource {
	private final ARankColumnModel model;

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
	protected void onMouseOver(Pick pick) {
		super.onMouseOver(pick);
		if (armed && !pick.isAnyDragging())
			context.getMouseLayer().addDragSource(this);
	}

	@Override
	protected void onMouseOut(Pick pick) {
		context.getMouseLayer().removeDragSource(this);
		super.onMouseOut(pick);
	}

	@Override
	protected void onDoubleClicked(Pick pick) {
		// TODO: add back to tour guide
	}

	@Override
	public GLElement createUI(IDragInfo info) {
		assert info instanceof ColumnDragInfo;
		GLElement elem = new DraggedScoreHeaderItem();
		elem.setSize(getSize().x(), getSize().y());
		Vec2f loc = ((ColumnDragInfo) info).getShift();
		elem.setLocation(-loc.x(), -loc.y());
		return elem;
	}

	@Override
	public void onDropped(IDnDItem info) {
		// TODO Auto-generated method stub

	}

	@Override
	public IDragInfo startSWTDrag(IDragEvent event) {
		return new ColumnDragInfo(this.model, event.getOffset());
	}

	class DraggedScoreHeaderItem extends GLElement {
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (ColumnPoolElem.this.getParent() != null)
				ColumnPoolElem.this.renderImpl(g, w, h);
		}
	}

}
