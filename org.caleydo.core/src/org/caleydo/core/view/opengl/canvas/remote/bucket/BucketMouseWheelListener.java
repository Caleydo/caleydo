package org.caleydo.core.view.opengl.canvas.remote.bucket;

import gleem.linalg.Vec3f;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.canvas.remote.GLCanvasRemoteRendering3D;

/**
 * Specialized mouse wheel listener for "diving" into the bucket.
 * 
 * @author Marc Streit
 *
 */
public class BucketMouseWheelListener 
implements MouseWheelListener {

	private IGeneralManager generalManager;
	
	private AGLCanvasUser bucketCanvas;
	
	private static int BUCKET_ZOOM_MAX = 400;
	private static int BUCKET_ZOOM_STEP = 16;
	
	private int iCurrentBucketZoom = 0;
	private int iAnimationZoomCounter = 0;
	
//	private float fBucketTransparency = 1;
	
	private boolean bZoomActionRunning = false;
	private boolean bZoomIn = true;
	private boolean bBucketBottomReached = false;
	
	
	/**
	 * Constructor.
	 */
	public BucketMouseWheelListener(final GLCanvasRemoteRendering3D remoteRendering3D,
			final IGeneralManager generalManager) 
	{
		this.bucketCanvas = remoteRendering3D;
		this.generalManager = generalManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	public void mouseWheelMoved(MouseWheelEvent event) 
	{
		bZoomActionRunning = true;
		
		// Turn off picking while zoom action is running
		generalManager.getViewGLCanvasManager().getPickingManager().enablePicking(false);
		
	    int notches = event.getWheelRotation();
	    if (notches < 0)
	    	bZoomIn = true;
	    else
	    	bZoomIn = false;
	}
	
	public void render() 
	{
		if (!bZoomActionRunning)
			return;
		
		if (iAnimationZoomCounter == 0 || iCurrentBucketZoom % BUCKET_ZOOM_MAX != 0)
		{
			if (bZoomIn)
			{
				iCurrentBucketZoom += BUCKET_ZOOM_STEP;
				iAnimationZoomCounter += BUCKET_ZOOM_STEP;
			
				bucketCanvas.getViewCamera().addCameraScale(
			    		new Vec3f( 0, 0, BUCKET_ZOOM_STEP / 100f));	

//				fBucketTransparency = fCurrentBucketZoom / BUCKET_ZOOM_MAX;
			}
			else
			{		
				iCurrentBucketZoom -= BUCKET_ZOOM_STEP;
				iAnimationZoomCounter -= BUCKET_ZOOM_STEP;

				bucketCanvas.getViewCamera().addCameraScale(
			    		new Vec3f( 0, 0, -BUCKET_ZOOM_STEP / 100f));	
				
//				fBucketTransparency = fCurrentBucketZoom / -BUCKET_ZOOM_MAX;
			}
			
			if (iCurrentBucketZoom == BUCKET_ZOOM_MAX)
			{
				bBucketBottomReached = true;
			}	
			else if (iCurrentBucketZoom == 0)
				bBucketBottomReached = false;
		}
		else
		{	
			iAnimationZoomCounter = 0;
			bZoomActionRunning = false;
			
			// Turn on picking after zoom action is done
			generalManager.getViewGLCanvasManager().getPickingManager().enablePicking(true);
		}				
	}
	
	public boolean isBucketBottomReached() 
	{
		return bBucketBottomReached;
	}

//	public float getBucketTransparency() 
//	{
//		return fBucketTransparency;
//	}
}
