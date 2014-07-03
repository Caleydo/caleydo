/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.animation;


/**
 * basic for animation description
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AAnimation implements Comparable<AAnimation> {
	protected boolean started;
	protected int remaining;
	protected final int duration;

	public AAnimation(int duration) {
		this.duration = duration;
		this.remaining = this.duration;
	}

	/**
	 * @return the remaining, see {@link #remaining}
	 */
	public final int getRemaining() {
		return remaining;
	}

	/**
	 * @return the started, see {@link #started}
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * elapsed time of this animation
	 *
	 * @return
	 */
	public final int getElapsed() {
		return duration - remaining;
	}

	@Override
	public final int compareTo(AAnimation o) {
		int r = this.getRemaining() - o.getRemaining();
		if (r != 0)
			return r;
		return getType().ordinal() - o.getType().ordinal();
	}

	public final boolean isDone() {
		return remaining <= 0;
	}

	public abstract EAnimationType getType();

	public enum EAnimationType {
		IN, OUT, MOVE
	}
}
