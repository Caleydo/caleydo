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

import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions.IMoveTransition;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * the MOVE Animation implementation
 *
 * @author Samuel Gratzl
 *
 */
public class MoveAnimation extends ALayoutAnimation {
	private final IMoveTransition animation;
	private Vec4f from = null;
	private Vec4f to = null;

	public MoveAnimation(int duration, IMoveTransition animation) {
		super(duration);
		this.animation = animation;
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
		this.from = from;
		this.to = to;
	}
}

