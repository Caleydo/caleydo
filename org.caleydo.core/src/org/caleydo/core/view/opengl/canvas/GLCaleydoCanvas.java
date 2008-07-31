package org.caleydo.core.view.opengl.canvas;

import java.util.logging.Level;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.FPSCounter;

/**
 * Class implements a GL canvas. The canvas is registered in the
 * ViewGLCanvasManager and automatically rendered in the animator loop.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class GLCaleydoCanvas
	extends GLCanvas
	implements GLEventListener
{

	private static final long serialVersionUID = 1L;

	private IGeneralManager generalManager;

	private int iGLCanvasID;

	private FPSCounter fpsCounter;

	private PickingJoglMouseListener joglMouseListener;

	public GLCaleydoCanvas(final IGeneralManager generalManager, final int iGLCanvasID,
			final GLCapabilities glCapabilities)
	{

		super(glCapabilities);

		this.generalManager = generalManager;

		joglMouseListener = new PickingJoglMouseListener();

		this.iGLCanvasID = iGLCanvasID;

		// Register mouse listener to GL canvas
		this.addMouseListener(joglMouseListener);
		this.addMouseMotionListener(joglMouseListener);
		this.addMouseWheelListener(joglMouseListener);
	}

	public void init(GLAutoDrawable drawable)
	{

		generalManager.getLogger().log(
				Level.INFO,
				"Creating canvas with ID " + iGLCanvasID + "." + "\nOpenGL capabilities:"
						+ drawable.getChosenGLCapabilities());

		GL gl = drawable.getGL();

		// This is specially important for Windows. Otherwise JOGL internally
		// slows down dramatically (factor of 10).
		gl.setSwapInterval(0);

		fpsCounter = new FPSCounter(drawable, 16);
		fpsCounter.setColor(0.5f, 0.5f, 0.5f, 1);

		gl.glShadeModel(GL.GL_SMOOTH); // Enables Smooth Shading
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // white Background
		gl.glClearDepth(1.0f); // Depth Buffer Setup
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		gl.glEnable(GL.GL_POINT_SMOOTH);
		gl.glHint(GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable
	 * , int, int, int, int)
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{

		// Implemented in registered GLEventListener classes
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable
	 * )
	 */
	public void display(GLAutoDrawable drawable)
	{

		final GL gl = drawable.getGL();

		// load identity matrix
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

		// clear screen
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		fpsCounter.draw();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.
	 * GLAutoDrawable, boolean, boolean)
	 */
	public void displayChanged(GLAutoDrawable drawable, final boolean modeChanged,
			final boolean deviceChanged)
	{

	}

	public final PickingJoglMouseListener getJoglMouseListener()
	{

		return joglMouseListener;
	}

	public final void setJoglMouseListener(final PickingJoglMouseListener joglMouseListener)
	{

		this.joglMouseListener = joglMouseListener;
	}

	public int getID()
	{

		return iGLCanvasID;
	}
}
