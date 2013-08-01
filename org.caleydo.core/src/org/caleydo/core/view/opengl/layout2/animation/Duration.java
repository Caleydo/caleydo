/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.animation;

/**
 * wrapper around an integer to have a better type for the layout data accessors
 *
 * @author Samuel Gratzl
 *
 */
public final class Duration {
	private final int duration;

	/**
	 *
	 */
	public Duration(int duration) {
		this.duration = duration;
	}

	/**
	 * @return the duration, see {@link #duration}
	 */
	public int getDuration() {
		return duration;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Duration [duration=");
		builder.append(duration);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + duration;
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
		Duration other = (Duration) obj;
		if (duration != other.duration)
			return false;
		return true;
	}

}
