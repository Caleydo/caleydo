package cerberus.view.gui.opengl.canvas.pathway;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import com.sun.opengl.util.GLUT;

import cerberus.data.collection.ISet;
import cerberus.data.pathway.Pathway;
import cerberus.manager.IGeneralManager;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.opengl.canvas.AGLCanvasUser_OriginRotation;
import cerberus.manager.ILoggerManager.LoggerType;


/**
 * @author Marc Streit
 *
 */
public class GLCanvasPathway2D 
extends AGLCanvasUser_OriginRotation 
implements IGLCanvasUser {
		  	 
	private float [][] viewingFrame;
	
	protected float[][] fAspectRatio;
	
	protected float[] fResolution;
	
	Pathway refCurrentPathway;
	
	public static final int X = 0;
	public static final int Y = 1;
	public static final int MIN = 0;
	public static final int MAX = 1;
	public static final int OFFSET = 2;
	
	/**
	 * Constructor
	 * 
	 * @param setGeneralManager
	 */
	public GLCanvasPathway2D( final IGeneralManager setGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel ) {
		
		super( setGeneralManager, 
				iViewId,  
				iParentContainerId, 
				sLabel );
		
		fAspectRatio = new float [2][3];
		viewingFrame = new float [2][2];
		
		fAspectRatio[X][MIN] = 0.0f;
		fAspectRatio[X][MAX] = 20.0f; 
		fAspectRatio[Y][MIN] = 0.0f; 
		fAspectRatio[Y][MAX] = 20.0f; 
		
		fAspectRatio[Y][OFFSET] = 0.0f; 
		fAspectRatio[Y][OFFSET] = -2.0f; 
		
		viewingFrame[X][MIN] = -1.0f;
		viewingFrame[X][MAX] = 1.0f; 
		viewingFrame[Y][MIN] = 1.0f; 
		viewingFrame[Y][MAX] = -1.0f; 
		
//		listHistogramData = new  LinkedList < HistogramData > ();
	}
	
	public void setTargetPathwayId(final int iTargetPathwayId) {
		
		refCurrentPathway = 
			refGeneralManager.getSingelton().getPathwayManager().
				getCurrentPathway();
		
		if (refCurrentPathway == null) 
		{
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					"GLCanvasPathway2D.setTargetSetId(" +
					iTargetPathwayId + ") failed, because Pathway does not exist!");

			return;
		}
		
		refGeneralManager.getSingelton().getLoggerManager().logMsg(
				"GLCanvasPathway2D.setTargetSetId(" +
				iTargetPathwayId + ") done!");
		
		createPathway();
	}
	
	@Override
	public void renderPart(GL gl)
	{
		gl.glTranslatef( 0, 0, 0.01f);
	
		displayPathway(gl);
	}
	
	public void update(GLAutoDrawable canvas)
	{
		System.err.println(" GLCanvasPathway2D.update(GLCanvas canvas)");	
//		
//		createHistogram( iCurrentHistogramLength );
	}

	public void destroy()
	{
		System.err.println(" GLCanvasPathway2D.destroy(GLCanvas canvas)");
	}
	

	public void createPathway() {
	
	}

	public void displayPathway(GL gl) {
 
		gl.glColor3f(1.0f,0.0f,0.0f);			// Red
		gl.glVertex3f( 0.0f, 1.0f, 0.0f);		// Top Of Triangle (Front)
		gl.glColor3f(0.0f,1.0f,0.0f);			// Green
		gl.glVertex3f(-1.0f,-1.0f, 1.0f);		// Left Of Triangle (Front)
		gl.glColor3f(0.0f,0.0f,1.0f);			// Blue
		gl.glVertex3f( 1.0f,-1.0f, 1.0f);		// Right Of Triangle (Front)
		
		renderText(gl, "Pathway TRALALA", 1.0f, 0.1f, 0.1f);
	}
	
	/**
	 * Method for rendering text in OpenGL.
	 * TODO: Move method to some kind of GL Utility class.
	 * 
	 * @param gl
	 * @param showText
	 * @param fx
	 * @param fy
	 * @param fz
	 */
	public void renderText( GL gl, 
			final String showText,
			final float fx, 
			final float fy, 
			final float fz ) {
		
		final float fFontSizeOffset = 0.09f;

		GLUT glut = new GLUT();

		// gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		// gl.glLoadIdentity();
		// gl.glTranslatef(0.0f,0.0f,-1.0f);

		// Pulsing Colors Based On Text Position
		gl.glColor3f(0.0f, 0.0f, 1.0f);

		// Position The Text On The Screen...fullscreen goes much slower than
		// the other
		// way so this is kind of necessary to not just see a blur in smaller
		// windows
		// and even in the 640x480 method it will be a bit blurry...oh well you
		// can
		// set it if you would like :)
		gl.glRasterPos2f(fx - fFontSizeOffset, fy - fFontSizeOffset);

		// Take a string and make it a bitmap, put it in the 'gl' passed over
		// and pick
		// the GLUT font, then provide the string to show
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24, showText);    
	}
	
}
