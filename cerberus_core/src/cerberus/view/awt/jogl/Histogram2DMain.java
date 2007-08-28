package cerberus.view.awt.jogl;

import java.awt.Frame;
//import java.awt.event.MouseListener;
//import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//import javax.media.opengl.*;
//import com.sun.opengl.util.*;

import java.util.Vector;

import java.awt.Dimension;

import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import com.sun.opengl.util.Animator;

import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.SetDataType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.singleton.OneForAllManager;
import cerberus.manager.type.ManagerObjectType;
//import cerberus.view.IView;
//import cerberus.view.AViewManagedRep;
//import cerberus.view.swt.base.ISwtJoglContainerViewRep;
import cerberus.view.jogl.IJoglMouseListener;
import cerberus.view.jogl.mouse.AViewCameraListenerObject;
import cerberus.view.jogl.mouse.JoglMouseListener;
//import cerberus.util.exception.GeneViewRuntimeException;
//import cerberus.util.exception.CerberusExceptionType;

//import cerberus.math.statistics.histogram.IHistogramStatistic;
//import cerberus.math.statistics.histogram.HistogramData;
import cerberus.math.statistics.histogram.HistogramStatisticInteger;
import cerberus.math.statistics.histogram.StatisticHistogramType;

/**
 * Gears.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel) <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */

public class Histogram2DMain 
extends AViewCameraListenerObject 
implements GLEventListener, IJoglMouseListener
{

	private boolean bHistogramIsValid = false;
	
	protected HistogramStatisticInteger refHistogramCreator;

	protected Dimension heatmapDim;
	
	protected Vector <ISet> vecSet;
	
	protected ISet refSet = null;
	
	
	private JoglMouseListener refMouseHandler;

	private float view_rotx = 00.0f, view_roty = 00.0f, view_rotz = 0.0f;

	private float view_x = 0.0f, view_y = 0.0f, view_z = -10.0f;
	
	private int iBorderIntervallLength = 200;

	public static void main(String[] args)
	{

		Histogram2DMain refGearsMainRoot = new Histogram2DMain();

		refGearsMainRoot.runMain();

	}

	public Histogram2DMain()
	{
		this( -1 , new OneForAllManager( null ) );
		
		OneForAllManager rootManager = (OneForAllManager) refGeneralManager;
		rootManager.initAll();
		
	}

	public Histogram2DMain( final int iNewId,
			final IGeneralManager setRefGeneralManager)
	{
		super(iNewId, setRefGeneralManager,null);
		
		vecSet = new Vector <ISet> (); 
		
		heatmapDim = new Dimension( 10, 10 );
		
		refHistogramCreator = new HistogramStatisticInteger( iBorderIntervallLength );
		
		refMouseHandler = new JoglMouseListener(this);

	}
	
	public void runMain()
	{
		createDummySet(); 
		 
		Histogram2DMain refmyGrears = new Histogram2DMain();

		Frame frame = new Frame("Gear Demo");
		GLCanvas canvas = new GLCanvas();

		canvas.addGLEventListener(refmyGrears);

		frame.add(canvas);
		frame.setSize(300, 300);

		final Animator animator = new Animator(canvas);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				// Run this on another thread than the AWT event queue to
				// make sure the call to Animator.stop() completes before
				// exiting
				new Thread(new Runnable()
				{
					public void run()
					{
						animator.stop();
						System.exit(0);
					}
				}).start();
			}
		});

		frame.setVisible(true);

		animator.start();
	}

//	public synchronized void setViewAngles(float fView_RotX, float fView_RotY,
//			float fViewRotZ)
//	{
//		view_rotx = fView_RotX;
//		view_roty = fView_RotY;
//		view_rotz = fViewRotZ;
//	}
//
//	public synchronized void setTranslation(float fView_X, float fView_Y,
//			float fView_Z)
//	{
//		view_x = fView_X;
//		view_y = fView_Y;
//		//	  view_rotz = fView_Z;
//	}

	public void init(GLAutoDrawable drawable)
	{
		// Use debug pipeline
		// drawable.setGL(new DebugGL(drawable.getGL()));

		createDummySet();
		
		GL gl = drawable.getGL();

		System.err.println("INIT GL IS: " + gl.getClass().getName());

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

	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height)
	{
		GL gl = drawable.getGL();

		float h = (float) height / (float) width;

		gl.glMatrixMode(GL.GL_PROJECTION);

		System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
		System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
		System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));
		gl.glLoadIdentity();
		gl.glFrustum(-1.0f, 1.0f, -h, h, 5.0f, 60.0f);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, -40.0f);
	}

	public void display(GLAutoDrawable drawable)
	{

		GL gl = drawable.getGL();
		if ((drawable instanceof GLJPanel)
				&& !((GLJPanel) drawable).isOpaque()
				&& ((GLJPanel) drawable)
						.shouldPreserveColorBufferIfTranslucent())
		{
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		} else
		{
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		}

		/* Clear The Screen And The Depth Buffer */
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity(); // Reset the current modelview matrix

		gl.glTranslatef(view_x, view_y, view_z);

		gl.glPushMatrix();
		gl.glRotatef(view_rotx, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(view_roty, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(view_rotz, 0.0f, 0.0f, 1.0f);

		//renderTriangle( gl );
		renderGL( gl );
		
		gl.glPopMatrix();
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged)
	{
	}
	
	public ManagerObjectType getBaseType() {
		return null;
	}
	
	protected void renderTriangle( GL gl ) {
		gl.glBegin(GL.GL_TRIANGLES); // Drawing using triangles
		gl.glColor3f(1.0f, 0.0f, 0.0f); // Set the color to red
		gl.glVertex3f(0.0f, 1.0f, 0.0f); // Top
		gl.glColor3f(0.0f, 1.0f, 0.0f); // Set the color to green
		gl.glVertex3f(-1.0f, -1.0f, 0.0f); // Bottom left
		gl.glColor3f(0.0f, 0.0f, 1.0f); // Set the color to blue
		gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom right
		gl.glEnd(); // Finish drawing the triangle

		gl.glTranslatef(0.0f, 0.0f, -1.0f); // Move right 3 units
		gl.glColor3f(0.5f, 0.5f, 1.0f); // Set the color to blue one time only
		gl.glBegin(GL.GL_QUADS); // Draw a quad
		gl.glVertex3f(-1.0f, 1.0f, 0.0f); // Top left
		gl.glVertex3f(1.0f, 1.0f, 0.0f); // Top right
		gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom right
		gl.glVertex3f(-1.0f, -1.0f, 0.0f); // Bottom left
		gl.glEnd(); // Done drawing the quad
	}
	
	/**
	 * Does the rendering.
	 * 
	 * @param drawable
	 */
	private void renderGL(GL gl) {
			
		//refSet = (ISet) this.refGeneralManager.getItem( 70100 );		
		//
		//GL gl = drawable.getGL();	

		
		  
		if ( ! bHistogramIsValid ) {
			
			refHistogramCreator.addDataValues( 
					refSet.getStorageByDimAndIndex(0,0).getArrayInt() );
			
			refHistogramCreator.setIntervalEqualSpaced( 100, 
					StatisticHistogramType.REGULAR_LINEAR,
					true, 0,0 );
			
			refHistogramCreator.updateHistogram();			
			
			bHistogramIsValid = true;
		}
		
		//int iPrimitive = 0;
		
		//gl.glDisable(GL.GL_LIGHTING);
		
//		float blue[] = { 0.2f, 0.2f, 1.0f, 1.0f };
//		
//		gl.glNewList(iPrimitive, GL.GL_COMPILE);
//			gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, blue);
//			gl.glShadeModel(GL.GL_FLAT);
//			gl.glNormal3f(0.0f, 0.0f, 1.0f);
//			gl.glBegin(GL.GL_QUAD_STRIP);
//			 gl.glVertex3f( 0,0,0 );
//			 gl.glVertex3f( 1,0,0 );
//			 gl.glVertex3f( 0,1,0 );
//			 gl.glVertex3f( 1,1,0 );
//			gl.glEnd(); 
//	    gl.glEndList();
	      
		//final int iCounterRows = refHistogramCreator.getRowWidth();
		
		final int[] iCounterPerRow = 
			refHistogramCreator.getHistogramIntervallsToInt();
		
		boolean bAlternateColor = true;
		
		final int iHistogramCell_width_X = 5;
		//final int iHistogramCell_height_Y = 10;
		
//		
//		final  int iHistogramCell_offsetX = 10;
//		final  int iHistogramCell_offsetY = 10
//			+ refHistogramCreator.getMaxCountPerRow()*iHistogramCell_height_Y;
		
		final  int iHistogramCell_incX = 5;
		final  int iHistogramCell_incY = 10;	
		
//		int iHistogramCell_PosX = iHistogramCell_offsetX;
//		int iHistogramCell_PosY = iHistogramCell_offsetY;
		
		final float fScaleXY = 0.0035f;
		
		float fRatioX = fScaleXY * iHistogramCell_incX;
		float fRatioY = fScaleXY * iHistogramCell_incY;
		
		float fWidthX = fScaleXY * iHistogramCell_width_X;
		//float fHeightY = fScaleXY * iHistogramCell_height_Y;
		
		float fYMaxHalf = fRatioY * refHistogramCreator.getMaxValuesInAllIntervalls() * 0.5f;
		
		float fOffsetX = (iCounterPerRow.length) * 0.5f * fRatioX;
		
		float fX1 = -fOffsetX;
		float fX2 = fX1 + fWidthX;	
		float fY1 = -fYMaxHalf;
		
		//gl.glRotatef( 0.7f, 0,0, 1 );
		
		for ( int i=0; i<iCounterPerRow.length; i++ ) {
			
			if ( bAlternateColor ) {
				gl.glColor3f( 0, 1, 0 );
				bAlternateColor = false;
			}
			else {
				gl.glColor3f( 1, 0, 0 );
				 bAlternateColor = true;
			}
			
			//gl.glLoadIdentity();
			
			float fY2 = fY1 + fRatioY * (iCounterPerRow[i]);
			
			//gl.glTranslatef( iX1, iY1, -1 );
			
			gl.glBegin(GL.GL_TRIANGLE_STRIP);
			 gl.glVertex3f( fX1, fY1, 0 );
			 gl.glVertex3f( fX2, fY1, 0 );
			 gl.glVertex3f( fX1, fY2, fY2*0.2f );
			 gl.glVertex3f( fX2, fY2, fY2*0.2f );
			gl.glEnd();
			
			fX1 += fRatioX;
			fX2 = fX1 + fWidthX;	
			
			//gl.glCallList( iPrimitive );

		} // end for...
		
		
		gl.glBegin(GL.GL_LINE_LOOP);
		 gl.glVertex3f( -fOffsetX, -fYMaxHalf, 0.0f );
		 gl.glVertex3f( fOffsetX, -fYMaxHalf, 0.0f );
		 gl.glVertex3f( fOffsetX, fYMaxHalf, 0.0f );
		 gl.glVertex3f( -fOffsetX, fYMaxHalf, 0.0f );
		gl.glEnd();
		
//		g.drawLine( iHistogramCell_offsetX,
//				iHistogramCell_offsetY,
//				iHistogramCell_offsetX + 
//					((int) (iCounterPerRow.length+1)*iHistogramCell_incX ) + 
//					iHistogramCell_width_X,
//				iHistogramCell_offsetY );
	}
	
	private void createDummySet() {
		IStorage createdStorage = 
			this.refGeneralManager.getSingelton().getStorageManager().createStorage( 
				ManagerObjectType.STORAGE_FLAT );
		
		int[] iRawDataArray = new int [200];
				
		for ( int i=0; i < iRawDataArray.length; i++ ) {
			iRawDataArray[i] = i % 31; 
		}
		
		createdStorage.setArrayInt( iRawDataArray );
		
		IVirtualArray createdSelection = 
			this.refGeneralManager.getSingelton().getVirtualArrayManager().createVirtualArray( 
				ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK );
		
		createdSelection.setLength( 150 );
		createdSelection.setOffset( 5 );
		createdSelection.setLabel( "test Selection AAA");
		createdSelection.setMultiOffset( 0 );
		createdSelection.setMultiRepeat( 1 );
		
		ISet createdSet = 
			this.refGeneralManager.getSingelton().getSetManager().createSet( 
					SetDataType.SET_LINEAR );
		
		createdSet.setVirtualArrayByDimAndIndex( createdSelection, 0 ,0 );
		createdSet.setStorageByDimAndIndex( createdStorage, 0, 0 );
		
		this.refSet = createdSet;
	}

}
