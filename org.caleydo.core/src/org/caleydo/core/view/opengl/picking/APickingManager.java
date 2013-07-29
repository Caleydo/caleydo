/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.picking;

import gleem.linalg.Vec2f;

import java.awt.Point;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;

import com.jogamp.common.nio.Buffers;

/**
 * a basic picking manager
 *
 * @author Samuel Gratzl
 *
 */
public abstract class APickingManager<T extends APickingEntry> {
	private static final int PICKING_SIZE = 1024;
	private static final int LEFT_MOUSE_BUTTON = 1;
	private static final int RIGHT_MOUSE_BUTTON = 3;

	/**
	 * the primary mouse listener to use for picking
	 */
	private final PickingMouseListener listener = new PickingMouseListener();

	// mapping from picking id to entry
	private List<T> mapping = new ArrayList<>();
	// free list for elements not used anymore
	private BitSet free = new BitSet();

	// a list of the current mouse in entries
	private final List<T> mouseIn = new ArrayList<>(3);

	private boolean isAnyDragging = false;

	/**
	 * @return the listener, see {@link #listener}
	 */
	public final IGLMouseListener getListener() {
		return listener;
	}

	public Vec2f getCurrentMousePos() {
		return listener.getCurrentMousePos();
	}

	protected final T get(int pickingId) {
		if (pickingId < 0)
			return null;
		if (pickingId >= mapping.size())
			return null;
		return mapping.get(pickingId);
	}

	protected final int add(T entry) {
		int id = free.nextSetBit(0); // find next free id
		if (id < 0)
			id = mapping.size();
		else
			free.clear(id);
		entry.pickingId = id;
		if (id == mapping.size())
			mapping.add(entry);
		else
			mapping.set(id, entry);
		return id;
	}

	/**
	 * unregister and free a given picking Id
	 *
	 * @param pickingId
	 */
	protected final void remove(T entry) {
		int id = entry.pickingId;
		mouseIn.remove(entry);
		mapping.set(id, null);
		free.set(id);
		entry.pickingId = -1;
	}

	/**
	 * performs picking and calls the registered listeners
	 *
	 * @param gl
	 *            gl context to use
	 * @param toRender
	 *            to runnable to render the picking content
	 */
	public final void doPicking(GL2 gl, Runnable toRender) {
		Collection<Pair<IMouseEvent, PickingMode>> events = compress(this.listener.fetchEvents());
		if (events.isEmpty())
			return;

		// cache for mouse pos to result
		Map<Point, List<PickHit>> cache = new HashMap<>();

		// for each event
		for (Pair<IMouseEvent, PickingMode> event : events) {
			PickingMode converted = convert(event.getFirst(), event.getSecond());
			IMouseEvent mouseEvent = event.getFirst();
			Point key = event.getFirst().getRAWPoint();

			List<PickHit> data = cache.get(key);

			if (event.getSecond() == PickingMode.MOUSE_OUT && !mouseIn.isEmpty()) {
				if (data == null) {
					data = doPickingImpl(key.x, key.y, gl, toRender);
					cache.put(key, data);
				}
				fireListeners(data, PickingMode.MOUSE_OUT, mouseEvent);
			} else if (converted != null) {
				if (data == null) {
					data = doPickingImpl(key.x, key.y, gl, toRender);
					cache.put(key, data);
				}
				fireListeners(data, converted, mouseEvent);
			}
		}
		return;
	}

	private static PickingMode convert(IMouseEvent event, PickingMode type) {
		if (type == PickingMode.CLICKED && event.getClickCount() > 1)
			return PickingMode.DOUBLE_CLICKED;
		if (type == PickingMode.DRAGGED)
			return PickingMode.DRAGGED;
		if (type == PickingMode.CLICKED && event.getButton() == LEFT_MOUSE_BUTTON)
			return PickingMode.CLICKED;
		if (type == PickingMode.CLICKED && event.getButton() == RIGHT_MOUSE_BUTTON)
			return PickingMode.RIGHT_CLICKED;
		if (type == PickingMode.MOUSE_MOVED || type == PickingMode.MOUSE_RELEASED || type == PickingMode.MOUSE_WHEEL)
			return type;
		return null;
	}

	/**
	 * tries to compress the events to avoid useless one between frames, e.g. multiple mouse moves, dragg,... by keeping
	 * only the last one of each type
	 *
	 * @param events
	 * @return the compressed version
	 */
	private static Collection<Pair<IMouseEvent, PickingMode>> compress(Deque<Pair<IMouseEvent, PickingMode>> events) {
		if (events.size() <= 1) // nothing to compress
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
	 * @param entries
	 *            the list of picking entries to check
	 */
	protected void doPicking(PickingMode mode, IMouseEvent event, final GL2 gl, Runnable toRender) {
		if (mode == null) // nothing changed
			return;

		List<PickHit> hits = Collections.emptyList();
		if (event != null) {
			Point rawPoint = event.getRAWPoint();
			hits = doPickingImpl(rawPoint.x, rawPoint.y, gl, toRender);
		}
		fireListeners(hits, mode, event);
	}

	private void fireListeners(List<PickHit> hits, PickingMode mode, IMouseEvent event) {
		PickHit nearest = hits.isEmpty() ? null : hits.get(0);
		float depth = nearest == null ? 0 : nearest.getZMin();

		List<Integer> picked = new ArrayList<>();
		if (nearest != null)
			for (int n : nearest.getNames())
				picked.add(n);

		BitSet wasMouseIn = new BitSet();

		// first send mouse out
		for (Iterator<T> it = this.mouseIn.iterator(); it.hasNext();) {
			T entry = it.next();
			if (picked.contains(entry.pickingId)) { //again picked
				wasMouseIn.set(entry.pickingId);
			} else if ((entry.isDragging() && (mode == PickingMode.MOUSE_RELEASED || mode == PickingMode.DRAGGED))) {
				// not picked but want to be picked
				picked.add(entry.pickingId); // set artificially picked for the dragging case
				wasMouseIn.set(entry.pickingId);
			} else {
				// send mouse out
				entry.fire(PickingMode.MOUSE_OUT, depth, isAnyDragging, event);
			}
		}
		this.mouseIn.clear();

		if (picked.isEmpty())
			return;

		boolean stillDragging = false;
		// second fire in the order of the names
		for (int name : picked) {
			T entry = get(name);
			if (entry == null)
				continue;
			if (!wasMouseIn.get(name)) {
				// send mouse in
				entry.fire(PickingMode.MOUSE_OVER, depth, isAnyDragging, event);
			}
			entry.fire(mode, depth, isAnyDragging, event);
			// query again for handling removal in between
			entry = get(name);
			if (entry != null) {
				mouseIn.add(entry);
				stillDragging = stillDragging || entry.isDragging();
			}
		}
		this.isAnyDragging = stillDragging;
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
	private static List<PickHit> doPickingImpl(float x, float y, GL2 gl, Runnable toRender) {
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

		final int hitCount = gl.glRenderMode(GL2.GL_RENDER);
		final int pickingBuffer[] = new int[PICKING_SIZE];
		pickingBufferNative.get(pickingBuffer);

		List<PickHit> hits = new ArrayList<>(hitCount);

		int pos = 0;
		for (int i = 0; i < hitCount; i++) {
			int nameCount = pickingBuffer[pos++];
			// if first object is no names skip
			if (nameCount == 0) {
				pos += 2;
				continue;
			}
			// iPickingBufferCounter++;
			// Check if object is nearer than previous objects
			int z_min = pickingBuffer[pos++];
			int z_max = pickingBuffer[pos++];
			hits.add(new PickHit(Arrays.copyOfRange(pickingBuffer, pos, pos + nameCount), getDepth(z_min),
					getDepth(z_max)));

			pos += nameCount; // skip names
		}

		Collections.sort(hits); // small z value first
		return hits;
	}

	private static float getDepth(int zvalue) {
		long depth = zvalue; // large -ve number
		// return as a float between 0 and 1
		return (1.0f + ((float) depth / 0x7fffffff));
	}

	/**
	 * interpretation of a the picking buffer
	 *
	 * @author Samuel Gratzl
	 *
	 */
	private static final class PickHit implements Comparable<PickHit> {
		private final int[] names;
		private final float zMin;
		private final float zMax;

		public PickHit(int[] names, float z_min, float z_max) {
			this.names = names;
			this.zMin = z_min;
			this.zMax = z_max;
		}

		public int[] getNames() {
			return names;
		}

		/**
		 * @return the zMin, see {@link #zMin}
		 */
		public float getZMin() {
			return zMin;
		}

		@Override
		public int compareTo(PickHit o) {
			return Float.compare(zMin, o.zMin);
		}
	}
}
