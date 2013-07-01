/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.treemap;

import java.util.Calendar;
import java.util.Vector;

import javax.media.opengl.GL2;

/**
 * 
 * Class which provides animation for the zoom function of
 * {@link GLHierarchicalTreeMap}.
 * 
 * @author Michael Lafer
 * 
 */

public class AnimationControle {

	GLHierarchicalTreeMap parentView;
	boolean bIsActive = false;

	float beginX, beginY, beginWidth, beginHeight;
	float endX, endY, endWidth, endHeight;

	Vector<GLTreeMap> beginThumbnails, endThumbnails;
	GLTreeMap beginMainView, endMainView;

	int direcetion;
	public final static int ZOOM_IN_ANIMATION = 1;
	public final static int ZOOM_OUT_ANIMATION = 2;

	int zoomStage;

	long animationTime = 1000;
	long startTime;

	void initAnimation(GLHierarchicalTreeMap parentView, GLTreeMap beginMainView, GLTreeMap endMainView, Vector<GLTreeMap> beginThumbnails,
			Vector<GLTreeMap> endThumbnails, int direction) {
		this.beginMainView = beginMainView;
		this.endMainView = endMainView;
		this.beginThumbnails = beginThumbnails;
		this.endThumbnails = endThumbnails;
		this.direcetion = direction;
		this.parentView = parentView;

		startTime = Calendar.getInstance().getTimeInMillis();

		// if(direction==ZOOM_OUT_ANIMATION)
		parentView.thumbnailTreemapViews = beginThumbnails;

		calcData();

		// if(direction==ZOOM_IN_ANIMATION)
		zoomStage = 0;

		// System.out.println("\nDirection: " + direction);
	}

	private void calcData() {
		float thumbNailWidth = (1 - parentView.xMargin * (GLHierarchicalTreeMap.MAX_THUMBNAILS + 1)) / GLHierarchicalTreeMap.MAX_THUMBNAILS;
		if (direcetion == ZOOM_IN_ANIMATION) {
			if (beginThumbnails == null || beginThumbnails.size() == 0) {
				beginX = 0;
				beginY = 0;
				beginWidth = 1;
				beginHeight = 1;

				endX = parentView.xMargin;
				endY = 0.8f + parentView.yMargin;
				endWidth = thumbNailWidth;
				endHeight = GLHierarchicalTreeMap.THUMBNAIL_HEIGHT;

			} else {
				beginX = 0;
				beginY = 0;
				beginWidth = 1;
				beginHeight = 0.8f;

				endX = thumbNailWidth * beginThumbnails.size() + parentView.xMargin * (beginThumbnails.size() + 1);
				endY = 0.8f + parentView.yMargin;
				endWidth = thumbNailWidth;
				endHeight = GLHierarchicalTreeMap.THUMBNAIL_HEIGHT;
			}

		} else {
			beginX = thumbNailWidth * (beginThumbnails.size() - 1) + parentView.xMargin * (beginThumbnails.size());
			beginY = 0.8f + parentView.yMargin;
			beginWidth = thumbNailWidth;
			beginHeight = GLHierarchicalTreeMap.THUMBNAIL_HEIGHT;

			endX = 0;
			endY = 0;
			endWidth = 1;

			if (endThumbnails == null || endThumbnails.size() == 0) {

				endHeight = 1;
			} else {
				endHeight = 0.8f;
			}
		}
	}

	private void calcSecondZoomStage() {
		float thumbNailWidth = (1 - parentView.xMargin * (GLHierarchicalTreeMap.MAX_THUMBNAILS + 1)) / GLHierarchicalTreeMap.MAX_THUMBNAILS;

		beginX = thumbNailWidth * (beginThumbnails.size() - 1) + parentView.xMargin * (beginThumbnails.size());
		beginY = 0.8f + parentView.yMargin;

		float rec[] = beginMainView.getSelectedArea();

		beginX = thumbNailWidth * (endThumbnails.size() - 1) + parentView.xMargin * (endThumbnails.size()) + thumbNailWidth * rec[0];
		beginY = 0.8f + parentView.yMargin + GLHierarchicalTreeMap.THUMBNAIL_HEIGHT * rec[1];
		beginWidth = thumbNailWidth * (rec[2] - rec[0]);
		beginHeight = GLHierarchicalTreeMap.THUMBNAIL_HEIGHT * (rec[3] - rec[1]);

		endX = 0;
		endY = 0;
		endWidth = 1;
		endHeight = 0.8f;

	}

	void display(GL2 gl) {
		float x, y, width, height;

		long time = Calendar.getInstance().getTimeInMillis();
		float progress = Math.min(((float) (time - startTime)) / animationTime, 1);

		x = (endX - beginX) * progress + beginX;
		y = (endY - beginY) * progress + beginY;
		width = (endWidth - beginWidth) * progress + beginWidth;
		height = (endHeight - beginHeight) * progress + beginHeight;

		parentView.displayThumbnailTreemaps(gl);

		if (direcetion == ZOOM_IN_ANIMATION && zoomStage == 1) {
			endMainView.getViewFrustum().setTop(parentView.getViewFrustum().getHeight() * (y + height));
			endMainView.getViewFrustum().setBottom(parentView.getViewFrustum().getHeight() * y);
			endMainView.getViewFrustum().setLeft(parentView.getViewFrustum().getWidth() * x);
			endMainView.getViewFrustum().setRight(parentView.getViewFrustum().getWidth() * (x + width));
			endMainView.setDisplayListDirty();

			endMainView.display(gl);
		} else {
			beginMainView.getViewFrustum().setTop(parentView.getViewFrustum().getHeight() * (y + height));
			beginMainView.getViewFrustum().setBottom(parentView.getViewFrustum().getHeight() * y);
			beginMainView.getViewFrustum().setLeft(parentView.getViewFrustum().getWidth() * x);
			beginMainView.getViewFrustum().setRight(parentView.getViewFrustum().getWidth() * (x + width));
			beginMainView.setDisplayListDirty();

			beginMainView.display(gl);
		}

		if (direcetion == ZOOM_IN_ANIMATION && progress >= 1 && zoomStage == 0) {
			zoomStage = 1;
			parentView.thumbnailTreemapViews = endThumbnails;
			for (GLTreeMap view : endThumbnails)
				view.setDisplayListDirty();

			calcSecondZoomStage();
			progress = 0;
			startTime = Calendar.getInstance().getTimeInMillis();
		}

		if (progress >= 1) {
			endAnimation();
			return;
		}

		// System.out.println(progress + " " + x + " " + y + " " + width + " " +
		// height + " " + beginMainView.getViewFrustum());
		// System.out.println(progress+" "+beginMainView.getViewFrustum());

	}

	private void endAnimation() {
		bIsActive = false;

		for (GLTreeMap view : endThumbnails)
			view.setDisplayListDirty();
		endMainView.setDisplayListDirty();

		parentView.thumbnailTreemapViews = endThumbnails;
	}

	boolean isActive() {
		return bIsActive;
	}

	void setActive(boolean flag) {
		bIsActive = flag;
	}
}
