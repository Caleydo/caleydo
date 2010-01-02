package org.caleydo.core.view.opengl.canvas.glyph.gridview;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Specialized mouse wheel listener for the glyph plane view
 * 
 * @author Sauer Stefan
 */
public class GlyphMouseListener
	implements MouseListener, MouseMotionListener, MouseWheelListener {
	protected float fZoomScale = 0.072f;

	protected float fPanScale = 3.0f;

	private AGLView viewCanvas;

	protected int prevMouseX, prevMouseY;

	protected boolean bEnablePan = false;
	protected boolean bEnableRotate = false;
	protected boolean bEnableZoom = false;

	protected Rotf currentRotation = null;

	protected float fMouseSensitivityRotation = 1.0f;

	/**
	 * Constructor.
	 */
	public GlyphMouseListener(final AGLView remoteRendering3D) {
		this.viewCanvas = remoteRendering3D;
		this.currentRotation = new Rotf();
	}

	/**
	 * Returns the Height of the camera
	 * 
	 * @return
	 */
	public float getCameraHeight() {
		Vec3f camPos = viewCanvas.getViewCamera().getCameraPosition();
		float fRot = viewCanvas.getViewCamera().getCameraRotation().get(new Vec3f(0, 0, 1));

		float height = camPos.y() * (float) Math.sin(fRot) + camPos.z() * (float) Math.cos(fRot);

		return height;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		handleZooming(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		if (prevMouseX == 0 && prevMouseY == 0) {
			prevMouseX = e.getX();
			prevMouseY = e.getY();
		}

		/* --- Middle -- Mouse Button --- */
		if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0)
			handleRotation(e);

		/* --- Right -- Mouse Button --- */
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
			handlePanning(e);

	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		/* --- Right -- Mouse Button --- */
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
			prevMouseX = e.getX();
			prevMouseY = e.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	/**
	 * This handles the panning for the view. It fly's over the drawing plane
	 * 
	 * @param e
	 *            MouseEvent
	 */
	private void handlePanning(MouseEvent e) {
		if (!bEnablePan)
			return;

		int x = e.getX();
		int y = e.getY();
		Dimension size = e.getComponent().getSize();

		float rot = viewCanvas.getViewCamera().getCameraRotation().get(new Vec3f(0, 0, 1));
		fPanScale = -viewCanvas.getViewCamera().getCameraPosition().z();

		Vec3f addVec3f =
			new Vec3f(fPanScale * (x - prevMouseX) / size.width, fPanScale * (prevMouseY - y) / size.height
				* (float) Math.cos(rot), -fPanScale * (prevMouseY - y) / size.height * (float) Math.sin(rot));

		prevMouseX = x;
		prevMouseY = y;

		viewCanvas.getViewCamera().addCameraPosition(addVec3f);
	}

	/**
	 * This handles the rotation for the view. Not used for now
	 * 
	 * @param MouseEvent
	 *            e
	 */
	private void handleRotation(MouseEvent e) {
		if (!bEnableRotate)
			return;
	}

	/**
	 * This handles the zooming for the view.
	 * 
	 * @param MouseEvent
	 *            e
	 */
	private void handleZooming(MouseWheelEvent e) {
		if (!bEnableZoom)
			return;
		float fZoom = fZoomScale * e.getWheelRotation();
		Vec3f camPosOld = viewCanvas.getViewCamera().getCameraPosition();
		float fRotOld = viewCanvas.getViewCamera().getCameraRotation().get(new Vec3f(0, 0, 1));

		Vec3f addVec3f =
			new Vec3f(0, (fZoom * (float) Math.sin(fRotOld)), fZoom * (float) Math.cos(fRotOld) + fZoom);

		Vec3f camPosNew = new Vec3f(camPosOld);
		camPosNew.add(addVec3f);

		float height = camPosNew.y() * (float) Math.sin(fRotOld) + camPosNew.z() * (float) Math.cos(fRotOld);

		if (height > -3)
			return;

		float angle = (float) (Math.PI / 4.0 - 2.0 * Math.PI * -(camPosOld.z() + 3) / 360.0);

		if (angle < 0) {
			angle = 0;

			if (addVec3f.z() < 0) {
				addVec3f = new Vec3f(0, 0, 0);
			}
		}

		viewCanvas.getViewCamera().addCameraPosition(addVec3f);

		Rotf t = new Rotf();
		t.set(new Vec3f(-1, 0, 0), angle);
		viewCanvas.getViewCamera().setCameraRotation(t);
	}

	/**
	 * Enables / Disables Mouse Zoom, Pan & Rotating Rotating is not functional in this special MouseListener
	 * 
	 * @param enable
	 *            /disable Panning
	 * @param enable
	 *            /disable Rotation
	 * @param enable
	 *            /disable Zooming
	 */
	public void setNavigationModes(boolean bEnablePan, boolean bEnableRotate, boolean bEnableZoom) {
		this.bEnablePan = bEnablePan;
		this.bEnableRotate = bEnableRotate;
		this.bEnableZoom = bEnableZoom;
	}
}
