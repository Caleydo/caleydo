//package org.caleydo.core.view.opengl.canvas.widgets;
//
//
//import javax.media.opengl.GL;
//
//import org.caleydo.core.data.collection.ISet;
//import org.caleydo.core.data.collection.IStorage;
//import org.caleydo.core.data.collection.IVirtualArray;
//import org.caleydo.core.data.collection.virtualarray.iterator.IVirtualArrayIterator;
//import org.caleydo.core.manager.IGeneralManager;
//import org.caleydo.core.manager.ILoggerManager.LoggerType;
//import org.caleydo.core.manager.type.ManagerObjectType;
//import org.caleydo.core.math.statistics.minmax.MinMaxDataInteger;
//import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
//
//import com.sun.opengl.util.GLUT;
//
///**
// * @author Michael Kalkusch
// *
// * @see org.caleydo.core.view.opengl.IGLCanvasUser
// * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver
// */
//public class GLCanvasWidget 
//extends AGLCanvasUser 
//{
//	
//	protected boolean bShowGrid = true;
//	
//	protected MinMaxDataInteger minMaxSeaker;
//	
//	private float [][] viewingFrame;
//	
//	private int iGridSize = 40;
//	
//	private float fPointSize = 1.0f;
//	
//	protected float [] colorDataPoints = { 1.0f, 0.0f, 0.0f };
//	
//	/**
//	 * Color for grid (0,1,2) 
//	 * grid text (3,4,5)
//	 * and point color (6,7,8)
//	 */
//	private float[] colorGrid = { 0.1f, 0.1f , 0.9f, 
//			0.1f, 0.9f, 0.1f,
//			0.9f, 0.1f, 0.1f };
//	
//	protected float[][] fAspectRatio;
//	
//	protected float[] fResolution;
//	
//	protected ISet targetSet;
//	
//	/**
//	 * Constructor.
//	 * 
//	 */
//	public GLCanvasWidget(final IGeneralManager generalManager,
//			int iViewID,
//			int iGLCanvasID,
//			String sLabel) {
//
//		super(generalManager, iViewID, iGLCanvasID, sLabel);
//	}
//	
//	public final void setShowGrid( boolean bSetShowGrid) {
//		this.bShowGrid = bSetShowGrid;	
//	}
//	
//	public final boolean getShowGrid() {
//		return bShowGrid;	
//	}
//	
//	public void setcolorDataPoints( final float [] setColorDataPoints) {
//		assert setColorDataPoints != null : "can not handle null poitner";
//		
//		if ( setColorDataPoints.length != 3 ) {
//			//assert false : "can not handle array with less than 3 values";
//			return;
//		}
//		
//		this.colorDataPoints = setColorDataPoints;
//	}
//	
//	public void renderText( GL gl, 
//			final String showText,
//			final float fx, 
//			final float fy, 
//			final float fz ) {
//		
//		
//		final float fFontSizeOffset = 0.09f;
//		
//	        GLUT glut = new GLUT();
//	        
////	        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
////	        gl.glLoadIdentity();
////	        gl.glTranslatef(0.0f,0.0f,-1.0f);
//	        
//	        // Pulsing Colors Based On Text Position
//	        gl.glColor3fv( colorGrid, 3);
//	        // Position The Text On The Screen...fullscreen goes much slower than the other
//	        //way so this is kind of necessary to not just see a blur in smaller windows
//	        //and even in the 640x480 method it will be a bit blurry...oh well you can
//	        //set it if you would like :)
//	        gl.glRasterPos2f( fx-fFontSizeOffset, fy-fFontSizeOffset );
//	        
//	        //Take a string and make it a bitmap, put it in the 'gl' passed over and pick
//	        //the GLUT font, then provide the string to show
//	        glut.glutBitmapString( GLUT.BITMAP_TIMES_ROMAN_24,
//	        		showText);
//	         
//	}
//	
//	public void setTargetSetId( final int iTargetCollectionSetId ) {
//		
//		targetSet = 
//			generalManager.getSingelton().getSetManager(
//					).getItemSet( iTargetCollectionSetId );
//		
//		if ( targetSet == null ) {
//			generalManager.getSingelton().logMsg(
//					"GLCanvasScatterPlot2D.setTargetSetId(" +
//					iTargetCollectionSetId + ") failed, because Set is not registed!",
//					LoggerType.FULL );
//		}
//		
//		generalManager.getSingelton().logMsg(
//				"GLCanvasScatterPlot2D.setTargetSetId(" +
//				iTargetCollectionSetId + ") done!",
//				LoggerType.FULL );
//		
//		updateMinMax();
//	}
//		
//	
//	@Override
//	public void renderPart(GL gl)
//	{
//		gl.glTranslatef( 0,0, 0.01f);
//		
//		if (( iGridSize > 1 )&&(bShowGrid))
//		{
//			drawScatterPlotGrid( gl ,iGridSize );
//		}
//		
//		if ( targetSet != null ) 
//		{
//			drawScatterPlotInteger( gl );		
//		}
//	
//		//System.err.println(" MinMax ScatterPlot2D .render(GLCanvas canvas)");
//	}
//
//	protected void drawScatterPlotGrid( GL gl, int iResolution) 
//	{
//		gl.glColor3fv( colorGrid, 0); // Set the color to red
//		
////		
////		float fIncX = (viewingFrame[X][MAX] - viewingFrame[X][MIN]) / (iResolution + 1);
////		float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN]) / (iResolution + 1);
////		
////		float fXvertical = viewingFrame[X][MIN] + fIncX;
////		float fYhoricontal = viewingFrame[Y][MIN] + fIncY;
////		
////		
////		/**
////		 * Box..
////		 */
////		
////		gl.glBegin(GL.GL_LINE_LOOP); // Drawing using triangles
////		gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MIN], viewingFrame[Z][MIN]); // Top
////		gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MIN], viewingFrame[Z][MIN]); // Bottom left
////		gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MAX], viewingFrame[Z][MIN]); // Bottom left
////		gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MAX], viewingFrame[Z][MIN]); // Bottom left
////		gl.glEnd(); // Finish drawing the triangle
////		
////		/**
////		 * End draw Box
////		 */
////		
////		
////		for ( int i=0; i < iResolution; i++ )
////		{
////			gl.glBegin(GL.GL_LINES); // Drawing using triangles
////			gl.glVertex3f(fXvertical, viewingFrame[Y][MIN], viewingFrame[Z][MIN]); // Top
////			gl.glVertex3f(fXvertical, viewingFrame[Y][MAX], viewingFrame[Z][MIN]); // Bottom left
////			
////			gl.glVertex3f(viewingFrame[X][MIN], fYhoricontal, viewingFrame[Z][MIN]); // Top
////			gl.glVertex3f(viewingFrame[X][MAX], fYhoricontal, viewingFrame[Z][MIN]); // Bottom left
////			
////			gl.glEnd(); // Finish drawing the triangle
////			
////			fXvertical += fIncX;
////			fYhoricontal += fIncY;
////		}
////		
////		gl.glTranslatef(0, 0, viewingFrame[Z][MIN]);
////		
////		renderText( gl, "Y-Axis",
////				viewingFrame[X][MAX], 
////				viewingFrame[Y][MIN] - viewingFrame[Y][MAX], 
////				viewingFrame[Z][MIN] );
////		
////		renderText( gl, "X-Axis", 
////				0,
////				viewingFrame[Y][MIN], 
////				viewingFrame[Z][MIN] );
////		
////		gl.glTranslatef(0, 0, - viewingFrame[Z][MIN]);
//		
//	}
//	
//	protected void drawScatterPlotInteger(GL gl) {
//		
//		
//		if ( targetSet.getDimensions() < 2 ) {
//			return;
//		}
//		
//		/**
//		 * Check type of set...
//		 */
//		ManagerObjectType typeData = targetSet.getBaseType();
//		
//		switch ( typeData )
//		{
//			case SET_PLANAR: break;
//			
//			case SET_MULTI_DIM: break;
//			
//			default:
//				generalManager.getSingelton().logMsg(
//						"GLCanvasScatterPlot assigned Set mut be at least 2-dimesional!",
//						LoggerType.VERBOSE );
//		} // switch
//				
//		if ( ! targetSet.getReadToken() ) 
//		{
//			return;
//		}
//		
//		IVirtualArray [] arraySelectionX = targetSet.getVirtualArrayByDim(0);
//		IVirtualArray [] arraySelectionY = targetSet.getVirtualArrayByDim(1);
//		
//		IStorage [] arrayStorageX = targetSet.getStorageByDim(0);
//		IStorage [] arrayStorageY = targetSet.getStorageByDim(1);
//				
//		int iLoopX = arraySelectionX.length;
//		int iLoopY = arraySelectionY.length;
//		int iLoopXY = iLoopX;
//		
//		if ( iLoopX != iLoopY )
//		{
//			if ( iLoopX < iLoopY )
//			{
//				iLoopXY = iLoopX;
//			}
//			else
//			{
//				iLoopXY = iLoopY;
//			}
//		}
//		
//		/**
//		 * Consistency check...
//		 */
//		if (( arrayStorageX.length < iLoopXY)||
//				( arrayStorageY.length < iLoopXY))
//		{
//			generalManager.getSingelton().logMsg(
//					"GLCanvasScatterPlot assigned Storage must contain at least equal number of Stprages as Selections!",
//					LoggerType.ERROR );
//			return;
//		}
//		
//		//gl.glTranslatef( 0, -2.5f, 0);
//		
//		gl.glPointSize( fPointSize );		
//		gl.glDisable( GL.GL_LIGHTING );
//		gl.glColor3fv( colorDataPoints, 0); // Set the color for all points
//		
//		
//		for ( int iOuterLoop = 0; iOuterLoop < iLoopXY; iOuterLoop++  ) 
//		{
//			IVirtualArray selectX = arraySelectionX[iOuterLoop];
//			IVirtualArray selectY = arraySelectionY[iOuterLoop];
//			
//			assert selectX != null : "selectX is NULL!";
//			assert selectY != null : "selectY is NULL!";
//			
//			if (( selectX.getReadToken())&&(selectY.getReadToken()))
//			{
//				IVirtualArrayIterator iterSelectX = selectX.iterator();
//				IVirtualArrayIterator iterSelectY = selectY.iterator();
//				
//				assert iterSelectX != null : "iterSelectX = null!";
//				assert iterSelectY != null : "iterSelectY = null!";
//				
//				IStorage storeX = arrayStorageX[iOuterLoop];
//				IStorage storeY = arrayStorageY[iOuterLoop];
//				
//				int [] arrayIntX = storeX.getArrayInt();
//				int [] arrayIntY = storeY.getArrayInt();
//				
//				//float fTri = 0.05f;
//				
//				//gl.glBegin(GL.GL_POINT); // Draw a quad
//				float fX = 0.0f;
//				float fY = 0.0f;
//				
//				while (( iterSelectX.hasNext() )&&( iterSelectY.hasNext() )) 
//				{
//					try
//					{
//						fX = (float) arrayIntX[ iterSelectX.next() ] / fAspectRatio[X][MAX];
//						fY = (float) arrayIntY[ iterSelectY.next() ] / fAspectRatio[Y][MAX];
//					}
//					catch ( ArrayIndexOutOfBoundsException aiobe) 
//					{
//						// ignore and abort loop!	
//						iterSelectX.setToEnd();
//						
//						break;
//					}
//					
//					//gl.glColor3f(fX * fY, 0.2f, 1 - fX); // Set the color to blue one time only
//					
//					
//					// gl.glBegin(GL.GL_TRIANGLES); // Draw a quad		
//					gl.glBegin(GL.GL_POINTS);
//					
//					gl.glVertex3f(fX + fAspectRatio[X][OFFSET] , fY +fAspectRatio[Y][OFFSET], viewingFrame[Z][MIN]); // Point				
//	//				gl.glVertex3f(fX + fAspectRatio[X][OFFSET] , fY-fTri +fAspectRatio[Y][OFFSET], 0.0f); // Point
//	//				gl.glVertex3f(fX-fTri + fAspectRatio[X][OFFSET] , fY +fAspectRatio[Y][OFFSET], 0.0f); // Point
//					
//					gl.glEnd(); // Done drawing the quad
//					
//	//				gl.glBegin(GL.GL_TRIANGLES); // Drawing using triangles
//	//				gl.glColor3f(0.0f, 0.0f, 1.0f); // Set the color to red
//	//				gl.glVertex3f(0.0f, -2.0f, 0.0f); // Top
//	//				gl.glColor3f(0.0f, 1.0f, 1.0f); // Set the color to green
//	//				gl.glVertex3f(-1.0f, -1.0f, 0.0f); // Bottom left
//	//				gl.glColor3f(1.0f, 1.0f, 0.0f); // Set the color to blue
//	//				gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom right
//	//				gl.glEnd(); // Finish drawing the triangle
//					
//					//System.out.println( fX + " ; " + fY );
//									
//				} // while (( iterSelectX.hasNext() )&&( iterSelectY.hasNext() )) 
//			
//				selectX.returnReadToken();
//				selectY.returnReadToken();
//						
//			} // if (( selectX.getReadToken())&&(selectY.getReadToken()))
//			
//			//gl.glEnd(); // Done drawing the quad
//			
//		} // for ( int iOuterLoop = 0; iOuterLoop < iLoopXY; iOuterLoop++  ) 			
//		
//		gl.glEnable( GL.GL_LIGHTING );
//		
//		// if ( targetSet.getReadToken() )
//		targetSet.returnReadToken();
//	}
//	
//	public void update(GL gl)
//	{
//		// TODO Auto-generated method stub
//		System.err.println(" GLCanvasMinMaxScatterPlot2D.update(GLCanvas canvas)");
//		
//		updateMinMax();
//	}
//
//	public void destroyGLCanvas()
//	{
//		generalManager.getSingelton().logMsg( 
//				"GLCanvasMinMaxScatterPlot2D.destroy(GLCanvas canvas)  id=" + this.iUniqueId,
//				LoggerType.FULL );
//	}
//	
//	protected void updateMinMax() {
//		
//		if ( targetSet == null )
//		{
//			return;
//		}
//		
//		if ( minMaxSeaker == null ) 
//		{
//			minMaxSeaker = new MinMaxDataInteger( targetSet.getId() );		
//		}
//		else
//		{
//			minMaxSeaker.useSet( targetSet );
//		}
//		
//		minMaxSeaker.updateData();
//		
//		if ( minMaxSeaker.getDimension() < 2 ) 
//		{
//			return;
//		}
//		
//		
////		fAspectRatio[X][MIN] = minMaxSeaker.getMin(0);
////		fAspectRatio[Y][MIN] = minMaxSeaker.getMin(1);
////		fAspectRatio[X][MAX] = minMaxSeaker.getMax(0);
////		fAspectRatio[Y][MAX] = minMaxSeaker.getMax(1);
//	}
//	
//}
