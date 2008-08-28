package org.caleydo.core.view.opengl.canvas.remote.bucket;

import gleem.linalg.Vec3f;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import org.caleydo.core.data.view.rep.renderstyle.layout.BucketLayoutRenderStyle;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;

/**
 * Specialized mouse wheel listener for "diving" into the bucket.
 * 
 * @author Marc Streit
 */
public class BucketMouseWheelListener
	implements MouseWheelListener
{
	private GLRemoteRendering bucketGLEventListener;

	private static int BUCKET_ZOOM_MAX = 400;

	private static int BUCKET_ZOOM_STEP = 16;

	private int iCurrentBucketZoom = 0;

	private int iAnimationZoomCounter = 0;

	// private float fBucketTransparency = 1;

	private boolean bZoomActionRunning = false;

	private boolean bZoomIn = true;

	private boolean bBucketBottomReached = false;

	private BucketLayoutRenderStyle bucketLayoutRenderStyle;

	/**
	 * Constructor.
	 */
	public BucketMouseWheelListener(final GLRemoteRendering remoteRendering3D,
			final BucketLayoutRenderStyle bucketLayoutRenderStyle)
	{
		this.bucketGLEventListener = remoteRendering3D;
		this.bucketLayoutRenderStyle = bucketLayoutRenderStyle;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event)
	{

		// CTRL = Change horizontal zoom factor
		// ALT = Change vertical zoom factor

		// Change bucket tilt angle
		if (event.isControlDown() || event.isAltDown())
		{
			float fTmpAngle = 0;
			float fStepSize = 0.1f;
			int notches = event.getWheelRotation();

			// Change bucket tilt angle of left and right wall if CTRL is down
			if (event.isControlDown())
			{
				fTmpAngle = bucketLayoutRenderStyle.getZoomFactor();

				if (notches < 0)
					fTmpAngle += fStepSize;
				else
					fTmpAngle -= fStepSize;

				if (fTmpAngle >= 1)
					bucketLayoutRenderStyle.setZoomFactor(1);
				else if (fTmpAngle <= 0)
					bucketLayoutRenderStyle.setZoomFactor(0);
				else
					bucketLayoutRenderStyle.setZoomFactor(fTmpAngle);
			}

			// Change bucket tilt angle of top and bottom wall if CTRL is down
			if (event.isAltDown())
			{
				fTmpAngle = bucketLayoutRenderStyle.getZoomFactor();

				if (notches < 0)
					fTmpAngle += fStepSize;
				else
					fTmpAngle -= fStepSize;

				if (fTmpAngle >= 1)
					bucketLayoutRenderStyle.setZoomFactor(1);
				else if (fTmpAngle <= 0)
					bucketLayoutRenderStyle.setZoomFactor(0);
				else
					bucketLayoutRenderStyle.setZoomFactor(fTmpAngle);
			}

			bucketLayoutRenderStyle.initStackLayer();
			bucketLayoutRenderStyle.initUnderInteractionLayer();
		}
		else
		// zoom to bottom of the bucket
		{
			bZoomActionRunning = true;

			// Turn off picking while zoom action is running
			GeneralManager.get().getViewGLCanvasManager().getPickingManager().enablePicking(
					false);

			int notches = event.getWheelRotation();
			if (notches < 0)
				bZoomIn = true;
			else
				bZoomIn = false;
		}
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

				bucketGLEventListener.getViewCamera().addCameraScale(
						new Vec3f(0, 0, BUCKET_ZOOM_STEP / 100f));

				// fBucketTransparency = fCurrentBucketZoom / BUCKET_ZOOM_MAX;
			}
			else
			{
				iCurrentBucketZoom -= BUCKET_ZOOM_STEP;
				iAnimationZoomCounter -= BUCKET_ZOOM_STEP;

				bucketGLEventListener.getViewCamera().addCameraScale(
						new Vec3f(0, 0, -BUCKET_ZOOM_STEP / 100f));

				// fBucketTransparency = fCurrentBucketZoom / -BUCKET_ZOOM_MAX;
			}

			if (iCurrentBucketZoom == BUCKET_ZOOM_MAX)
			{
				bBucketBottomReached = true;

				// Update detail level of view in center bucket position
				int iGLEventListenerID = bucketGLEventListener
						.getUnderInteractionHierarchyLayer().getElementIdByPositionIndex(0);

				if (iGLEventListenerID != -1)
				{
					GeneralManager.get().getViewGLCanvasManager().getGLEventListener(
							iGLEventListenerID).setDetailLevel(EDetailLevel.HIGH);
				}
			}
			else if (iCurrentBucketZoom == 0)
			{
				bBucketBottomReached = false;

				// Update detail level of view in center bucket position
				int iGLEventListenerID = bucketGLEventListener
						.getUnderInteractionHierarchyLayer().getElementIdByPositionIndex(0);

				if (iGLEventListenerID != -1)
				{
					GeneralManager.get().getViewGLCanvasManager().getGLEventListener(
							iGLEventListenerID).setDetailLevel(EDetailLevel.MEDIUM);
				}
			}
		}
		else
		{
			iAnimationZoomCounter = 0;
			bZoomActionRunning = false;

			// Turn on picking after zoom action is done
			GeneralManager.get().getViewGLCanvasManager().getPickingManager().enablePicking(
					true);
		}
	}

	public boolean isZoomedIn()
	{
		return bBucketBottomReached;
	}

	// public float getBucketTransparency()
	// {
	// return fBucketTransparency;
	// }
}
