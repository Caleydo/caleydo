/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import com.google.common.base.Stopwatch;

/**
 * utility class for computing and handling time differences
 *
 * @author Samuel Gratzl
 *
 */
public class TimeDelta {
	private final Stopwatch stopWatch = new Stopwatch();

	/**
	 * return the time between the last call and the current one, reset the timer
	 * 
	 * @return
	 */
	public int getDeltaTimeMs() {
		int deltaTimeMs = 0;
		if (stopWatch.isRunning()) {
			deltaTimeMs = (int) stopWatch.elapsedMillis();
			stopWatch.reset().start();
		} else {
			stopWatch.start();
		}
		return deltaTimeMs;
	}

	public void stop() {
		if (stopWatch.isRunning())
			stopWatch.stop().reset();
	}

	public void reset() {
		stop();
	}
}
