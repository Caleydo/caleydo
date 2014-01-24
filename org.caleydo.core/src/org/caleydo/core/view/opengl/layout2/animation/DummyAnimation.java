/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.animation;

import gleem.linalg.Vec4f;

import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * @author Samuel Gratzl
 *
 */
public class DummyAnimation extends ALayoutAnimation {
	private final EAnimationType type;
	private Vec4f to;

	public DummyAnimation(EAnimationType type) {
		super(0);
		this.type = type;
	}

	@Override
	protected void animate(IGLLayoutElement animated, float alpha, float w, float h) {

	}

	@Override
	protected void firstTime(IGLLayoutElement animated, float w, float h) {

	}

	@Override
	protected void lastTime(IGLLayoutElement animated) {
		switch (type) {
		case IN:
		case MOVE:
			if (to != null)
				animated.setBounds(to);
			break;
		case OUT:
			animated.hide();
			break;
		default:
			break;
		}
	}

	/**
	 * @return the type, see {@link #type}
	 */
	@Override
	public EAnimationType getType() {
		return type;
	}

	@Override
	public void init(Vec4f from, Vec4f to) {
		this.to = to;
	}
}
