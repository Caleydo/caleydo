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

import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

import gleem.linalg.Vec4f;

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
