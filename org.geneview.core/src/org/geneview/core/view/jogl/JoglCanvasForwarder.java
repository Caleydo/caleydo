package org.geneview.core.view.jogl;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.view.jogl.mouse.PickingJoglMouseListener;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;
import org.geneview.core.view.opengl.util.FPSCounter;

/**
 *
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class JoglCanvasForwarder 
extends GLCanvas 
implements GLEventListener {

	private IGeneralManager generalManager;
	
	private int iGLCanvasID;
	
	private FPSCounter fpsCounter;
	
	private PickingJoglMouseListener joglMouseListener;
	
	public JoglCanvasForwarder(final IGeneralManager generalManager,
			final int iGLCanvasID) {
		
		this.generalManager = generalManager;
		
		joglMouseListener = new PickingJoglMouseListener();
		
		this.iGLCanvasID = iGLCanvasID;
		
		// Register mouse listener to GL canvas
		this.addMouseListener(joglMouseListener);
		this.addMouseMotionListener(joglMouseListener);
		this.addMouseWheelListener(joglMouseListener);
	}
	
	public void init(GLAutoDrawable drawable) {

		generalManager.getSingelton().logMsg(
				"JoglCanvasForwarder [" + iGLCanvasID + "] init() ... ",
				LoggerType.STATUS);
		
		GL gl = drawable.getGL();
		
		// This is specially important for Windows. Otherwise JOGL internally slows down dramatically (factor of 10).
	    gl.setSwapInterval(0);
		
		fpsCounter = new FPSCounter(drawable, 16);
		fpsCounter.setColor(0.5f, 0.5f, 0.5f, 1);
	
		gl.glShadeModel(GL.GL_SMOOTH); // Enables Smooth Shading
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // white Background
		gl.glClearDepth(1.0f); // Depth Buffer Setup
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		
		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);	
	}

	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	public void reshape(GLAutoDrawable drawable, 
			int x, 
			int y, 
			int width,
			int height) {
		
		// Implemented in registered GLEventListener classes
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	public void display(GLAutoDrawable drawable) {
			    
		final GL gl = drawable.getGL();
		
		// load identity matrix
		gl.glMatrixMode (GL.GL_MODELVIEW);
		gl.glLoadIdentity(); 

		// clear screen
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
	    fpsCounter.draw();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable, boolean, boolean)
	 */
	public void displayChanged(GLAutoDrawable drawable, 
			final boolean modeChanged,
			final boolean deviceChanged) {

	}
	
	/*
	 * 
	 */
	public final ManagerObjectType getBaseType() {
		return ManagerObjectType.VIEW_CANVAS_FORWARDER;
	}
	
	public final PickingJoglMouseListener getJoglMouseListener() {

		return joglMouseListener;
	}

	public final void setJoglMouseListener(
			final PickingJoglMouseListener joglMouseListener) {

		this.joglMouseListener = joglMouseListener;
	}
	
	public int getID() {
		
		return iGLCanvasID;
	}
}
