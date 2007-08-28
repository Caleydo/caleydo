package cerberus.view.jogl;

//import java.awt.Frame;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import gleem.linalg.Vec3f;

import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.ILoggerManager.LoggerType;
//import cerberus.math.MathUtil;
import cerberus.util.exception.GeneViewRuntimeException;
//import cerberus.view.jogl.PickingJoglMouseListenerDebug;
import cerberus.view.jogl.mouse.AViewCameraListenerObject;
import cerberus.view.jogl.mouse.JoglMouseListener;
import cerberus.view.jogl.mouse.PickingJoglMouseListener;
import cerberus.view.opengl.IGLCanvasDirector;
//import cerberus.data.view.camera.IViewCamera;
import cerberus.view.opengl.IGLCanvasUser;

/**
 *JoglCanvasForwarder handles several objects and forwards the OpenGL events to them.
 * 
 * @see cerberus.view.swt.jogl.SwtJoglGLCanvasViewRep
 * @see cerberus.view.jogl.IJoglMouseListener
 * @see cerberus.view.opengl.IGLCanvasDirector
 * 
 * @author Michael Kalkusch
 */
public class JoglCanvasForwarder 
extends AViewCameraListenerObject
implements GLEventListener {

	private final IGLCanvasDirector refGLCanvasDirector;

	private boolean bCallInitOpenGL = true;
	
	private JoglMouseListener refMouseHandler;
	
	protected Vector <IGLCanvasUser> vecGLCanvasUser;
	
	/**
	 * This flag indicates, that the canvas was created.
	 */
	protected AtomicBoolean abEnableRendering;
	
	public JoglCanvasForwarder( final IGeneralManager refGeneralManager,
			final IGLCanvasDirector refGLCanvasDirector, 
			final int iUniqueId) {

		super(iUniqueId, refGeneralManager,null);
		
//		refMouseHandler = new JoglMouseListenerDebug(this);
//		refMouseHandler = new PickingJoglMouseListenerDebug(this);
		refMouseHandler = new PickingJoglMouseListener(this);

		this.refGLCanvasDirector = refGLCanvasDirector;
		
		vecGLCanvasUser = new Vector <IGLCanvasUser> ();	
		
		abEnableRendering = new AtomicBoolean( true );
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
	

	public final JoglMouseListener getJoglMouseListener() {
		return this.refMouseHandler;
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
						"JoglCanvasForwarder init() can not call director, because director==null!",
						LoggerType.MINOR_ERROR);
			}
		
			Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();				
			while( iter.hasNext() ) {
				iter.next().initGLCanvas(gl);				
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
				LoggerType.MINOR_ERROR);

		GL gl = drawable.getGL();

		float h = (float) height / (float) width * 4.0f;
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		
		refGeneralManager.getSingelton().logMsg(
				"JoglCanvasForwarder  RESHAPE GL" +
				"\nGL_VENDOR: " + gl.glGetString(GL.GL_VENDOR)+
				"\nGL_RENDERER: " + gl.glGetString(GL.GL_RENDERER) +
				"\nGL_VERSION: " + gl.glGetString(GL.GL_VERSION),
				LoggerType.STATUS);
		
		gl.glLoadIdentity();
		//gl.glFrustum(-1.0f, 1.0f, -h, h, 1.0f, 1000.0f);
		
		//FIXME: It must be considered in the new 
		gl.glOrtho(-4.0f, 4.0f, -h, h, 1.0f, 60.0f);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
////		/* Begin Default Setup */
//		gl.glMatrixMode(GL.GL_PROJECTION);
//		gl.glLoadIdentity();
//		gl.glFrustum(-1.0f, 1.0f, -h, h, 1.0f, 1000.0f);
////		gl.glMatrixMode(GL.GL_MODELVIEW);
////		gl.glLoadIdentity();
////		
////		
////		gl.glMatrixMode(GL.GL_PROJECTION);
////		gl.glLoadIdentity();
//////		gl.glFrustum(-1.0, 1.0, -1.0, 1.0, 1, 10000);
////		/* END Default Setup */
//		
//		/* Manuelas Setup ... */
////		gl.glMatrixMode(GL.GL_PROJECTION);
////		gl.glLoadIdentity();
////		gl.glOrtho(-0.5, 0.5, -0.5, 0.5, 0.01, 1000.0); 			
//		
//		gl.glMatrixMode(GL.GL_MODELVIEW);
//		gl.glLoadIdentity();
//		
//		float [] homography_matrix = {
//				0.767400f,
//				0.017298f,
//				0.000000f,
//				0.130714f,
//				0.017507f,
//				0.641607f,
//				0.000000f,
//				0.107709f,
//				0.000000f,
//				0.000000f,
//				1.000000f,
//				0.000000f,
//				0.072922f,
//				0.039414f,
//				0.000000f,
//				1.119212f }; 
//	
//		gl.glLoadMatrixf(homography_matrix, 0);
//		
//		/* ENDE: Manuelas Setup ... */
		
		gl.glTranslatef(0.0f, 0.0f, -40.0f);
		
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
		
//		if ((drawable instanceof GLJPanel)
//				&& !((GLJPanel) drawable).isOpaque()
//				&& ((GLJPanel) drawable)
//						.shouldPreserveColorBufferIfTranslucent())
//		{
//			gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
//		} else
//		{
//			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
//		}

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
//		
//		renderTestTriangle(gl);
		
		gl.glTranslatef(0,
				0,
				distanceZ );
		
//		renderTestTriangle(gl);
		
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
				"SwtJoglGLCanvasViewRep [" +
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
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.opengl.IGLCanvasDirector#initGLCanvasUser()
	 */
	public synchronized void initGLCanvasUser(GL gl) {
		
		refGeneralManager.getSingelton().logMsg(
				"SwtJoglCanvasViewRep.initGLCanvasUser() [" + iUniqueId + "] " + 
				this.getClass().toString(),
				LoggerType.STATUS);
					
		if ( vecGLCanvasUser.isEmpty() ) 
		{
			refGeneralManager.getSingelton().logMsg(
					"SwtJoglCanvasViewRep.initGLCanvasUser() [" + iUniqueId + "] " + 
					this.getClass().toString() + "  no GLCanvasUSer yet!",
					LoggerType.MINOR_ERROR);
			return;
		}
		
		Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();
		
		refGeneralManager.getSingelton().logMsg(
				"SwtJoglCanvasViewRep.initGLCanvasUser() [" + iUniqueId + "] " + 
				this.getClass().toString() + "  init GLCanvasUSer ..",
				LoggerType.STATUS);
		
		while ( iter.hasNext() ) {

			IGLCanvasUser glCanvas = iter.next();
			if  ( ! glCanvas.isInitGLDone() )
			{
				glCanvas.initGLCanvas( gl );
			}
			
		} // while ( iter.hasNext() ) {
		
		return;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.opengl.IGLCanvasDirector#renderGLCanvasUser(javax.media.opengl.GLAutoDrawable)
	 */
	public void renderGLCanvasUser( GL gl) {
		
		if ( abEnableRendering.get() ) 
		{
			Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();
			
			while ( iter.hasNext() ) {
				
//				//old code: test if canvas was initialized
//				IGLCanvasUser glCanvas = iter.next();
//				if ( glCanvas.isInitGLDone() )
//				{
//					glCanvas.render( drawable );
//				}
				
				iter.next().render( gl );
			}
		}
	}
	
	public void reshapeGLCanvasUser(GL gl, 
			final int x, final int y, 
			final int width, final int height) {

		if ( abEnableRendering.get() ) 
		{
			Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();
			
			while ( iter.hasNext() ) {
				IGLCanvasUser glCanvas = iter.next();
				if ( glCanvas.isInitGLDone() )
				{
					glCanvas.reshape( gl, x, y, width, height );
				}
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.opengl.IGLCanvasDirector#updateGLCanvasUser(javax.media.opengl.GLAutoDrawable)
	 */
	public void updateGLCanvasUser(GL gl) {
		
		if ( abEnableRendering.get() ) 
		{
			Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();
			
			while ( iter.hasNext() ) {
				iter.next().update( gl );
			}
		}
	}
	
	
	public void displayGLChanged(GL gl, 
			final boolean modeChanged, 
			final boolean deviceChanged) {

		if ( abEnableRendering.get() ) 
		{
			Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();
			
			while ( iter.hasNext() ) {
				iter.next().displayChanged(gl, modeChanged, deviceChanged);
			}
		}
		
	}

	
	/**
	 * @return the refGLCanvasDirector
	 */
	public final IGLCanvasDirector getRefGLCanvasDirector() {
	
		assert false : "probably it is not necessary to expose this object!";
	
		return refGLCanvasDirector;
	}
	
}
