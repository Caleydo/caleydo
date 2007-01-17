/**
 * 
 */
package cerberus.view.gui.opengl.canvas.isosurface;

import gleem.linalg.Vec3f;

//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import com.sun.opengl.util.GLUT;
//import javax.media.opengl.GLCanvas;

//import gleem.linalg.Vec3f;
//import gleem.linalg.Vec4f;

//import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.ISet;
//import cerberus.data.collection.IStorage;
//import cerberus.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
//import cerberus.math.statistics.histogram.HistogramData;
//import cerberus.math.statistics.histogram.HistogramStatisticsSet;
//import cerberus.math.statistics.histogram.StatisticHistogramType;
import cerberus.view.gui.opengl.GLCanvasStatics;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.opengl.canvas.AGLCanvasUser_OriginRotation;


/**
 * @author Michael Kalkusch
 *
 */
public class GLCanvasIsoSurface3D 
extends AGLCanvasUser_OriginRotation 
implements IGLCanvasUser
{
	
	private CTDataLoader loader = null;
	
	private SoWrapper isosurface = null;
	
	private boolean bHasIsoSurface = false;
	
	//private int iSetCacheId = 0;
	 	 
	/**
	 * Defien number of histogram slots.
	 * Default is 0 to ensure valid settings. 
	 * 
	 *  @see cerberus.view.gui.opengl.canvas.histogram.GLCanvasHistogram2D#createHistogram(int)
	 */
	private int iCurrentHistogramLength = 0;
	
	private float [][] viewingFrame;
	
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
	
	
	private static final int X = GLCanvasStatics.X;
	private static final int Y = GLCanvasStatics.Y;
	private static final int Z = GLCanvasStatics.Z;
	private static final int MIN = GLCanvasStatics.MIN;
	private static final int MAX = GLCanvasStatics.MAX;
	private static final int OFFSET = GLCanvasStatics.OFFSET;
	
	
	private int iCounterRender = 0;
	

	
	/**
	 * @param setGeneralManager
	 */
	public GLCanvasIsoSurface3D( final IGeneralManager setGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel )
	{
		super( setGeneralManager, 
				iViewId,  
				iParentContainerId, 
				sLabel );
		
		fAspectRatio = new float [2][3];
		viewingFrame = new float [3][2];
		
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
		
		viewingFrame[Z][MIN] = 0.0f; 
		viewingFrame[Z][MAX] = 0.0f; 
		
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
	

	public void setResolution( float[] setResolution ) {
		
//		if ( fResolution.length < 6 ) {
//			throw new RuntimeException("GLCanvasMinMaxScatterPlot2D.setResolution() array must contain 3 items.");
//		}
		
		this.fResolution = setResolution;
		
		fAspectRatio[X][MIN] = fResolution[0];
		fAspectRatio[X][MAX] = fResolution[1]; 
		fAspectRatio[Y][MIN] = fResolution[2]; 
		fAspectRatio[Y][MAX] = fResolution[3]; 
		
		fAspectRatio[X][OFFSET] = fResolution[4]; 
		fAspectRatio[Y][OFFSET] = fResolution[5];
		
		viewingFrame[X][MIN] = fResolution[6];
		viewingFrame[X][MAX] = fResolution[7]; 
		viewingFrame[Y][MIN] = fResolution[8]; 
		viewingFrame[Y][MAX] = fResolution[9];
		
		viewingFrame[Z][MIN] = fResolution[10]; 
		viewingFrame[Z][MAX] = fResolution[11]; 
				
		iCurrentHistogramLength = (int) fResolution[12]; 
		
	}
	
	public void setTargetSetId( final int iTargetCollectionSetId ) {
		
		targetSet = 
			refGeneralManager.getSingelton().getSetManager(
					).getItemSet( iTargetCollectionSetId );
		
		if ( targetSet == null ) {
			refGeneralManager.getSingelton().logMsg(
					"GLCanvasIsosurface3D.setTargetSetId(" +
					iTargetCollectionSetId + ") failed, because Set is not registed!",
					LoggerType.ERROR_ONLY );
		}
		
		refGeneralManager.getSingelton().logMsg(
				"GLCanvasIsosurface3D.setTargetSetId(" +
				iTargetCollectionSetId + ") done!",
				LoggerType.STATUS );
		
		if ( iCurrentHistogramLength > 0 ) 
		{
			refGeneralManager.getSingelton().logMsg(
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
	
	
	@Override
	public void renderPart(GL gl)
	{
		gl.glTranslatef( 0,0, 0.01f);
	
		if ( iCounterRender > 50 ) {
			displayHistogram( gl );
		}
		iCounterRender++;
		
		//System.err.println(" MinMax ScatterPlot2D .render(GLCanvas canvas)");
	}

	
	public void update(GLAutoDrawable canvas)
	{
		System.err.println(" GLCanvasHistogram2D.update(GLCanvas canvas)");	
		
		createIsoSurface( iCurrentHistogramLength );
	}

	public void destroy()
	{
		refGeneralManager.getSingelton().logMsg( "GLCanvasHistogram2D.destroy(GLCanvas canvas)  id=" + this.iUniqueId ,
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
	    
	  /**
		 * Box..
		 */
		
		gl.glColor3fv( colorGrid, 0); // Set the color to red
		gl.glBegin(GL.GL_LINE_LOOP); // Drawing using triangles
		gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MIN], viewingFrame[Z][MIN]); // Top
		gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MIN], viewingFrame[Z][MIN]); // Bottom left
		gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MAX], viewingFrame[Z][MIN]); // Bottom left
		gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MAX], viewingFrame[Z][MIN]); // Bottom left
		gl.glEnd(); // Finish drawing the triangle
		
		/**
		 * End draw Box
		 */
		
	



	    if (( isosurface != null )&&( isosurface.hasValidIsosurface() )) {
	    	
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
  
	public void displayChanged(GLAutoDrawable drawable, 
			final boolean modeChanged, 
			final boolean deviceChanged) {

		// TODO Auto-generated method stub
		
	}
}
