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

package org.caleydo.core.util.system;

/**
 * Implementation of {@link demos.util.Time} interface based on {@link java.lang.System.currentTimeMillis}.
 * Performs smoothing internally to avoid effects of poor granularity of currentTimeMillis on Windows platform
 * in particular.
 */

public class Time {

	private static final int DEFAULT_NUM_SMOOTHING_SAMPLES = 10;

	private long[] samples = new long[DEFAULT_NUM_SMOOTHING_SAMPLES];

	private int numSmoothingSamples;

	private int curSmoothingSample; // Index of current sample to be replaced

	private long baseTime = System.currentTimeMillis();

	private boolean hasCurTime;

	private double curTime;

	private double deltaT;

	/**
	 * Sets number of smoothing samples. Defaults to 10. Note that there may be a discontinuity in the
	 * reported time after a call to this method.
	 */
	public void setNumSmoothingSamples(int num) {

		samples = new long[num];
		numSmoothingSamples = 0;
		curSmoothingSample = 0;
		hasCurTime = false;
	}

	/** Returns number of smoothing samples; default is 10. */
	public int getNumSmoothingSamples() {

		return samples.length;
	}

	/**
	 * Rebases this timer. After very long periods of time the resolution of this timer may decrease; the
	 * application can call this to restore higher resolution. Note that there may be a discontinuity in the
	 * reported time after a call to this method.
	 */
	public void rebase() {

		baseTime = System.currentTimeMillis();
		setNumSmoothingSamples(samples.length);
	}

	/**
	 * Updates this Time object. Call update() each frame before calling the accessor routines.
	 */
	public void update() {

		long tmpTime = System.currentTimeMillis();
		long diffSinceBase = tmpTime - baseTime;
		samples[curSmoothingSample] = diffSinceBase;
		curSmoothingSample = (curSmoothingSample + 1) % samples.length;
		numSmoothingSamples = Math.min(1 + numSmoothingSamples, samples.length);
		// Average of samples is current time
		double newCurTime = 0.0;
		for (int i = 0; i < numSmoothingSamples; i++) {
			newCurTime += samples[i];
		}
		newCurTime /= 1000.0f * numSmoothingSamples;
		double lastTime = curTime;
		if (!hasCurTime) {
			lastTime = newCurTime;
			hasCurTime = true;
		}
		deltaT = newCurTime - lastTime;
		curTime = newCurTime;
	}

	/**
	 * Time in seconds since beginning of application.
	 */
	public double time() {

		return curTime;
	}

	/**
	 * Time in seconds since last update.
	 */
	public double deltaT() {

		return deltaT;
	}
}