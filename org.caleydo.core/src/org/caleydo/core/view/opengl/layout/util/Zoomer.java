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
package org.caleydo.core.view.opengl.layout.util;

import java.awt.Point;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;

import org.caleydo.core.util.color.Colors;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.canvas.listener.IMouseWheelHandler;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.scrollbar.IScrollBarUpdateHandler;
import org.caleydo.core.view.opengl.util.scrollbar.ScrollBar;
import org.caleydo.core.view.opengl.util.scrollbar.ScrollBarRenderer;

public class Zoomer implements IMouseWheelHandler, IScrollBarUpdateHandler {

	protected PixelGLConverter pixelGLConverter;
	protected int viewportPositionX;
	protected int viewportPositionY;
	protected AGLView parentView;

	private float previousZoomScale = 1.0f;
	private float currentZoomScale = 1.0f;

	private ScrollBar hScrollBar;
	private ScrollBar vScrollBar;
	private LayoutManager hScrollBarLayoutManager;
	// private LayoutConfiguration hScrollBarTemplate;
	private LayoutManager vScrollBarLayoutManager;
	// private LayoutConfiguration vScrollBarTemplate;

	private boolean wasMouseWheeled = false;

	private Point mouseWheelPosition;

	private float relativeViewTranlateX;

	private float relativeViewTranlateY;

	private DragAndDropController scrollBarDragAndDropController;

	private ViewFrustum viewFrustum;

	private GLMouseListener glMouseListener;

	private ElementLayout parentLayout;

	public Zoomer(AGLView parentView, ElementLayout parentLayout) {
		this.parentView = parentView;
		this.parentLayout = parentLayout;
		this.glMouseListener = parentView.getGLMouseListener();
		parentView.registerMouseWheelListener(this);

		pixelGLConverter = parentView.getPixelGLConverter();
		scrollBarDragAndDropController = new DragAndDropController(parentView);
		viewFrustum = new ViewFrustum();
		initScrollBars();
	}

	public void destroy() {
		parentView.unregisterRemoteViewMouseWheelListener(this);
		parentView = null;
		parentLayout = null;

	}

	private void initScrollBars() {

		hScrollBarLayoutManager = new LayoutManager(viewFrustum, pixelGLConverter);
		// hScrollBarTemplate = new LayoutConfiguration();
		hScrollBar = new ScrollBar(0, 10, 5, 5, parentView.createNewScrollBarID(), this);

		Column baseColumn = new Column();

		ElementLayout hScrollBarLayout = new ElementLayout("horizontalScrollBar");
		hScrollBarLayout.setPixelSizeY(10);
		hScrollBarLayout.setRatioSizeX(1.0f);
		hScrollBarLayout
.setRenderer(new ScrollBarRenderer(hScrollBar, parentView, true,
				scrollBarDragAndDropController, Colors.BLACK));

		ElementLayout hSpacingLayout = new ElementLayout("horizontalSpacing");
		hSpacingLayout.setRatioSizeX(1.0f);

		baseColumn.append(hScrollBarLayout);
		baseColumn.append(hSpacingLayout);

		hScrollBarLayoutManager.setBaseElementLayout(baseColumn);

		// hScrollBarLayoutManager.setTemplate(hScrollBarTemplate);

		vScrollBarLayoutManager = new LayoutManager(viewFrustum, pixelGLConverter);
		// vScrollBarTemplate = new LayoutConfiguration();
		vScrollBar = new ScrollBar(0, 10, 5, 5, parentView.createNewScrollBarID(), this);

		Row baseRow = new Row();

		ElementLayout vScrollBarLayout = new ElementLayout("horizontalScrollBar");
		vScrollBarLayout.setPixelSizeX(10);
		vScrollBarLayout.setRatioSizeY(1.0f);
		vScrollBarLayout.setRenderer(new ScrollBarRenderer(vScrollBar, parentView, false,
				scrollBarDragAndDropController, Colors.BLACK));

		ElementLayout vSpacingLayout = new ElementLayout("verticalSpacing");
		vSpacingLayout.setRatioSizeX(1.0f);

		baseRow.append(vSpacingLayout);
		baseRow.append(vScrollBarLayout);

		vScrollBarLayoutManager.setBaseElementLayout(baseRow);

		// vScrollBarLayoutManager.setTemplate(vScrollBarTemplate);
	}

	/**
	 * This method shall be called before the view is rendered in order to be zoomed.
	 *
	 * @param gl
	 */
	public void beginZoom(GL2 gl) {

		viewportPositionX = pixelGLConverter.getPixelWidthForCurrentGLTransform(gl);
		viewportPositionY = pixelGLConverter.getPixelHeightForCurrentGLTransform(gl);

		double[] clipPlane1 = new double[] { 0.0, 1.0, 0.0, 0.0 };
		double[] clipPlane2 = new double[] { 1.0, 0.0, 0.0, 0.0 };
		double[] clipPlane3 = new double[] { -1.0, 0.0, 0.0, parentLayout.getSizeScaledX() };
		double[] clipPlane4 = new double[] { 0.0, -1.0, 0.0, parentLayout.getSizeScaledY() };

		gl.glClipPlane(GL2ES1.GL_CLIP_PLANE0, clipPlane1, 0);
		gl.glClipPlane(GL2ES1.GL_CLIP_PLANE1, clipPlane2, 0);
		gl.glClipPlane(GL2ES1.GL_CLIP_PLANE2, clipPlane3, 0);
		gl.glClipPlane(GL2ES1.GL_CLIP_PLANE3, clipPlane4, 0);
		gl.glEnable(GL2ES1.GL_CLIP_PLANE0);
		gl.glEnable(GL2ES1.GL_CLIP_PLANE1);
		gl.glEnable(GL2ES1.GL_CLIP_PLANE2);
		gl.glEnable(GL2ES1.GL_CLIP_PLANE3);

		float x = parentLayout.getSizeScaledX();
		float y = parentLayout.getSizeScaledY();

		// System.out.println(currentZoomScale);

		if (currentZoomScale == 1.0f) {
			relativeViewTranlateX = 0;
			relativeViewTranlateY = 0;
			return;
		}

		float viewTranslateX = relativeViewTranlateX * x;
		float viewTranslateY = relativeViewTranlateY * y;

		// float zoomCenterX = relativeZoomCenterX * viewFrustum.getWidth();
		// float zoomCenterY = relativeZoomCenterY * viewFrustum.getHeight();
		//
		// float viewTranslateX;
		// float viewTranslateY;

		if (wasMouseWheeled) {

			float viewPositionX = pixelGLConverter.getGLWidthForCurrentGLTransform(gl);
			float viewPositionY = pixelGLConverter.getGLHeightForCurrentGLTransform(gl);
			float wheelPositionX = pixelGLConverter.getGLWidthForPixelWidth(mouseWheelPosition.x);
			float wheelPositionY = pixelGLConverter.getGLHeightForPixelHeight(parentView.getParentGLCanvas()
					.getHeight() - mouseWheelPosition.y);

			// viewTranslateX =
			// (viewFrustum.getWidth() / 2.0f) - zoomCenterX - (previousZoomScale - 1) * zoomCenterX;
			// viewTranslateY =
			// (viewFrustum.getHeight() / 2.0f) - zoomCenterY - (previousZoomScale - 1) * zoomCenterY;

			float zoomCenterMouseX = wheelPositionX - viewPositionX;
			float zoomCenterMouseY = wheelPositionY - viewPositionY;

			float relativeImageCenterX = (-viewTranslateX + zoomCenterMouseX) / (x * previousZoomScale);
			float relativeImageCenterY = (-viewTranslateY + zoomCenterMouseY) / (y * previousZoomScale);

			float zoomCenterX = relativeImageCenterX * x;
			float zoomCenterY = relativeImageCenterY * y;

			// zoomCenterX = viewPositionX + viewFrustum.getWidth() - wheelPositionX;
			// zoomCenterY = viewPositionY + viewFrustum.getHeight() - wheelPositionY;
			viewTranslateX = (x / 2.0f) - zoomCenterX - (currentZoomScale - 1) * zoomCenterX;
			viewTranslateY = (y / 2.0f) - zoomCenterY - (currentZoomScale - 1) * zoomCenterY;

			if (viewTranslateX > 0)
				viewTranslateX = 0;
			if (viewTranslateY > 0)
				viewTranslateY = 0;

			if (viewTranslateX < -(x * (currentZoomScale - 1)))
				viewTranslateX = -(x * (currentZoomScale - 1));
			if (viewTranslateY < -(y * (currentZoomScale - 1)))
				viewTranslateY = -(y * (currentZoomScale - 1));

			relativeViewTranlateX = viewTranslateX / x;
			relativeViewTranlateY = viewTranslateY / y;

			// System.out.println("=========================================");
			// System.out.println("viewPos: " + viewPositionX + "," + viewPositionY + "\n zoomCenter: "
			// + zoomCenterX + "," + zoomCenterY + "\n Frustum: " + viewFrustum.getWidth() + ","
			// + viewFrustum.getHeight() + "\n Translate: " + viewTranlateX + "," + viewTranlateY
			// + "\n currentZoom: " + currentZoomScale + "; prevZoom: " + previousZoomScale);
		}

		float relativeImageCenterX = (-viewTranslateX + x / 2.0f) / (x * currentZoomScale);
		float relativeImageCenterY = (-viewTranslateY + y / 2.0f) / (y * currentZoomScale);

		float zoomCenterX = relativeImageCenterX * x;
		float zoomCenterY = relativeImageCenterY * y;

		hScrollBar.setPageSize(pixelGLConverter.getPixelWidthForGLWidth((x - x / currentZoomScale) / currentZoomScale));
		hScrollBar.setMaxValue(pixelGLConverter.getPixelWidthForGLWidth(x - x / (currentZoomScale * 2.0f)));
		hScrollBar.setMinValue(pixelGLConverter.getPixelWidthForGLWidth(x / (currentZoomScale * 2.0f)));
		hScrollBar.setSelection(pixelGLConverter.getPixelWidthForGLWidth(zoomCenterX));

		vScrollBar.setPageSize(pixelGLConverter.getPixelWidthForGLWidth((y - y / currentZoomScale) / currentZoomScale));
		vScrollBar.setMaxValue(pixelGLConverter.getPixelWidthForGLWidth(y - y / (currentZoomScale * 2.0f)));
		vScrollBar.setMinValue(pixelGLConverter.getPixelWidthForGLWidth(y / (currentZoomScale * 2.0f)));
		vScrollBar.setSelection(pixelGLConverter.getPixelWidthForGLWidth(zoomCenterY));

		// viewTranslateX = (viewFrustum.getWidth() / 2.0f) - zoomCenterX - (currentZoomScale - 1) *
		// zoomCenterX;
		// viewTranslateY =
		// (viewFrustum.getHeight() / 2.0f) - zoomCenterY - (currentZoomScale - 1) * zoomCenterY;
		//
		// relativeZoomCenterX = zoomCenterX / viewFrustum.getWidth();
		// relativeZoomCenterY = zoomCenterY / viewFrustum.getHeight();

		gl.glPushMatrix();
		gl.glTranslatef(viewTranslateX, viewTranslateY, 0);
		gl.glScalef(currentZoomScale, currentZoomScale, 1);

		// JUST FOR TESTING OF 1D ZOOM IN Z-DIRECTION
		// TODO: ADD MODE FOR X-ZOOM, Y-ZOOM OR BOTH
		// gl.glTranslatef(0, viewTranslateY, 0);
		// gl.glScalef(1, currentZoomScale, 1);

	}

	/**
	 * This method shall be called after the view has been rendered, if beginZoom(GL) has been called beforehand.
	 *
	 * @param gl
	 */
	public void endZoom(GL2 gl) {

		previousZoomScale = currentZoomScale;

		gl.glDisable(GL2ES1.GL_CLIP_PLANE0);
		gl.glDisable(GL2ES1.GL_CLIP_PLANE1);
		gl.glDisable(GL2ES1.GL_CLIP_PLANE2);
		gl.glDisable(GL2ES1.GL_CLIP_PLANE3);

		if (currentZoomScale == 1.0f)
			return;
		gl.glPopMatrix();
		wasMouseWheeled = false;

		hScrollBarLayoutManager.render(gl);
		vScrollBarLayoutManager.render(gl);

		scrollBarDragAndDropController.handleDragging(gl, glMouseListener);

	}

	@Override
	public void handleMouseWheel(int wheelAmount, Point wheelPosition) {

		int viewportWidth = pixelGLConverter.getPixelWidthForGLWidth(parentLayout.getSizeScaledX());
		int viewportHeight = pixelGLConverter.getPixelHeightForGLHeight(parentLayout.getSizeScaledY());

		if ((wheelPosition.x >= viewportPositionX) && (wheelPosition.x <= viewportPositionX + viewportWidth)
				&& (parentView.getParentGLCanvas().getHeight() - wheelPosition.y >= viewportPositionY)
				&& (parentView.getParentGLCanvas().getHeight() - wheelPosition.y <= viewportPositionY + viewportHeight)) {

			currentZoomScale += (wheelAmount / 3.0f);
			if (currentZoomScale < 1.0f)
				currentZoomScale = 1.0f;
			wasMouseWheeled = true;
			mouseWheelPosition = wheelPosition;
		}
	}

	@Override
	public void handleScrollBarUpdate(ScrollBar scrollBar) {
		if (scrollBar == hScrollBar) {
			float zoomCenterX = pixelGLConverter.getGLWidthForPixelWidth(scrollBar.getSelection());
			float viewTranslateX = (viewFrustum.getWidth() / 2.0f) - zoomCenterX - (currentZoomScale - 1) * zoomCenterX;

			if (viewTranslateX > 0)
				viewTranslateX = 0;
			if (viewTranslateX < -(viewFrustum.getWidth() * (currentZoomScale - 1)))
				viewTranslateX = -(viewFrustum.getWidth() * (currentZoomScale - 1));

			relativeViewTranlateX = viewTranslateX / viewFrustum.getWidth();
		}
		if (scrollBar == vScrollBar) {
			float zoomCenterY = pixelGLConverter.getGLHeightForPixelHeight(scrollBar.getSelection());
			float viewTranslateY = (viewFrustum.getHeight() / 2.0f) - zoomCenterY - (currentZoomScale - 1)
					* zoomCenterY;

			if (viewTranslateY > 0)
				viewTranslateY = 0;
			if (viewTranslateY < -(viewFrustum.getHeight() * (currentZoomScale - 1)))
				viewTranslateY = -(viewFrustum.getHeight() * (currentZoomScale - 1));

			relativeViewTranlateY = viewTranslateY / viewFrustum.getHeight();
		}
	}

	public void setLimits(float x, float y) {
		viewFrustum.setLeft(0);
		viewFrustum.setBottom(0);
		viewFrustum.setRight(x);
		viewFrustum.setTop(y);
		hScrollBarLayoutManager.setViewFrustum(viewFrustum);
		vScrollBarLayoutManager.setViewFrustum(viewFrustum);

		hScrollBarLayoutManager.updateLayout();
		vScrollBarLayoutManager.updateLayout();
	}
}
