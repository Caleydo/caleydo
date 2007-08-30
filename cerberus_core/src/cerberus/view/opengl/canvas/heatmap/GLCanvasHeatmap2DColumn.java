/**
 * 
 */
package cerberus.view.opengl.canvas.heatmap;

import gleem.linalg.Vec2f;

import java.awt.Point;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;

import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import cerberus.data.mapping.GenomeMappingType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.IGenomeIdManager;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.math.statistics.minmax.MinMaxDataInteger;
import cerberus.view.jogl.mouse.PickingJoglMouseListener;
//import cerberus.view.opengl.canvas.heatmap.AGLCanvasHeatmap2D;
import cerberus.view.opengl.canvas.heatmap.GLCanvasHeatmap2D;

/**
 * @author Michael Kalkusch
 * 
 * @see cerberus.view.opengl.IGLCanvasUser
 */
public class GLCanvasHeatmap2DColumn 
//extends AGLCanvasHeatmap2D
extends GLCanvasHeatmap2D
		implements IMediatorReceiver, IMediatorSender, IGLCanvasHeatmap2D {

	//private int[] iIndexPickedCoored = {-1,-1};
	
	private HashMap <Integer,Integer> hashNCBI_GENE2index;
	
	private ArrayList <Vec2f> fIndexPickedCoored = new ArrayList <Vec2f> (1);
	
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

	private int[] iSelectionStartAtIndexX;

	private int[] iSelectionStartAtIndexY;

	private int[] iSelectionLengthX;

	private int[] iSelectionLengthY;

	

	private MinMaxDataInteger refMinMaxDataInteger;

	private int iHeatmapDisplayListId = -1;

	// private int iGridSize = 40;

	// private float fPointSize = 1.0f;

	/**
	 * Color for grid (0,1,2) grid text (3,4,5) and point color (6,7,8)
	 */
	private float[] colorGrid =
	{ 0.1f, 0.1f, 0.9f, 0.1f, 0.9f, 0.1f, 0.9f, 0.1f, 0.1f };






	protected ISet targetSet;

	/**
	 * Picking Mosue handler
	 */
	private PickingJoglMouseListener pickingTriggerMouseAdapter;
	//private DragAndDropMouseListener pickingTriggerMouseAdapter;
	
	/**
	 * @param setGeneralManager
	 */
	public GLCanvasHeatmap2DColumn(final IGeneralManager setGeneralManager,
			int iViewId,
			int iParentContainerId,
			String sLabel) {

		super(setGeneralManager, 
				iViewId, 
				iParentContainerId, 
				sLabel);

		//hashNCBI_GENE2index = new HashMap <Integer,Integer> ();
		
		fAspectRatio = new float[2][3];
		viewingFrame = new float[3][2];

		fAspectRatio[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MIN] = 0.0f;
		fAspectRatio[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MAX] = 20.0f;
		fAspectRatio[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MIN] = 0.0f;
		fAspectRatio[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MAX] = 20.0f;

		fAspectRatio[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.OFFSET] = 0.0f;
		fAspectRatio[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.OFFSET] = -2.0f;

		viewingFrame[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MIN] = -1.0f;
		viewingFrame[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MAX] = 1.0f;
		viewingFrame[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MIN] = 1.0f;
		viewingFrame[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MAX] = -1.0f;

		viewingFrame[AGLCanvasHeatmap2D.Z][AGLCanvasHeatmap2D.MIN] = 0.0f;
		viewingFrame[AGLCanvasHeatmap2D.Z][AGLCanvasHeatmap2D.MAX] = 0.0f;

		refMinMaxDataInteger = new MinMaxDataInteger(1);
		
		System.err.println("  GLCanvasHeatmap2DColumn()");
		
		this.init(null);
	}

	/** 
	 * init after IGenomeIdManager has load all its data from file.
	 */
	public void init( final IGenomeIdManager readFromIGenomeIdManager ) {
		IGenomeIdManager refIGenomeIdManager = refGeneralManager.getSingelton().getGenomeIdManager();		
		
		hashNCBI_GENE2index = 
			refIGenomeIdManager.getAllKeysByGenomeIdTypeHashMap(
					//GenomeMappingType.NCBI_GENEID_2_NCBI_GENEID_CODE);
					GenomeMappingType.NCBI_GENEID_2_NCBI_GENEID_CODE);

	}
	
	public void setKeysForHeatmap( int[] keys ) {
		
	}
	
	protected void drawSelectionX(GL gl, final float fStartX,
			final float fStartY, final float fEndX, final float fEndY,
			final float fIncX, final float fIncY) {

		gl.glColor4f(0.0f, 0.0f, 1.0f, 0.4f);

		float fNowY = fStartY - 0.01f;
		float fNextY = fEndY + 0.01f;

		float fBias_Z = viewingFrame[AGLCanvasHeatmap2D.Z][AGLCanvasHeatmap2D.MIN] + 0.0001f;

		for (int i = 0; i < this.iSelectionStartAtIndexX.length; i++)
		{

			float fNowX = fStartX + fIncX * iSelectionStartAtIndexX[i];
			float fNextX = fNowX + fIncX * iSelectionLengthX[i];

			gl.glBegin(GL.GL_TRIANGLE_FAN);
			// gl.glBegin( GL.GL_LINE_LOOP );

			gl.glVertex3f(fNowX, fNowY, fBias_Z);
			gl.glVertex3f(fNextX, fNowY, fBias_Z);
			gl.glVertex3f(fNextX, fNextY, fBias_Z);
			gl.glVertex3f(fNowX, fNextY, fBias_Z);

			gl.glEnd();

		} // for ( int i=0; i < this.iSelectionStartAtIndex.length; i++ ) {

	}

	/* --------------------------- */
	/* -----  BEGEN: PICKING ----- */
	
//	private void setPickingBegin(GL gl,int id) {
//		gl.glPushMatrix();
//		gl.glPushName( id );
//	}
//	
//	private void setPickingEnd(GL gl) {
//		gl.glPopName();
//		gl.glPopMatrix();
//	}
//
//	private void setPickingrowAndColum(GL gl,int id) {
//		gl.glLoadName(id);
//	}

	
	protected boolean processHits(final GL gl, int iHitCount,
			int iArPickingBuffer[], 
			final Point pickPoint, 
			ArrayList <Vec2f> fIndexPickedCoored) {

		// System.out.println("Number of hits: " +iHitCount);

		float fDepthSort = Float.MAX_VALUE;
		int iResultPickCoordIndex = 0;

		int[] resultPickPointCoord = {-1,-1};
		
		int iPtr = 0;
		int i = 0;

		//int iPickedObjectId = 0;

		System.out.println("GLCanvasHeatmap2DColumn  PICK: ----- " );
		
		// Only pick object that is nearest
		for (i = 0; i < iHitCount; i++)
		{
			int iNumbersPerHit = iArPickingBuffer[iPtr];
			System.out.print(" #name for this hit=" + iArPickingBuffer[iPtr] );			
			iPtr++;
			
//			// Check if object is nearer than previous objects
//			if (iArPickingBuffer[iPtr] < iMinimumZValue)
//			{
//				System.out.print(" nearer than previouse hit! ");				
//			}
			
			/* ist doch ein float! */
			//iMinimumZValue = iArPickingBuffer[iPtr];
			
			float fZmin = (float) iArPickingBuffer[iPtr];
			
			System.out.print(" minZ=" + fZmin );
			iPtr++;
			System.out.print(" maxZ=" + (float) iArPickingBuffer[iPtr] );
			iPtr++;
			
			System.out.print(" Pick-> [");
			
			int iName = -1;
			
			for (int j=0; j<iNumbersPerHit; j++) {
				iName = iArPickingBuffer[iPtr];
				if  (fZmin < fDepthSort) {					
					fDepthSort = fZmin - AGLCanvasHeatmap2D.fPickingBias;
					if ( iResultPickCoordIndex < 2) {
						resultPickPointCoord[iResultPickCoordIndex] = iName;
						iResultPickCoordIndex++;
					}
				}
				
				System.out.print(" #" + i+ " name=" + iName + "," );
				iPtr++;
			}
			System.out.println("]");
		}
		
		if (iHitCount < 1)
		{
			// Remove pathway pool fisheye

			return false;
		}
		
		System.out.println("GLCanvasHeatmap2DColumn  PICKED index=[" +resultPickPointCoord[0] + "," + resultPickPointCoord[1] + "]" );
		 
		addPickedPoint(fIndexPickedCoored,
				(float) resultPickPointCoord[0],
				(float) resultPickPointCoord[1] );		
		
		return true;
	}

	
	/* -----   END: PICKING  ----- */
	/* --------------------------- */
	
	/**
	 * @see cerberus.view.opengl.IGLCanvasUser#initGLCanvas(javax.media.opengl.GLCanvas)
	 */
	@Override
	public void initGLCanvas(GL gl)
	{
		pickingTriggerMouseAdapter = 
			(PickingJoglMouseListener) 
			openGLCanvasDirector.getJoglCanvasForwarder().getJoglMouseListener();
				
		setInitGLDone();		
	}
	
	protected void drawSelectionY(GL gl, final float fStartX,
			final float fStartY, final float fEndX, final float fEndY,
			final float fIncX, final float fIncY) {

		gl.glColor4f(0.0f, 0.0f, 1.0f, 0.4f);

		float fNowX = fStartX - 0.01f;
		float fNextX = fEndX + 0.01f;

		float fBias_Z = viewingFrame[AGLCanvasHeatmap2D.Z][AGLCanvasHeatmap2D.MIN] + 0.0002f;

		for (int i = 0; i < this.iSelectionStartAtIndexY.length; i++)
		{

			float fNowY = fStartY + fIncY * iSelectionStartAtIndexY[i];
			float fNextY = fNowY + fIncY * iSelectionLengthY[i];

			gl.glBegin(GL.GL_TRIANGLE_FAN);
			// gl.glBegin( GL.GL_LINE_LOOP );

			gl.glVertex3f(fNowX, fNowY, fBias_Z);
			gl.glVertex3f(fNextX, fNowY, fBias_Z);
			gl.glVertex3f(fNextX, fNextY, fBias_Z);
			gl.glVertex3f(fNowX, fNextY, fBias_Z);

			gl.glEnd();

		} // for ( int i=0; i < this.iSelectionStartAtIndex.length; i++ ) {

	}
	    
	    



	

	
	@Override
	public void renderPart(GL gl)
	{
		handlePicking(gl);
		
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
				System.out.println("GLCanvasHeatmap2DColumn - UPDATED!");
				
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

		  //renderGLSingleQuad(gl,this.fIndexPickedCoored,0);
		  
		 

		  gl.glDisable( GL.GL_LIGHTING );
		  
		  gl.glCallList( iHeatmapDisplayListId );
		  
		  gl.glTranslatef(0, 0, -AGLCanvasHeatmap2D.fPickingBias);
		  gl.glColor3f(1, 1, 0);
		  renderGLAllQuadRectangle(gl,this.fIndexPickedCoored);
		  
		  gl.glColor3f(0.8f, 0.8f, 0);
		  renderGLAllQuadDots(gl,this.fIndexPickedCoored);
		  
		  gl.glEnable( GL.GL_LIGHTING );
		  
		//System.err.println(" Heatmap2D ! .render(GLCanvas canvas)");
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
		  
		  
		  float fValuesInColum = (float) refMinMaxDataInteger.getItems( 0 ) / (float) iValuesInRow;
		  
		  iValuesInColum = (int)( fValuesInColum ) ;
	  }
	  else
	  {
		  System.err.println("Error while init color mapping for Heatmap!");
	  }
  }
  
  protected void colorMapping(final GL gl, final int iValue) {
	  
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

				float fIncX = (viewingFrame[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MAX] - viewingFrame[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MIN])
						/ (float) (iValuesInRow + 1);
				float fIncY = (viewingFrame[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MAX] - viewingFrame[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MIN])
						/ (float) (iValuesInColum );

				float fNowX = viewingFrame[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MIN];
				float fNextX = fNowX + fIncX;

				float fNowY = viewingFrame[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MIN];
				float fNextY = fNowY + fIncY;

				gl.glNormal3f(0.0f, 0.0f, 1.0f);

				while (iter.hasNext())
				{
					gl.glBegin(GL.GL_TRIANGLE_FAN);
					// gl.glBegin( GL.GL_LINE_LOOP );

					colorMapping(gl, dataArrayInt[iter.next()]);

					gl.glVertex3f(fNowX, fNowY, viewingFrame[AGLCanvasHeatmap2D.Z][AGLCanvasHeatmap2D.MIN]);
					gl.glVertex3f(fNextX, fNowY, viewingFrame[AGLCanvasHeatmap2D.Z][AGLCanvasHeatmap2D.MIN]);
					gl.glVertex3f(fNextX, fNextY, viewingFrame[AGLCanvasHeatmap2D.Z][AGLCanvasHeatmap2D.MIN]);
					gl.glVertex3f(fNowX, fNextY, viewingFrame[AGLCanvasHeatmap2D.Z][AGLCanvasHeatmap2D.MIN]);

					gl.glEnd();
					
					fNowX = fNextX;
					fNextX += fIncX;
					iCountValuesInRow++;
					

					if (iCountValuesInRow > iValuesInRow)
					{
						fNowY = fNextY;
						fNextY += fIncY;

						fNowX = viewingFrame[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MIN];
						fNextX = fNowX + fIncX;

						iCountValuesInRow = 1;
					} // if (iCountValuesInRow > iValuesInRow)

					
				} //while (iter.hasNext())

				gl.glEnable(GL.GL_DEPTH_TEST);
				gl.glEnable(GL.GL_BLEND);
				gl.glBlendFunc( GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				
				/* Selection ? */
				if (iSelectionStartAtIndexY != null) {
					drawSelectionY(gl, 
							viewingFrame[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MIN], 
							viewingFrame[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MIN], 
							viewingFrame[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MAX],
							viewingFrame[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MAX],
							fIncX, 
							fIncY);
				}
				
				if (iSelectionStartAtIndexX != null) {
					
					
					drawSelectionX(gl, 
							viewingFrame[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MIN], 
							viewingFrame[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MIN], 
							viewingFrame[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MAX],
							viewingFrame[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MAX],
							fIncX, 
							fIncY);
				}
				
				gl.glDisable(GL.GL_BLEND);
				gl.glDisable(GL.GL_DEPTH_TEST);
				
			} // if (i_dataValues != null)

			float fBias_Z = viewingFrame[AGLCanvasHeatmap2D.Z][AGLCanvasHeatmap2D.MIN] + 0.0001f;

		
			
			/* Sourrounding box */
			gl.glColor3f(1.0f, 1.0f, 0.1f);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(viewingFrame[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MIN], viewingFrame[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MIN], fBias_Z);
			gl.glVertex3f(viewingFrame[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MAX], viewingFrame[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MIN], fBias_Z);
			gl.glVertex3f(viewingFrame[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MAX], viewingFrame[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MAX], fBias_Z);
			gl.glVertex3f(viewingFrame[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MIN], viewingFrame[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MAX], fBias_Z);
			gl.glEnd();

		} // if (this.targetSet != null)

	}
  
  
    
    
    /* (non-Javadoc)
	 * @see cerberus.view.opengl.canvas.heatmap.IGLCanvasHeatmap2D#setSelectionItems(int[], int[], int[], int[])
	 */
    public void setSelectionItems( int[] selectionStartAtIndexX, 
    		int[] selectionLengthX,
    		int[] selectionStartAtIndexY, 
    		int[] selectionLengthY ) {
    	   	
    	if  (selectionStartAtIndexX != null ) 
    	{
    		/* consistency */ 
        	assert selectionLengthX != null : "selectionStartAtIndex is null-pointer";    	
        	assert selectionStartAtIndexX.length == selectionLengthX.length : "both arrays must have equal length";
        	
	    	iSelectionStartAtIndexX = selectionStartAtIndexX;    	
	    	iSelectionLengthX = selectionLengthX;
    	}
    	
    	if  (selectionStartAtIndexY != null ) 
    	{
    		/* consistency */ 
        	assert selectionLengthY != null : "selectionStartAtIndex is null-pointer";    	
        	assert selectionStartAtIndexX.length == selectionLengthX.length : "both arrays must have equal length";
        	
	    	iSelectionStartAtIndexY = selectionStartAtIndexY;    	
	    	iSelectionLengthY = selectionLengthY;
    	}
    	
    }
  
	
	
	public void updateReceiver(Object eventTrigger) {
		System.err.println( "UPDATE BINGO !");
	}
	
	
	public void updateReceiver(Object eventTrigger, 
			ISet updatedSet) {
		
		System.err.println( "UPDATE BINGO !");
		
		System.err.println( " UPDATE SET: " + updatedSet.toString() );
		
		IStorage[] storage = updatedSet.getStorageByDim(0);
		IVirtualArray[] virtualArray = updatedSet.getVirtualArrayByDim(0);
		
		for ( int i=0; i < virtualArray.length; i++) {
			IVirtualArray vaBuffer = virtualArray[i];
			
			int[] intBuffer = storage[0].getArrayInt();
			
			for ( int j=0; j<intBuffer.length; j++) {
				
				Integer indexInt = hashNCBI_GENE2index.get(intBuffer[j]);
				
				if  (indexInt != null ) {
					System.err.print( "[" + intBuffer[j] + "=>" + indexInt + "], ");
				} else {
					System.err.print( "[" + intBuffer[j] + "=> ?? ], ");
				}
								
//				String ncbi_code = 
//					refGeneralManager.getSingelton().getGenomeIdManager().getIdStringFromIntByMapping(
//						intBuffer[j], 
//						GenomeMappingType.NCBI_GENEID_2_NCBI_GENEID_CODE);				
			}
		}
		
		
		
		
	}


}
