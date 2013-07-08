/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.animation;

import gleem.linalg.Vec4f;

import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions.IMoveTransition;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * the MOVE Animation implementation
 *
 * @author Samuel Gratzl
 *
 */
public class ManualMoveAnimation extends ALayoutAnimation {
	private final IMoveTransition animation;
	private final Vec4f from;
	private Vec4f to = null;

	public ManualMoveAnimation(int duration, IMoveTransition animation, Vec4f from) {
		super(duration);
		this.animation = animation;
		this.from = from;
	}

	@Override
	protected void animate(IGLLayoutElement animated, float alpha, float w, float h) {
		animated.setBounds(animation.move(from, to, w, h, alpha));
	}

	@Override
	protected void firstTime(IGLLayoutElement animated, float w, float h) {
		animated.setBounds(from);
	}

	@Override
	protected void lastTime(IGLLayoutElement animated) {
		animated.setBounds(to);
	}

	@Override
	public EAnimationType getType() {
		return EAnimationType.MOVE;
	}

	@Override
	public void init(Vec4f from, Vec4f to) {
		this.to = to;
	}
}

