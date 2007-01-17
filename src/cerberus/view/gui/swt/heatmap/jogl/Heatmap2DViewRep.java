package cerberus.view.gui.swt.heatmap.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;

//import com.sun.opengl.util.Animator;

import cerberus.manager.IGeneralManager;
//import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.awt.jogl.TriangleMain;
import cerberus.view.gui.swt.base.AJoglViewRep;
import cerberus.view.gui.IView;
//import cerberus.view.gui.swt.widget.SWTEmbeddedJoglWidget;
//import demos.gears.Gears;

public class Heatmap2DViewRep 
extends AJoglViewRep 
implements IView, GLEventListener
{
	protected GLCanvas refGLCanvas;
	
	public Heatmap2DViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);
		
		
		
//		initView();
//		retrieveGUIContainer();
//		drawView();
	}
	
	public void initView()
	{
		System.err.println("Heatmap2DViewRep.initView()");
		
		TriangleMain renderer = new TriangleMain();
		
		setGLEventListener( renderer );
	}
	
	public void init(GLAutoDrawable drawable)
	{
		System.err.println("Heatmap2DViewRep.init( GL )");
		
		GL gl = drawable.getGL();
		
		gl.glShadeModel(GL.GL_SMOOTH);           // Enables Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
		gl.glClearDepth(1.0f);                   // Depth Buffer Setup
		gl.glEnable(GL.GL_DEPTH_TEST);           // Enables Depth Testing
		gl.glDepthFunc(GL.GL_LEQUAL);            // The Type Of Depth Test To Do
        
        /* Really Nice Perspective Calculations */
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);  
	}

	public void display(GLAutoDrawable drawable)
	{
		System.err.println("Heatmap2DViewRep.display( GL )");
		
		GL gl = drawable.getGL();	

		gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );	// Clear The Screen And The Depth Buffer
		gl.glLoadIdentity();					// Reset The Current Modelview Matrix

		gl.glTranslatef(0.0f,0.0f,-10.0f);	
		
//		gl.glTranslatef(-1.5f,0.0f,-6.0f);		
		
		gl.glDisable(GL.GL_LIGHTING);
		
		gl.glColor3f( 1.0f, 0, 0);		
		
		gl.glBegin(GL.GL_TRIANGLE_STRIP);
		 gl.glVertex3f( 0, 1, 0 );
		 gl.glVertex3f( -1, 0, 0 );
		 gl.glVertex3f( -1, -1, 0 );
		 gl.glVertex3f( 0, -1, 0 );
		gl.glEnd();
		
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		System.err.println("Heatmap2DViewRep.reshape()");
		
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
	{
		System.err.println("Heatmap2DViewRep.displayChanged()");
		
	}
	
}
