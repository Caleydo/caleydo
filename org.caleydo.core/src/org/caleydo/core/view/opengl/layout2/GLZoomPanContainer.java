/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * Allow a stack of GLElements to be zoomed and scrolled. All elements will be
 * drawn at the same size.
 *
 * @author Thomas Geymayer
 *
 */
public class GLZoomPanContainer extends ScrollingDecorator {

	protected GLScaleStack elementStack;

	public GLZoomPanContainer() {
		super(null, new ScrollBar(true), new ScrollBar(false), 6.f);

		elementStack = new GLScaleStack();
		setContent(elementStack);
		setMinSizeProvider(elementStack);

		elementStack.setVisibility(EVisibility.PICKABLE);
		elementStack.onPick(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				switch (pick.getPickingMode()) {
				case DRAG_DETECTED:
					if (!pick.isAnyDragging())
						pick.setDoDragging(true);
					break;
				case DRAGGED:
					if (pick.isDoDragging())
						moveContent(pick.getD().scaled(-1));
					break;
				case MOUSE_WHEEL:
					// Get mouse position relative to content
					Vec2f pos = new Vec2f(pick.getPickedPoint());
					pos.sub(content.getLocation());
					Vec2f relCenter = pos.divided(elementStack.getSize());

					float scale = ((IMouseEvent) pick).getWheelRotation() > 0 ? 1.5f : 1 / 1.5f;
					if (!elementStack.scale(scale))
						return;

					// Try to show same part of the element at the cursor
					// location as before
					moveContentTo(relCenter.times(elementStack.getSize().minus(getSize())));
					break;
				default:
					break;
				}
			}
		});
	}

	public void setScaleLimits(float min, float max) {
		elementStack.setScaleLimits(min, max);
	}

	public void setBackgroundColor(Color color) {
		elementStack.setBackgroundColor(color);
	}

	/**
	 * Add an element on top of all previously added elements
	 *
	 * @param child
	 */
	public void add(GLElement child) {
		elementStack.add(child);
	}

	public void set(int index, GLElement child) {
		elementStack.set(index, child);
	}

	public void clear() {
		elementStack.clear();
	}

	public boolean isEmpty() {
		return elementStack.isEmpty();
	}
}