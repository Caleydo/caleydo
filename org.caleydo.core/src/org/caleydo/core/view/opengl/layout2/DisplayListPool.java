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

import java.util.ArrayDeque;
import java.util.Deque;

import javax.media.opengl.GL2;

/**
 * pool of display list indices, with reusing
 *
 * @author Samuel Gratzl
 *
 */
public class DisplayListPool {
	private static final int MAX_DISPLAY_LISTS = 512;

	private Deque<Integer> displayListIndices = new ArrayDeque<>();
	private int totalAllocated = 0;
	private int currentRecordingDisplayList = -1;

	/**
	 * returns an id of a free display list index
	 *
	 * @param g
	 * @return
	 */
	public int checkOut(GLGraphics g) {
		if (displayListIndices.isEmpty())
			createDisplayListIndices(g);
		if (displayListIndices.isEmpty()) {
			return -1;
		}
		int i = displayListIndices.pop();
		return i;
	}

	private void createDisplayListIndices(GLGraphics g) {
		if (isRecording()) // no allocation if we are recording
			return;
		if (totalAllocated >= MAX_DISPLAY_LISTS) // not more than 100
			return;
		int start = g.gl.glGenLists(10);
		totalAllocated += 10;
		for (int i = 0; i < 10; ++i)
			displayListIndices.push(start + i);
	}

	/**
	 * puts the display list index back into the pool
	 *
	 * @param id
	 */
	public void checkIn(int id) {
		displayListIndices.push(id);
	}

	/**
	 * will a display list currently recorded
	 *
	 * @return
	 */
	public boolean isRecording() {
		return currentRecordingDisplayList >= 0;
	}

	void startRecording(int displayListIndex) {
		assert !isRecording();
		currentRecordingDisplayList = displayListIndex;
	}

	public boolean isRecording(int displayListIndex) {
		return currentRecordingDisplayList == displayListIndex;
	}

	void stopRecording() {
		assert isRecording();
		currentRecordingDisplayList = -1;
	}

	/**
	 * cleanup display lists
	 * 
	 * @param gl
	 */
	void deleteAll(GL2 gl) {
		for (Integer d : this.displayListIndices) {
			gl.glDeleteLists(d.intValue(), 1);
		}
	}

}
