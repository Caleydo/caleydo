/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.animation;

import gleem.linalg.Vec4f;

import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * basic for animation description
 *
 * @author Samuel Gratzl
 *
 */
public abstract class ALayoutAnimation extends AAnimation {
	public ALayoutAnimation(int duration) {
		super(duration);
	}

	protected abstract void animate(IGLLayoutElement animated, float alpha, float w, float h);

	protected abstract void firstTime(IGLLayoutElement animated, float w, float h);

	protected abstract void lastTime(IGLLayoutElement animated);

	/**
	 * performs the animation
	 *
	 * @param delta
	 *            between last call in ms
	 * @return whether this animation ended
	 */
	public boolean apply(IGLLayoutElement animated, int delta, float w, float h) {
		if (!started) {
			firstTime(animated, w, h);
			started = true;
		}
		if (delta < 3)
			return false;
		remaining -= delta;
		float alpha = 0;
		if (remaining <= 0) { // last one
			lastTime(animated);
		} else {
			alpha = 1 - (remaining / (float) duration);
			animate(animated, alpha, w, h);
		}
		return remaining <= 0;
	}

	public abstract void init(Vec4f from, Vec4f to);
}
