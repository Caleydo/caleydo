/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import javax.media.opengl.GL2;

/**
 * cache implementation for evaluating whether a display list should be used for a task or not
 *
 * @author Samuel Gratzl
 *
 */
public final class RenderCache {
	private int validCounter = 0;
	private int displayListIndex = -1;
	private int numChars;
	private boolean wasDirty;
	private int numVertices;

	/**
	 * is the cache currently active, i.e a display list is used
	 * 
	 * @return
	 */
	public boolean isActive() {
		return displayListIndex >= 0;
	}

	/**
	 *
	 * @param pool
	 * @return returns the last validCounter
	 */
	public int invalidate(DisplayListPool pool) {
		int bak = validCounter;
		validCounter = 0;
		if (displayListIndex >= 0) { // free the display list
			if (pool.isRecording(displayListIndex)) {
				// lazy stopping
			} else {
				freeDisplayList(pool);
			}
		}
		return bak;
	}

	private void freeDisplayList(DisplayListPool pool) {
		pool.checkIn(displayListIndex);
		displayListIndex = -1;
	}

	/**
	 * renders the display lists
	 *
	 * @param g
	 * @return true if a cache was used
	 */
	public boolean render(DisplayListPool pool, GL2 gl) {
		if (displayListIndex <= 0)
			return false;
		if (pool.isRecording() && validCounter < 100) {
			// use the higher display list for caching and release me if I'm not a really long stable part
			freeDisplayList(pool);
			return false;
		}
		gl.glCallList(displayListIndex);
		return true;
	}

	/**
	 * starts optionally recording a display list
	 *
	 * @param context
	 * @param g
	 */
	public void begin(DisplayListPool pool, GLGraphics g, float w, float h) {
		if (enableCaching(pool, g.getStats(), w, h)) {
			displayListIndex = pool.checkOut(g.gl);
			if (displayListIndex >= 0) { // got one
				pool.startRecording(displayListIndex);
				g.gl.glNewList(displayListIndex, GL2.GL_COMPILE_AND_EXECUTE);
			}
		}
		updateStats(g.getStats(), false);
		if (validCounter < 0)
			validCounter--;
		else
			validCounter++;
	}

	private boolean enableCaching(DisplayListPool pool, GLGraphicsStats stats, float w, float h) {
		// TODO better determine strategy + ensure that it will be correctly notified
		if (validCounter <= 0 || pool.isRecording())
			return false;
		if (validCounter < 30) // 30 frames no change and not yet recording
			return false;
		if (w * h < 2500 || numVertices < 100) // too small area
			return false;
		// TODO no cache on text
		if (numChars > 0)
			return false;
		if (wasDirty)
			return false;
		return true;
	}

	/**
	 * @param stats
	 */
	private void updateStats(GLGraphicsStats stats, boolean end) {
		if (validCounter < 20)
			return;
		if (end) {
			numChars += stats.getNumChars();
			numVertices += stats.getNumVertices();
			wasDirty = stats.isDirtyTextTexture();
		} else {
			numChars = -stats.getNumChars();
			numVertices = -stats.getNumVertices();
			wasDirty = stats.isDirtyTextTexture();
		}
	}

	/**
	 * stops recording
	 *
	 * @param g
	 */
	public void end(DisplayListPool pool, GLGraphics g) {
		updateStats(g.getStats(), true);
		if (displayListIndex >= 0) {
			g.gl.glEndList();
			pool.stopRecording();
			if (throwAwayRecordedTexture()) { // invalidated in between
				freeDisplayList(pool);
			}
		}
	}


	private boolean throwAwayRecordedTexture() {
		if (validCounter == 0)
			return true;
		if (wasDirty)
			return true;
		return false;
	}

	public void takeDown(DisplayListPool pool) {
		invalidate(pool);
	}
}

