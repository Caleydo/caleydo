/**
 * 
 */
package cerberus.view.gui.opengl.canvas.heatmap;

//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import com.sun.opengl.util.GLUT;
//import javax.media.opengl.GLCanvas;

//import gleem.linalg.Vec3f;
//import gleem.linalg.Vec4f;

import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.math.statistics.minmax.MinMaxDataInteger;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.opengl.canvas.AGLCanvasUser_OriginRotation;

/**
 * @author Michael Kalkusch
 *
 */
public class GLCanvasHeatmap2D 
extends AGLCanvasUser_OriginRotation 
implements IGLCanvasUser, IMediatorReceiver, IMediatorSender
{

	private boolean bUseGLWireframe = false;
	
	private int iSetCacheId = 0;

	private float fColorMappingShiftFromMean = 1.0f;
	
	private float fColorMappingHighValue = 1.0f;
	private float fColorMappingLowValue = 0.0f;
	private float fColorMappingMiddleValue = 0.25f;
	
	private float fColorMappingHighRangeDivisor = 1 / 0.75f;
	private float fColorMappingLowRangeDivisor = 1 / 0.25f;
	private float fColorMappingLowRange = 0.25f;
	
	/**
	 * Stretch lowest color in order to be not Back!
	 */
	private float fColorMappingPercentageStretch = 0.2f;
	
	
	private float [][] viewingFrame;
	
	private MinMaxDataInteger refMinMaxDataInteger;
	
	private int iHeatmapDisplayListId = -1;
	
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
	
	
	private int iValuesInRow = 10;
	
	private int iValuesInColum = 10;
	
	protected float[][] fAspectRatio;
	
	protected float[] fResolution;
	
	protected ISet targetSet;
	
	
	public static final int X = 0;
	public static final int Y = 1;
	public static final int Z = 2;
	public static final int MIN = 0;
	public static final int MAX = 1;
	public static final int OFFSET = 2;

	
	/**
	 * @param setGeneralManager
	 */
	public GLCanvasHeatmap2D( final IGeneralManager setGeneralManager,
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
		
		refMinMaxDataInteger = new MinMaxDataInteger( 1 );
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
				
		iValuesInRow = (int) fResolution[12]; 
		
	}
	
	public void setTargetSetId( final int iTargetCollectionSetId ) {
		
		targetSet = 
			refGeneralManager.getSingelton().getSetManager(
					).getItemSet( iTargetCollectionSetId );
		
		if ( targetSet == null ) {
			refGeneralManager.getSingelton().logMsg(
					"GLCanvasScatterPlot2D.setTargetSetId(" +
					iTargetCollectionSetId + ") failed, because Set is not registed!",
					LoggerType.FULL );
		}
		
		refGeneralManager.getSingelton().logMsg(
				"GLCanvasScatterPlot2D.setTargetSetId(" +
				iTargetCollectionSetId + ") done!",
				LoggerType.FULL );
		
		refMinMaxDataInteger.useSet( targetSet );
		initColorMapping( fColorMappingShiftFromMean );

	}	
	
	protected void createDisplayLists(GL gl) {
		
		iHeatmapDisplayListId = gl.glGenLists(1);
		
		gl.glNewList(iHeatmapDisplayListId, GL.GL_COMPILE);	
		displayHeatmap( gl );
		gl.glEndList();
		
		  refGeneralManager.getSingelton().logMsg(
				  "createHeatmap() create DsiplayList)",
				  LoggerType.FULL );
		  
	}
	
	
	@Override
	public void renderPart(GL gl)
	{
		gl.glTranslatef( 0,0, 0.01f);
	
		  if ( targetSet == null ) 
		  {
			  refGeneralManager.getSingelton().logMsg(
					  "createHistogram() can not create Heatmap, because targetSet=null",
					  LoggerType.STATUS );
			  return;
		  }
		  
		  if ( iValuesInRow < 1) {
			  refGeneralManager.getSingelton().logMsg(
					  "createHistogram() can not create Heatmap, because histogramLevels are outside range [1..max]",
					  LoggerType.FULL );
			  return;
		  }
		  
		  IStorage refBufferStorage = targetSet.getStorageByDimAndIndex(0,0);
		  IVirtualArray refBufferSelection = targetSet.getVirtualArrayByDimAndIndex(0,0);
	  		  
	
		  if ((targetSet.hasCacheChanged(iSetCacheId))||
				  ( iHeatmapDisplayListId == -1 ))
			{

				iSetCacheId = targetSet.getCacheId();

				//	    			System.out.print("H:");
				//	    			for ( int i=0;i<iHistogramIntervalls.length; i++) {
				//	    				System.out.print(";" +
				//	    						Integer.toString(iHistogramIntervalls[i]) );
				//	    			}
				System.out.println(" UPDATED!");
				
				createDisplayLists( gl );
				
				  refGeneralManager.getSingelton().logMsg(
						  "createHistogram() use IVirtualArray(" + refBufferSelection.getLabel() + ":" + refBufferSelection.toString() + ")",
						  LoggerType.FULL );
				  
			}
		  
		  if ( refBufferStorage == null ) {
			  return;
		  }
		
		  if (bUseGLWireframe) {
		    	gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
		    }
		    
//		    else 
//		    {
//		    	gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
//		    }     
		  

		  gl.glDisable( GL.GL_LIGHTING );
		  
		  gl.glCallList( iHeatmapDisplayListId );
		  
		  gl.glEnable( GL.GL_LIGHTING );
		  
		//System.err.println(" Heatmap2D ! .render(GLCanvas canvas)");
	}

	
	public void update(GLAutoDrawable canvas)
	{
		System.err.println(" GLCanvasHistogram2D.update(GLCanvas canvas)");	
		
		
	}

	public void destroyGLCanvas()
	{
		refGeneralManager.getSingelton().logMsg( 
				"GLCanvasHeatmap2D.destroy(GLCanvas canvas)  id=" + this.iUniqueId,
				LoggerType.FULL );
	}
	

	
 //public int[] createHistogram(final int iHistogramLevels) {
  public void renderHeatmap(final int iHistogramLevels) {
	  
	


	 
	  refGeneralManager.getSingelton().logMsg( "HEATMAP: set  ", LoggerType.FULL );


	  
  }

  
  public int getHeatmapValuesInRow() {
	  return iValuesInRow;
  }
  
  /**
   * 
   * @param fColorMappingShiftFromMean 1.0f indicates no shift
   */
  private void initColorMapping( final float fColorMappingShiftFromMean ) {
	    
	  if ( refMinMaxDataInteger.isValid() )
	  {
		  fColorMappingLowValue = (float) refMinMaxDataInteger.getMin(0);
		  fColorMappingHighValue = (float) refMinMaxDataInteger.getMax(0);
		  fColorMappingMiddleValue = (float) refMinMaxDataInteger.getMean(0)*fColorMappingShiftFromMean;
		  
		  fColorMappingLowRange = fColorMappingMiddleValue - fColorMappingLowValue;
		  fColorMappingHighRangeDivisor = 1.0f / (fColorMappingHighValue - fColorMappingMiddleValue);
		  fColorMappingLowRangeDivisor = 1.0f / (fColorMappingLowRange * (1.0f + fColorMappingPercentageStretch));
		  
		  
		  iValuesInColum = (int)( (float) refMinMaxDataInteger.getItems( 0 ) / (float) (iValuesInRow) ) ;
	  }
	  else
	  {
		  System.err.println("Error while init color mapping for Heatmap!");
	  }
  }
  
  private void colorMapping(final GL gl, final int iValue) {
	  
	  float fValue = fColorMappingMiddleValue - (float) iValue;
	  
	  if ( fValue < 0.0f ) {
		  // range [fColorMappingLowValue..fColorMappingMiddleValue[
		  
		  float fScale = (fColorMappingLowRange + fValue) * fColorMappingLowRangeDivisor;		  
		  gl.glColor3f( 0, 1.0f - fScale, 0 );
		  
		  return;
	  }
	  //else
	  
	  //range [fColorMappingMiddleValue..fColorMappingHighValue]
	  float fScale = (fValue) * fColorMappingHighRangeDivisor;
	  
	  gl.glColor3f( fScale, 0, 0 );
	  
  }
  
  public void displayHeatmap(GL gl) {

	  /**
	   * Get data from Set...
	   */
		if (this.targetSet != null)
		{

			IStorage refStorage = this.targetSet.getStorageByDimAndIndex(0, 0);

			int[] dataArrayInt = refStorage.getArrayInt();

			IVirtualArray refVArray = this.targetSet.getVirtualArrayByDimAndIndex(
					0, 0);

			IVirtualArrayIterator iter = refVArray.iterator();

			int[] i_dataValues = refStorage.getArrayInt();

			if (i_dataValues != null)
			{

				if (targetSet.hasCacheChanged(iSetCacheId))
				{

					iSetCacheId = targetSet.getCacheId();

					//	    			System.out.print("H:");
					//	    			for ( int i=0;i<iHistogramIntervalls.length; i++) {
					//	    				System.out.print(";" +
					//	    						Integer.toString(iHistogramIntervalls[i]) );
					//	    			}
					System.err.println(" UPDATED inside DispalyList!");
				}
				//System.out.print("-");

				/**
				 * force update ...
				 */

				int iCountValuesInRow = 0;

				float fIncX = (viewingFrame[X][MAX] - viewingFrame[X][MIN])
						/ (float) (iValuesInRow + 1);
				float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN])
						/ (float) (iValuesInColum + 1);

				float fNowX = viewingFrame[X][MIN];
				float fNextX = fNowX + fIncX;

				float fNowY = viewingFrame[Y][MIN];
				float fNextY = fNowY + fIncY;

				gl.glNormal3f(0.0f, 0.0f, 1.0f);

				while (iter.hasNext())
				{

					gl.glBegin(GL.GL_TRIANGLE_FAN);
					// gl.glBegin( GL.GL_LINE_LOOP );

					colorMapping(gl, dataArrayInt[iter.next()]);

					gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
					gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
					gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);
					gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);

					gl.glEnd();
					
					fNowX = fNextX;
					fNextX += fIncX;
					iCountValuesInRow++;

					if (iCountValuesInRow > iValuesInRow)
					{
						fNowY = fNextY;
						fNextY += fIncY;

						fNowX = viewingFrame[X][MIN];
						fNextX = fNowX + fIncX;

						iCountValuesInRow = 0;
					} // if (iCountValuesInRow > iValuesInRow)

				} //while (iter.hasNext())

			} // if (i_dataValues != null)

			float fBias_Z = viewingFrame[Z][MIN] + 0.0001f;

			gl.glColor3f(1.0f, 1.0f, 0.1f);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MIN], fBias_Z);
			gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MIN], fBias_Z);
			gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MAX], fBias_Z);
			gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MAX], fBias_Z);
			gl.glEnd();

		} // if (this.targetSet != null)

	}
  
	public void displayChanged(GLAutoDrawable drawable, 
			final boolean modeChanged, 
			final boolean deviceChanged) {

		this.render( drawable );
		
	}
	
	public void updateReceiver(Object eventTrigger) {
		System.err.println( "UPDATE BINGO !");
	}
	
	
	public void updateReceiver(Object eventTrigger, 
			ISet updatedSet) {
		
		System.err.println( "UPDATE BINGO !");
	}
}
