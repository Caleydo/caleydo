/**
 * 
 */
package cerberus.view.gui.opengl.canvas.histogram;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import com.sun.opengl.util.GLUT;
//import javax.media.opengl.GLCanvas;

//import gleem.linalg.Vec3f;
//import gleem.linalg.Vec4f;

import cerberus.data.collection.ISelection;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.selection.iterator.ISelectionIterator;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.math.statistics.histogram.HistogramData;
import cerberus.math.statistics.histogram.HistogramStatisticsSet;
import cerberus.math.statistics.histogram.StatisticHistogramType;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.opengl.canvas.AGLCanvasUser_OriginRotation;
import cerberus.manager.ILoggerManager.LoggerType;

/**
 * @author kalkusch
 *
 */
public class GLCanvasHistogram2D 
extends AGLCanvasUser_OriginRotation 
implements IGLCanvasUser
{
	
	private boolean bUseGLWireframe = false;
	
	private int iSetCacheId = 0;
	 
	private List < HistogramData > listHistogramData;
	  
	private StatisticHistogramType enumCurrentHistogramMode = StatisticHistogramType.REGULAR_LINEAR;
	 
	/**
	 * Defien number of histogram slots.
	 * Default is 0 to ensure valid settings. 
	 * 
	 *  @see cerberus.view.gui.opengl.canvas.histogram.GLCanvasHistogram2D#createHistogram(int)
	 */
	private int iCurrentHistogramLength = 0;
	
	private float [][] viewingFrame;
	
	private int iGridSize = 40;
	
	private float fPointSize = 1.0f;
	
	/**
	 * Color for grid (0,1,2) 
	 * grid text (3,4,5)
	 * and point color (6,7,8)
	 */
	private float[] colorGrid = { 0.1f, 0.1f , 0.9f, 
			0.1f, 0.9f, 0.1f,
			0.9f, 0.1f, 0.1f };
	
	private int iBorderIntervallLength = 200;
	
	protected float[][] fAspectRatio;
	
	protected float[] fResolution;
	
	protected ISet targetSet;
	
	
	public static final int X = 0;
	public static final int Y = 1;
	public static final int MIN = 0;
	public static final int MAX = 1;
	public static final int OFFSET = 2;

	
	/**
	 * @param setGeneralManager
	 */
	public GLCanvasHistogram2D( final IGeneralManager setGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel )
	{
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
		
		listHistogramData = new  LinkedList < HistogramData > ();
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
		
		iGridSize = (int) fResolution[10]; 
		
		fPointSize = fResolution[11]; 
		
	}
	
	public void setTargetSetId( final int iTargetCollectionSetId ) {
		
		targetSet = 
			refGeneralManager.getSingelton().getSetManager(
					).getItemSet( iTargetCollectionSetId );
		
		if ( targetSet == null ) {
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					"GLCanvasScatterPlot2D.setTargetSetId(" +
					iTargetCollectionSetId + ") failed, because Set is not registed!");
		}
		
		refGeneralManager.getSingelton().getLoggerManager().logMsg(
				"GLCanvasScatterPlot2D.setTargetSetId(" +
				iTargetCollectionSetId + ") done!");
		
		if ( iCurrentHistogramLength > 0 ) 
		{
			createHistogram( iCurrentHistogramLength );
		}
	}
	
	@Override
	public void renderPart(GL gl)
	{
		gl.glTranslatef( 0,0, 0.01f);
	
		displayHistogram( gl );
		
		//System.err.println(" MinMax ScatterPlot2D .render(GLCanvas canvas)");
	}

	
	public void update(GLAutoDrawable canvas)
	{
		System.err.println(" GLCanvasHistogram2D.update(GLCanvas canvas)");	
		
		createHistogram( iCurrentHistogramLength );
	}

	public void destroy()
	{
		System.err.println(" GLCanvasHistogram2D.destroy(GLCanvas canvas)");
	}
	

	
 //public int[] createHistogram(final int iHistogramLevels) {
  public void createHistogram(final int iHistogramLevels) {
	  
	  if ( targetSet == null ) 
	  {
		  refGeneralManager.getSingelton().getLoggerManager().logMsg(
				  "createHistogram() can not create Histogram, because targetSet=null",
				  LoggerType.STATUS );
		  return;
	  }
	  
	  if ( iHistogramLevels < 1) {
		  refGeneralManager.getSingelton().getLoggerManager().logMsg(
				  "createHistogram() can not create Histogram, because histogramLevels are outside range [1..max]",
				  LoggerType.FULL );
		  return;
	  }
	  
	  IStorage refBufferStorage = targetSet.getStorageByDimAndIndex(0,0);
	  ISelection refBufferSelection = targetSet.getSelectionByDimAndIndex(0,0);
  		  
	  refGeneralManager.getSingelton().getLoggerManager().logMsg(
			  "createHistogram() use ISelection(" + refBufferSelection.getLabel() + ":" + refBufferSelection.toString() + ")",
			  LoggerType.FULL );
	  
	   
	  if ( refBufferStorage == null ) {
		  return;
	  }
	  
	  HistogramStatisticsSet histogramCreatorSet = 
		  new HistogramStatisticsSet( iBorderIntervallLength );
	  
	  histogramCreatorSet.addData( targetSet );
	  histogramCreatorSet.setIntervalEqualSpacedInt( iHistogramLevels ,
			  enumCurrentHistogramMode,
			  true, 0 , 0 );
	  
	  HistogramData refResultBuffer = 
		  histogramCreatorSet.getUpdatedHistogramData();
	 
	  listHistogramData.clear();
	  
	  if ( refResultBuffer != null ) {
		  listHistogramData.add( refResultBuffer );
	  }
	  
  }
  
  public void toggleMode() {
	  
	  enumCurrentHistogramMode = enumCurrentHistogramMode.incrementMode();
	  
	  System.out.println(" TOGGLE MODE: " + 
			  enumCurrentHistogramMode.toString() );
	  
	  createHistogram( iCurrentHistogramLength );
	  
	  iSetCacheId = targetSet.getCacheId();
  }
  
  public int getHistogramLength() {
	  return iCurrentHistogramLength;
  }
  
  public void setHistogramLength( final int iSetLegth ) {
	  
	  if (( iSetLegth > 5 )&&(iSetLegth < 10000 )) {
		  iCurrentHistogramLength = iSetLegth;
		
		  if ( targetSet != null )
		  {
			  createHistogram( iCurrentHistogramLength );
			  
			  iSetCacheId = targetSet.getCacheId();
		  }
	  }
	  else {
		  
		  System.out.println("exceed range [3..10000]");
		  
//		  throw new RuntimeException("setHistogramLength(" +
//				  Integer.toString(iSetLegth) + ") exceeded range [3..10000]");
	  }
  }
  
  public void displayHistogram(GL gl) {

	    //gl.glClear(GL.GL_COLOR_BUFFER_BIT|GL.GL_DEPTH_BUFFER_BIT);


	    if (bUseGLWireframe) {
	    	gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
	    }
	    
//	    else 
//	    {
//	    	gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
//	    }     
	    

	    // draw background
//	    gl.glDisable(GL.GL_DEPTH_TEST);
//	    drawSkyBox(gl);
//	    gl.glEnable(GL.GL_DEPTH_TEST);


	    gl.glDisable( GL.GL_LIGHTING );

	    if ( this.targetSet != null ) {
	    	
	    	IStorage refStorage = this.targetSet.getStorageByDimAndIndex(0,0);
	    	
	    	int[] i_dataValues = refStorage.getArrayInt();
	    	
	    	if ( i_dataValues != null ) {
	    		
		    	
		    	if ( targetSet.hasCacheChanged( iSetCacheId ) ) {
		    		
	    			//iHistogramIntervalls = createHistogram(iHistogramSpacing);
	    			createHistogram( iCurrentHistogramLength );
	    			//bUpdateHistogram = false;
	    			
	    			iSetCacheId = targetSet.getCacheId();
	    			
//	    			System.out.print("H:");
//	    			for ( int i=0;i<iHistogramIntervalls.length; i++) {
//	    				System.out.print(";" +
//	    						Integer.toString(iHistogramIntervalls[i]) );
//	    			}
	    			System.out.println(" UPDATED!");
	    		}
		    	//System.out.print("-");
	    		
		    	/**
		    	 * force update ...
		    	 */
		    	Iterator <HistogramData> iter = 
		    		listHistogramData.iterator();

		    	while (iter.hasNext()) { 
		    		HistogramData currentHistogram = iter.next();
			    			    		
		    		iCurrentHistogramLength = currentHistogram.getHistogramSlotCounter();
		    		
			    	float fMinX = -0.7f;
			    	float fMaxX = 0.7f;
			    	
			    	float fMinY = -0.7f;
			    	float fMaxY = 0.7f;
			    	
			    	float fIncX = (fMaxX - fMinX) / 
			    		(float) iCurrentHistogramLength;
			    	float fIncY = (fMaxY - fMinY) / 
			    		(float) currentHistogram.iMaxValuesInIntervall;
			    	
			    	float fNowX = fMinX;
			    	float fNextX = fNowX + fIncX;
			    	
			    	gl.glNormal3f( 0.0f, 0.0f, 1.0f );
		    	
		        	boolean bToggleColor = true;
		        	
		        	//TODO: isert getToken ABC
		        	
			    	for ( int i=0; i < iCurrentHistogramLength; i++ ) {
					    gl.glBegin( GL.GL_TRIANGLE_FAN );
					    			  
					    if ( bToggleColor) {
					    	gl.glColor3f( 1.0f ,0,0 );
					    	bToggleColor = false;
					    }
					    else {
					    	gl.glColor3f( 0, 1.0f ,0 );
					    	bToggleColor = true;
					    }
					    
					    float fBar =  fMinY + fIncY * 
					    	currentHistogram.iCounterPerItervall[i];
					    //iHistogramIntervalls[i];
					    
							gl.glVertex3f( fNowX, fMinY, 0.0f );
							gl.glVertex3f( fNextX, fMinY, 0.0f );
							gl.glVertex3f( fNextX, fBar, 0.0f );
							gl.glVertex3f( fNowX, fBar, 0.0f );						
							
							fNowX  += fIncX;
							fNextX += fIncX;
													
						gl.glEnd();
			    	} //end for:		   
		    	
			    	gl.glColor3f( 0.1f, 0.1f, 1.0f );
			    	gl.glBegin( GL.GL_LINE_LOOP );
				    	gl.glVertex3f( fMinX, fMinY, 0.0f );
						gl.glVertex3f( fMaxX, fMinY, 0.0f );
						gl.glVertex3f( fMaxX, fMaxY, 0.0f );
						gl.glVertex3f( fMinX, fMaxY, 0.0f );
					gl.glEnd();
		    	} //end: if
		    	
	    	} // end while
	    }
	    //else {
		    gl.glBegin( GL.GL_TRIANGLES );
				gl.glNormal3f( 0.0f, 0.0f, 1.0f );
				gl.glColor3f( 1,0,0 );
				gl.glVertex3f( -1.0f, -1.0f, -0.5f );
				gl.glColor3f( 1,0,1 );
				gl.glVertex3f( 1.0f, 1.0f, -0.5f );
				gl.glColor3f( 0,1,0 );
				gl.glVertex3f( 1.0f, -1.0f, -0.5f );
			gl.glEnd();
	    //}
	    
			
			
	    gl.glEnable( GL.GL_LIGHTING );
	    
	    gl.glMatrixMode(GL.GL_MODELVIEW);
	    gl.glPopMatrix();
	  }
  
}
