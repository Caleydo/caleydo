package org.caleydo.core.view.opengl.mouse;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Iterator;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;

/**
 * Jogl mouse event listener
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */

public class JoglMouseListener
	implements MouseListener, MouseMotionListener, MouseWheelListener
{

	protected ArrayList<AGLEventListener> alGlCanvas;

	protected float fZoomScale = 0.072f;

	protected float fPanScale = 3.1f;

	protected int prevMouseX, prevMouseY;

	protected Point pressedMousePosition;

	protected boolean bMouseLeftButtonDown = false;
	protected boolean bMouseRightButtonDown = false;
	protected boolean bMouseMiddleButtonDown = false;
	protected boolean bMouseLeft_StandbyZoom = false;
	protected boolean bMouseRight_StandbyRotate = false;
	
	protected boolean bEnablePan = true;
	protected boolean bEnableRotate = true;
	protected boolean bEnableZoom = true;
	
	/**
	 * Define mouse sensitivity. Higher value indicates more degrees of
	 * rotation. Default value 1.0
	 */
	protected float fMouseSensitivityRotation = 1.0f;

	public JoglMouseListener()
	{

		pressedMousePosition = new Point();
		alGlCanvas = new ArrayList<AGLEventListener>();
	}

	public final void setMouseSensitivityRotation(float fSetMouseSensitivityRotation)
	{

		this.fMouseSensitivityRotation = fSetMouseSensitivityRotation;
	}

	public final float getMouseSensitivityRotation()
	{

		return this.fMouseSensitivityRotation;
	}

	public final boolean isMouseRightButtondown()
	{

		return bMouseRightButtonDown;
	}

	public final boolean isMouseMiddleButtondown()
	{

		return bMouseMiddleButtonDown;
	}

	public final boolean isMouseLeftButtondown()
	{

		return bMouseLeftButtonDown;
	}

	public final Point getPressedMousePosition()
	{

		return pressedMousePosition;
	}

	/**
	 * Register this Listener to the canvas as MouseListener and
	 * MouseMotionListener.
	 * 
	 * @param canvas
	 */
	// public void addMouseListenerAll(GLAutoDrawable canvas) {
	// canvas.addMouseListener(this);
	// canvas.addMouseMotionListener(this);
	// }
	/* Methods required for the implementation of MouseListener */
	@Override
	public void mouseEntered(MouseEvent e)
	{

	}
	@Override
	public void mouseExited(MouseEvent e)
	{
		
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{

	}
	@Override
	public void mousePressed(MouseEvent e)
	{

		prevMouseX = e.getX();
		prevMouseY = e.getY();

		pressedMousePosition.x = prevMouseX;
		pressedMousePosition.y = prevMouseY;

		/* --- Left -- Mouse Button --- */
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
		{
			// System.err.println(" -- Left --");
			this.bMouseLeftButtonDown = true;
		}

		/* --- Right -- Mouse Button --- */
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
		{
			// System.err.println(" -- Right --");

			if (bMouseLeft_StandbyZoom)
			{
				/*
				 * first button pressed was "middle" and it is still pressed,
				 * but also "right" button is pressed now..
				 */
				bMouseMiddleButtonDown = true;
			}
			else
			{
				/*
				 * first button pressed was NOT "middle" and but now "right"
				 * button is pressed..
				 */

				bMouseRightButtonDown = true;
				bMouseRight_StandbyRotate = true;
			}
		}

		/* --- Middle -- Mouse Button --- */
		if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0)
		{
			bMouseMiddleButtonDown = true;
			// System.err.println(" -- Middle --");

			if (bMouseRight_StandbyRotate)
			{
				/*
				 * first button was "right" and "right"-button is still pressed
				 */
				bMouseRightButtonDown = false;
			}
			else
			{
				/*
				 * enable standby zoom... First button pressed is "middle"
				 */
				bMouseLeft_StandbyZoom = true;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{

		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
		{
			bMouseLeft_StandbyZoom = false;
			bMouseMiddleButtonDown = false;

			if (bMouseRight_StandbyRotate)
			{
				/*
				 * first button pressed was "right" and now "left" button is
				 * released ==> same state as if only "right" button was
				 * pressed.
				 */
				bMouseRightButtonDown = true;
			}
		}

		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
		{

			if (bMouseLeft_StandbyZoom)
			{
				/*
				 * First "left" button was pressed and not released since and
				 * "right" button was pressed now ==> emmulate "middle" button
				 */
				bMouseMiddleButtonDown = false;
			}
			else
			{
				bMouseRightButtonDown = false;

				/*
				 * Now "right" button is released ==> no more standby
				 * RightButton.
				 */
				bMouseRight_StandbyRotate = false;
			}
		}

		if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0)
		{
			bMouseMiddleButtonDown = false;
		}
	}


	@Override
	public void mouseDragged(MouseEvent event)
	{

		int x = event.getX();
		int y = event.getY();
		Dimension size = event.getComponent().getSize();

		if (!bMouseRightButtonDown)
		{
			if (!bMouseMiddleButtonDown && bEnableRotate)
			{
				 /**
				 * --- ROTATION ---
				 */
				 Rotf currentRotX = new Rotf();
				 Rotf currentRotY = new Rotf();
						   	    
				 float fpercentX = (float)(x-prevMouseX)/(float)(size.width)
				 * fMouseSensitivityRotation;
						    	
				 float fpercentY = (float)(y-prevMouseY)/(float)(size.height)
				 * fMouseSensitivityRotation;
						    	
						    	
				 currentRotX.set(new Vec3f(0,1,0),
				 fpercentX * (float)Math.PI);
						   	    
				 currentRotY.set(new Vec3f(1,0,0),
				 fpercentY * (float)Math.PI);
						   	    
				 /* concatinate rotations.. */
				 currentRotX = currentRotX.times(currentRotY);
						   	    
				 prevMouseX = x;
				 prevMouseY = y;
							    
				 /* set new paramters to ViewCamera */
				 Iterator<AGLEventListener> iterGLCanvas = alGlCanvas.iterator();
							    
				 while (iterGLCanvas.hasNext())
				 {
				 iterGLCanvas.next().getViewCamera().addCameraRotation(
				 currentRotX);
				 }
			}
			else if (bEnableZoom)
			{
				/**
				 * --- ZOOMING ---
				 */
				float zoomX = fZoomScale * (x - prevMouseX);
				float zoomY = fZoomScale * (prevMouseY - y);

				/* take abs(zoomX) */
				if ((zoomX < 0.0f) && (zoomY > 0.0f))
				{
					zoomX = -zoomX;
				}

				prevMouseX = x;
				prevMouseY = y;

				/* set new paramters to ViewCamera */
				Iterator<AGLEventListener> iterGLCanvas = alGlCanvas.iterator();

				while (iterGLCanvas.hasNext())
				{
					iterGLCanvas.next().getViewCamera().addCameraScale(
							new Vec3f(0, 0, zoomY + zoomX));
				}
			}
		}
		else if (bEnablePan)
		{
			/**
			 * --- PANING ---
			 */
			Vec3f addVec3f = new Vec3f(fPanScale
					* ((float) (x - prevMouseX) / (float) size.width), fPanScale
					* ((float) (prevMouseY - y) / (float) size.height), 0.0f);

			prevMouseX = x;
			prevMouseY = y;

			/* set new paramters to ViewCamera */
			Iterator<AGLEventListener> iterGLCanvas = alGlCanvas.iterator();

			while (iterGLCanvas.hasNext())
			{
				iterGLCanvas.next().getViewCamera().addCameraPosition(addVec3f);
			}
		}
	}

	public void addGLCanvas(final AGLEventListener gLCanvas)
	{

		alGlCanvas.add(gLCanvas);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{

		/**
		 * --- NORMAL ZOOM ---
		 */
		float fZoom = fZoomScale * e.getWheelRotation();

		Iterator<AGLEventListener> iterGLCanvas = alGlCanvas.iterator();

		while (iterGLCanvas.hasNext())
		{
			iterGLCanvas.next().getViewCamera().addCameraScale(new Vec3f(0, 0, fZoom));
		}
	}
		
	public void setNavigationModes(boolean bEnablePan, boolean bEnableRotate, boolean bEnableZoom)
	{
		this.bEnablePan = bEnablePan;
		this.bEnableRotate = bEnableRotate;
		this.bEnableZoom = bEnableZoom;
	}
}
