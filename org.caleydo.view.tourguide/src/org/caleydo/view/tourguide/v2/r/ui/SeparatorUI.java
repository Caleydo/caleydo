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
package org.caleydo.view.tourguide.v2.r.ui;

import gleem.linalg.Vec2f;

import java.awt.Color;

import javax.media.opengl.GL2;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IMouseLayer;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragInfo;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * @author Samuel Gratzl
 *
 */
public class SeparatorUI extends PickableGLElement {
	protected boolean hovered;
	protected boolean canDrop;
	protected int index;

	public SeparatorUI(int index) {
		this.index = index;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (hovered || canDrop) {
			renderTriangle(g, w);
		}
		g.gl.glPushAttrib(GL2.GL_LINE_BIT);
		renderRepr(g, w, h);
		g.gl.glPopAttrib();
	}

	protected void renderRepr(GLGraphics g, float w, float h) {
		g.color(Color.BLACK).lineWidth(canDrop ? 3 : 1).drawLine(w * 0.5f, 0, w * 0.5f, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (hovered || canDrop) {
			renderTriangle(g, w);
		}
		super.renderPickImpl(g, w, h);
	}

	/**
	 * @param g
	 */
	protected void renderTriangle(GLGraphics g, float w) {
		g.color(Color.BLACK).fillPolygon(new Vec2f(0, 10), new Vec2f(-10, 0), new Vec2f(w + 10, 0), new Vec2f(w, 10));
	}

	@Override
	protected void onMouseOver(Pick pick) {
		System.out.println("hovered");
		IMouseLayer mouse = context.getMouseLayer();
		if (mouse.hasDraggable(ScoreColumnDragInfo.class)) {
			mouse.setDropable(ScoreColumnDragInfo.class, true);
			this.canDrop = true;
			repaint();
		} else if (!context.getMouseLayer().hasDraggables()) {
			this.hovered = true;
			repaintAll();
		}
	}

	@Override
	protected void onMouseOut(Pick pick) {
		if (this.hovered) {
			this.hovered = false;
			repaintAll();
		}
		IMouseLayer mouse = context.getMouseLayer();
		if (mouse.hasDraggable(ScoreColumnDragInfo.class)) {
			mouse.setDropable(ScoreColumnDragInfo.class, false);
			this.canDrop = true;
			repaint();
		}
	}

	@Override
	protected void onMouseReleased(Pick pick) {
		IMouseLayer mouse = context.getMouseLayer();
		if (mouse.hasDraggable(ScoreColumnDragInfo.class)) {
			Pair<GLElement, ScoreColumnDragInfo> dragged = mouse.getFirstDraggable(ScoreColumnDragInfo.class);
			mouse.removeDraggable(dragged.getSecond());
			this.canDrop = false;
			((ScoreTableUI) this.parent).moveColumnNextTo(dragged.getSecond().getCol(), this.index);
			repaint();
		}
	}
}

class CombinedSeparatorUI extends SeparatorUI {
	public CombinedSeparatorUI(int index) {
		super(index);
	}

	@Override
	protected void onMouseOver(Pick pick) {
		super.onMouseOver(pick);
		IMouseLayer mouse = context.getMouseLayer();
		if (mouse.hasDraggable(ScoreDragInfo.COMBINED_ALIGN)) {
			mouse.setDropable(ScoreDragInfo.COMBINED_ALIGN, true);
			this.canDrop = true;
			repaintAll();
		}
	}

	@Override
	protected void onMouseOut(Pick pick) {
		super.onMouseOut(pick);
		IMouseLayer mouse = context.getMouseLayer();
		if (mouse.hasDraggable(ScoreDragInfo.COMBINED_ALIGN)) {
			mouse.setDropable(ScoreDragInfo.COMBINED_ALIGN, false);
			repaint();
		}
	}

	@Override
	protected void onMouseReleased(Pick pick) {
		super.onMouseReleased(pick);
		IMouseLayer mouse = context.getMouseLayer();
		if (mouse.hasDraggable(ScoreDragInfo.COMBINED_ALIGN)) {
			Pair<GLElement, IDragInfo> draggable = mouse.getFirstDraggable(ScoreDragInfo.COMBINED_ALIGN);
			mouse.removeDraggable(draggable.getFirst());
			((ScoreTableUI) this.parent).alignCombined(index);
			repaintAll();
		}
	}
}

class AlignSeparatorUI extends SeparatorUI {

	public AlignSeparatorUI(int index) {
		super(index);
	}

	@Override
	protected void renderRepr(GLGraphics g, float w, float h) {
		g.color(Color.BLACK).lineWidth(canDrop ? 4 : 3).drawLine(w * 0.5f, 0, w * 0.5f, h);
	}

	@Override
	protected void onClicked(Pick pick) {
		if (context.getMouseLayer().hasDraggables())
			return;
		GLElement d = new GLElement(new IGLRenderer() {
			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				renderImpl(g, w, h);
			}
		});
		Vec2f vec = toRelative(pick.getPickedPoint());
		d.setLocation(-vec.x(), -vec.y());
		d.setSize(getSize().x(), getSize().y());
		context.getMouseLayer().addDraggable(d, ScoreDragInfo.COMBINED_ALIGN);
		pick.setDoDragging(true);
	}


	@Override
	protected void onMouseOut(Pick pick) {
		super.onMouseOut(pick);
		if (pick.isDoDragging()) {
			context.getMouseLayer().removeDraggable(ScoreDragInfo.COMBINED_ALIGN);
		}
	}

	@Override
	protected void onMouseReleased(Pick pick) {
		super.onMouseReleased(pick);
		IMouseLayer mouse = context.getMouseLayer();
		if (pick.isDoDragging()) {
			mouse.removeDraggable(ScoreDragInfo.COMBINED_ALIGN);
		}
	}

}