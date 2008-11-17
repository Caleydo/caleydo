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
import java.util.ArrayList;
import javax.media.opengl.GL;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;

/**
 * Specialized mouse wheel listener for the glyph plane view
 * 
 * @author Sauer Stefan
 */
public class GlyphMouseListener
	implements MouseListener, MouseMotionListener, MouseWheelListener
{
	protected float fZoomScale = 0.072f;

	protected float fPanScale = 3.0f;

	private AGLEventListener viewCanvas;

	protected int prevMouseX, prevMouseY;

	protected boolean bEnablePan = false;
	protected boolean bEnableRotate = false;
	protected boolean bEnableZoom = false;

	protected boolean bRubberBandEnabled = false;

	protected int iRubberBandStartX = 0, iRubberBandStartY = 0;
	protected int iRubberBandCurrentX = 0, iRubberBandCurrentY = 0;

	protected Vec3f vRubberBandStart = null;
	protected Vec3f vRubberBandCurrent = null;

	protected Rotf currentRotation = null;

	protected float fMouseSensitivityRotation = 1.0f;

	/**
	 * Constructor.
	 */
	public GlyphMouseListener(final AGLEventListener remoteRendering3D)
	{

		this.viewCanvas = remoteRendering3D;

		this.currentRotation = new Rotf();
		this.vRubberBandStart = new Vec3f();
		this.vRubberBandCurrent = new Vec3f();
	}

	/**
	 * This function returns the corner points of the Rubberband. Empty list, if
	 * there is no rubberband
	 */
	public ArrayList<Vec3f> getRubberBandPoints()
	{
		ArrayList<Vec3f> list = new ArrayList<Vec3f>();
		if (bRubberBandEnabled && iRubberBandStartX != 0 && iRubberBandStartY != 0)
		{
			list.add(new Vec3f(vRubberBandStart));
			list.add(new Vec3f(vRubberBandCurrent));
		}
		return list;
	}

	/**
	 * renders the rubberband selection
	 * 
	 * @param gl
	 */
	public void render(GL gl)
	{
		if (!bRubberBandEnabled)
			return;

		if (iRubberBandStartX != 0 && iRubberBandStartY != 0)
		{
			// starting point
			if (vRubberBandStart.x() == 0 && vRubberBandStart.y() == 0)
				vRubberBandStart = this.convertMousePositionToWorldPosition(gl,
						iRubberBandStartX, iRubberBandStartY);

			// moving point
			vRubberBandCurrent = this.convertMousePositionToWorldPosition(gl,
					iRubberBandCurrentX, iRubberBandCurrentY);

		}
		else
		{
			if (vRubberBandStart.x() != 0 && vRubberBandStart.y() != 0)
			{
				vRubberBandStart = new Vec3f();
				vRubberBandCurrent = new Vec3f();
			}
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		handleZooming(e);
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{

		if (prevMouseX == 0 && prevMouseY == 0)
		{
			prevMouseX = e.getX();
			prevMouseY = e.getY();
		}

		/* --- Left -- Mouse Button --- */
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
		{
			if (iRubberBandStartX != 0 && iRubberBandStartY != 0)
			{
				iRubberBandCurrentX = e.getX();
				iRubberBandCurrentY = e.getY();
			}
		}

		/* --- Middle -- Mouse Button --- */
		if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0)
		{
			handleRotation(e);
		}

		/* --- Right -- Mouse Button --- */
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
		{
			handlePanning(e);
		}

	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{

		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
		{
			if (bRubberBandEnabled)
			{
				iRubberBandStartX = e.getX();
				iRubberBandStartY = e.getY();
				iRubberBandCurrentX = e.getX();
				iRubberBandCurrentY = e.getY();
			}
		}
		/* --- Right -- Mouse Button --- */
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
		{
			prevMouseX = e.getX();
			prevMouseY = e.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{
		iRubberBandStartX = 0;
		iRubberBandStartX = 0;
	}

	/**
	 * This handles the panning for the view. It flyes over the drawing plane
	 * 
	 * @param MouseEvent e
	 */
	private void handlePanning(MouseEvent e)
	{
		if (!bEnablePan)
			return;

		int x = e.getX();
		int y = e.getY();
		Dimension size = e.getComponent().getSize();

		float rot = viewCanvas.getViewCamera().getCameraRotation().get(new Vec3f(0, 0, 1));
		fPanScale = -viewCanvas.getViewCamera().getCameraPosition().z();

		Vec3f addVec3f = new Vec3f(
				fPanScale * ((float) (x - prevMouseX) / (float) size.width), fPanScale
						* ((float) (prevMouseY - y) / (float) size.height)
						* (float) Math.cos(rot), -fPanScale
						* ((float) (prevMouseY - y) / (float) size.height)
						* (float) Math.sin(rot));

		prevMouseX = x;
		prevMouseY = y;

		viewCanvas.getViewCamera().addCameraPosition(addVec3f);
	}

	/**
	 * This handles the rotation for the view.
	 * Not used for now
	 * 
	 * @param MouseEvent e
	 */
	private void handleRotation(MouseEvent e)
	{
		if (!bEnableRotate)
			return;

		// Rotf currentRotX = new Rotf();
		// Rotf currentRotY = new Rotf();
		//					   	    
		// float fpercentX = (float)(x-prevMouseX)/(float)(size.width)
		// * fMouseSensitivityRotation;
		//					    	
		// float fpercentY = (float)(y-prevMouseY)/(float)(size.height)
		// * fMouseSensitivityRotation;
		//					    	
		//					    	
		// currentRotX.set(new Vec3f(0,1,0),
		// fpercentX * (float)Math.PI);
		//					   	    
		// currentRotY.set(new Vec3f(1,0,0),
		// fpercentY * (float)Math.PI);
		//					   	    
		// /* concatinate rotations.. */
		// currentRotation = currentRotX.times(currentRotY);
		//					   	    
		// prevMouseX = x;
		// prevMouseY = y;
		//						    
		// viewCanvas.getViewCamera().addCameraRotation(currentRotation);
	}

	/**
	 * This handles the zooming for the view.
	 * 
	 * @param MouseEvent e
	 */
	private void handleZooming(MouseWheelEvent e)
	{
		if (!bEnableZoom)
			return;
		float fZoom = fZoomScale * e.getWheelRotation();
		Vec3f camPosOld = viewCanvas.getViewCamera().getCameraPosition();
		float fRotOld = viewCanvas.getViewCamera().getCameraRotation().get(new Vec3f(0, 0, 1));

		Vec3f addVec3f = new Vec3f(0, (fZoom * (float) Math.sin(fRotOld)),
				(fZoom * (float) Math.cos(fRotOld)) + fZoom);

		Vec3f camPosNew = new Vec3f(camPosOld);
		camPosNew.add(addVec3f);

		float height = camPosNew.y() * ((float) Math.sin(fRotOld)) + camPosNew.z()
				* ((float) Math.cos(fRotOld));

		if (height > -3)
			return;

		float angle = (float) (Math.PI / 4.0 - 2.0 * Math.PI * (-(camPosOld.z() + 3)) / 360.0);

		if (angle < 0)
		{
			angle = 0;

			if (addVec3f.z() < 0)
				addVec3f = new Vec3f(0, 0, 0);
		}

		viewCanvas.getViewCamera().addCameraPosition(addVec3f);

		Rotf t = new Rotf();
		t.set(new Vec3f(-1, 0, 0), angle);
		viewCanvas.getViewCamera().setCameraRotation(t);
	}
	

	/**
	 * This method converts the mouse coordinates to world coordinates
	 * (including perspective correction)
	 * 
	 * @param gl
	 * @param x
	 * @param y
	 * @return new coordinates
	 */
	private Vec3f convertMousePositionToWorldPosition(GL gl, int x, int y)
	{
		float[] fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, x, y);

		Vec3f mousePos = new Vec3f(fArTargetWorldCoordinates[0], fArTargetWorldCoordinates[1],
				fArTargetWorldCoordinates[2]);

		Vec3f camPos = viewCanvas.getViewCamera().getCameraPosition();
		float camRot = viewCanvas.getViewCamera()
				.getCameraRotationRadiant(new Vec3f(-1, 0, 0));

		float yy = camPos.z() * (float) Math.cos(Math.PI / 2 - camRot) - camPos.y()
				* (float) Math.sin(Math.PI / 2 - camRot);
		float zz = -camPos.z() * (float) Math.sin(Math.PI / 2 - camRot) - camPos.y()
				* (float) Math.cos(Math.PI / 2 - camRot);

		Vec3f camPosWorld = new Vec3f(-camPos.x(), yy, zz);

		Vec3f dir = mousePos.minus(camPosWorld);
		dir.normalize();

		float l = -camPosWorld.z() / dir.z();

		float xp = (camPosWorld.x() + l * dir.x());
		float yp = camPosWorld.y() + l * dir.y();
		float zp = camPosWorld.z() + l * dir.z();

		return new Vec3f(xp, yp, zp);
	}

	/**
	 * Enables / Disables Mouse Zoom, Pan & Rotating Rotating is not functional
	 * in this special MouseListener
	 * 
	 * @param enable/disable Panning
	 * @param enable/disable Rotation
	 * @param enable/disable Zooming
	 */
	public void setNavigationModes(boolean bEnablePan, boolean bEnableRotate,
			boolean bEnableZoom)
	{
		this.bEnablePan = bEnablePan;
		this.bEnableRotate = bEnableRotate;
		this.bEnableZoom = bEnableZoom;
	}
}
