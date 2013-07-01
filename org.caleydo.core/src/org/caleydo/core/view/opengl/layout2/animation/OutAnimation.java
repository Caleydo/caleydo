/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.animation;

import gleem.linalg.Vec4f;

import org.caleydo.core.view.opengl.layout2.animation.InOutTransitions.IOutTransition;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * the out animation implementation
 *
 * @author Samuel Gratzl
 *
 */
public class OutAnimation extends ALayoutAnimation {
	private final IOutTransition animation;
	private Vec4f from = null;

	public OutAnimation(int duration, IOutTransition animation) {
		super(duration);
		this.animation = animation;
	}

	@Override
	protected void animate(IGLLayoutElement animated, float alpha, float w, float h) {
		Vec4f f = animation.out(from, w, h, alpha);
		animated.setBounds(f);
	}

	@Override
	protected void firstTime(IGLLayoutElement animated, float w, float h) {
		animated.setBounds(from);
	}

	@Override
	protected void lastTime(IGLLayoutElement animated) {
		animated.hide();
	}

	@Override
	public EAnimationType getType() {
		return EAnimationType.OUT;
	}
	@Override
	public void init(Vec4f from, Vec4f to) {
		this.from = from;
	}
}

