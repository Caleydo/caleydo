/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui;

import gleem.linalg.Vec2f;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDnDItem;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragEvent;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragGLSource;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragInfo;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDropGLTarget;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.lineup.ui.column.StackedColumnHeaderUI;
import org.caleydo.vis.lineup.ui.column.StackedColumnHeaderUI.AlignmentDragInfo;


/**
 * a special {@link SeparatorUI}, which is draggable determined by the given {@link IMoveHereChecker}
 *
 * @author Samuel Gratzl
 *
 */
public class StackedSeparatorUI extends SeparatorUI implements IDropGLTarget, IDragGLSource {
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
		return super.isDraggingAColumn() || context.getMouseLayer().isDragging(AlignmentDragInfo.class);
	}

	@Override
	protected void onMouseOver(Pick pick) {
		super.onMouseOver(pick);
		if (armed)
			return;
		context.getMouseLayer().addDropTarget(this);
		if (pick.isAnyDragging() || !isAlignment)
			return;
		context.getMouseLayer().addDragSource(this);
	}

	@Override
	public boolean canSWTDrop(IDnDItem input) {
		IDragInfo info = input.getInfo();
		if (info != getStacked().align)
			return false;
		armed = true;
		repaint();
		return true;
	}

	private StackedColumnHeaderUI getStacked() {
		return ((StackedColumnHeaderUI) getParent());
	}

	@Override
	public void onDrop(IDnDItem info) {
		armed = false;
		getStacked().setAlignment(index);
	}

	@Override
	public void onItemChanged(IDnDItem input) {

	}

	@Override
	protected void onMouseOut(Pick pick) {
		context.getMouseLayer().removeDropTarget(this);
		context.getMouseLayer().removeDragSource(this);
		if (armed) {
			armed = false;
		}
		super.onMouseOut(pick);
	}

	@Override
	public GLElement createUI(IDragInfo info) {
		GLElement e = new GLElement(new IGLRenderer() {
			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				renderHint(g, w, h);
			}
		});
		e.setSize(getSize().x(), getSize().y());
		return e;
	}

	@Override
	public void onDropped(IDnDItem info) {
		// TODO Auto-generated method stub

	}

	@Override
	public IDragInfo startSWTDrag(IDragEvent event) {
		return getStacked().align;
	}
}
