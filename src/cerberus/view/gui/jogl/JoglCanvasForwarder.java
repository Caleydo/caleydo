package cerberus.view.gui.jogl;

//import java.awt.Frame;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import javax.media.opengl.GL;
//import javax.media.opengl.GLCanvas;
//import javax.media.opengl.GLJPanel;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

//import com.sun.opengl.util.Animator;

import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.view.gui.jogl.JoglMouseListener;
import cerberus.view.gui.jogl.IJoglMouseListener;
import cerberus.view.gui.opengl.IGLCanvasDirector;
//import cerberus.data.view.camera.IViewCamera;

/**
 * Gears.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel) <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */

public class JoglCanvasForwarder 
extends AViewCameraListenerObject
implements GLEventListener, IJoglMouseListener {

	private final IGLCanvasDirector refGLCanvasDirector;

	private boolean bCallInitOpenGL = true;
	
	private JoglMouseListener refMouseHandler;

//	private float view_rotx = 0.0f, view_roty = 0.0f, view_rotz = 0.0f;
//
//	private float view_x = 0.0f, view_y = 0.0f, view_z = 0.0f;

	//private Animator refAnimator = null;
	


	public JoglCanvasForwarder( final IGeneralManager refGeneralManager,
			final IGLCanvasDirector refGLCanvasDirector, 
			final int iUniqueId) {

		super(iUniqueId, refGeneralManager);
		
		refMouseHandler = new JoglMouseListener(this);

		this.refGLCanvasDirector = refGLCanvasDirector;
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

		gl.glPushMatrix();

		gl.glBegin(GL.GL_TRIANGLES); // Drawing using triangles
		gl.glColor3f(1.0f, 0.0f, 0.0f); // Set the color to red
		gl.glVertex3f(0.0f, 1.0f, 0.0f); // Top
		gl.glColor3f(0.0f, 1.0f, 0.0f); // Set the color to green
		gl.glVertex3f(-1.0f, -1.0f, 0.0f); // Bottom left
		gl.glColor3f(0.0f, 0.0f, 1.0f); // Set the color to blue
		gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom right
		gl.glEnd(); // Finish drawing the triangle

		gl.glPopMatrix();

	}
	

//	/**
//	 * @see cerberus.view.gui.awt.jogl.IJoglMouseListener#setViewAngles(float, float, float)
//	 */
//	public synchronized void setViewAngles(float fView_RotX, float fView_RotY,
//			float fView_RotZ) {
//
//		view_rotx = fView_RotX;
//		view_roty = fView_RotY;
//		view_rotz = fView_RotZ;
//	}
//
//	/**
//	 * 
//	 * @see cerberus.view.gui.awt.jogl.IJoglMouseListener#setTranslation(float, float, float)
//	 */
//	public synchronized void setTranslation(float fView_X, float fView_Y,
//			float fView_Z) {
//
//		view_x = fView_X;
//		view_y = fView_Y;
//		view_z = fView_Z;
//	}



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
			
			if (refGLCanvasDirector != null)
			{
				refGLCanvasDirector.initGLCanvasUser();
			}
			else 
			{
				refGeneralManager.getSingelton().logMsg(
						"JoglCanvasForwarder init() can not call director, becaus director==null!",
						LoggerType.MINOR_ERROR);
			}
			
		} // if  ( bCallInitOpenGL ) {
		else
		{			
			refGeneralManager.getSingelton().logMsg(
					"JoglCanvasForwarder [" + iUniqueId + "] init() ... done",
					LoggerType.STATUS);
		}
				
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

		float h = (float) height / (float) width;

		gl.glMatrixMode(GL.GL_PROJECTION);

		refGeneralManager.getSingelton().logMsg(
				"JoglCanvasForwarder  RESHAPE GL" +
				"\nGL_VENDOR: " + gl.glGetString(GL.GL_VENDOR)+
				"\nGL_RENDERER: " + gl.glGetString(GL.GL_RENDERER) +
				"\nGL_VERSION: " + gl.glGetString(GL.GL_VERSION),
				LoggerType.STATUS);

		gl.glLoadIdentity();
		gl.glFrustum(-1.0f, 1.0f, -h, h, 5.0f, 60.0f);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, -40.0f);
		
		if (refGLCanvasDirector != null)
		{
			refGLCanvasDirector.reshapeGLCanvasUser(drawable,
					x, 
					y, 
					width,
					height);
		}
	}

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

		//view_rotx += 0.9f;
		
		/** Read viewing parameters... */
		Vec3f rot_Vec3f = new Vec3f();
		Vec3f position = refViewCamera.getCameraPosition();
		Rotf rotation = refViewCamera.getCameraRotation();
		//Vec3f positionZoom = refViewCamera.getCameraScale();
		
		float w = rotation.get(rot_Vec3f) * 180.0f / (float) Math.PI;
		
	
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
		drawXYZ(gl);
		
		renderTestTriangle(gl);
		
		gl.glTranslatef(0,
				0,
				distanceZ );
		
		renderTestTriangle(gl);
		
		/** end: visual debugging ...*/

		
		if (refGLCanvasDirector != null)
		{
			refGLCanvasDirector.renderGLCanvasUser(drawable);
		}

	}

	public void displayChanged(GLAutoDrawable drawable, 
			final boolean modeChanged,
			final boolean deviceChanged) {

		refGLCanvasDirector.displayGLChanged( drawable, modeChanged, deviceChanged);
	}
	
	public final ManagerObjectType getBaseType() {
		return ManagerObjectType.VIEW_CANVAS_FORWARDER;
	}

}
