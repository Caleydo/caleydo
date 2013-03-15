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
