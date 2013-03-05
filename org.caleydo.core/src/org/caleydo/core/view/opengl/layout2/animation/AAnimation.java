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

import java.util.Objects;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.animation.Durations.IDuration;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * basic for animation description
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AAnimation implements Comparable<AAnimation> {
	private int startIn;
	private int remaining;
	private final IDuration duration; // TODO
	private int durationValue;
	protected final IGLLayoutElement animated;

	protected float lastAlpha = -1;

	public AAnimation(int startIn, IDuration duration, IGLLayoutElement animated) {
		this.duration = duration;
		this.durationValue = duration.getDuration();
		this.startIn = startIn;
		this.remaining = this.durationValue;
		this.animated = animated;
	}

	/**
	 * @return the animated, see {@link #animated}
	 */
	public GLElement getAnimatedElement() {
		return animated.asElement();
	}

	/**
	 * @return the animated, see {@link #animated}
	 */
	public IGLLayoutElement getAnimated() {
		return animated;
	}

	/**
	 * @return the remaining, see {@link #remaining}
	 */
	public int getRemaining() {
		return remaining;
	}

	/**
	 * returns when this animations stops
	 *
	 * @return
	 */
	public int getStopAt() {
		return startIn + remaining;
	}

	/**
	 * elapsed time of this animation
	 *
	 * @return
	 */
	public int getElapsed() {
		return durationValue - remaining;
	}

	/**
	 * when this animation starts or -1 for active ones
	 *
	 * @return
	 */
	public int getStartIn() {
		return startIn;
	}

	/**
	 * does this animation currently runs
	 *
	 * @return
	 */
	public boolean isRunning() {
		return startIn < 0;
	}

	@Override
	public int compareTo(AAnimation o) {
		return this.getStopAt() - o.getStopAt();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((animated == null) ? 0 : animated.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AAnimation other = (AAnimation) obj;
		if (!Objects.equals(animated, other.animated))
			return false;
		return true;
	}

	/**
	 * performs the animation
	 *
	 * @param delta
	 *            between last call in ms
	 * @return whether this animation ended
	 */
	public boolean apply(int delta, float w, float h) {
		if (startIn >= 0) {
			startIn -= delta;
			if (startIn <= 0) {
				delta = -startIn;
				startIn = -1;
			} else {
				delta = 0;
			}
			firstTime(w, h);
			lastAlpha = 0;
		}
		if (delta < 3)
			return false;
		remaining -= delta;
		float alpha = 0;
		if (remaining <= 0) { //last one
			lastTime();
			lastAlpha = 1;
		} else {
			alpha = 1 - (remaining / (float) durationValue);
			animate(alpha, w, h);
			lastAlpha = alpha;
		}
		return remaining <= 0;
	}

	public boolean hasRenderAnimation() {
		return false;
	}

	public boolean isDone() {
		return remaining <= 0;
	}

	protected abstract void animate(float alpha, float w, float h);

	public void render(GLGraphics g) {
		getAnimatedElement().render(g);
	}

	protected abstract void firstTime(float w, float h);

	protected abstract void lastTime();

	public abstract EAnimationType getType();

	public enum EAnimationType {
		IN, OUT, MOVE, STYLE
	}
}
