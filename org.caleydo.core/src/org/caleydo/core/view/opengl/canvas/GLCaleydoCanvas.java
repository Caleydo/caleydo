package org.caleydo.core.view.opengl.canvas;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.FPSCounter;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

/**
 * Class implements a GL2 canvas. The canvas is registered in the ViewGLCanvasManager and automatically
 * rendered in the animator loop.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class GLCaleydoCanvas
	extends GLCanvas
	implements GLEventListener, IUniqueObject {
	private static final long serialVersionUID = 1L;

	private int iGLCanvasID;

	private FPSCounter fpsCounter;

	private GLMouseListener glMouseListener;

	private Composite parentComposite;

	/**
	 * Constructor.
	 */
	public GLCaleydoCanvas(final GLCapabilities glCapabilities) {
		super(glCapabilities);
		// this.getContext().setSynchronized(true);

		glMouseListener = new GLMouseListener();

		this.iGLCanvasID = GeneralManager.get().getIDManager().createID(EManagedObjectType.VIEW_GL_CANVAS);

		// Register mouse listener to GL2 canvas
		this.addMouseListener(glMouseListener);
		this.addMouseMotionListener(glMouseListener);
		this.addMouseWheelListener(glMouseListener);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		Logger.log(new Status(IStatus.INFO, this.toString(), "Creating canvas with ID " + iGLCanvasID + "."
			+ "\nOpenGL2 capabilities:" + drawable.getChosenGLCapabilities()));

		GL2 gl = drawable.getGL().getGL2();

		// This is specially important for Windows. Otherwise JOGL2 internally
		// slows down dramatically (factor of 10).
		gl.setSwapInterval(0);

		fpsCounter = new FPSCounter(drawable, 16);
		fpsCounter.setColor(0.5f, 0.5f, 0.5f, 1);

		gl.glShadeModel(GL2.GL_SMOOTH); // Enables Smooth Shading
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // white Background
		gl.glClearDepth(1.0f); // Depth Buffer Setup
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT | GL2.GL_STENCIL_BUFFER_BIT);

		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);

		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		// gl.glEnable(GL2.GL_POINT_SMOOTH);
		// gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		// gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		// gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_DIFFUSE);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		// Implemented in registered GLEventListener classes
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();

		// load identity matrix
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		// clear screen
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		// fpsCounter.draw();
	}

//	@Override
//	public void displayChanged(GLAutoDrawable drawable, final boolean modeChanged, final boolean deviceChanged) {
//
//	}

	public final GLMouseListener getGLMouseListener() {
		return glMouseListener;
	}

	@Override
	public int getID() {
		return iGLCanvasID;
	}

	public void setNavigationModes(boolean bEnablePan, boolean bEnableRotate, boolean bEnableZoom) {
		glMouseListener.setNavigationModes(bEnablePan, bEnableRotate, bEnableZoom);
	}

	public void setParentComposite(Composite composite) {
		parentComposite = composite;
	}

	public Composite getParentComposite() {
		return parentComposite;
	}

	/**
	 * Returns the internal unique-id as hashcode
	 * 
	 * @return internal unique-id as hashcode
	 */
	@Override
	public int hashCode() {
		return getID();
	}

	/**
	 * Checks if the given object is equals to this one by comparing the internal unique-id
	 * 
	 * @return <code>true</code> if the 2 objects are equal, <code>false</code> otherwise
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof IUniqueObject) {
			return this.getID() == ((IUniqueObject) other).getID();
		}
		else {
			return false;
		}
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

}
