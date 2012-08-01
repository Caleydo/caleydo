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
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Iterator;

import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Mouse picking listener for JOGL2 views
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class GLMouseListener
	extends MouseAdapter
	implements MouseMotionListener, MouseWheelListener {

	/**
	 * All canvas objects which camera is manipulated by the mouse listener
	 */
	private ArrayList<AGLView> glCanvasList = new ArrayList<AGLView>();

	private Point pickedPointDragStart = new Point();
	private Point pickedPointCurrent;

	private boolean leftMouseButtonPressed = false;
	private boolean rightMouseButtonPressed = false;
	private boolean mouseMiddleButtonPressed = false;
	private boolean mouseReleased = false;
	private boolean mouseDragged = false;
	private boolean mouseMoved = false;
	private boolean mouseDoubleClick = false;

	private int prevMouseX, prevMouseY;

	private float zoomScale = 0.072f;
	private float panScale = 3.1f;

	private boolean enablePan = true;
	private boolean enableRotate = true;
	private boolean enableZoom = true;

	public int[] mousePosition = new int[2];;

	/**
	 * Define mouse sensitivity. Higher value indicates more degrees of rotation. Default value 1.0
	 */
	private float mouseSensitivityRotation = 1.0f;

	@Override
	public void mousePressed(MouseEvent mouseEvent) {

		mouseReleased = false;
		leftMouseButtonPressed = false;
		rightMouseButtonPressed = false;

		pickedPointDragStart.setLocation(mouseEvent.getPoint());

		prevMouseX = mouseEvent.getX();
		prevMouseY = mouseEvent.getY();

		if (mouseEvent.getClickCount() > 1) {
			mouseDoubleClick = true;
//			 System.out.println("Double click!");
			return;
		}

		if (mouseEvent.getButton() == MouseEvent.BUTTON1)
			leftMouseButtonPressed = true;
		else if (mouseEvent.getButton() == MouseEvent.BUTTON3)
			rightMouseButtonPressed = true;
	}

	@Override
	public void mouseMoved(MouseEvent mouseEvent) {
		this.mousePosition[0] = mouseEvent.getXOnScreen();
		this.mousePosition[1] = mouseEvent.getYOnScreen();

		mouseMoved = true;
		pickedPointCurrent = mouseEvent.getPoint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent) {

		mouseDragged = false;

		if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
			pickedPointCurrent = mouseEvent.getPoint();
		}

		mouseReleased = true;

		mouseDoubleClick = false;

		if ((mouseEvent.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
			mouseMiddleButtonPressed = false;
		}
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent) {

		mouseDragged = true;
		pickedPointCurrent = mouseEvent.getPoint();

		int x = mouseEvent.getX();
		int y = mouseEvent.getY();
		Dimension size = mouseEvent.getComponent().getSize();

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
	public void mouseWheelMoved(MouseWheelEvent e) {
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
	public void mouseEntered(MouseEvent arg0) {
		Iterator<AGLView> iterGLCanvas = glCanvasList.iterator();

		while (iterGLCanvas.hasNext()) {
			iterGLCanvas.next().setLazyMode(false);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		Iterator<AGLView> iterGLCanvas = glCanvasList.iterator();

		while (iterGLCanvas.hasNext()) {
			iterGLCanvas.next().setLazyMode(true);
		}
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
