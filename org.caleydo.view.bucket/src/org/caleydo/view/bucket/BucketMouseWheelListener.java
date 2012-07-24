/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.bucket;

import gleem.linalg.Vec3f;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;

/**
 * Specialized mouse wheel listener for "diving" into the bucket.
 * 
 * @author Marc Streit
 */
public class BucketMouseWheelListener extends MouseAdapter implements MouseWheelListener,
		MouseMotionListener {

	private GLBucket bucketGLEventListener;

	private float fBuketZoomMax = BUCKET_ZOOM_MAX;

	private final static int BUCKET_ZOOM_MAX = 400;

	private final static int BUCKET_ZOOM_STEP = 16;

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
	public BucketMouseWheelListener(final GLBucket remoteRendering3D,
			final BucketLayoutRenderStyle bucketLayoutRenderStyle) {
		this.bucketGLEventListener = remoteRendering3D;
		this.bucketLayoutRenderStyle = bucketLayoutRenderStyle;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {

		// CTRL = Change horizontal zoom factor
		// ALT = Change vertical zoom factor

		if (bZoomActionRunning)
			return;

		fBuketZoomMax = BUCKET_ZOOM_MAX;// *
		// bucketGLEventListener.getAspectRatio();

		// Change bucket tilt angle
		if (event.isControlDown() || event.isAltDown()) {
			float fTmpAngle = 0;
			float fStepSize = 0.1f;
			int notches = event.getWheelRotation();

			// Change bucket tilt angle of left and right wall if CTRL is down
			if (event.isControlDown() || event.isAltDown()) {
				fTmpAngle = bucketLayoutRenderStyle.getZoomFactor();

				if (notches < 0) {
					fTmpAngle += fStepSize;
				} else {
					fTmpAngle -= fStepSize;
				}

				if (fTmpAngle >= 1) {
					bucketLayoutRenderStyle.setZoomFactor(1);
				} else if (fTmpAngle <= 0) {
					bucketLayoutRenderStyle.setZoomFactor(0);
				} else {
					bucketLayoutRenderStyle.setZoomFactor(fTmpAngle);
				}
			}

			bucketLayoutRenderStyle.initStackLevel();
			bucketLayoutRenderStyle.initFocusLevel();
			bucketLayoutRenderStyle.initPoolLevel(-1);
		} else
		// zoom to bottom of the bucket
		{
			int notches = event.getWheelRotation();
			if (notches < 0) {
				if (iCurrentBucketZoom == (int) fBuketZoomMax)
					return;

				bZoomIn = true;
				bucketLayoutRenderStyle.setZoomedIn(bZoomIn);
			} else {
				if (iCurrentBucketZoom == 0)
					return;

				bZoomIn = false;
				bucketLayoutRenderStyle.setZoomedIn(bZoomIn);
			}

			bZoomActionRunning = true;
			bucketLayoutRenderStyle.initStackLevel();
			bucketLayoutRenderStyle.initPoolLevel(-1);
			bucketLayoutRenderStyle.initMemoLevel();

			// Turn off picking while zoom action is running
			GeneralManager.get().getViewManager().getPickingManager()
					.enablePicking(false);
		}
		GeneralManager.get().getViewManager().getConnectedElementRepresentationManager()
				.clearTransformedConnections();

	}

	public void render() {
		if (!bZoomActionRunning)
			return;

		if (iAnimationZoomCounter == 0 || // iCurrentBucketZoom < fBuketZoomMax)
				iCurrentBucketZoom % (int) fBuketZoomMax != 0) {
			if (bZoomIn) {
				iCurrentBucketZoom += BUCKET_ZOOM_STEP;
				iAnimationZoomCounter += BUCKET_ZOOM_STEP;

				bucketGLEventListener.getViewCamera().addCameraScale(
						new Vec3f(0, 0, BUCKET_ZOOM_STEP / 100f));

				// fBucketTransparency = fCurrentBucketZoom / BUCKET_ZOOM_MAX;
			} else {
				iCurrentBucketZoom -= BUCKET_ZOOM_STEP;
				iAnimationZoomCounter -= BUCKET_ZOOM_STEP;

				bucketGLEventListener.getViewCamera().addCameraScale(
						new Vec3f(0, 0, -BUCKET_ZOOM_STEP / 100f));

				// fBucketTransparency = fCurrentBucketZoom / -BUCKET_ZOOM_MAX;
			}

			if (iCurrentBucketZoom >= fBuketZoomMax)// iCurrentBucketZoom ==
			// (int)fBuketZoomMax)
			{
				bBucketBottomReached = true;

				// Update detail level of view in center bucket position
				AGLView glView = bucketGLEventListener.getFocusLevel()
						.getElementByPositionIndex(0).getGLView();

				if (glView != null) {
					glView.setDetailLevel(EDetailLevel.HIGH);
				}
			} else if (iCurrentBucketZoom == 0) {
				bBucketBottomReached = false;

				// Update detail level of view in center bucket position
				AGLView glView = bucketGLEventListener.getFocusLevel()
						.getElementByPositionIndex(0).getGLView();

				if (glView != null) {
					glView.setDetailLevel(EDetailLevel.MEDIUM);
				}
			}
		} else {
			iAnimationZoomCounter = 0;
			bZoomActionRunning = false;

			GeneralManager.get().getViewManager()
					.getConnectedElementRepresentationManager()
					.clearTransformedConnections();
			// Turn on picking after zoom action is done
			GeneralManager.get().getViewManager().getPickingManager().enablePicking(true);
		}
	}

	public boolean isZoomedIn() {
		return bBucketBottomReached;
	}

	public void triggerZoom(boolean bZoomIn) {
		bZoomActionRunning = true;
		this.bZoomIn = bZoomIn;
		bucketLayoutRenderStyle.setZoomedIn(bZoomIn);
		bucketLayoutRenderStyle.initStackLevel();
		bucketLayoutRenderStyle.initPoolLevel(-1);
		bucketLayoutRenderStyle.initMemoLevel();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		grabFocus();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		grabFocus();
	}

	/**
	 * Method forces the current view to be in focus. This is needed to get
	 * events properly.
	 */
	private void grabFocus() {
		// Potential performance problem
		bucketGLEventListener.getParentGLCanvas().requestFocus();
	}

	public boolean isZoomActionRunning() {
		return bZoomActionRunning;
	}
}