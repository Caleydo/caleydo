/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
	public int checkOut(GL2 gl) {
		if (isRecording()) // no new display lists during recording
			return -1;
		if (displayListIndices.isEmpty())
			createDisplayListIndices(gl);
		if (displayListIndices.isEmpty()) { // have no
			return -1;
		}
		int i = displayListIndices.pop();
		return i;
	}

	private void createDisplayListIndices(GL2 gl) {
		if (isRecording()) // no allocation if we are recording
			return;
		if (totalAllocated >= MAX_DISPLAY_LISTS) // not more than 100
			return;
		int start = gl.glGenLists(10);
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
