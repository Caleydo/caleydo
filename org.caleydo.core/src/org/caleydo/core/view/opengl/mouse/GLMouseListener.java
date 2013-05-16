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
package org.caleydo.core.view.opengl.mouse;

import gleem.linalg.Rotf;
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

	private Point pickedPointDragStart = new Point();
	private Point pickedPointCurrent;

	private boolean rightMouseButtonPressed = false;
	private boolean mouseMiddleButtonPressed = false;
	private boolean mouseReleased = false;
	private boolean mouseDragged = false;
	private boolean mouseInCanvas = false;

	private int prevMouseX, prevMouseY;

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
		rightMouseButtonPressed = false;

		pickedPointDragStart.setLocation(mouseEvent.getPoint());

		prevMouseX = mouseEvent.getPoint().x;
		prevMouseY = mouseEvent.getPoint().y;

		if (mouseEvent.getClickCount() > 1) {
//			 System.out.println("Double click!");
			return;
		}

		if (mouseEvent.getButton() == 3)
			rightMouseButtonPressed = true;
	}

	@Override
	public void mouseMoved(IMouseEvent mouseEvent) {
		pickedPointCurrent = mouseEvent.getPoint();
	}

	@Override
	public void mouseClicked(IMouseEvent arg0) {
	}

	@Override
	public void mouseReleased(IMouseEvent mouseEvent) {

		mouseDragged = false;

		if (mouseEvent.getButton() == 1) {
			pickedPointCurrent = mouseEvent.getPoint();
		}

		mouseReleased = true;

		if (mouseEvent.isButtonDown(2)) {
			mouseMiddleButtonPressed = false;
		}
	}

	@Override
	public void mouseDragged(IMouseEvent mouseEvent) {

		mouseDragged = true;
		pickedPointCurrent = mouseEvent.getPoint();

		int x = mouseEvent.getPoint().x;
		int y = mouseEvent.getPoint().y;
		Dimension size = mouseEvent.getParentSize();

		if (!rightMouseButtonPressed) {
			if (!mouseMiddleButtonPressed && enableRotate) {

				/**
				 * --- ROTATION ---
				 */
				Rotf currentRotX = new Rotf();
				Rotf currentRotY = new Rotf();

				float fpercentX = (float) (x - prevMouseX) / (float) size.width * mouseSensitivityRotation;
				float fpercentY = (float) (y - prevMouseY) / (float) size.height * mouseSensitivityRotation;

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

	public final boolean wasRightMouseButtonPressed() {
		boolean bTmp = rightMouseButtonPressed;
		rightMouseButtonPressed = false;
		return bTmp;
	}


	public final boolean wasMouseReleased() {
		return mouseReleased;
	}

	public final boolean wasMouseDragged() {
		return mouseDragged;
	}

	public final Point getPickedPoint() {
		return pickedPointCurrent;
	}

	public final Point getPickedPointDragStart() {
		return pickedPointDragStart;
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
