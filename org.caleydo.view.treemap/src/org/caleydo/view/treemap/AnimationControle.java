package org.caleydo.view.treemap;

import java.util.Calendar;
import java.util.Vector;

import javax.media.opengl.GL;

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

	long animationTime = 500;
	long startTime;

	void initAnimation(GLHierarchicalTreeMap parentView, GLTreeMap beginMainView, GLTreeMap endMainView, Vector<GLTreeMap> beginThumbnails, Vector<GLTreeMap> endThumbnails, int direction) {
		this.beginMainView = beginMainView;
		this.endMainView = endMainView;
		this.beginThumbnails = beginThumbnails;
		this.endThumbnails = endThumbnails;
		this.direcetion = direction;
		this.parentView=parentView;

		startTime = Calendar.getInstance().getTimeInMillis();

		parentView.thumbnailTreemapViews = beginThumbnails;

		calcData();
	}

	private void calcData() {
		if (direcetion == ZOOM_IN_ANIMATION) {
			float thumbNailWidth = (1 - parentView.xMargin * (GLHierarchicalTreeMap.MAX_THUMBNAILS + 1)) / GLHierarchicalTreeMap.MAX_THUMBNAILS;
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

		}
	}

	
	
	void display(GL gl) {
		float x, y, width, height;

		long time = Calendar.getInstance().getTimeInMillis();
		float progress = ((float)(time - startTime)  ) / animationTime;

		if(progress>=1){
			endAnimation();
			return;
		}
		
		x = (endX - beginX) * progress + beginX;
		y = (endY - beginY) * progress + beginY;
		width = (endWidth - beginWidth) * progress + beginWidth;
		height = (endHeight - beginHeight) * progress + beginHeight;

		if (direcetion == ZOOM_IN_ANIMATION) {

			parentView.displayThumbnailTreemaps(gl);
			parentView.displayMainTreeMap(gl, true);

			beginMainView.getViewFrustum().setTop(parentView.getViewFrustum().getHeight()*(y+height));
			beginMainView.getViewFrustum().setBottom(parentView.getViewFrustum().getHeight()*y);
			beginMainView.getViewFrustum().setLeft(parentView.getViewFrustum().getWidth()*x);
			beginMainView.getViewFrustum().setRight(parentView.getViewFrustum().getWidth()*(x+width));
			beginMainView.setDisplayListDirty();
			
//			gl.glPushMatrix();
//			gl.glTranslatef(x, y, 0);
//			gl.glScalef(width, height, 1);
			
			beginMainView.display(gl);
			
//			gl.glPopMatrix();
		}
		
		System.out.println(progress+" "+x+" "+y+" "+width+" "+height+" "+beginMainView.getViewFrustum());
//		System.out.println(progress+" "+beginMainView.getViewFrustum());

	}

	private void endAnimation(){
		bIsActive=false;
		
		for(GLTreeMap view:endThumbnails)
			view.setDisplayListDirty();
		
		parentView.thumbnailTreemapViews=endThumbnails;
	}
	
	boolean isActive() {
		return bIsActive;
	}

	void setActive(boolean flag) {
		bIsActive = flag;
	}
}
