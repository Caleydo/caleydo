/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.Units;
import org.caleydo.core.view.opengl.picking.AdvancedPick;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.eclipse.swt.graphics.Point;

/**
 * Show an image with some layers on top of it. Layers are pickable using the
 * alpha value.
 *
 * @author Thomas Geymayer
 *
 */
public class GLImageViewer extends GLZoomPanContainer {
	protected IGLCanvas canvas;

	/**
	 * Position of last mouse event.
	 */
	protected Point mousePos = new Point(-1, -1);

	/**
	 * Element the cursor is currently hovering.
	 */
	protected GLElement hoverElement = null;

	/**
	 * Last active element (eg. element before mouse left)
	 */
	protected GLElement lastElement = null;

	/**
	 * Depth value of topmost layer.
	 */
	protected float lastZ;

	public GLImageViewer() {
		elementStack.setVisibility(EVisibility.PICKABLE);
		elementStack.onPick(new IPickingListener() {
			@Override
			public void pick(Pick pick) {

				if (pick.getPickingMode() == PickingMode.MOUSE_OUT) {
					hoverElement = null;
				} else if (pick.getPickingMode() != PickingMode.DRAGGED) {
					mousePos.x = canvas.toRawPixel(pick.getPickedPoint(Units.DIP).x());
					mousePos.y = canvas.toRawPixel(canvas.getDIPHeight() - pick.getPickedPoint(Units.DIP).y());
				}

				if (hoverElement != lastElement) {
					if (lastElement != null)
						lastElement.handlePick(new AdvancedPick(pick, lastElement, PickingMode.MOUSE_OUT));

					if (hoverElement != null)
						hoverElement.handlePick(new AdvancedPick(pick, hoverElement, PickingMode.MOUSE_OVER));

					lastElement = hoverElement;
				}

				if (lastElement == null)
					return;

				if (pick.getPickingMode() != PickingMode.MOUSE_OUT && pick.getPickingMode() != PickingMode.MOUSE_OVER)
					lastElement.handlePick(pick);
			}
		});
	}

	@Override
	public void clear() {
		super.clear();
		lastZ = 0;
	}

	/**
	 * Set the image shown in the base layer.
	 *
	 * @param path
	 * @return
	 */
	public GLElement setBaseImage(String path) {
		GLImageElement baseImg = new GLImageElement(path);
		if (isEmpty())
			add(baseImg);
		else
			set(0, baseImg);
		return baseImg;
	}

	/**
	 * Add a layer on top of the base image and all added layers.
	 *
	 * @param imgPath
	 *            Path to image used for this layer.
	 * @param maskPath
	 *            Path to an image which will be used to extend the area used
	 *            for picking
	 * @return
	 */
	public GLImageElement addLayer(String imgPath, String maskPath) {

		// OpenGL picking does not work with alpha transparency. Therefore we
		// use a different z value for each pickable layer and read back the
		// depth value at the position of the cursor afterwards, to get the
		// correct layer.
		lastZ += 0.05f;

		GLImageElement element = new GLImageElement(imgPath);
		element.setAlphaThreshold(.3f);
		element.setzDelta(lastZ);
		add(element);

		if (maskPath != null && !maskPath.isEmpty()) {
			GLImageElement mask = new GLImageElement(maskPath);
			mask.setzDelta(lastZ);
			mask.setColor(new Color(1, 1, 1, .04f));
			mask.setAlphaThreshold(.01f);
			add(mask);
		}

		return element;
	}

	/**
	 * Add an image layer on top of all already added layers and the base image.
	 *
	 * @param imgPath
	 * @return
	 */
	public GLImageElement addLayer(String imgPath) {
		return addLayer(imgPath, null);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);

		if (mousePos.x < 0 || mousePos.y < 0)
			return;

		hoverElement = null;

		// Check the depth at the mouse position
		FloatBuffer winZ = FloatBuffer.allocate(1);
		g.gl.glReadPixels(mousePos.x, mousePos.y, 1, 1, GL2ES2.GL_DEPTH_COMPONENT, GL.GL_FLOAT, winZ);
		mousePos.x = -1;

		float[] m = new float[16];
		g.gl.glGetFloatv(GLMatrixFunc.GL_PROJECTION_MATRIX, m, 0);
		float zScale = m[10];

		// TODO check the offset
		float baseZ = g.z() + content.getzDelta() + .1f;
		float z = (winZ.get(0) * 2 - 1) / zScale - baseZ;

		if (z < 0.01)
			return;

		// Get the element with a depth value equal to the read value
		for (GLElement element : elementStack)
			if (Math.abs(z - element.getzDelta()) < 0.01) {
				hoverElement = element;
				break;
			}
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		canvas = findParent(AGLElementView.class).getParentGLCanvas();
	}
}
