/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.mouse;

import gleem.linalg.Rotf;
import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener;

/**
 * Mouse picking listener for JOGL2 views
 *
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class GLMouseListener implements IGLMouseListener {

	/**
	 * All canvas objects which camera is manipulated by the mouse listener
	 */
	private ArrayList<AGLView> glCanvasList = new ArrayList<AGLView>();

	private Vec2f pickedPointDragStart = new Vec2f();
	private Vec2f pickedPointCurrent;
	private Point pickedRAWPointCurrent;

	private boolean leftMouseButtonPressed = false;
	private boolean rightMouseButtonPressed = false;
	private boolean mouseMiddleButtonPressed = false;
	private boolean mouseReleased = false;
	private boolean mouseDragged = false;
	private boolean mouseMoved = false;
	private boolean mouseDoubleClick = false;
	private boolean mouseInCanvas = false;

	private float prevMouseX, prevMouseY;

	private float zoomScale = 0.072f;
	private float panScale = 3.1f;

	private boolean enablePan = true;
	private boolean enableRotate = true;
	private boolean enableZoom = true;

	/**
	 * Define mouse sensitivity. Higher value indicates more degrees of rotation. Default value 1.0
	 */
	private float mouseSensitivityRotation = 1.0f;


	@Override
	public void mousePressed(IMouseEvent mouseEvent) {

		mouseReleased = false;
		leftMouseButtonPressed = false;
		rightMouseButtonPressed = false;

		pickedPointDragStart.set(mouseEvent.getPoint());

		prevMouseX = mouseEvent.getPoint().x();
		prevMouseY = mouseEvent.getPoint().y();

		if (mouseEvent.getClickCount() > 1) {
			mouseDoubleClick = true;
//			 System.out.println("Double click!");
			return;
		}

		if (mouseEvent.getButton() == 1)
			leftMouseButtonPressed = true;
		else if (mouseEvent.getButton() == 3)
			rightMouseButtonPressed = true;
	}

	@Override
	public void mouseMoved(IMouseEvent mouseEvent) {
		mouseMoved = true;
		pickedPointCurrent = mouseEvent.getPoint();
		pickedRAWPointCurrent = mouseEvent.getRAWPoint();
	}

	@Override
	public void mouseClicked(IMouseEvent arg0) {
	}

	@Override
	public void mouseReleased(IMouseEvent mouseEvent) {

		mouseDragged = false;

		if (mouseEvent.getButton() == 1) {
			pickedPointCurrent = mouseEvent.getPoint();
			pickedRAWPointCurrent = mouseEvent.getRAWPoint();
		}

		mouseReleased = true;

		mouseDoubleClick = false;

		if (mouseEvent.isButtonDown(2)) {
			mouseMiddleButtonPressed = false;
		}
	}

	@Override
	public void mouseDragged(IMouseEvent mouseEvent) {

		mouseDragged = true;
		pickedPointCurrent = mouseEvent.getPoint();
		pickedRAWPointCurrent = mouseEvent.getRAWPoint();

		float x = pickedPointCurrent.x();
		float y = pickedPointCurrent.y();
		Dimension size = mouseEvent.getParentSize();

		if (!rightMouseButtonPressed) {
			if (!mouseMiddleButtonPressed && enableRotate) {

				/**
				 * --- ROTATION ---
				 */
				Rotf currentRotX = new Rotf();
				Rotf currentRotY = new Rotf();

				float fpercentX = (x - prevMouseX) / size.width * mouseSensitivityRotation;
				float fpercentY = (y - prevMouseY) / size.height * mouseSensitivityRotation;

				currentRotX.set(new Vec3f(0, 1, 0), fpercentX * (float) Math.PI);
				currentRotY.set(new Vec3f(1, 0, 0), fpercentY * (float) Math.PI);

				/* concatinate rotations.. */
				currentRotX = currentRotX.times(currentRotY);

				prevMouseX = x;
				prevMouseY = y;

				/* set new paramters to ViewCamera */
				Iterator<AGLView> iterGLCanvas = glCanvasList.iterator();

				while (iterGLCanvas.hasNext()) {
					iterGLCanvas.next().getViewCamera().addCameraRotation(currentRotX);
				}
			}
			else if (enableZoom) {
				/**
				 * --- ZOOMING ---
				 */
				float zoomX = zoomScale * (x - prevMouseX);
				float zoomY = zoomScale * (prevMouseY - y);

				/* take abs(zoomX) */
				if (zoomX < 0.0f && zoomY > 0.0f) {
					zoomX = -zoomX;
				}

				prevMouseX = x;
				prevMouseY = y;

				/* set new paramters to ViewCamera */
				Iterator<AGLView> iterGLCanvas = glCanvasList.iterator();

				while (iterGLCanvas.hasNext()) {
					iterGLCanvas.next().getViewCamera().addCameraScale(new Vec3f(0, 0, zoomY + zoomX));
				}
			}
		}
		else if (enablePan) {
			/**
			 * --- PANING ---
			 */
			Vec3f addVec3f =
				new Vec3f(panScale * (x - prevMouseX) / size.width,
					panScale * (prevMouseY - y) / size.height, 0.0f);

			prevMouseX = x;
			prevMouseY = y;

			/* set new paramters to ViewCamera */
			Iterator<AGLView> iterGLCanvas = glCanvasList.iterator();

			while (iterGLCanvas.hasNext()) {
				iterGLCanvas.next().getViewCamera().addCameraPosition(addVec3f);
			}
		}
	}

	@Override
	public void mouseWheelMoved(IMouseEvent e) {
		if (!enableZoom)
			return;

		/**
		 * --- NORMAL ZOOM ---
		 */
		float fZoom = zoomScale * e.getWheelRotation();

		Iterator<AGLView> iterGLCanvas = glCanvasList.iterator();

		while (iterGLCanvas.hasNext()) {
			iterGLCanvas.next().getViewCamera().addCameraScale(new Vec3f(0, 0, fZoom));
		}
	}

	@Override
	public void mouseEntered(IMouseEvent arg0) {
		Iterator<AGLView> iterGLCanvas = glCanvasList.iterator();

		while (iterGLCanvas.hasNext()) {
			iterGLCanvas.next().setLazyMode(false);
		}
		mouseInCanvas = true;
	}

	@Override
	public void mouseExited(IMouseEvent e) {
		Iterator<AGLView> iterGLCanvas = glCanvasList.iterator();

		while (iterGLCanvas.hasNext()) {
			iterGLCanvas.next().setLazyMode(true);
		}
		mouseInCanvas = false;
	}

	/**
	 * @return the mouseInCanvas, see {@link #mouseInCanvas}
	 */
	public boolean isMouseInCanvas() {
		return mouseInCanvas;
	}

	public final boolean wasLeftMouseButtonPressed() {
		boolean bTmp = leftMouseButtonPressed;
		leftMouseButtonPressed = false;
		return bTmp;
	}

	public final boolean wasMouseDoubleClicked() {
		boolean bTmp = mouseDoubleClick;
		mouseDoubleClick = false;
		return bTmp;
	}

	public final boolean wasRightMouseButtonPressed() {
		boolean bTmp = rightMouseButtonPressed;
		rightMouseButtonPressed = false;
		return bTmp;
	}

	public final boolean wasMouseMoved() {
		boolean bTmp = mouseMoved;
		mouseMoved = false;
		return bTmp;
	}

	public final boolean wasMouseReleased() {
		return mouseReleased;
	}

	public final boolean wasMouseDragged() {
		return mouseDragged;
	}

	public final Vec2f getDIPPickedPoint() {
		return pickedPointCurrent;
	}

	public Point getRAWPickedPoint() {
		return pickedRAWPointCurrent;
	}

	public final Vec2f getDIPPickedPointDragStart() {
		return pickedPointDragStart;
	}

	@Override
	public void mouseDragDetected(IMouseEvent mouseEvent) {

	}

	public void setNavigationModes(boolean bEnablePan, boolean bEnableRotate, boolean bEnableZoom) {
		this.enablePan = bEnablePan;
		this.enableRotate = bEnableRotate;
		this.enableZoom = bEnableZoom;
	}

	public void addGLCanvas(final AGLView gLCanvas) {

		glCanvasList.add(gLCanvas);
	}
}
