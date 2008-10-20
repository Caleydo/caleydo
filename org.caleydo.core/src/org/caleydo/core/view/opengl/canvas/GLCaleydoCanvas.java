package org.caleydo.core.view.opengl.canvas;

import java.util.logging.Level;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
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
	implements GLEventListener, IUniqueObject
{
	private static final long serialVersionUID = 1L;

	private int iGLCanvasID;

	private FPSCounter fpsCounter;

	private PickingJoglMouseListener joglMouseListener;

	public GLCaleydoCanvas(final GLCapabilities glCapabilities)
	{
		super(glCapabilities);
//		this.getContext().setSynchronized(true);

		joglMouseListener = new PickingJoglMouseListener();

		this.iGLCanvasID = GeneralManager.get().getIDManager().createID(
				EManagedObjectType.VIEW_GL_CANVAS);

		// Register mouse listener to GL canvas
		this.addMouseListener(joglMouseListener);
		this.addMouseMotionListener(joglMouseListener);
		this.addMouseWheelListener(joglMouseListener);

	}

	@Override
	public void init(GLAutoDrawable drawable)
	{
		GeneralManager.get().getLogger().log(
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

//		gl.glEnable(GL.GL_POINT_SMOOTH);
//		gl.glHint(GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{

		// Implemented in registered GLEventListener classes
	}

	@Override
	public void display(GLAutoDrawable drawable)
	{
		final GL gl = drawable.getGL();
		
		// turn this on during debugging if anything changes in the init() code
//		init(drawable);
		
		// load identity matrix
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

		// clear screen
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		fpsCounter.draw();
	}

	@Override
	public void displayChanged(GLAutoDrawable drawable, final boolean modeChanged,
			final boolean deviceChanged)
	{

	}

	public final PickingJoglMouseListener getJoglMouseListener()
	{
		return joglMouseListener;
	}

	@Override
	public int getID()
	{
		return iGLCanvasID;
	}

	public void setNavigationModes(boolean bEnablePan, boolean bEnableRotate,
			boolean bEnableZoom)
	{
		joglMouseListener.setNavigationModes(bEnablePan, bEnableRotate, bEnableZoom);
	}
}
