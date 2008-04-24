package org.caleydo.core.view.opengl.canvas.isosurface;

import java.util.ArrayList;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.manager.view.EPickingMode;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.manager.view.Pick;
import org.caleydo.core.view.jogl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer;

import com.sun.opengl.util.GLUT;


/**
 * @author Michael Kalkusch
 *
 * @see org.caleydo.core.view.opengl.IGLCanvasUser
 */
public class GLCanvasIsoSurface3D 
extends AGLCanvasUser 
{
	
	private CTDataLoader loader = null;
	
	private SoWrapper isosurface = null;
	
	private boolean bHasIsoSurface = false;
	
	//private int iSetCacheId = 0;
	 	 
	/**
	 * Defien number of histogram slots.
	 * Default is 0 to ensure valid settings. 
	 * 
	 *  @see org.caleydo.core.view.opengl.canvas.histogram.GLCanvasHistogram2D#createHistogram(int)
	 */
	private int iCurrentHistogramLength = 0;
	
	//private int iGridSize = 40;
	
	//private float fPointSize = 1.0f;
	
	/**
	 * Color for grid (0,1,2) 
	 * grid text (3,4,5)
	 * and point color (6,7,8)
	 */
	private float[] colorGrid = { 0.1f, 0.1f , 0.9f, 
			0.1f, 0.9f, 0.1f,
			0.9f, 0.1f, 0.1f };

	
	protected float[][] fAspectRatio;
	
	protected float[] fResolution;
	
	protected ISet targetSet;
		
	private int iCounterRender = 0;
	
	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasIsoSurface3D(final IGeneralManager generalManager,
			final int iViewId,
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum) {

		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initLocal(javax.media.opengl.GL)
	 */	
	public void initLocal(final GL gl)
	{
		//iGLDisplayListIndexLocal = gl.glGenLists(1);	
		init(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initRemote(javax.media.opengl.GL, int, org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer, org.caleydo.core.view.jogl.mouse.PickingJoglMouseListener)
	 */
	public void initRemote(final GL gl, 
			final int iRemoteViewID, 
			final JukeboxHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter)
	{
		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;
	
		//iGLDisplayListIndexRemote = gl.glGenLists(1);	
		init(gl);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl.GL)
	 */
	public void init(final GL gl) {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayLocal(javax.media.opengl.GL)
	 */
	public void displayLocal(final GL gl) {
		
		pickingManager.handlePicking(iUniqueId, gl, true);
		
		display(gl);
		pickingTriggerMouseAdapter.resetEvents();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayRemote(javax.media.opengl.GL)
	 */
	public void displayRemote(final GL gl) {
	
		display(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#display(javax.media.opengl.GL)
	 */
	public void display(final GL gl) {
		
		
		gl.glTranslatef( 0,0, 0.01f);
		
		if ( iCounterRender > 50 ) {
			displayHistogram( gl );
		}
		iCounterRender++;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable, boolean, boolean)
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {

		((GLEventListener)parentGLCanvas).displayChanged(
				drawable, modeChanged, deviceChanged);		
	}

	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {

	}
	
	public void renderText( GL gl, 
			final String showText,
			final float fx, 
			final float fy, 
			final float fz ) {
		
		
		final float fFontSizeOffset = 0.09f;
		
	        GLUT glut = new GLUT();
	        
//	        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
//	        gl.glLoadIdentity();
//	        gl.glTranslatef(0.0f,0.0f,-1.0f);
	        
	        // Pulsing Colors Based On Text Position
	        gl.glColor3fv( colorGrid, 3);
	        // Position The Text On The Screen...fullscreen goes much slower than the other
	        //way so this is kind of necessary to not just see a blur in smaller windows
	        //and even in the 640x480 method it will be a bit blurry...oh well you can
	        //set it if you would like :)
	        gl.glRasterPos2f( fx-fFontSizeOffset, fy-fFontSizeOffset );
	        
	        //Take a string and make it a bitmap, put it in the 'gl' passed over and pick
	        //the GLUT font, then provide the string to show
	        glut.glutBitmapString( GLUT.BITMAP_TIMES_ROMAN_24,
	        		showText);
	         
	}
	
	public void setTargetSetId( final int iTargetCollectionSetId ) {
		
		targetSet = 
			generalManager.getSingleton().getSetManager(
					).getItemSet( iTargetCollectionSetId );
		
		if ( targetSet == null ) {
			generalManager.getSingleton().logMsg(
					"GLCanvasIsosurface3D.setTargetSetId(" +
					iTargetCollectionSetId + ") failed, because Set is not registed!",
					LoggerType.ERROR );
		}
		
		generalManager.getSingleton().logMsg(
				"GLCanvasIsosurface3D.setTargetSetId(" +
				iTargetCollectionSetId + ") done!",
				LoggerType.STATUS );
		
		if ( iCurrentHistogramLength > 0 ) 
		{
			generalManager.getSingleton().logMsg(
					"GLCanvasIsosurface3D.setTargetSetId(" +
					iTargetCollectionSetId + ") skip isovalue (not implemented yet!",
					LoggerType.STATUS );
			
			//createIsoSurface( iCurrentHistogramLength );
		}
	}
	
	private void initIsoSurface() {
		
		loader = new CTDataLoader();
		
		//loader.readHeader( inputfile + ".hdr" );
		
		//int [] iDim = {512,512,80,1};
		int [] iDim = {32,32,32,1};
		
		loader.setDimensions( iDim );
		loader.createDataSet();		
		//loader.createEmptyDataSet();
		
		//loader.setFileName( inputfile + ".img" );		
		//loader.readDataSet();
		
		isosurface = new SoWrapper();
		
		isosurface.setDimensions( iDim ) ;
		isosurface.assignData( loader.getDataArray() );
		
		//createIsoSurface( 26 );
				
	}
	
	private void createIsoSurface( final int iIsoValue ) {
		
		if ( isosurface == null ) 
		{
			initIsoSurface();
		}
	
		isosurface.march( iIsoValue );
		
		this.bHasIsoSurface = true;
	}

	public void destroyGLCanvas()
	{
		generalManager.getSingleton().logMsg( "GLCanvasHistogram2D.destroy(GLCanvas canvas)  id=" + this.iUniqueId ,
				LoggerType.STATUS );
	}
  
  public void setIsoValue( final int iSetLegth ) {
	  
	  assert false : "setIsoValue() nut supported yet!";
  
	 //throw new RuntimeException("setIsoValue() nut supported yet!");	 
  }
  
  public void displayHistogram(GL gl) {

	    //gl.glClear(GL.GL_COLOR_BUFFER_BIT|GL.GL_DEPTH_BUFFER_BIT);

	  //gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
	    
	  if  ( ! bHasIsoSurface ) 
	  {
		  this.createIsoSurface( 24 );
	  }
	  
	  gl.glDisable( GL.GL_LIGHTING );
	    
//	  /**
//	   * Box..
//	   */		
//	  gl.glColor3fv( colorGrid, 0); // Set the color to red
//	  gl.glBegin(GL.GL_LINE_LOOP); // Drawing using triangles
//	  gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MIN], viewingFrame[Z][MIN]); // Top
//	  gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MIN], viewingFrame[Z][MIN]); // Bottom left
//	  gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MAX], viewingFrame[Z][MIN]); // Bottom left
//	  gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MAX], viewingFrame[Z][MIN]); // Bottom left
//	  gl.glEnd(); // Finish drawing the triangle
//	  /**
//	   *  End draw Box
//	   */

	  if (( isosurface != null )&&( isosurface.hasValidIsosurface() )) 
	  {  	
	    	Vec3f[] vertexArray = isosurface.getVertices();	    	
	    	int[] facesetArray = isosurface.getFaceSet();
	    	
	    	int iVertexArraySize = isosurface.getVertexSize();
	    	int iFaceSetArraySize = isosurface.getFaceSetSize();

	    	float fx = 0.1f;	    	
	    	float fy = 0.15f;
	    	
	    	if ( vertexArray == null ) 
	    	{
	    		return;
	    	}
	    	
	    	gl.glPointSize( 4.0f );
	    	
	    	gl.glColor3f( 0.1f, 0.1f, 1.0f );
	    	
	    	
	    	
	    	
//	    	Points only!
	    	
	    	for (int i=0; i < iVertexArraySize; i++ ) 
	    	{
	    	
			    gl.glVertex3f( vertexArray[i].x(), 
			    		vertexArray[i].y(),
			    		vertexArray[i].z() );
			    	
			    
			    gl.glVertex3f( vertexArray[i].x(), 
			    		vertexArray[i].y() + fy,
			    		vertexArray[i].z() );
			    
			    gl.glVertex3f( vertexArray[i].x() + fx, 
			    		vertexArray[i].y(),
			    		vertexArray[i].z() );
			    
	    	} // for (int i=0; i < iVertexArraySize; i++ )
	    	
	    	
	    	for (int i=0; i < iFaceSetArraySize; i = i+3 ) 
	    	{
	    	
	    		gl.glBegin( GL.GL_TRIANGLES );
	    		
			    gl.glVertex3f( vertexArray[ facesetArray[i] ].x(), 
			    		vertexArray[ facesetArray[i] ].y(),
			    		vertexArray[ facesetArray[i] ].z() );
			    			    
			    gl.glVertex3f( vertexArray[ facesetArray[i+2] ].x() + fx, 
			    		vertexArray[ facesetArray[i+2] ].y(),
			    		vertexArray[ facesetArray[i+2] ].z() );
			    				   
			    gl.glVertex3f( vertexArray[ facesetArray[i+1] ].x(), 
			    		vertexArray[ facesetArray[i+1] ].y() + fy,
			    		vertexArray[ facesetArray[i+1] ].z() );
			    
			    gl.glEnd();
			    
	    	} // for (int i=0; i < iFaceSetArraySize; i+=3 )
	    
	    	
	    	
	    	
	    }
	    //else {
//		    gl.glBegin( GL.GL_TRIANGLES );
//				gl.glNormal3f( 0.0f, 0.0f, 1.0f );
//				gl.glColor3f( 1,0,0 );
//				gl.glVertex3f( -1.0f, -1.0f, -0.5f );
//				//gl.glColor3f( 1,0,1 );
//				gl.glVertex3f( 1.0f, 1.0f, -0.5f );
//				//gl.glColor3f( 0,1,0 );
//				gl.glVertex3f( 1.0f, -1.0f, -0.5f );
//			gl.glEnd();
//			
//			float fmin = -2.0f;
//			float fmax = 2.0f;
//			
//			float fshiftX = -1.0f;
//			float fshiftY = -2.0f;
//			
//			gl.glBegin( GL.GL_TRIANGLES );
//				gl.glNormal3f( 0.0f, 0.0f, 0.0f );
//				gl.glColor3f( 1,1,0 );
//				
//				gl.glVertex3f( fmin+fshiftX, fmax+fshiftY, 0.0f );
//				gl.glColor3f( 1,0,1 );
//				gl.glVertex3f( fmax+fshiftX, fmin+fshiftY, 0.0f );
//				gl.glColor3f( 0,1,1 );
//				gl.glVertex3f( fmax+fshiftX, fmax+fshiftY, 0.0f );
//			gl.glEnd();
	    //}
	    
			
			
	    gl.glEnable( GL.GL_LIGHTING );
	    
	    //gl.glMatrixMode(GL.GL_MODELVIEW);
	    //gl.glPopMatrix();
	  }
  /*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#handleEvents(org.caleydo.core.manager.view.EPickingType, org.caleydo.core.manager.view.EPickingMode, int, org.caleydo.core.manager.view.Pick)
	 */
	protected void handleEvents(final EPickingType ePickingType, 
			final EPickingMode ePickingMode, 
			final int iExternalID,
			final Pick pick)
	{
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#getInfo()
	 */
	public ArrayList<String> getInfo() {
		
		ArrayList<String> sAlInfo = new ArrayList<String>();
		sAlInfo.add("No info available!");
		return sAlInfo;
	}
}