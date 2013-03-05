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

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.animation.Durations.IDuration;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ACustomAnimation extends AAnimation {
	public ACustomAnimation(int startIn, IDuration duration) {
		super(startIn, duration);
	}

	protected abstract void firstTime(GLGraphics g, float w, float h);

	protected abstract void animate(GLGraphics g, float alpha, float w, float h);

	protected abstract void lastTime(GLGraphics g, float w, float h);

	/**
	 * performs the animation
	 *
	 * @param delta
	 *            between last call in ms
	 * @return whether this animation ended
	 */
	public boolean apply(GLGraphics g, int delta, float w, float h) {
		if (startIn >= 0) {
			startIn -= delta;
			if (startIn <= 0) {
				delta = -startIn;
				startIn = -1;
				firstTime(g, w, h);
			} else
				return false;
		}
		if (delta < 1)
			return false;
		remaining -= delta;
		float alpha = 0;
		if (remaining <= 0) { // last one
			lastTime(g, w, h);
		} else {
			alpha = 1 - (remaining / (float) durationValue);
			animate(g, alpha, w, h);
		}
		return remaining <= 0;
	}

	@Override
	public final EAnimationType getType() {
		return EAnimationType.CUSTOM;
	}

}
