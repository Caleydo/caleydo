/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.animation;

import gleem.linalg.Vec4f;

import org.caleydo.core.view.opengl.layout2.animation.Durations.IDuration;
import org.caleydo.core.view.opengl.layout2.animation.InOutTransitions.IOutTransition;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * the out animation implementation
 *
 * @author Samuel Gratzl
 *
 */
public class OutAnimation extends Animation {
	private final IOutTransition animation;
	private Vec4f from = null;

	public OutAnimation(int startIn, IDuration duration, IGLLayoutElement animated, IOutTransition animation) {
		super(startIn, duration, animated);
		this.from = animated.getBounds();
		this.animation = animation;
	}

	@Override
	protected void animate(float alpha, float w, float h) {
		Vec4f f = animation.out(from, w, h, alpha);
		animated.setBounds(f);
	}

	@Override
	protected void firstTime(float w, float h) {
		animated.setBounds(from);
	}

	@Override
	protected void lastTime() {
		animated.hide();
	}

	@Override
	public EAnimationType getType() {
		return EAnimationType.OUT;
	}
	@Override
	public void init(Vec4f from, Vec4f to) {

	}
}

