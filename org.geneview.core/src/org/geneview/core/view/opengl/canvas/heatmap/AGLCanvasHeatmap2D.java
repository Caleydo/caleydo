/**
 * 
 */
package org.geneview.core.view.opengl.canvas.heatmap;

import org.geneview.core.data.view.camera.IViewCamera;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;


/**
 * @author Michael Kalkusch
 *
 */
public abstract class AGLCanvasHeatmap2D 
extends AGLCanvasUser
		implements IGLCanvasHeatmap2D {

	public static final int OFFSET = 2;
	public static final int X = 0;
	public static final int MIN = 0;
	public static final int Y = 1;
	public static final int Z = 2;
	public static final int MAX = 1;

	protected static final float fPickingBias = 0.01f;
	
	protected float[][] fAspectRatio;

	protected float[] fResolution;
	
	protected float[][] viewingFrame;

	protected int iValuesInRow = 10;

	protected int iValuesInColum = 10;
	
	/**
	 * @param setGeneralManager
	 * @param refViewCamera
	 * @param viewId
	 * @param parentContainerId
	 * @param label
	 */
	public AGLCanvasHeatmap2D(IGeneralManager setGeneralManager,
			IViewCamera refViewCamera,
			int viewId,
			int parentContainerId,
			String label) {

		super(setGeneralManager, refViewCamera, viewId, parentContainerId,
				label);
		// TODO Auto-generated constructor stub
	}


	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#destroyGLCanvas()
	 */
	public final void destroyGLCanvas()
	{
		refGeneralManager.getSingelton().logMsg( 
				"IGLCanvasHeatmap2D.destroy(GLCanvas canvas)  id=" + this.iUniqueId,
				LoggerType.FULL );
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.heatmap.IGLCanvasHeatmap2D#setResolution(float[])
	 */
	public final void setResolution( float[] setResolution ) {
		
//		if ( fResolution.length < 6 ) {
//			throw new RuntimeException("GLCanvasMinMaxScatterPlot2D.setResolution() array must contain 3 items.");
//		}
		
		this.fResolution = setResolution;
		
		fAspectRatio[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MIN] = fResolution[0];
		fAspectRatio[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MAX] = fResolution[1]; 
		fAspectRatio[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MIN] = fResolution[2]; 
		fAspectRatio[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MAX] = fResolution[3]; 
		
		fAspectRatio[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.OFFSET] = fResolution[4]; 
		fAspectRatio[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.OFFSET] = fResolution[5];
		
		viewingFrame[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MIN] = fResolution[6];
		viewingFrame[AGLCanvasHeatmap2D.X][AGLCanvasHeatmap2D.MAX] = fResolution[7]; 
		viewingFrame[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MIN] = fResolution[8]; 
		viewingFrame[AGLCanvasHeatmap2D.Y][AGLCanvasHeatmap2D.MAX] = fResolution[9];
		
		viewingFrame[AGLCanvasHeatmap2D.Z][AGLCanvasHeatmap2D.MIN] = fResolution[10]; 
		viewingFrame[AGLCanvasHeatmap2D.Z][AGLCanvasHeatmap2D.MAX] = fResolution[11]; 
				
		iValuesInRow = (int) fResolution[12]; 
		
	}


}
