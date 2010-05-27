package com.jmex.game.state.load;

/**
 * Useful for scenarios where you may want to conditionally display a Loader but don't want to
 * add all the checks in your code. This loader does nothing.
 * 
 * @author Matthew D. Hicks
 */
public class NullLoader implements Loader {
	public float increment() {
		return 0;
	}

	public float increment(int steps) {
		return 0;
	}

	public float increment(String activity) {
		return 0;
	}

	public float increment(int steps, String activity) {
		return 0;
	}

	public void setProgress(float progress) {
	}

	public void setProgress(float progress, String activity) {
	}
}
