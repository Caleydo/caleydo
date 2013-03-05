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

	public InAnimation(int startIn, IDuration duration, IGLLayoutElement animated, IInTransition animation) {
		super(startIn, duration, animated);
		this.animation = animation;
	}

	@Override
	protected void animate(float alpha, float w, float h) {
		animated.setBounds(animation.in(to, w, h, alpha));
	}

	@Override
	protected void firstTime(float w, float h) {
		animate(0, w, h);
	}

	@Override
	protected void lastTime() {
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

