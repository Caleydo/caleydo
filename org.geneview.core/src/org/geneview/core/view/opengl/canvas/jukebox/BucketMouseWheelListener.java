package org.geneview.core.view.opengl.canvas.jukebox;

import gleem.linalg.Vec3f;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.geneview.core.view.opengl.canvas.AGLCanvasUser;

/**
 * Specialized mouse wheel listener for "diving" into the bucket.
 * 
 * @author Marc Streit
 *
 */
public class BucketMouseWheelListener 
implements MouseWheelListener {

	private AGLCanvasUser bucketCanvas;
	
	private static float BUCKET_ZOOM_MAX = 4f;
	private static float BUCKET_ZOOM_STEP = 0.15f;
	
	private float fCurrentBucketZoom = 0;
	
//	private float fBucketTransparency = 1;
	
	private boolean bZoomActionRunning = false;
	private boolean bZoomIn = true;
	private boolean bBucketBottomReached = false;
	
	
	/**
	 * Constructor.
	 */
	public BucketMouseWheelListener(final GLCanvasOverallJukebox3D bucketCanvas) 
	{
		this.bucketCanvas = bucketCanvas;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	public void mouseWheelMoved(MouseWheelEvent event) 
	{
		bZoomActionRunning = true;
		
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
		
		if (Math.abs(fCurrentBucketZoom) < BUCKET_ZOOM_MAX)
		{
			if (bZoomIn)
			{
				fCurrentBucketZoom += BUCKET_ZOOM_STEP;
			
				bucketCanvas.getViewCamera().addCameraScale(
			    		new Vec3f( 0, 0, BUCKET_ZOOM_STEP));	

//				fBucketTransparency = fCurrentBucketZoom / BUCKET_ZOOM_MAX;
			}
			else
			{		
				fCurrentBucketZoom -= BUCKET_ZOOM_STEP;

				bucketCanvas.getViewCamera().addCameraScale(
			    		new Vec3f( 0, 0, -BUCKET_ZOOM_STEP));	
				
//				fBucketTransparency = fCurrentBucketZoom / -BUCKET_ZOOM_MAX;
			}
		}
		else
		{
			if (fCurrentBucketZoom >= BUCKET_ZOOM_MAX)
				bBucketBottomReached = true;
			else if (fCurrentBucketZoom <= -BUCKET_ZOOM_MAX)
				bBucketBottomReached = false;
			
			fCurrentBucketZoom = 0;
			bZoomActionRunning = false;
			return;
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
