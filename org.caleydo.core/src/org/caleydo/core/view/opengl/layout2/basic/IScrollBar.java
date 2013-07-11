/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.basic;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;

/**
 * describes a scrollbar widget
 *
 * @author Samuel Gratzl
 *
 */
public interface IScrollBar extends IGLRenderer, IPickingListener {

	void setWidth(float w);

	float getWidth();

	void renderPick(GLGraphics g, float w, float h, GLElement parent);

	/**
	 * bounds of this scrollbar
	 *
	 * @param offset
	 *            from top
	 * @param view
	 *            the view/page currently visible
	 * @param total
	 *            in total from 0 to toal
	 * @return
	 */
	float setBounds(float offset, float window, float size);

	float getOffset();

	float getWindow();

	float getSize();

	void setCallback(IScrollBarCallback callback);

	public interface IScrollBarCallback {
		/**
		 * callback for scrollbar changes
		 *
		 * @param scrollBar
		 * @param offset
		 *            the new value in the notation of the bounds
		 */
		void onScrollBarMoved(IScrollBar scrollBar, float offset);

		/**
		 * converts the given mouse pickpoint in relative coordinates of the wrapping {@link GLElement}
		 *
		 * @param pickedPoint
		 * @return
		 */
		Vec2f toRelative(Vec2f pickedPoint);

		/**
		 * returns the total height/width that is used to render the scrollbar
		 *
		 * @param scrollBar
		 * @return
		 */
		float getHeight(IScrollBar scrollBar);

		/**
		 * triggers that the parent should be repainted
		 */
		void repaint();
	}
}
