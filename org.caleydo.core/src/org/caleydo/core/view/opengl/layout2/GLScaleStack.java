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

	protected boolean scaleToFit = true;

	public GLScaleStack() {
		setLayout(this);
		setRenderer(GLRenderers.fillRect(backgroundColor));
	}

	/**
	 * @param backgroundColor
	 *            setter, see {@link backgroundColor}
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		setRenderer(GLRenderers.fillRect(backgroundColor));
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
		if (!setScaleNoRelayout(newScale))
			return false;

		scaleToFit = false;

		relayout();
		relayoutParent();

		// Update layout immediately
		// FIXME HACK
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
	 * Enable automatic scale to fit until first manual scale (call to
	 * {@link #scale(float)}, {@link #setScale(float)} or
	 * {@link #setScaleLimits(float, float)})
	 */
	public void scaleToFit() {
		scaleToFit = true;
	}

	/**
	 * Get current scale factor
	 *
	 * @return The current scale factor
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * Internal helper to set scale without triggering a relayout.
	 *
	 * @param newScale
	 * @return
	 */
	protected boolean setScaleNoRelayout(float newScale) {
		newScale = Math.max(scaleMin, Math.min(newScale, scaleMax));

		if (newScale == scale)
			return false;

		scale = newScale;

		return true;
	}

	@Override
	public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h, IGLLayoutElement parent,
			int deltaTimeMs) {
		if (children.isEmpty())
			return false;

		// 1st layer determines scale
		IGLLayoutElement baseLayer = children.get(0);

		boolean needsRelayout = false;
		if (!baseLayer.asElement().getMinSize().equals(originalSize)) {
			originalSize.set(baseLayer.asElement().getMinSize());

			needsRelayout = true;
		}

		if (scaleToFit) {
			Vec2f availSize = getParent().getSize();

			float xScale = availSize.x() / originalSize.x();
			float yScale = availSize.y() / originalSize.y();

			// Scale to fit - image just fits in label.
			if (setScaleNoRelayout(Math.min(xScale, yScale)))
				needsRelayout = true;
		}

		// Center images on stack (visible and scrolled parts)
		float width = getMinSize().x();
		float height = getMinSize().y();
		float x = .5f * (w - width);
		float y = .5f * (h - height);

		for (IGLLayoutElement child : children)
			child.setBounds(x, y, width, height);

		if (needsRelayout)
			relayoutParent();
		return needsRelayout;
	}

	@Override
	public Vec2f getMinSize() {
		return new Vec2f(originalSize.x() * scale, originalSize.y() * scale);
	}
}