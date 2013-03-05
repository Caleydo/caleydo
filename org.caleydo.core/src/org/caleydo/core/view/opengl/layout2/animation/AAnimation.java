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

import org.caleydo.core.view.opengl.layout2.animation.Durations.IDuration;

/**
 * basic for animation description
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AAnimation implements Comparable<AAnimation> {
	protected int startIn;
	protected int remaining;
	private final IDuration duration; // TODO
	protected final int durationValue;

	public AAnimation(int startIn, IDuration duration) {
		this.duration = duration;
		this.durationValue = duration.getDuration();
		this.startIn = startIn;
		this.remaining = this.durationValue;
	}

	/**
	 * @return the remaining, see {@link #remaining}
	 */
	public final int getRemaining() {
		return remaining;
	}

	/**
	 * returns when this animations stops
	 *
	 * @return
	 */
	public final int getStopAt() {
		return startIn + remaining;
	}

	/**
	 * elapsed time of this animation
	 *
	 * @return
	 */
	public final int getElapsed() {
		return durationValue - remaining;
	}

	/**
	 * when this animation starts or -1 for active ones
	 *
	 * @return
	 */
	public final int getStartIn() {
		return startIn;
	}

	/**
	 * does this animation currently runs
	 *
	 * @return
	 */
	public final boolean isRunning() {
		return startIn < 0;
	}

	@Override
	public final int compareTo(AAnimation o) {
		int r = this.getStopAt() - o.getStopAt();
		if (r != 0)
			return r;
		return getType().ordinal() - o.getType().ordinal();
	}

	public final boolean isDone() {
		return remaining <= 0;
	}

	public abstract EAnimationType getType();

	public enum EAnimationType {
		IN, OUT, MOVE, STYLE, CUSTOM
	}
}
