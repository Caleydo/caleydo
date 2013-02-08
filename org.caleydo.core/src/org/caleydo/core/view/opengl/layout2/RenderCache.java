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

	public void invalidate(IElementContext context) {
		validCounter = 0;
		if (displayListIndex >= 0) { // free the display list
			DisplayListPool pool = context.getDisplayListPool();
			if (pool.isRecording(displayListIndex)) {
				// lazy stopping
				return;
			} else {
				freeDisplayList(pool);
			}
		}
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
	public boolean render(IElementContext context, GLGraphics g) {
		if (displayListIndex <= 0)
			return false;
		DisplayListPool pool = context.getDisplayListPool();
		if (pool.isRecording() && validCounter < 100) {
			// use the higher display list for caching and release me if I'm not a really long stable part
			freeDisplayList(pool);
			return false;
		}
		g.gl.glCallList(displayListIndex);
		return true;
	}

	/**
	 * starts optionally recording a display list
	 *
	 * @param context
	 * @param g
	 */
	public void begin(IElementContext context, GLGraphics g, float w, float h) {
		DisplayListPool pool = context.getDisplayListPool();
		if (enableCaching(pool, w, h)) {
			displayListIndex = pool.checkOut(g);
			if (displayListIndex >= 0) { // got one
				pool.startRecording(displayListIndex);
				g.gl.glNewList(displayListIndex, GL2.GL_COMPILE_AND_EXECUTE);
			}
		}
		if (validCounter < 0)
			validCounter--;
		else
			validCounter++;
	}

	private boolean enableCaching(DisplayListPool pool, float w, float h) {
		// TODO better determine strategy + ensure that it will be correctly notified
		if (validCounter <= 0 || pool.isRecording())
			return false;
		if (w * h < 1000) // too small area
			return false;
		if (validCounter > 30) // 30 frames no change and not yet recording
			return true;
		return false;
	}
	/**
	 * stops recording
	 *
	 * @param g
	 */
	public void end(IElementContext context, GLGraphics g) {
		if (displayListIndex >= 0) {
			DisplayListPool pool = context.getDisplayListPool();
			g.gl.glEndList();
			pool.stopRecording();
			if (validCounter == 0) { // invalidated inbetween
				freeDisplayList(pool);
			}
		}
	}

	public void takeDown(IElementContext context) {
		invalidate(context);
	}
}

