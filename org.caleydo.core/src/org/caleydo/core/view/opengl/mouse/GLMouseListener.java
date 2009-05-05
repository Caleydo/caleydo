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

import org.caleydo.core.view.opengl.canvas.AGLEventListener;

/**
 * Mouse picking listener for JOGL views
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
	private ArrayList<AGLEventListener> alGlCanvas;

	private Point pickedPointDragStart;
	private Point pickedPointCurrent;

	private boolean bLeftMouseButtonPressed = false;
	private boolean bRightMouseButtonPressed = false;
	private boolean bMouseMiddleButtonPressed = false;
	private boolean bMouseReleased = false;
	private boolean bMouseDragged = false;
	private boolean bMouseMoved = false;
	private boolean bMouseDoubleClick = false;

	private boolean bMouseLeft_StandbyZoom = false;
	private boolean bMouseRight_StandbyRotate = false;

	private int prevMouseX, prevMouseY;

	private float fZoomScale = 0.072f;
	private float fPanScale = 3.1f;

	private boolean bEnablePan = true;
	private boolean bEnableRotate = true;
	private boolean bEnableZoom = true;

	/**
	 * Define mouse sensitivity. Higher value indicates more degrees of rotation. Default value 1.0
	 */
	private float fMouseSensitivityRotation = 1.0f;

	/**
	 * Constructor.
	 */
	public GLMouseListener() {
		super();
		pickedPointDragStart = new Point();

		alGlCanvas = new ArrayList<AGLEventListener>();
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {

		bMouseReleased = false;
		bLeftMouseButtonPressed = false;
		bRightMouseButtonPressed = false;

		pickedPointDragStart.setLocation(mouseEvent.getPoint());

		prevMouseX = mouseEvent.getX();
		prevMouseY = mouseEvent.getY();

		if (mouseEvent.getClickCount() > 1) {
			bMouseDoubleClick = true;
			// System.out.println("Double click!");
			return;
		}

		if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
			bLeftMouseButtonPressed = true;
		}
		else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {

			if (bMouseLeft_StandbyZoom) {
				/*
				 * first button pressed was "middle" and it is still pressed, but also "right" button is
				 * pressed now..
				 */
				bMouseMiddleButtonPressed = true;
			}
			else {
				/*
				 * first button pressed was NOT "middle" and but now "right" button is pressed..
				 */
				bRightMouseButtonPressed = true;
				bMouseRight_StandbyRotate = true;
			}
		}

		/* --- Middle -- Mouse Button --- */
		if ((mouseEvent.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
			bMouseMiddleButtonPressed = true;
			// System.err.println(" -- Middle --");

			if (bMouseRight_StandbyRotate) {
				/*
				 * first button was "right" and "right"-button is still pressed
				 */
				bRightMouseButtonPressed = false;
			}
			else {
				/*
				 * enable standby zoom... First button pressed is "middle"
				 */
				bMouseLeft_StandbyZoom = true;
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent mouseEvent) {

		bMouseMoved = true;
		pickedPointCurrent = mouseEvent.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent) {

		bMouseDragged = false;

		if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
			pickedPointCurrent = mouseEvent.getPoint();
		}

		bMouseReleased = true;

		bMouseDoubleClick = false;

		if ((mouseEvent.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
			bMouseLeft_StandbyZoom = false;
			bMouseMiddleButtonPressed = false;

			if (bMouseRight_StandbyRotate) {
				/*
				 * first button pressed was "right" and now "left" button is released ==> same state as if
				 * only "right" button was pressed.
				 */
				bRightMouseButtonPressed = true;
			}
		}

		if ((mouseEvent.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {

			if (bMouseLeft_StandbyZoom) {
				/*
				 * First "left" button was pressed and not released since and "right" button was pressed now
				 * ==> emmulate "middle" button
				 */
				bMouseMiddleButtonPressed = false;
			}
			else {
				bRightMouseButtonPressed = false;

				/*
				 * Now "right" button is released ==> no more standby RightButton.
				 */
				bMouseRight_StandbyRotate = false;
			}
		}

		if ((mouseEvent.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
			bMouseMiddleButtonPressed = false;
		}
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent) {

		bMouseDragged = true;
		pickedPointCurrent = mouseEvent.getPoint();

		int x = mouseEvent.getX();
		int y = mouseEvent.getY();
		Dimension size = mouseEvent.getComponent().getSize();

		if (!bRightMouseButtonPressed) {
			if (!bMouseMiddleButtonPressed && bEnableRotate) {

				/**
				 * --- ROTATION ---
				 */
				Rotf currentRotX = new Rotf();
				Rotf currentRotY = new Rotf();

				float fpercentX = (float) (x - prevMouseX) / (float) size.width * fMouseSensitivityRotation;
				float fpercentY = (float) (y - prevMouseY) / (float) size.height * fMouseSensitivityRotation;

				currentRotX.set(new Vec3f(0, 1, 0), fpercentX * (float) Math.PI);
				currentRotY.set(new Vec3f(1, 0, 0), fpercentY * (float) Math.PI);

				/* concatinate rotations.. */
				currentRotX = currentRotX.times(currentRotY);

				prevMouseX = x;
				prevMouseY = y;

				/* set new paramters to ViewCamera */
				Iterator<AGLEventListener> iterGLCanvas = alGlCanvas.iterator();

				while (iterGLCanvas.hasNext()) {
					iterGLCanvas.next().getViewCamera().addCameraRotation(currentRotX);
				}
			}
			else if (bEnableZoom) {
				/**
				 * --- ZOOMING ---
				 */
				float zoomX = fZoomScale * (x - prevMouseX);
				float zoomY = fZoomScale * (prevMouseY - y);

				/* take abs(zoomX) */
				if (zoomX < 0.0f && zoomY > 0.0f) {
					zoomX = -zoomX;
				}

				prevMouseX = x;
				prevMouseY = y;

				/* set new paramters to ViewCamera */
				Iterator<AGLEventListener> iterGLCanvas = alGlCanvas.iterator();

				while (iterGLCanvas.hasNext()) {
					iterGLCanvas.next().getViewCamera().addCameraScale(new Vec3f(0, 0, zoomY + zoomX));
				}
			}
		}
		else if (bEnablePan) {
			/**
			 * --- PANING ---
			 */
			Vec3f addVec3f =
				new Vec3f(fPanScale * (float) (x - prevMouseX) / (float) size.width, fPanScale
					* (float) (prevMouseY - y) / (float) size.height, 0.0f);

			prevMouseX = x;
			prevMouseY = y;

			/* set new paramters to ViewCamera */
			Iterator<AGLEventListener> iterGLCanvas = alGlCanvas.iterator();

			while (iterGLCanvas.hasNext()) {
				iterGLCanvas.next().getViewCamera().addCameraPosition(addVec3f);
			}
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!bEnableZoom)
			return;

		/**
		 * --- NORMAL ZOOM ---
		 */
		float fZoom = fZoomScale * e.getWheelRotation();

		Iterator<AGLEventListener> iterGLCanvas = alGlCanvas.iterator();

		while (iterGLCanvas.hasNext()) {
			iterGLCanvas.next().getViewCamera().addCameraScale(new Vec3f(0, 0, fZoom));
		}
	}

	public final boolean wasLeftMouseButtonPressed() {
		boolean bTmp = bLeftMouseButtonPressed;
		bLeftMouseButtonPressed = false;
		return bTmp;
	}

	public final boolean wasMouseDoubleClicked() {
		return bMouseDoubleClick;
	}

	public final boolean wasRightMouseButtonPressed() {
		boolean bTmp = bRightMouseButtonPressed;
		bRightMouseButtonPressed = false;
		return bTmp;
	}

	public final boolean wasMouseMoved() {
		boolean bTmp = bMouseMoved;
		bMouseMoved = false;
		return bTmp;
	}

	public final boolean wasMouseReleased() {
		return bMouseReleased;
	}

	public final boolean wasMouseDragged() {
		return bMouseDragged;
	}

	public final Point getPickedPoint() {
		return pickedPointCurrent;
	}

	public final Point getPickedPointDragStart() {
		return pickedPointDragStart;
	}

	public void setNavigationModes(boolean bEnablePan, boolean bEnableRotate, boolean bEnableZoom) {
		this.bEnablePan = bEnablePan;
		this.bEnableRotate = bEnableRotate;
		this.bEnableZoom = bEnableZoom;
	}

	public void addGLCanvas(final AGLEventListener gLCanvas) {

		alGlCanvas.add(gLCanvas);
	}
}
