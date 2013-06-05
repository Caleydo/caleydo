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
		// if (numChars > 0)
		// return false;
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

