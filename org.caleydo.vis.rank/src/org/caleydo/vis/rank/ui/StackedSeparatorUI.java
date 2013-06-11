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
package org.caleydo.vis.rank.ui;

import gleem.linalg.Vec2f;

import java.awt.Color;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IMouseLayer;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.ui.column.StackedColumnHeaderUI;
import org.caleydo.vis.rank.ui.column.StackedColumnHeaderUI.AlignmentDragInfo;


/**
 * a special {@link SeparatorUI}, which is draggable determined by the given {@link IMoveHereChecker}
 *
 * @author Samuel Gratzl
 *
 */
public class StackedSeparatorUI extends SeparatorUI {
	private boolean isAlignment = false;

	public StackedSeparatorUI(IMoveHereChecker model, int index) {
		super(model, index);
	}

	public void setAlignment(int alignentIndex) {
		this.setAlignment(alignentIndex == index);
	}
	/**
	 * @param isAlignment
	 *            setter, see {@link isAlignment}
	 */
	private void setAlignment(boolean isAlignment) {
		if (this.isAlignment == isAlignment)
			return;
		this.isAlignment = isAlignment;
		if (this.isAlignment) {
			setTooltip("Drag this element to change the alignment");
		} else {
			setTooltip(null);
		}
		repaint();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (this.isAlignment) {
			float rx = RenderStyle.HEADER_ROUNDED_RADIUS_X;
			float ry = RenderStyle.HEADER_ROUNDED_RADIUS_Y;
			g.color(Color.BLACK); // getStacked().getModel().getBgColor());
			if (index > 0) //left there
				g.fillPolygon(new Vec2f(w - rx, 0), new Vec2f(w, 0), new Vec2f(w, ry));
			if (index < getStacked().getModel().size()) // right there
				g.fillPolygon(new Vec2f(0, 0), new Vec2f(rx, 0), new Vec2f(0, ry));
			// renderTriangle(g, w);
		}
		super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);
		if (this.isAlignment) {
			float rx = RenderStyle.HEADER_ROUNDED_RADIUS_X;
			float ry = RenderStyle.HEADER_ROUNDED_RADIUS_Y;
			g.color(Color.ORANGE); // getStacked().getModel().getBgColor());
			g.incZ().incZ();
			if (index > 0) // left there
				g.fillPolygon(new Vec2f(w - rx, 0), new Vec2f(w, 0), new Vec2f(w, ry));
			if (index < getStacked().getModel().size()) // right there
				g.fillPolygon(new Vec2f(0, 0), new Vec2f(rx, 0), new Vec2f(0, ry));
			g.decZ().decZ();
		}
	}

	@Override
	protected void renderHint(GLGraphics g, float w, float h) {
		g.color(Color.GRAY);
		float tw = 5;
		g.fillRect(-(tw - w) * 0.5f, 0, tw, h);
	}

	@Override
	protected boolean isDraggingAColumn() {
		return super.isDraggingAColumn() || context.getMouseLayer().hasDraggable(AlignmentDragInfo.class);
	}

	@Override
	protected void onMouseOver(Pick pick) {
		super.onMouseOver(pick);
		if (armed)
			return;
		if (!pick.isAnyDragging())
			return;
		if (pick.isDoDragging())
			return;
		IMouseLayer m = context.getMouseLayer();
		if (!m.hasDraggable(AlignmentDragInfo.class))
			return;
		Pair<GLElement, AlignmentDragInfo> info = m.getFirstDraggable(AlignmentDragInfo.class);
		if (info.getSecond() != getStacked().align)
			return;
		m.setDropable(ARankColumnModel.class, true);
		armed = true;
		repaint();
	}

	@Override
	protected void onClicked(Pick pick) {
		if (pick.isAnyDragging() || !isAlignment)
			return;
		pick.setDoDragging(true);
		IMouseLayer m = context.getMouseLayer();
		GLElement e = new GLElement(new IGLRenderer() {
			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				renderHint(g, w, h);
			}
		});
		e.setSize(getSize().x(), getSize().y());
		m.addDraggable(e, getStacked().align);
	}

	private StackedColumnHeaderUI getStacked() {
		return ((StackedColumnHeaderUI) getParent());
	}

	@Override
	protected void onMouseReleased(Pick pick) {
		IMouseLayer m = context.getMouseLayer();
		if (isAlignment && pick.isDoDragging()) {
			if (!m.isDropable(getStacked().align))
				m.removeDraggable(getStacked().align);
			//drop me
		} else if (armed) {
			m.setDropable(AlignmentDragInfo.class, false);
			Pair<GLElement, AlignmentDragInfo> info = m.getFirstDraggable(AlignmentDragInfo.class);
			if (info != null) {
				m.removeDraggable(info.getFirst());
				context.getSWTLayer().setCursor(-1);
				armed = false;
				getStacked().setAlignment(index);
			}
		}
		super.onMouseReleased(pick);
	}

	@Override
	protected void onMouseOut(Pick pick) {
		if (armed) {
			context.getMouseLayer().setDropable(AlignmentDragInfo.class, false);
		}
		super.onMouseOut(pick);
	}
}
