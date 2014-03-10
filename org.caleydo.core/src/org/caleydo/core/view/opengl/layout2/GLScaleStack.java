/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import java.util.List;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout2;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;

/**
 * An element which draws a stack of elements on top of each other - all with
 * the same size. The actual size is calculated by multiplying the setSize of
 * the first element added and the scale factor. Any remaining background is
 * filled with color.
 *
 * @author Thomas Geymayer
 *
 */
public class GLScaleStack extends GLElementContainer implements IGLLayout2 {

	protected Vec2f originalSize = new Vec2f(128, 128);

	protected float scale = 1f;

	protected float scaleMin = .1f;

	protected float scaleMax = 4f;

	protected Color backgroundColor = new Color(.3f, .3f, .3f, 1f);

	public GLScaleStack() {
		setLayout(this);

		GLElement background = new GLElement();
		background.setRenderer(GLRenderers.fillRect(backgroundColor));
		add(background);
	}

	/**
	 * Set the limits of the scale factor (default min=0.1, max=4).
	 *
	 * @param min
	 *            Minimal scale factor (zoomed out)
	 * @param max
	 *            Maximal scale factor (zoomed in)
	 */
	public void setScaleLimits(float min, float max) {
		if (min < 0.01f)
			throw new IllegalArgumentException("min < 0.01");
		if (max < min)
			throw new IllegalArgumentException("max < min");

		scaleMin = min;
		scaleMax = max;

		setScale(scale);
	}

	/**
	 * Set new scale factor
	 *
	 * @param newScale
	 *            The new scale factor
	 * @return true if the scale factor has changed
	 */
	public boolean setScale(float newScale) {
		newScale = Math.max(scaleMin, Math.min(newScale, scaleMax));

		if (newScale == scale)
			return false;

		scale = newScale;
		relayout();
		relayoutParent();

		// Update layout immediately
		((GLElement) getParent()).layout(0);

		return true;
	}

	/**
	 * Multiply current scale factor by given factor
	 *
	 * @param fac
	 *            Factor to multiply current scale factor with
	 * @return true if the scale factor has changed
	 */
	public boolean scale(float fac) {
		return setScale(scale * fac);
	}

	/**
	 * Get current scale factor
	 *
	 * @return The current scale factor
	 */
	public float getScale() {
		return scale;
	}

	@Override
	public GLElement set(int index, GLElement child) {
		// Skip background (== first element)
		return super.set(index + 1, child);
	}

	@Override
	public boolean isEmpty() {
		// Ignore background
		return size() < 2;
	}

	@Override
	public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h, IGLLayoutElement parent,
			int deltaTimeMs) {
		if (children.isEmpty())
			return false;

		// Background fills all available space
		children.get(0).setBounds(0, 0, w, h);

		if (children.size() < 2)
			return false;

		// 2nd layer determines scale
		IGLLayoutElement baseLayer = children.get(1);

		if (!baseLayer.getSetSize().equals(originalSize)) {
			originalSize.set(baseLayer.getSetSize());

			relayoutParent();

			// Update layout with new size
			return true;
		}

		// Center images on stack (visible and scrolled parts)
		float width = getMinSize().x();
		float height = getMinSize().y();
		float x = .5f * (w - width);
		float y = .5f * (h - height);

		for (int i = 1; i < children.size(); ++i)
			children.get(i).setBounds(x, y, width, height);

		return false;
	}

	@Override
	public Vec2f getMinSize() {
		return new Vec2f(originalSize.x() * scale, originalSize.y() * scale);
	}
}