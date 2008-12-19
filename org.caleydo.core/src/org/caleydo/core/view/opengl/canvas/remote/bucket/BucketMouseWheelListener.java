package org.caleydo.core.view.opengl.canvas.remote.bucket;

import gleem.linalg.Vec3f;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.SwingUtilities;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.renderstyle.layout.BucketLayoutRenderStyle;

/**
 * Specialized mouse wheel listener for "diving" into the bucket.
 * 
 * @author Marc Streit
 */
public class BucketMouseWheelListener
	implements MouseWheelListener, MouseListener
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

		if (bZoomActionRunning)
			return;

		// Change bucket tilt angle
		if (event.isControlDown() || event.isAltDown())
		{
			float fTmpAngle = 0;
			float fStepSize = 0.1f;
			int notches = event.getWheelRotation();

			// Change bucket tilt angle of left and right wall if CTRL is down
			if (event.isControlDown() || event.isAltDown())
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

			bucketLayoutRenderStyle.initStackLevel(false);
			bucketLayoutRenderStyle.initFocusLevel();
			bucketLayoutRenderStyle.initPoolLevel(false, -1);
		}
		else
		// zoom to bottom of the bucket
		{
			int notches = event.getWheelRotation();
			if (notches < 0)
			{
				if (iCurrentBucketZoom == BUCKET_ZOOM_MAX)
					return;

				bZoomIn = true;
			}
			else
			{
				if (iCurrentBucketZoom == 0)
					return;

				bZoomIn = false;
			}

			bZoomActionRunning = true;
			bucketLayoutRenderStyle.initStackLevel(bZoomIn);
			bucketLayoutRenderStyle.initPoolLevel(bZoomIn, -1);

			// Turn off picking while zoom action is running
			GeneralManager.get().getViewGLCanvasManager().getPickingManager().enablePicking(
					false);
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
				int iGLEventListenerID = bucketGLEventListener.getFocusLevel()
						.getElementByPositionIndex(0).getContainedElementID();

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
				int iGLEventListenerID = bucketGLEventListener.getFocusLevel()
						.getElementByPositionIndex(0).getContainedElementID();

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

	public void triggerZoom(boolean bZoomIn)
	{
		bZoomActionRunning = true;
		this.bZoomIn = bZoomIn;
		bucketLayoutRenderStyle.initStackLevel(bZoomIn);
		bucketLayoutRenderStyle.initPoolLevel(bZoomIn, -1);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		if (bucketGLEventListener == null)
			return;

		// TODO: investigate which of these calls is really needed to force
		// focus.
		bucketGLEventListener.getParentGLCanvas().getParentComposite().getDisplay().asyncExec(
				new Runnable()
				{
					public void run()
					{
						bucketGLEventListener.getParentGLCanvas().getParentComposite()
								.setFocus();
						bucketGLEventListener.getParentGLCanvas().getParentComposite()
								.forceFocus();
						bucketGLEventListener.getParentGLCanvas().getParentComposite()
								.isFocusControl();
						bucketGLEventListener.getParentGLCanvas().getParentComposite()
								.redraw();
						// bucketGLEventListener.getParentGLCanvas().getParentComposite().notifyAll();
					}
				});

		bucketGLEventListener.getParentGLCanvas().setVisible(true);
		bucketGLEventListener.getParentGLCanvas().setFocusable(true);
		bucketGLEventListener.getParentGLCanvas().requestFocusInWindow();
		bucketGLEventListener.getParentGLCanvas().requestFocus();
		bucketGLEventListener.getParentGLCanvas().getParent().setFocusable(true);
		bucketGLEventListener.getParentGLCanvas().getParent().requestFocus();
		bucketGLEventListener.getParentGLCanvas().getParent().requestFocusInWindow();

		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				bucketGLEventListener.getParentGLCanvas().requestFocusInWindow();
				bucketGLEventListener.getParentGLCanvas().requestFocus();

			}
		});
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub
	}
}