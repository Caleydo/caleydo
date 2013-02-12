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
package org.caleydo.core.view.opengl.picking;

import java.awt.Point;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.BitSet;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;

import com.google.common.collect.Lists;
import com.jogamp.common.nio.Buffers;

/**
 * picking logic
 *
 * @author Samuel Gratzl
 *
 */
public class PickingUtils {
	private static final int PICKING_SIZE = 1024;

	/**
	 * uses the given {@link GLMouseListener} to convert the current state to a {@link PickingMode}
	 *
	 * @param glMouseListener
	 * @return
	 */
	public static PickingMode convertToPickingMode(GLMouseListener glMouseListener) {
		if (glMouseListener.wasMouseDoubleClicked()) {
			return PickingMode.DOUBLE_CLICKED;
		} else if (glMouseListener.wasMouseDragged()) {
			return PickingMode.DRAGGED;
		} else if (glMouseListener.wasLeftMouseButtonPressed()) {
			return PickingMode.CLICKED;
		} else if (glMouseListener.wasRightMouseButtonPressed()) {
			return PickingMode.RIGHT_CLICKED;
		} else if (glMouseListener.wasMouseMoved()) {
			return PickingMode.MOUSE_MOVED;
		} else if (glMouseListener.wasMouseReleased()) {
			return PickingMode.MOUSE_RELEASED;
		} else {
			return null; // no picking
		}
	}

	private static final int LEFT_MOUSE_BUTTON = 1;
	private static final int RIGHT_MOUSE_BUTTON = 3;

	private static PickingMode convert(IMouseEvent event, PickingMode type) {
		if (type == PickingMode.CLICKED && event.getClickCount() > 1)
			return PickingMode.DOUBLE_CLICKED;
		if (type == PickingMode.DRAGGED)
			return PickingMode.DRAGGED;
		if (type == PickingMode.CLICKED && event.getButton() == LEFT_MOUSE_BUTTON)
			return PickingMode.CLICKED;
		if (type == PickingMode.CLICKED && event.getButton() == RIGHT_MOUSE_BUTTON)
			return PickingMode.RIGHT_CLICKED;
		if (type == PickingMode.MOUSE_MOVED)
			return PickingMode.MOUSE_MOVED;
		if (type == PickingMode.MOUSE_RELEASED)
			return type;
		return null;
	}

	/**
	 * performs picking using a given {@link PickingMouseListener}
	 *
	 * @param l
	 * @param gl
	 * @param toRender
	 * @param anyWaiting
	 * @param entries
	 * @return
	 */
	public static boolean doPicking(PickingMouseListener l, GL2 gl, Runnable toRender, boolean anyWaiting,
			Iterable<? extends APickingEntry> entries) {

		Deque<Pair<IMouseEvent, PickingMode>> events = l.fetchEvents();
		if (events.isEmpty())
			return anyWaiting;

		// cache for mouse pos to result
		Map<Pair<Integer, Integer>, Pair<BitSet, Float>> cache = new HashMap<>();

		// for each event
		for (Pair<IMouseEvent, PickingMode> event : compress(events)) {
			PickingMode converted = convert(event.getFirst(), event.getSecond());
			Pair<Integer, Integer> key = Pair.make(event.getFirst().getPoint().x, event.getFirst().getPoint().y);
			Pair<BitSet, Float> data = cache.get(key);

			if (event.getSecond() == PickingMode.MOUSE_OUT && anyWaiting) {
				if (data == null) {
					data = doPickingImpl(event.getFirst().getPoint().x, event.getFirst().getPoint().y, gl, toRender);
					cache.put(key, data);
				}
				anyWaiting = fireListeners(entries, data.getFirst(), data.getSecond(), PickingMode.MOUSE_OUT, event
						.getFirst().getPoint(), true);
			} else if (converted != null) {
				if (data == null) {
					data = doPickingImpl(event.getFirst().getPoint().x, event.getFirst().getPoint().y, gl, toRender);
					cache.put(key, data);
				}
				anyWaiting = fireListeners(entries, data.getFirst(), data.getSecond(), converted, event.getFirst()
						.getPoint(), anyWaiting);
			}
		}
		return anyWaiting;
	}

	/**
	 * tries to compress the events to avoid useless one between frames, e.g. multiple mouse moves, dragg,... by keeping
	 * only the last one of each type
	 *
	 * @param events
	 * @return the compressed version
	 */
	private static Iterable<Pair<IMouseEvent, PickingMode>> compress(Deque<Pair<IMouseEvent, PickingMode>> events) {
		if (events.size() == 1) // nothing to compress
			return events;
		Deque<Pair<IMouseEvent, PickingMode>> result = new LinkedList<>();

		EnumSet<PickingMode> has = EnumSet.noneOf(PickingMode.class); // type already seen

		// back to front
		for (Iterator<Pair<IMouseEvent, PickingMode>> it = events.descendingIterator(); it.hasNext();) {
			Pair<IMouseEvent, PickingMode> elem = it.next();
			// just the last one of every type
			if (has.contains(elem.getSecond()))
				continue;
			has.add(elem.getSecond());
			// add to first to keep order of events
			result.addFirst(elem);
		}
		return result;
	}

	/**
	 * implementation of the actual picking logic
	 *
	 * @param mode
	 *            the current {@link PickingMode}
	 * @param mousePos
	 *            the position of the mouse
	 * @param gl
	 * @param toRender
	 *            the element to render for picking
	 * @param anyWaiting
	 *            are the any elements waiting for a mouse_out event
	 * @param entries
	 *            the list of picking entries to check
	 * @return the new state for anyWaiting
	 */
	public static boolean doPicking(PickingMode mode, Point mousePos, final GL2 gl, Runnable toRender,
			boolean anyWaiting,
			Iterable<? extends APickingEntry> entries) {
		BitSet picked = new BitSet();
		float depth = 0.0f;

		if (mode == null) // nothing changed
			return anyWaiting;

		if (mousePos != null) {
			Pair<BitSet, Float> tmp = doPickingImpl(mousePos.x, mousePos.y, gl, toRender);
			picked = tmp.getFirst();
			depth = tmp.getSecond();
		}
		return fireListeners(entries, picked, depth, mode, mousePos, anyWaiting);
	}

	private static boolean fireListeners(Iterable<? extends APickingEntry> entries, BitSet picked, float depth,
			PickingMode mode, Point mousePos, boolean anyWaiting) {
		if (picked.isEmpty() && !(anyWaiting || mode == PickingMode.MOUSE_OUT))
			return anyWaiting;

		anyWaiting = false;
		for (APickingEntry entry : Lists.newArrayList(entries)) {
			if (picked.get(entry.pickingId)) { // currently picked
				if (!entry.isHovered()) {
					// send mouse in
					entry.fire(PickingMode.MOUSE_OVER, mousePos, depth);
				}
				anyWaiting = true;
				entry.fire(mode, mousePos, depth);
			} else if (entry.isDragging() && (mode == PickingMode.MOUSE_RELEASED || mode == PickingMode.DRAGGED)) {
				entry.fire(mode, mousePos, depth);
				anyWaiting = anyWaiting || (mode == PickingMode.DRAGGED);
			} else if (entry.isHovered()) { // was picked last time
				// send mouse out
				entry.fire(PickingMode.MOUSE_OUT, mousePos, depth);
			}
		}
		return anyWaiting;
	}

	/**
	 * picking implementation
	 *
	 * @param x
	 *            mouse x coordinate
	 * @param y
	 *            mouse y coordinate
	 * @param gl
	 * @param toRender
	 * @return a pair containing the list of picked ids and the minimum depth
	 */
	private static Pair<BitSet, Float> doPickingImpl(float x, float y, GL2 gl, Runnable toRender) {
		IntBuffer pickingBufferNative = Buffers.newDirectIntBuffer(PICKING_SIZE);
		int viewport[] = new int[4];

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		gl.glSelectBuffer(PICKING_SIZE, pickingBufferNative);
		gl.glRenderMode(GL2.GL_SELECT);

		gl.glInitNames();

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glPushMatrix();

		// retrieve original
		FloatBuffer perspective = FloatBuffer.allocate(16);
		gl.glGetFloatv(GLMatrixFunc.GL_PROJECTION_MATRIX, perspective);

		gl.glLoadIdentity();

		/* create 5x5 pixel picking region near cursor location */
		GLU glu = new GLU();
		glu.gluPickMatrix(x, (viewport[3] - y),//
				5.0, 5.0, viewport, 0); // pick width and height is set to 5
		// (i.e. picking tolerance)

		// multiply the original back
		gl.glMultMatrixf(perspective);

		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

		toRender.run();

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

		int hitCount = gl.glRenderMode(GL2.GL_RENDER);
		int pickingBuffer[] = new int[PICKING_SIZE];
		pickingBufferNative.get(pickingBuffer);

		return processHits(hitCount, pickingBuffer);
	}

	/**
	 * Extracts the nearest hit from the provided iArPickingBuffer Stores it internally Can process only one hit at at
	 * time at the moment
	 *
	 * @param hitCount
	 * @param pickingBuffer
	 */
	private static Pair<BitSet, Float> processHits(int hitCount, int[] pickingBuffer) {
		int iPickingBufferCounter = 0;


		// Only pick object that is nearest
		int iMinimumZValue = Integer.MAX_VALUE;
		int iNumberOfNames = 0;
		int iNearestObjectIndex = 0;
		float fMinimumZValue = 0;
		for (int iCount = 0; iCount < hitCount; iCount++) {
			// if first object is no hit skip z values
			if (pickingBuffer[iPickingBufferCounter] == 0) {
				iPickingBufferCounter += 3;
				continue;
			}
			// iPickingBufferCounter++;
			// Check if object is nearer than previous objects
			if (pickingBuffer[iPickingBufferCounter + 1] < iMinimumZValue) {
				// first element is number of names on name stack
				// second element is min Z Value
				iMinimumZValue = pickingBuffer[iPickingBufferCounter + 1];

				iNearestObjectIndex = iPickingBufferCounter;

				// third element is max Z Value
				// fourth element is name of lowest name on stack
				// iAlPickedObjectId.add(iArPickingBuffer[iPickingBufferCounter+3
				// ]);
			}
			fMinimumZValue = getDepth(iMinimumZValue);
			// System.out.println("Z Value: " + getDepth(iMinimumZValue));
			iPickingBufferCounter = iPickingBufferCounter + 3 + pickingBuffer[iPickingBufferCounter];

		}

		iNumberOfNames = pickingBuffer[iNearestObjectIndex];

		BitSet picked = new BitSet(8);
		for (int i = 0; i < iNumberOfNames; i++) {
			picked.set(pickingBuffer[iNearestObjectIndex + 3 + i]);
		}
		return Pair.make(picked, fMinimumZValue);
	}

	private static float getDepth(int zvalue) {
		long depth = zvalue; // large -ve number
		// return as a float between 0 and 1
		return (1.0f + ((float) depth / 0x7fffffff));
	}
}
