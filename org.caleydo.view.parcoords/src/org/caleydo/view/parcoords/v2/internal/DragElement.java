/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.parcoords.v2.internal;

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.AGLButton;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * @author Samuel Gratzl
 *
 */
public class DragElement extends AGLButton {
	private boolean dragged;
	/**
	 * callback for selection state changes
	 */
	private IDragCallback callback = DUMMY_CALLBACK;


	/**
	 * effect to render when the component is selected
	 */
	private IGLRenderer draggedRenderer = null;

	protected final void fireCallback(float dx, float dy) {
		callback.onDragged(this, dx, dy);
	}

	/**
	 * @param callback
	 *            setter, see {@link callback}
	 */
	public final DragElement setCallback(IDragCallback callback) {
		if (callback == null)
			callback = DUMMY_CALLBACK;
		if (this.callback == callback)
			return this;
		this.callback = callback;
		return this;
	}

	/**
	 * @param draggedRenderer
	 *            setter, see {@link selectedRenderer}
	 */
	public DragElement setDraggedRenderer(IGLRenderer draggedRenderer) {
		if (this.draggedRenderer != null && this.draggedRenderer.equals(draggedRenderer))
			return this;
		this.draggedRenderer = draggedRenderer;
		if (dragged)
			repaint();
		return this;
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
		fireCallback(pick.getDx(), pick.getDy());
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
		if (dragged && draggedRenderer != null)
			draggedRenderer.render(g, w, h, this);
		else
			super.renderImpl(g, w, h);

		if (hovered)
			hoverEffect.render(g, w, h, this);
		if (armed)
			armedEffect.render(g, w, h, this);
	}/**
	 * callback interface for selection changes of a button
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public interface IDragCallback {
		void onDragged(DragElement elem, float dx, float dy);
	}

	static final IDragCallback DUMMY_CALLBACK = new IDragCallback() {
		@Override
		public void onDragged(DragElement elem, float dx, float dy) {

		}
	};
}
