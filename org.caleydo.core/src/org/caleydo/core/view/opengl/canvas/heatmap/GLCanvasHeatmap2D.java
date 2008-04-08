package org.caleydo.core.view.opengl.canvas.heatmap;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.awt.Point;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.view.EPickingMode;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.manager.view.Pick;
import org.caleydo.core.math.statistics.minmax.MinMaxDataInteger;
import org.caleydo.core.view.jogl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.canvas.parcoords.GLParCoordsToolboxRenderer;
import org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;

/**
 * @author Michael Kalkusch
 * 
 * @see org.caleydo.core.view.opengl.IGLCanvasUser
 */
public class GLCanvasHeatmap2D 
extends AGLCanvasHeatmap2D
implements IMediatorReceiver, IMediatorSender, IGLCanvasHeatmap2D {

	//private int[] iIndexPickedCoored = {-1,-1};
	
	protected boolean bEnablePicking = true;
	
	protected ArrayList <Vec2f> fIndexPickedCoored = new ArrayList <Vec2f> (1);
	
	protected boolean bUseGLWireframe = false;

	protected int iSetCacheId = 0;

	protected float fColorMappingShiftFromMean = 1.0f;

	protected float fColorMappingHighValue = 1.0f;

	protected float fColorMappingLowValue = 0.0f;

	protected float fColorMappingMiddleValue = 0.25f;

	protected float fColorMappingHighRangeDivisor = 1 / 0.75f;

	protected float fColorMappingLowRangeDivisor = 1 / 0.25f;

	protected float fColorMappingLowRange = 0.25f;

	/**
	 * Stretch lowest color in order to be not Back!
	 */
	protected float fColorMappingPercentageStretch = 0.2f;

	protected int[] iSelectionStartAtIndexX;

	protected int[] iSelectionStartAtIndexY;

	protected int[] iSelectionLengthX;

	protected int[] iSelectionLengthY;

	private boolean bEnalbeMultipleSelection = false;
	
	protected MinMaxDataInteger refMinMaxDataInteger;

	protected int iHeatmapDisplayListId = -1;

	// private int iGridSize = 40;

	// private float fPointSize = 1.0f;

	/**
	 * Color for grid (0,1,2) grid text (3,4,5) and point color (6,7,8)
	 */
	protected float[] colorGrid =
	{ 0.1f, 0.1f, 0.9f, 0.1f, 0.9f, 0.1f, 0.9f, 0.1f, 0.1f };


	private ISet targetSet;

	/**
	 * Picking Mouse handler
	 */
	protected PickingJoglMouseListener pickingTriggerMouseAdapter;
	//private DragAndDropMouseListener pickingTriggerMouseAdapter;

	
	private boolean bIsMousePickingEvent = false;
	
	private boolean bMouseOverEvent = false;
	
	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasHeatmap2D(final IGeneralManager generalManager,
			final int iViewId,
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum) {

		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum);

		fAspectRatio = new float[2][3];
		viewingFrame = new float[3][2];

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

		refMinMaxDataInteger = new MinMaxDataInteger(1);
		
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
		
		
		handlePicking(gl, 0.0f);
		
		gl.glTranslatef( 0,0, 0.01f);
	
		  if ( targetSet == null ) 
		  {
			  generalManager.getSingelton().logMsg(
					  "createHistogram() can not create Heatmap, because targetSet=null",
					  LoggerType.STATUS );
			  return;
		  }
		  
		  if ( iValuesInRow < 1) {
			  generalManager.getSingelton().logMsg(
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
				System.out.println("GLCanvasHeatmap2D - UPDATED!");
				
				render_createDisplayLists( gl );
				
				  generalManager.getSingelton().logMsg(
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
	
	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable, boolean, boolean)
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {

		((GLEventListener)parentGLCanvas).displayChanged(drawable, modeChanged, deviceChanged);
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
	
	}
	
	protected void drawSelectionX(GL gl, final float fStartX,
			final float fStartY, final float fEndX, final float fEndY,
			final float fIncX, final float fIncY) {

		gl.glColor4f(0.0f, 0.0f, 1.0f, 0.4f);

		float fNowY = fStartY - 0.01f;
		float fNextY = fEndY + 0.01f;

		float fBias_Z = viewingFrame[Z][MIN] + 0.0001f;

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

	protected void renderPart4pickingX(GL gl,
			final float fIncX) {
		
		/* public void render_displayListHeatmap(GL gl) { */

		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		
		/**
		 * force update ...
		 */

//		float fIncX = (viewingFrame[X][MAX] - viewingFrame[X][MIN])
//				/ (float) (iValuesInRow );

		float fNowY = viewingFrame[Y][MIN];
		float fNextY = viewingFrame[Y][MAX];

		float fNowX = viewingFrame[X][MIN];
		float fNextX = fNowX + fIncX;

		
		
		/* Y_min .. Y_max*/
		for (int yCoord_name=0; yCoord_name<iValuesInRow; yCoord_name++)
		{					
			//this.setPickingBegin(gl, yCoord_name);
			gl.glLoadName( yCoord_name );
			gl.glPushName( yCoord_name );
			
			gl.glBegin(GL.GL_TRIANGLE_FAN);

			gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);

			gl.glEnd();			
			gl.glPopName();
			
			fNowX = fNextX;
			fNextX += fIncX;
			
		} //for (int yCoord_name=0; yCoord_name<iValuesInRow; yCoord_name++)
  
	}

	protected void renderPart4pickingY(GL gl,
			final float fIncY) {

		/* public void render_displayListHeatmap(GL gl) { */

		gl.glNormal3f(0.0f, 0.0f, 1.0f);

		int ycoord_name = 0;

		/**
		 * force update ...
		 */

//		float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN])
//				/ (float) (iValuesInColum);
		
		float fNowX = viewingFrame[X][MIN];
		float fNextX = viewingFrame[X][MAX];

		float fNowY = viewingFrame[Y][MIN];
		float fNextY = fNowY + fIncY;

		/* Y_min .. Y_max */
		for (int i = 0; i < iValuesInColum; i++)
		{
			gl.glLoadName(ycoord_name);
			gl.glPushName(ycoord_name);

			gl.glBegin(GL.GL_TRIANGLE_FAN);

			gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);

			gl.glEnd();
			gl.glPopName();

			fNowY = fNextY;
			fNextY += fIncY;
			ycoord_name++;

		} // while (iter.hasNext())

		// /* Selection ? */
		// if (iSelectionStartAtIndexY != null) {
		// drawSelectionY(gl,
		// viewingFrame[X][MIN],
		// viewingFrame[Y][MIN],
		// viewingFrame[X][MAX],
		// viewingFrame[Y][MAX],
		// fIncX,
		// fIncY);
		// }
		//				
		// if (iSelectionStartAtIndexX != null) {
		//					
		//					
		// drawSelectionX(gl,
		// viewingFrame[X][MIN],
		// viewingFrame[Y][MIN],
		// viewingFrame[X][MAX],
		// viewingFrame[Y][MAX],
		// fIncX,
		// fIncY);
		// }

		// float fBias_Z = viewingFrame[Z][MIN] + 0.0001f;

	}
	
	protected void pickObjects(final GL gl, Point pickPoint,
			final float fIncX) {

		int PICKING_BUFSIZE = 1024;

		int iArPickingBuffer[] = new int[PICKING_BUFSIZE];
		IntBuffer pickingBuffer = BufferUtil.newIntBuffer(PICKING_BUFSIZE);
		int iHitCount = -1;
		int viewport[] = new int[4];
		
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glSelectBuffer(PICKING_BUFSIZE, pickingBuffer);
		gl.glRenderMode(GL.GL_SELECT);
		gl.glInitNames();

		//gl.glPushName(0);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();		
		gl.glLoadIdentity();
		
		/* create 5x5 pixel picking region near cursor location */
		GLU glu = new GLU();
		glu.gluPickMatrix((double) pickPoint.x,
				(double) (viewport[3] - pickPoint.y),// 
				1.0, 
				1.0, 
				viewport, 
				0); // pick width and height is set to 5
		// (i.e. picking tolerance)

		float h = (float) (float) (viewport[3] - viewport[1])
				/ (float) (viewport[2] - viewport[0]) * 4.0f;

		// FIXME: values have to be taken from XML file!!
		gl.glOrtho(-4.0f, 4.0f, -h, h, 1.0f, 60.0f);

		// Store picked point
		Point tmpPickPoint = (Point) pickPoint.clone();
		// Reset picked point
		pickPoint = null;

		renderPart4pickingX(gl, fIncX);
	
		/* second layer of picking.. */
		gl.glPushMatrix();
		
		float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN])
		/ (float) (iValuesInColum);
		
		gl.glTranslatef( 0,0, AGLCanvasHeatmap2D.fPickingBias );
		renderPart4pickingY(gl,fIncY);
		
		gl.glPopMatrix();
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		
	

		iHitCount = gl.glRenderMode(GL.GL_RENDER);
		pickingBuffer.get(iArPickingBuffer);
		
		//boolean bPickinedNewObject = 
		processHits(gl, iHitCount, iArPickingBuffer, tmpPickPoint, fIndexPickedCoored);
				
	}

	protected void handlePicking(final GL gl,
			final float fIncX) {

		Point pickPoint = null;
		
		/* if no pickingTriggerMouseAdapter was assinged yet, skip it.. */
		if  (pickingTriggerMouseAdapter==null) {
			return;
		}
		
		
//		if ( pickingTriggerMouseAdapter.wasMouseDragged() ) {
//			this.bMouseOverEvent = false;
//		}
//		if ( pickingTriggerMouseAdapter.wasMouseMoved())
//		pickingTriggerMouseAdapter.wasMouseMoved()
		
		if (pickingTriggerMouseAdapter.wasLeftMouseButtonPressed())
		{
			pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
//			bIsMousePickingEvent = true;
		}
//		else
//		{
//			pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
//			bIsMousePickingEvent = false;
//		}

		// Check if an object was picked
		if (pickPoint != null)
		{
			pickObjects(gl, pickPoint, fIncX);
		}

	}
	
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

		System.out.println("GLCanvasHeatmap2D  PICK: ----- " );
		
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
		
		System.out.println("GLCanvasHeatmap2D  PICKED index=[" +resultPickPointCoord[0] + "," + resultPickPointCoord[1] + "]" );
		 
		int[] selectedIndexArray = addPickedPoint(fIndexPickedCoored,
				(float) resultPickPointCoord[0],
				(float) resultPickPointCoord[1] );
		
		if ( selectedIndexArray != null ) {
			
		}
		
		return true;
	}

	/**
	 * @return the bEnablePicking
	 */
	public final boolean isEnablePicking() {
	
		return bEnablePicking;
	}
	
	/**
	 * @param enablePicking the bEnablePicking to set
	 */
	public final void setEnablePicking(boolean enablePicking) {
	
		bEnablePicking = enablePicking;
		
		generalManager.getSingelton().logMsg(
				this.getClass().getSimpleName() + ".setEnablePicking( " +
				Boolean.toString(enablePicking) + " )",
				LoggerType.STATUS);
	}
	
	/* -----   END: PICKING  ----- */
	/* --------------------------- */
	
	protected void drawSelectionY(GL gl, final float fStartX,
			final float fStartY, final float fEndX, final float fEndY,
			final float fIncX, final float fIncY) {

		gl.glColor4f(0.0f, 0.0f, 1.0f, 0.4f);

		float fNowX = fStartX - 0.01f;
		float fNextX = fEndX + 0.01f;

		float fBias_Z = viewingFrame[Z][MIN] + 0.0002f;

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
	


	
	/* (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.heatmap.IGLCanvasHeatmap2D#setTargetSetId(int)
	 */
	public void setTargetSetId( final int iTargetCollectionSetId ) {
		
		targetSet = 
			generalManager.getSingelton().getSetManager(
					).getItemSet( iTargetCollectionSetId );
		
		if ( targetSet == null ) {
			generalManager.getSingelton().logMsg(
					"GLCanvasScatterPlot2D.setTargetSetId(" +
					iTargetCollectionSetId + ") failed, because Set is not registed!",
					LoggerType.FULL );
		}
		
		generalManager.getSingelton().logMsg(
				"GLCanvasScatterPlot2D.setTargetSetId(" +
				iTargetCollectionSetId + ") done!",
				LoggerType.FULL );
		
		refMinMaxDataInteger.useSet( targetSet );
		initColorMapping( fColorMappingShiftFromMean );

	}	
	
	protected void render_createDisplayLists(GL gl) {
		
		iHeatmapDisplayListId = gl.glGenLists(1);
		
		gl.glNewList(iHeatmapDisplayListId, GL.GL_COMPILE);	
		render_displayListHeatmap( gl );
		gl.glEndList();
		
		  generalManager.getSingelton().logMsg(
				  "createHeatmap() create DsiplayList)",
				  LoggerType.FULL );
		  
	}
	
	protected void renderGLSingleQuad( GL gl, ArrayList <Vec2f> fIndexPickedCoord, int iIndexStart ) {
	
		if ( fIndexPickedCoord.isEmpty() ) {
			return;			
		}
		Vec2f current = fIndexPickedCoord.get(iIndexStart);
		
		float fIncX = (viewingFrame[X][MAX] - viewingFrame[X][MIN])
		/ (float) (iValuesInRow );		
		float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN])
		/ (float) (iValuesInColum );

		float fNowX = viewingFrame[X][MIN] + fIncX * current.x();
		float fNextX = viewingFrame[X][MIN] + fIncX * (current.x() +1.0f);
		float fNowY = viewingFrame[Y][MIN] + fIncY * current.y();
		float fNextY = viewingFrame[Y][MIN] + fIncY * (current.y() +1.0f);
			
		gl.glBegin(GL.GL_TRIANGLE_FAN);

			gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);

		gl.glEnd();			
	}

	protected int[] addPickedPoint( ArrayList <Vec2f> fIndexPickedCoord, 
			final float addIndexCoordX, float addIndexCoordY) {
		
		int iSize = fIndexPickedCoord.size();
		
		if ( (iSize % 2) == 1) {
			/* one remaining point of last picking */
			Vec2f lastPickedIndexCoord= fIndexPickedCoord.get(iSize-1);
			
			Vec2f lowerLeftPoint = new Vec2f();
			Vec2f upperRightPoint = new Vec2f();
			
			/* create a rectangle with lower.left point and upper,right point */
			if (lastPickedIndexCoord.x() < addIndexCoordX ) {
				lowerLeftPoint.setX( lastPickedIndexCoord.x() );
				upperRightPoint.setX( addIndexCoordX );
			} else {
				lowerLeftPoint.setX( addIndexCoordX );
				upperRightPoint.setX( lastPickedIndexCoord.x());
			}
			
			/* create a rectangle with lower.left point and upper,right point */
			if (lastPickedIndexCoord.y() < addIndexCoordY) {
				lowerLeftPoint.setY( lastPickedIndexCoord.y() );
				upperRightPoint.setY( addIndexCoordY );
			} else {
				lowerLeftPoint.setY( addIndexCoordY );
				upperRightPoint.setY( lastPickedIndexCoord.y());
			}
			
			if  ( bEnalbeMultipleSelection ) 
			{
				fIndexPickedCoord.set( iSize-1, lowerLeftPoint);
				fIndexPickedCoord.add( upperRightPoint );
			}
			else 
			{			
				fIndexPickedCoord.clear();
				fIndexPickedCoord.add( lowerLeftPoint);
				fIndexPickedCoord.add( upperRightPoint );
			}
			
			/** Calculate all indices between left and right point and create an array with all these indices.. */
			int iCurrentIndex = (int) lowerLeftPoint.x();
			int iLength = (int) upperRightPoint.x() - iCurrentIndex;
			
			int[] resultArray = new int[iLength+1];
			for ( int i=0; i< iLength+1; i++) {
				resultArray[i] = iCurrentIndex;
				iCurrentIndex++;
			}
			
			return resultArray;
		} else {
			fIndexPickedCoord.add( new Vec2f(addIndexCoordX,addIndexCoordY) );
			return null;
		}		
	}
	
	protected void renderGLAllQuadDots( GL gl, ArrayList <Vec2f> fIndexPickedCoord) {
		
		if ( fIndexPickedCoord.isEmpty() ) {
			return;			
		}
		
		Iterator <Vec2f> iter = fIndexPickedCoord.iterator();
		
		while ( iter.hasNext() ) {
			float fIncX = (viewingFrame[X][MAX] - viewingFrame[X][MIN])
			/ (float) (iValuesInRow );		
			float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN])
			/ (float) (iValuesInColum );
	
			Vec2f fVec2fMultiplyer =  iter.next();
			
			float fNowX = viewingFrame[X][MIN] + fIncX * fVec2fMultiplyer.x();
			float fNextX = viewingFrame[X][MIN] + fIncX * (fVec2fMultiplyer.x() +1.0f);
			float fNowY = viewingFrame[Y][MIN] + fIncY * fVec2fMultiplyer.y();
			float fNextY = viewingFrame[Y][MIN] + fIncY * (fVec2fMultiplyer.y() +1.0f);
				
			gl.glBegin(GL.GL_TRIANGLE_FAN);
	
				gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
				gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
				gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);
				gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);
	
			gl.glEnd();	
		}
	}
	
	protected void renderGLQuad( GL gl, 
			final float fX1,
			final float fX2, 
			final float fY1, 
			final float fY2, 
			final float fZ) {
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		
		gl.glVertex3f(fX1, fY1, fZ);
		gl.glVertex3f(fX2, fY1, fZ);
		gl.glVertex3f(fX2, fY2, fZ);
		gl.glVertex3f(fX1, fY2, fZ);

	gl.glEnd();	
	}
	
	protected void renderGLAllQuadRectangle( GL gl, ArrayList <Vec2f> fIndexPickedCoord) {
		
		if ( fIndexPickedCoord.isEmpty() ) {
			return;			
		}
		
		Iterator <Vec2f> iter = fIndexPickedCoord.iterator();
		int iIndex = 0;
		
		while ( iter.hasNext() ) {
			float fIncX = (viewingFrame[X][MAX] - viewingFrame[X][MIN])
			/ (float) (iValuesInRow );		
			float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN])
			/ (float) (iValuesInColum );
	
			Vec2f fVec2fMultiplyer =  iter.next();
			iIndex++;
			
			if  ( iter.hasNext() ) {
				/** Rectangle - mode*/
				Vec2f fVec2fMultiplyerUpperRight = iter.next();
				iIndex++;
				
				/** first strip */
				renderGLQuad(gl, 
						viewingFrame[X][MIN] + fIncX * fVec2fMultiplyer.x(), 
						viewingFrame[X][MIN] + fIncX * (fVec2fMultiplyer.x() + 1),
						viewingFrame[Y][MIN] + fIncY * fVec2fMultiplyer.y(), 
						viewingFrame[Y][MIN] + fIncY * (fVec2fMultiplyerUpperRight.y() +1), 
						viewingFrame[Z][MIN]);
				
				/** second strip */
				renderGLQuad(gl, 
						viewingFrame[X][MIN] + fIncX * fVec2fMultiplyerUpperRight.x(), 
						viewingFrame[X][MIN] + fIncX * (fVec2fMultiplyerUpperRight.x() + 1),
						viewingFrame[Y][MIN] + fIncY * fVec2fMultiplyer.y(), 
						viewingFrame[Y][MIN] + fIncY * (fVec2fMultiplyerUpperRight.y() +1), 
						viewingFrame[Z][MIN]);
				
				
				/** third strip */
				renderGLQuad(gl, 
						viewingFrame[X][MIN] + fIncX * (fVec2fMultiplyer.x() +1), 
						viewingFrame[X][MIN] + fIncX * fVec2fMultiplyerUpperRight.x(),
						viewingFrame[Y][MIN] + fIncY * fVec2fMultiplyer.y(), 
						viewingFrame[Y][MIN] + fIncY * (fVec2fMultiplyer.y() +1), 
						viewingFrame[Z][MIN]);
				
				/** forth strip */
				renderGLQuad(gl, 
						viewingFrame[X][MIN] + fIncX * (fVec2fMultiplyer.x() +1), 
						viewingFrame[X][MIN] + fIncX * fVec2fMultiplyerUpperRight.x(),
						viewingFrame[Y][MIN] + fIncY * fVec2fMultiplyerUpperRight.y(), 
						viewingFrame[Y][MIN] + fIncY * (fVec2fMultiplyerUpperRight.y() +1), 
						viewingFrame[Z][MIN]);
			}
			else {
				/** */
				renderGLSingleQuad(gl ,fIndexPickedCoord, iIndex-1);
			}
			
		
			
		}
	}

	
 //public int[] createHistogram(final int iHistogramLevels) {
  public void renderHeatmap(final int iHistogramLevels) {

	  generalManager.getSingelton().logMsg( "HEATMAP: set  ", LoggerType.FULL );
	  
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
  
  public void render_displayListHeatmap(GL gl) {

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
					System.err.println(" UPDATED inside DisplayList!");
				}
				//System.out.print("-");

				/**
				 * force update ...
				 */

				int iCountValuesInRow = 0;

				float fIncX = (viewingFrame[X][MAX] - viewingFrame[X][MIN])
						/ (float) (iValuesInRow + 1);
				float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN])
						/ (float) (iValuesInColum );

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

						iCountValuesInRow = 1;
					} // if (iCountValuesInRow > iValuesInRow)

					
				} //while (iter.hasNext())

				gl.glEnable(GL.GL_DEPTH_TEST);
				gl.glEnable(GL.GL_BLEND);
				gl.glBlendFunc( GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				
				/* Selection ? */
				if (iSelectionStartAtIndexY != null) {
					drawSelectionY(gl, 
							viewingFrame[X][MIN], 
							viewingFrame[Y][MIN], 
							viewingFrame[X][MAX],
							viewingFrame[Y][MAX],
							fIncX, 
							fIncY);
				}
				
				if (iSelectionStartAtIndexX != null) {
					
					
					drawSelectionX(gl, 
							viewingFrame[X][MIN], 
							viewingFrame[Y][MIN], 
							viewingFrame[X][MAX],
							viewingFrame[Y][MAX],
							fIncX, 
							fIncY);
				}
				
				gl.glDisable(GL.GL_BLEND);
				gl.glDisable(GL.GL_DEPTH_TEST);
				
			} // if (i_dataValues != null)

			float fBias_Z = viewingFrame[Z][MIN] + 0.0001f;

		
			
			/* Surrounding box */
			gl.glColor3f(1.0f, 1.0f, 0.1f);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MIN], fBias_Z);
			gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MIN], fBias_Z);
			gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MAX], fBias_Z);
			gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MAX], fBias_Z);
			gl.glEnd();

		} // if (this.targetSet != null)

	}
  
  
    
    
    /* (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.heatmap.IGLCanvasHeatmap2D#setSelectionItems(int[], int[], int[], int[])
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
		
		generalManager.getSingelton().logMsg(
				this.getClass().getSimpleName()+
				": updateReceiver( (" + 
				eventTrigger.getClass().getSimpleName() + ") " +
				eventTrigger.toString() + ")",
				LoggerType.STATUS );
	}
	
	
	public void updateReceiver(Object eventTrigger, 
			ISet updatedSet) {
		
		generalManager.getSingelton().logMsg(
				this.getClass().getSimpleName()+
				": updateReceiver( (" + 
				eventTrigger.getClass().getSimpleName() + ") " +
				eventTrigger.toString() + ", (ISet) " + 
				updatedSet.toString() +	")",
				LoggerType.STATUS );
	}

	
	/**
	 * @return the pickingTriggerMouseAdapter
	 */
	public final PickingJoglMouseListener getPickingTriggerMouseAdapter() {
	
		return pickingTriggerMouseAdapter;
	}

	
	/**
	 * @param pickingTriggerMouseAdapter the pickingTriggerMouseAdapter to set
	 */
	public final void setPickingTriggerMouseAdapter(
			PickingJoglMouseListener pickingTriggerMouseAdapter) {
	
		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;
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
