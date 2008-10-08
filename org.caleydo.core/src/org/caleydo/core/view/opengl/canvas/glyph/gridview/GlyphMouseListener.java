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
import javax.media.opengl.GL;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;

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

	private IGeneralManager generalManager;

	private AGLEventListener viewCanvas;

	protected int prevMouseX, prevMouseY;

	protected int iRubberBandStartX = 0, iRubberBandStartY = 0;

	protected int iRubberBandCurrentX = 0, iRubberBandCurrentY = 0;

	protected Rotf currentRotation = null;

	protected float fMouseSensitivityRotation = 1.0f;

	/**
	 * Constructor.
	 */
	public GlyphMouseListener(final AGLEventListener remoteRendering3D,
			final IGeneralManager generalManager)
	{

		this.viewCanvas = remoteRendering3D;
		this.generalManager = generalManager;

		this.currentRotation = new Rotf();
	}

	public void render(GL gl)
	{

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
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

	// Methods required for the implementation of MouseMotionListener
	public void mouseDragged(MouseEvent e)
	{

		int x = e.getX();
		int y = e.getY();
		Dimension size = e.getComponent().getSize();

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

			/**
			 * --- ROTATION ---
			 */
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

		/* --- Right -- Mouse Button --- */
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
		{
			/**
			 * --- PANING ---
			 */

			float rot = viewCanvas.getViewCamera().getCameraRotation().get(new Vec3f(0, 0, 1));
			fPanScale = -viewCanvas.getViewCamera().getCameraPosition().z();

			Vec3f addVec3f = new Vec3f(fPanScale
					* ((float) (x - prevMouseX) / (float) size.width),
					fPanScale * ((float) (prevMouseY - y) / (float) size.height)
							* (float) Math.cos(rot), -fPanScale
							* ((float) (prevMouseY - y) / (float) size.height)
							* (float) Math.sin(rot));

			prevMouseX = x;
			prevMouseY = y;

			viewCanvas.getViewCamera().addCameraPosition(addVec3f);
		}

	}

	public void mouseMoved(MouseEvent e)
	{

	}

	public void mouseClicked(MouseEvent arg0)
	{

		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent arg0)
	{

		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent arg0)
	{

		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e)
	{

		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
		{
			iRubberBandStartX = e.getX();
			iRubberBandStartY = e.getY();
			iRubberBandCurrentX = e.getX();
			iRubberBandCurrentY = e.getY();
		}
		/* --- Right -- Mouse Button --- */
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
		{
			prevMouseX = e.getX();
			prevMouseY = e.getY();
		}
	}

	public void mouseReleased(MouseEvent arg0)
	{

		iRubberBandStartX = 0;
		iRubberBandStartX = 0;
	}

}
