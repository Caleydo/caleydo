package org.caleydo.core.view.opengl.canvas.histogram;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.view.EPickingMode;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.manager.view.Pick;
import org.caleydo.core.math.statistics.histogram.HistogramData;
import org.caleydo.core.math.statistics.histogram.HistogramStatisticsSet;
import org.caleydo.core.math.statistics.histogram.StatisticHistogramType;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLayer;

import com.sun.opengl.util.GLUT;

/**
 * @author Michael Kalkusch
 * 
 * @see  org.caleydo.core.view.opengl.IGLCanvasUser
 */
public class GLCanvasHistogram2D 
extends AGLCanvasUser {
	
	private boolean bUseGLWireframe = false;
	
	private int iSetCacheId = 0;
	 
	private List < HistogramData > listHistogramData;
	  
	private StatisticHistogramType enumCurrentHistogramMode = StatisticHistogramType.REGULAR_LINEAR;
	
	/**
	 * Define number of histogram slots.
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
	
	private int iBorderIntervallLength = 5;
	
	protected ISet targetSet;
	
	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasHistogram2D(final IGeneralManager generalManager,
			final int iViewId,
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum) {

		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum);
		
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
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initRemote(javax.media.opengl.GL, int, org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer, org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener, org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D)
	 */
	public void initRemote(final GL gl, 
			final int iRemoteViewID,
			final RemoteHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas) 
	{
		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;
	
		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;
	
		//iGLDisplayListIndexRemote = gl.glGenLists(1);	
		init(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl.GL)
	 */
	public void init(final GL gl) {
		
		createHistogram( iCurrentHistogramLength );
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
		
		displayHistogram( gl );
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
	
	public void setTargetSetId( final int iTargetCollectionSetId ) {
		
		targetSet = 
			generalManager.getSetManager(
					).getItemSet( iTargetCollectionSetId );
		
		if ( targetSet == null ) {
//			generalManager.logMsg(
//					"GLCanvasScatterPlot2D.setTargetSetId(" +
//					iTargetCollectionSetId + ") failed, because Set is not registed!",
//					LoggerType.ERROR );
		}
		
//		generalManager.logMsg(
//				"GLCanvasScatterPlot2D.setTargetSetId(" +
//				iTargetCollectionSetId + ") done!",
//				LoggerType.STATUS );
		
		if ( iCurrentHistogramLength > 0 ) 
		{
			createHistogram( iCurrentHistogramLength );
		}
	}
	
	public void destroyGLCanvas()
	{
//		generalManager.logMsg( "GLCanvasHistogram2D.destroy(GLCanvas canvas)  id=" + this.iUniqueId,
//				LoggerType.STATUS );
	}
	

 //public int[] createHistogram(final int iHistogramLevels) {
  public void createHistogram(final int iHistogramLevels) {
	  
	  if ( targetSet == null ) 
	  {
//		  generalManager.logMsg(
//				  "createHistogram() can not create Histogram, because targetSet=null",
//				  LoggerType.STATUS );
		  return;
	  }
	  
	  if ( iHistogramLevels < 1) {
//		  generalManager.logMsg(
//				  "createHistogram() can not create Histogram, because histogramLevels are outside range [1..max]",
//				  LoggerType.FULL );
		  return;
	  }
	  
	  IStorage refBufferStorage = targetSet.getStorageByDimAndIndex(0,0);
	  IVirtualArray refBufferSelection = targetSet.getVirtualArrayByDimAndIndex(0,0);
  		  
//	  generalManager.logMsg(
//			  "createHistogram() use IVirtualArray(" + refBufferSelection.getLabel() + ":" + refBufferSelection.toString() + ")",
//			  LoggerType.FULL );
	  
	   
	  if ( refBufferStorage == null ) {
		  return;
	  }
	  
	  HistogramStatisticsSet histogramCreatorSet = 
		  new HistogramStatisticsSet( iBorderIntervallLength );
	  
	  histogramCreatorSet.setHistoramGetMinMaxFromDataEnabled( true );
	  histogramCreatorSet.addData( targetSet );
	  histogramCreatorSet.setIntervalEqualSpacedInt( iHistogramLevels ,
			  enumCurrentHistogramMode,
			  true, 0 , 0 );
	  
	  HistogramData refResultBuffer = 
		  histogramCreatorSet.getUpdatedHistogramData();
	 
//	  generalManager.logMsg( 
//			  "HISTOGRAM:\n  " + refResultBuffer.toString(),
//			  LoggerType.FULL );
	  
	  listHistogramData.clear();
	  
	  if ( refResultBuffer != null ) {
		  listHistogramData.add( refResultBuffer );
	  }
	  
  }
  
  public void toggleMode() {
	  
	  enumCurrentHistogramMode = enumCurrentHistogramMode.incrementMode();
	  
	  System.out.println("GLCanvasHistogram2D.toggleMode() mode= " + 
			  enumCurrentHistogramMode.toString() );
	  
	  createHistogram( iCurrentHistogramLength );
	  
	  iSetCacheId = targetSet.getCacheId();
  }
  
  public int getHistogramLength() {
	  return iCurrentHistogramLength;
  }
  
  public void setHistogramLength( final int iSetLegth ) {
	  
	  if (( iSetLegth > 0 )&&(iSetLegth < 10000 )) {
		  iCurrentHistogramLength = iSetLegth;
		
		  if ( targetSet != null )
		  {
			  createHistogram( iCurrentHistogramLength );
			  
			  iSetCacheId = targetSet.getCacheId();
		  }
	  }
	  else {
		  
		  System.out.println("GLCanvasHistogram2D.setHistogramLength() exceed range [3..10000]");
		  
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
	    			System.out.println("GLCanvasHistogram2D - UPDATED!");
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
		    				    		
		    		                
//			    	float fMinX = -0.7f;
//			    	float fMaxX = 0.7f;
//			    	
//			    	float fMinY = -0.7f;
//			    	float fMaxY = 0.7f;
			    	
//			    	float fIncX = (viewingFrame[X][MAX] - viewingFrame[X][MIN]) / 
//			    		(float) iCurrentHistogramLength;
//			    	float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN]) / 
//			    		(float) currentHistogram.iMaxValuesInIntervall;
//			    	
//			    	float fNowX = viewingFrame[X][MIN];
//			    	float fNextX = fNowX + fIncX;
			    	
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
					    
//					    float fBar =  
//				    		viewingFrame[Y][MIN] + fIncY * 
//					    	currentHistogram.iCounterPerItervall[i];
//					    //iHistogramIntervalls[i];
//					    
//							gl.glVertex3f( fNowX,  viewingFrame[Y][MIN], viewingFrame[Z][MIN] );
//							gl.glVertex3f( fNextX, viewingFrame[Y][MIN], viewingFrame[Z][MIN] );
//							gl.glVertex3f( fNextX, fBar, viewingFrame[Z][MIN] );
//							gl.glVertex3f( fNowX, fBar, viewingFrame[Z][MIN] );						
//							
//							fNowX  += fIncX;
//							fNextX += fIncX;
//													
//						gl.glEnd();
//			    	} //end for:		   
//		    	
//			    	gl.glColor3f( 0.1f, 0.1f, 1.0f );
//			    	gl.glBegin( GL.GL_LINE_LOOP );
//				    	gl.glVertex3f( viewingFrame[X][MIN], viewingFrame[Y][MIN], viewingFrame[Z][MIN] );
//						gl.glVertex3f( viewingFrame[X][MAX], viewingFrame[Y][MIN], viewingFrame[Z][MIN] );
//						gl.glVertex3f( viewingFrame[X][MAX], viewingFrame[Y][MAX], viewingFrame[Z][MIN] );
//						gl.glVertex3f( viewingFrame[X][MIN], viewingFrame[Y][MAX], viewingFrame[Z][MIN] );
//					gl.glEnd();
		    	} //end: if
		    	
	    	} // end while
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
