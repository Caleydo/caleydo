/**
 * 
 */
package org.geneview.core.view.opengl.canvas.scatterplot;

import javax.media.opengl.GL;

//import gleem.linalg.Vec3f;
//import gleem.linalg.Vec4f;

import org.geneview.core.data.collection.IVirtualArray;
import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.view.opengl.GLCanvasStatics;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;

/**
 * @author Michael Kalkusch
 *
 * @see org.geneview.core.view.opengl.IGLCanvasUser
 */
public class GLCanvasScatterPlot2D 
extends AGLCanvasUser 
{
	
	protected float[][] fAspectRatio;
	
	protected int[] iResolution;
	
	protected ISet targetSet;
	
	private static final int X = GLCanvasStatics.X;
	private static final int Y = GLCanvasStatics.Y;
	//private static final int Z = GLCanvasStatics.Z;
	private static final int MIN = GLCanvasStatics.MIN;
	private static final int MAX = GLCanvasStatics.MAX;
	//private static final int OFFSET = GLCanvasStatics.OFFSET;
	
	/**
	 * @param setGeneralManager
	 */
	public GLCanvasScatterPlot2D( final IGeneralManager setGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel )
	{
		super( setGeneralManager, 
				null,
				iViewId,  
				iParentContainerId, 
				sLabel );
		
		fAspectRatio = new float [2][2];
		
		fAspectRatio[X][MIN] = 0.0f;
		fAspectRatio[X][MAX] = 20.0f; 
		fAspectRatio[Y][MIN] = 0.0f; 
		fAspectRatio[Y][MAX] = 20.0f; 
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
	}
	
	
	@Override
	public void renderPart(GL gl)
	{
		
		drawScatterPlotInteger( gl );
		
		//System.err.println(" TestTriangle.render(GLCanvas canvas)");
	}

	protected void drawScatterPlotInteger(GL gl) {
		
		
//		gl.glBegin(GL.GL_TRIANGLES); // Drawing using triangles
//		gl.glColor3f(0.0f, 0.0f, 1.0f); // Set the color to red
//		gl.glVertex3f(0.0f, -2.0f, 0.0f); // Top
//		gl.glColor3f(0.0f, 1.0f, 1.0f); // Set the color to green
//		gl.glVertex3f(-1.0f, -1.0f, 0.0f); // Bottom left
//		gl.glColor3f(1.0f, 1.0f, 0.0f); // Set the color to blue
//		gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom right
//		gl.glEnd(); // Finish drawing the triangle
		
		
		if ( targetSet.getDimensions() < 2 ) {
			return;
		}
		
		/**
		 * Check type of set...
		 */
		ManagerObjectType typeData = targetSet.getBaseType();
		
		switch ( typeData )
		{
			case SET_PLANAR: break;
			
			case SET_MULTI_DIM: break;
			
			default:
				refGeneralManager.getSingelton().logMsg(
						"GLCanvasScatterPlot2D assigned Set mut be at least 2-dimesional!",
						LoggerType.VERBOSE );
		} // switch
		
		IVirtualArray [] arraySelectionX = targetSet.getVirtualArrayByDim(0);
		IVirtualArray [] arraySelectionY = targetSet.getVirtualArrayByDim(1);
		
		IStorage [] arrayStorageX = targetSet.getStorageByDim(0);
		IStorage [] arrayStorageY = targetSet.getStorageByDim(1);
				
		int iLoopX = arraySelectionX.length;
		int iLoopY = arraySelectionY.length;
		int iLoopXY = iLoopX;
		
		if ( iLoopX != iLoopY )
		{
			if ( iLoopX < iLoopY )
			{
				iLoopXY = iLoopX;
			}
			else
			{
				iLoopXY = iLoopY;
			}
		}
		
		/**
		 * Consistency check...
		 */
		if (( arrayStorageX.length < iLoopXY)||
				( arrayStorageY.length < iLoopXY))
		{
			refGeneralManager.getSingelton().logMsg(
					"GLCanvasScatterPlot2D assigned Storage must contain at least equal number of Stprages as Selections!",
					LoggerType.ERROR );
			return;
		}
		
		gl.glTranslatef( -1, -2.5f, 0);
		
		//gl.glPointSize( 2.0f );		
		gl.glColor3f(1.0f, 0.2f, 0.0f); // Set the color to blue one time only	
		
		for ( int iOuterLoop = 0; iOuterLoop < iLoopXY; iOuterLoop++  ) 
		{
			IVirtualArray selectX = arraySelectionX[iOuterLoop];
			IVirtualArray selectY = arraySelectionY[iOuterLoop];
			
			IVirtualArrayIterator iterSelectX = selectX.iterator();
			IVirtualArrayIterator iterSelectY = selectY.iterator();
			
			IStorage storeX = arrayStorageX[iOuterLoop];
			IStorage storeY = arrayStorageY[iOuterLoop];
			
			int [] arrayIntX = storeX.getArrayInt();
			int [] arrayIntY = storeY.getArrayInt();
			
			float fTri = 0.05f;
			
			//gl.glBegin(GL.GL_POINT); // Draw a quad
			
			while (( iterSelectX.hasNext() )&&( iterSelectY.hasNext() )) 
			{
				float fX = (float) arrayIntX[ iterSelectX.next() ] / fAspectRatio[X][MAX];
				float fY = (float) arrayIntY[ iterSelectY.next() ] / fAspectRatio[Y][MAX];
				
				//gl.glColor3f(fX * fY, 0.2f, 1 - fX); // Set the color to blue one time only
				
				
				gl.glBegin(GL.GL_TRIANGLES); // Draw a quad		
				
				gl.glVertex3f(fX, fY, 0.0f); // Point				
				gl.glVertex3f(fX, fY-fTri, 0.0f); // Point
				gl.glVertex3f(fX-fTri, fY, 0.0f); // Point
				gl.glEnd(); // Done drawing the quad
	
				
				System.out.println("GLCanvasScatterPlot2D "+ fX + " ; " + fY );
								
			} // while (( iterSelectX.hasNext() )&&( iterSelectY.hasNext() )) 
			
			//gl.glEnd(); // Done drawing the quad
			
		} // for ( int iOuterLoop = 0; iOuterLoop < iLoopXY; iOuterLoop++  ) 			

	}

	
}
