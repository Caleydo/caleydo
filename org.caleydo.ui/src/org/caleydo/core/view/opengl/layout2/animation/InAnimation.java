/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.animation;

import gleem.linalg.Vec4f;

import org.caleydo.core.view.opengl.layout2.animation.InOutTransitions.IInTransition;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * in animation implementation
 *
 * @author Samuel Gratzl
 *
 */
public class InAnimation extends ALayoutAnimation {
	private final IInTransition animation;
	private Vec4f to = null;

	public InAnimation(int duration, IInTransition animation) {
		super(duration);
		this.animation = animation;
	}

	@Override
	protected void animate(IGLLayoutElement animated, float alpha, float w, float h) {
		animated.setBounds(animation.in(to, w, h, alpha));
	}

	@Override
	protected void firstTime(IGLLayoutElement animated, float w, float h) {
		animate(animated, 0, w, h);
	}

	@Override
	protected void lastTime(IGLLayoutElement animated) {
		animated.setBounds(to);
	}

	@Override
	public EAnimationType getType() {
		return EAnimationType.IN;
	}

	@Override
	public void init(Vec4f from, Vec4f to) {
		this.to = to;
	}
}

