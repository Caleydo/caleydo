package org.geneview.core.view.jogl;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.view.jogl.mouse.AViewCameraListenerObject;
import org.geneview.core.view.jogl.mouse.JoglMouseListener;
import org.geneview.core.view.jogl.mouse.PickingJoglMouseListener;
import org.geneview.core.view.opengl.IGLCanvasDirector;
import org.geneview.core.view.opengl.IGLCanvasUser;

/**
 *JoglCanvasForwarder handles several objects and forwards the OpenGL events to them.
 * 
 * @see cerberus.view.swt.jogl.SwtJoglGLCanvasViewRep
 * @see cerberus.view.jogl.IJoglMouseListener
 * @see cerberus.view.opengl.IGLCanvasDirector
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class JoglCanvasForwarder 
extends AViewCameraListenerObject
implements GLEventListener {

	private boolean bCallInitOpenGL = true;
	
	private JoglMouseListener refMouseHandler;
	
	private int iGlForwarederId;
	
	protected Vector <IGLCanvasUser> vecGLCanvasUser;
	
	/**
	 * This flag indicates, that the canvas was created.
	 */
	protected AtomicBoolean abEnableRendering;
	
	public JoglCanvasForwarder( final IGeneralManager refGeneralManager,
			final IGLCanvasDirector refGLCanvasDirector, 
			final int iUniqueId) {

		super(iUniqueId, refGeneralManager,null);
		
		if ( refGLCanvasDirector!= null ) {
			iGlForwarederId = refGLCanvasDirector.getGlEventListernerId();
		} else {
			assert false : "IGLCanvasDirector refGLCanvasDirector==null !";
		}
		
//		refMouseHandler = new JoglMouseListenerDebug(this);
//		refMouseHandler = new PickingJoglMouseListenerDebug(this);
		refMouseHandler = new PickingJoglMouseListener(this);
		
		vecGLCanvasUser = new Vector <IGLCanvasUser> ();	
		
		abEnableRendering = new AtomicBoolean( true );
	}

	public final JoglMouseListener getJoglMouseListener() {
		return this.refMouseHandler;
	}
	
	public final int getGlEventListernerId() {
		return iGlForwarederId;
	}
	
	/* -----  BEGIN GLEvent forwarding ----- */
	
	public void init(GLAutoDrawable drawable) {

		// Use debug pipeline
		// drawable.setGL(new DebugGL(drawable.getGL()));

		refGeneralManager.getSingelton().logMsg(
				"JoglCanvasForwarder [" + iUniqueId + "] init() ... ",
				LoggerType.STATUS);
		
		if  ( bCallInitOpenGL ) {
			bCallInitOpenGL = false;
			
			GL gl = drawable.getGL();
	
			refGeneralManager.getSingelton().logMsg(
					"JoglCanvasForwarder [" + iUniqueId +
					"] INIT GL IS: ",
					LoggerType.STATUS);
	
			//gl.resizeGLScene();                      // Initialize the GL viewport

			
			gl.glShadeModel(GL.GL_SMOOTH); // Enables Smooth Shading
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
			gl.glClearDepth(1.0f); // Depth Buffer Setup
	
			gl.glEnable(GL.GL_DEPTH_TEST); // Enables Depth Testing
			gl.glDepthFunc(GL.GL_LEQUAL); // The Type Of Depth Test To Do
	
			/* Really Nice Perspective Calculations */
			gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
	
			/**
			 * You must register the MouseMotionListener here!
			 */
			drawable.addMouseListener(this.refMouseHandler);
			drawable.addMouseMotionListener(this.refMouseHandler);
			
			if ( vecGLCanvasUser.isEmpty() ) {
				refGeneralManager.getSingelton().logMsg(
						"JoglCanvasForwarder [" + iUniqueId + "] init() no GL Canvas objects registered; can not initiate GL Canvas objects! " +
						" gl_forwareder=[" + this.iGlForwarederId + "]",
						LoggerType.MINOR_ERROR);
			} else {			
				/* there are GL Canvas objects; iterate and initGLCanvas(GL) each one.. */
				Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();				
				while( iter.hasNext() ) {
					iter.next().initGLCanvas(gl);				
				}
			}
		}	
		
		refGeneralManager.getSingelton().logMsg(
					"JoglCanvasForwarder [" + iUniqueId + "] init() ... done",
					LoggerType.STATUS);
				
	}

	
	public void reshape(GLAutoDrawable drawable, 
			int x, 
			int y, 
			int width,
			int height) {

		refGeneralManager.getSingelton().logMsg(
				"JoglCanvasForwarder  RESHAPE GL",
				LoggerType.STATUS);

		GL gl = drawable.getGL();

		float h = (float) height / (float) width;
		
		refGeneralManager.getSingelton().logMsg(
				"JoglCanvasForwarder  RESHAPE GL" +
				"\nGL_VENDOR: " + gl.glGetString(GL.GL_VENDOR)+
				"\nGL_RENDERER: " + gl.glGetString(GL.GL_RENDERER) +
				"\nGL_VERSION: " + gl.glGetString(GL.GL_VERSION),
				LoggerType.STATUS);
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		
		//FIXME Perspective/ortho projection should be chosen in XML file
		gl.glOrtho(-4.0f, 4.0f, -4*h, 4*h, 1.0f, 1000.0f);
		//gl.glFrustum(-1.0f, 1.0f, -h, h, 1.0f, 1000.0f);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();		
		while ( iter.hasNext() ) {		
			iter.next().reshape(gl,
					x, 
					y, 
					width,
					height);
		} //end while ( iter.hasNext() )
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	public void display(GLAutoDrawable drawable) {

		GL gl = drawable.getGL();

		/* Clear The Screen And The Depth Buffer */
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity(); // Reset the current modelview matrix

		
		float distanceZ_move_Center = 10;		
		float distanceZ = 2;
		
		
		/** Read viewing parameters... */
		final Vec3f rot_Vec3f = new Vec3f();
		final Vec3f position = refViewCamera.getCameraPosition();
		final float w = refViewCamera.getCameraRotationGrad(rot_Vec3f);
		
	
		/** Translation */
		gl.glTranslatef(position.x(),
				position.y(),
				position.z() - distanceZ_move_Center );
		
		
		/** Rotation */		
		gl.glRotatef( w, 
				rot_Vec3f.x(), 
				rot_Vec3f.y(), 
				rot_Vec3f.z());
		
		/** visual debugging ...*/
//		drawXYZ(gl);
//		renderTestTriangle(gl);
		
		gl.glTranslatef(0,
				0,
				distanceZ );
		
		/** end: visual debugging ...*/

		Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();		
		while ( iter.hasNext() ) {
			iter.next().render(gl);
		}

	}

	public void displayChanged(GLAutoDrawable drawable, 
			final boolean modeChanged,
			final boolean deviceChanged) {

		GL gl = drawable.getGL();
		
		Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();		
		while ( iter.hasNext() ) {
			iter.next().displayChanged(gl, modeChanged, deviceChanged);
		}
		
	}
	
	/* -----  END GLEvent forwarding ----- */
	
	public final ManagerObjectType getBaseType() {
		return ManagerObjectType.VIEW_CANVAS_FORWARDER;
	}

	/**
	 * Test if a user is registered.
	 * 
	 * @param user GLCanvas user to be tested
	 * @return TRUE if user is registered
	 */
	public boolean containsGLCanvasUser( final IGLCanvasUser user ) {
		return this.vecGLCanvasUser.contains(user);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.swt.jogl.IGLCanvasDirector#addGLCanvasUser(cerberus.view.opengl.IGLCanvasUser)
	 */
	public void addGLCanvasUser( final IGLCanvasUser user ) {

		if ( vecGLCanvasUser.contains( user ) ) {
			throw new GeneViewRuntimeException("addGLCanvasUser() try to same user twice!");
		}
		
		/**
		 * Try to avoid java.util.ConcurrentModificationException
		 * 
		 * Critical section:
		 */
		
		//FIXME: test adding of IGLCanvasUser later on
		//super.abEnableRendering.set( false );
						
		vecGLCanvasUser.addElement( user );
		
		/**
		 * End of critical section
		 */		
		
		refGeneralManager.getSingelton().logMsg(
				"JoglCanvasForwarder [" +
				getId() + "] added GLCanvas user=[" +
				user.getId() + "] " + 
				user.toString() ,
				LoggerType.TRANSITION );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.swt.jogl.IGLCanvasDirector#removeGLCanvasUser(cerberus.view.opengl.IGLCanvasUser)
	 */
	public void removeGLCanvasUser( IGLCanvasUser user ) {
		if ( ! vecGLCanvasUser.remove( user ) ) {
			throw new GeneViewRuntimeException("removeGLCanvasUser() failed, because the user is not registered!");
		}
		user.destroyGLCanvas();
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.swt.jogl.IGLCanvasDirector#removeAllGLCanvasUsers()
	 */
	public void removeAllGLCanvasUsers() {
		
		abEnableRendering.set( false );
		
		Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();
		
		while ( iter.hasNext() ) {		
			IGLCanvasUser refIGLCanvasUser = iter.next();
			refIGLCanvasUser.destroyGLCanvas();
			refIGLCanvasUser = null;
		}
		vecGLCanvasUser.clear();
	}
	
	
	public Collection<IGLCanvasUser> getAllGLCanvasUsers() {
		return new ArrayList <IGLCanvasUser> (this.vecGLCanvasUser);
	}	
	
	protected void drawXYZ( GL gl ) {
		float fMax = 200;
		
		gl.glBegin(GL.GL_LINES);
			gl.glColor3f(1, 0, 0);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(fMax, 0, 0);
			
			gl.glColor3f(0, 1, 0);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, fMax, 0);
			
			gl.glColor3f(0, 0, 1);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, 0, fMax);
		gl.glEnd();
	}

	protected void renderTestTriangle(GL gl) {
		
		gl.glBegin(GL.GL_TRIANGLES); // Drawing using triangles
		gl.glColor3f(1.0f, 0.0f, 0.0f); // Set the color to red
		gl.glVertex3f(0.0f, 1.0f, 0.0f); // Top
		gl.glColor3f(0.0f, 1.0f, 0.0f); // Set the color to green
		gl.glVertex3f(-1.0f, -1.0f, 0.0f); // Bottom left
		gl.glColor3f(0.0f, 0.0f, 1.0f); // Set the color to blue
		gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom right
		gl.glEnd(); // Finish drawing the triangle
	}
}
