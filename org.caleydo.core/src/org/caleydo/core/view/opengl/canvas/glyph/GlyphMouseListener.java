package org.caleydo.core.view.opengl.canvas.glyph;

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
import org.caleydo.core.view.opengl.util.GLHelperFunctions;

/**
 * Specialized mouse wheel listener for "diving" into the bucket.
 * 
 * @author Marc Streit
 */
public class GlyphMouseListener
	implements MouseListener, MouseMotionListener, MouseWheelListener
{

	private IGeneralManager generalManager;

	private AGLEventListener viewCanvas;

	private int notches_ = 0;

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

		viewCanvas.getViewCamera().addCameraScale(new Vec3f(0, 0, notches_ / 1f));
		// viewCanvas.getViewCamera().addCameraRotation(currentRotation);

		// float x = currentRotation.get(new Vec3f(1,0,0));
		// float y = currentRotation.get(new Vec3f(1,0,0));
		// float z = currentRotation.get(new Vec3f(1,0,0));

		// System.out.println("rotx="+rotX);

		int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0); // size of window

		float fAspectRatio = (viewport[3] - viewport[1]) / (viewport[2] - viewport[0]);

		// System.out.println(fAspectRatio + " => " + viewport[0] + ", " +
		// viewport[1] + ", " + viewport[2] + ", " + viewport[3] );

		int offsetX = viewport[2] / 2;
		int offsetY = viewport[3] / 2;

		// System.out.println( fAspectRatio);

		Vec3f camS = viewCanvas.getViewCamera().getCameraScale();

		float camZScale = camS.z() - 1.0f;

		if (camZScale < 0)
			camZScale += 2 * -camZScale;

		camZScale /= 10f;

		// System.out.println( camS.x() + ", " + camS.y() + ", " + camS.z() );

		// gl.glRotatef( (float)rotX , 1.0f, 0, 0);
		// gl.glRotatef(prevMouseY / 1000.0f , 0, 1.0f, 0);
		// gl.glRotatef(prevMouseZ , 0, 0, 1.0f);

		if (iRubberBandStartX != 0 && iRubberBandStartY != 0)
		{
			// float txs = ((fAspectRatio / 2.9f) * 12.9f) / viewport[2];
			// float txc = ((fAspectRatio / 2.9f) * 12.9f) / viewport[2];

			// txs *= (iRubberBandStartX-offsetX);
			// txc *= (iRubberBandCurrentX-offsetX);

			float xs = (iRubberBandStartX - offsetX) * (fAspectRatio / 10.0f)
					* (camZScale + 1.0f);
			float ys = (iRubberBandStartY - offsetY) * (fAspectRatio / 10.0f)
					* (camZScale + 1.0f);

			float xc = (iRubberBandCurrentX - offsetX) * (fAspectRatio / 10.0f)
					* (camZScale + 1.0f);
			float yc = (iRubberBandCurrentY - offsetY) * (fAspectRatio / 10.0f)
					* (camZScale + 1.0f);

			gl.glTranslatef(xs - 1, -ys - 1, 0);

			GLHelperFunctions.drawAxis(gl);

			gl.glTranslatef((xc - xs), -(yc - ys), 0);

			GLHelperFunctions.drawAxis(gl);

		}

		notches_ = 0;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event)
	{

		// Turn off picking while zoom action is running
		// generalManager.getViewGLCanvasManager().getPickingManager().
		// enablePicking(false);

		int notches = event.getWheelRotation();

		// System.out.println("Mouse Wheel: " + Integer.toString(notches) );

		notches_ += notches;

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
			// fpercentX * MathUtil.PI);
			//		   	    
			// currentRotY.set(new Vec3f(1,0,0),
			// fpercentY * MathUtil.PI);
			//		   	    
			// /* concatinate rotations.. */
			// currentRotation = currentRotX.times(currentRotY);
			//		   	    
			// prevMouseX = x;
			// prevMouseY = y;
			//			    
			// /* set new paramters to ViewCamera */
			// /*
			// Iterator<AGLCanvasUser> iterGLCanvas = alGlCanvas.iterator();
			//			    
			// while (iterGLCanvas.hasNext())
			// {
			//iterGLCanvas.next().getViewCamera().addCameraRotation(currentRotX)
			// ;
			// }
			// */
			//			    
			// viewCanvas.getViewCamera().addCameraRotation(currentRotation);
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
	}

	public void mouseReleased(MouseEvent arg0)
	{

		iRubberBandStartX = 0;
		iRubberBandStartX = 0;
	}

}
