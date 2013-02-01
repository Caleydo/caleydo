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
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;

import com.google.common.primitives.Ints;
import com.jogamp.common.nio.Buffers;

/**
 * simpler version of {@link PickingManager} for a view specific setup
 *
 * registration: {@link #addPickingListener(Object, int, IPickingListener)} or
 * {@link #addTypePickingListener(Object, IPickingListener)} or {@link #register(Object)} using annotation
 *
 * querying: {@link #getPickingID(Object, int)}
 *
 * executing: {@link #doPicking(GL2, AGLView)}
 *
 * @author Samuel Gratzl
 *
 */
public class PickingManager2 {
	private static final int PICKING_SIZE = 1024;
	/**
	 * counter variable for picking ids
	 */
	private AtomicInteger idCounter = new AtomicInteger(1);

	/**
	 * indicator, whether any element was hovered in the last run, used for optimization
	 */
	private boolean anyHovered = false;

	private Map<Integer, SpacePickingManager> spacePickingManagers = new HashMap<>();

	/**
	 * returns a space specific picking manager, e.g. one per view instance
	 * 
	 * @param spaceId
	 * @return
	 */
	public SpacePickingManager get(int spaceId) {
		SpacePickingManager p = spacePickingManagers.get(spaceId);
		if (p == null) {
			p = new SpacePickingManager(idCounter);
			spacePickingManagers.put(spaceId, p);
		}
		return p;
	}

	/**
	 * see {@link #doPicking(GL2, PickingMode, Point, IRenderable)} but simpler version for a {@link AGLView}
	 *
	 * @param gl
	 * @param view
	 */
	public void doPicking(final GL2 gl, final AGLView view) {
		PickingMode mode = convertToPickingMode(view.getGLMouseListener());
		Point pickPoint = view.getGLMouseListener().getPickedPoint();

		doPicking(gl, mode, pickPoint, new Runnable() {
			@Override
			public void run() {
				view.display(gl);
			}
		});
	}

	/**
	 * performs picking on the given view and calls all the registered picking listeners
	 *
	 * @param gl
	 *            the gl context to use
	 * @param mode
	 *            the picking mode, if null only the last mouseOut will be executed
	 * @param pickPoint
	 *            the position of the mouse
	 * @param toRender
	 *            the element to render
	 */
	public void doPicking(GL2 gl, PickingMode mode, Point pickPoint, Runnable toRender) {
		BitSet picked = new BitSet();
		float depth = 0.0f;

		if (mode == null)
			return;
		if (pickPoint != null) {
			Pair<int[], Float> tmp = doPickingImpl(pickPoint.x, pickPoint.y, gl, toRender);
			for (int pi : tmp.getFirst())
				picked.set(pi);
			depth = tmp.getSecond();
		}

		if (picked.isEmpty() && !(anyHovered || mode == PickingMode.MOUSE_OUT))
			return;

		anyHovered = false;
		for (SpacePickingManager spacePicker : this.spacePickingManagers.values()) {
			anyHovered = spacePicker.doPicking(picked, mode, depth, pickPoint) || anyHovered;
		}
	}

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
		} else {
			return null; // no picking
		}
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
	static Pair<int[], Float> doPickingImpl(float x, float y, GL2 gl, Runnable toRender) {
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
	private static Pair<int[], Float> processHits(int hitCount, int[] pickingBuffer) {
		int iPickingBufferCounter = 0;

		ArrayList<Integer> iAlPickedObjectId = new ArrayList<Integer>(4);

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

		for (int i = 0; i < iNumberOfNames; i++) {
			iAlPickedObjectId.add(pickingBuffer[iNearestObjectIndex + 3 + i]);
		}

		return Pair.make(Ints.toArray(iAlPickedObjectId), fMinimumZValue);
	}

	private static float getDepth(int zvalue) {
		long depth = zvalue; // large -ve number
		// return as a float between 0 and 1
		return (1.0f + ((float) depth / 0x7fffffff));
	}


}
