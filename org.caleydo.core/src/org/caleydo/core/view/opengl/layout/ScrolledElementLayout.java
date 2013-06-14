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
package org.caleydo.core.view.opengl.layout;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.scrollbar.IScrollBarUpdateHandler;
import org.caleydo.core.view.opengl.util.scrollbar.ScrollBar;
import org.caleydo.core.view.opengl.util.scrollbar.ScrollBarRenderer;

/**
 * wrapper for an (fixed size) elementlayout to provide scrollbars around it
 *
 * @author Samuel Gratzl
 *
 */
public final class ScrolledElementLayout extends Row implements IScrollBarUpdateHandler {

	private final AGLView view;
	private final DragAndDropController dndController;

	private ScrollBar hScrollBar;
	private ScrollBar vScrollBar;

	private float originX;
	private float originY;

	private ScrolledContent wrapper;
	private final ElementLayout content;

	public ScrolledElementLayout(AGLView parentView, ElementLayout content) {
		this.view = parentView;
		dndController = new DragAndDropController(parentView);

		Column tmp = new Column();
		tmp.setBottomUp(true);

		hScrollBar = new ScrollBar(0, 10, 5, 5, PickingType.ZOOM_SCROLLBAR, parentView.createNewScrollBarID(), this);
		ElementLayout hBar = ElementLayouts.wrap(new ScrollBarRenderer(hScrollBar, view, true, dndController,
				new Color(0.5f)), -1);
		hBar.setPixelSizeY(10);
		tmp.add(hBar);

		wrapper = new ScrolledContent();
		wrapper.setGrabX(true);
		wrapper.setGrabY(true);
		wrapper.setBottomUp(false);
		tmp.add(wrapper);

		this.add(tmp);

		vScrollBar = new ScrollBar(0, 10, 0, 5, PickingType.ZOOM_SCROLLBAR, parentView.createNewScrollBarID(), this);
		ElementLayout vBar = ElementLayouts.wrap(new ScrollBarRenderer(vScrollBar, view, false, dndController,
				new Color(0.5f)), 10);
		vBar.setGrabY(true);
		vBar.setPixelSizeX(10);
		this.add(vBar);

		this.setGrabX(true);
		this.setGrabY(true);

		this.content = content;
		wrapper.add(content);
	}

	private class ScrolledContent extends Column {
		@Override
		public void render(GL2 gl) {
			beginScrolling(gl);
			super.render(gl);
			endScrolling(gl);
		}

		@Override
		protected void updateSpacings() {
			super.updateSpacings();
			updateScrollBars();
		}

		@Override
		public void updateSubLayout() {
			super.updateSubLayout();
		}
	}

	void beginScrolling(GL2 gl) {
		float width = wrapper.getSizeScaledX();
		float height = wrapper.getSizeScaledY();
		float x = wrapper.getTranslateX();
		float y = wrapper.getTranslateY();

		gl.glPushAttrib(GL2.GL_ENABLE_BIT);
		if (!getScrollBarElement(true).isHidden()) {
			double[] clipPlane1 = new double[] { 1.0, 0.0, 0.0, -x };
			double[] clipPlane3 = new double[] { -1.0, 0.0, 0.0, width + x };
			gl.glClipPlane(GL2ES1.GL_CLIP_PLANE0, clipPlane1, 0);
			gl.glClipPlane(GL2ES1.GL_CLIP_PLANE1, clipPlane3, 0);
			gl.glEnable(GL2ES1.GL_CLIP_PLANE0);
			gl.glEnable(GL2ES1.GL_CLIP_PLANE1);
		}
		if (!getScrollBarElement(false).isHidden()) {
			double[] clipPlane2 = new double[] { 0.0, 1.0, 0.0, -y };
			double[] clipPlane4 = new double[] { 0.0, -1.0, 0.0, height + y };
			gl.glClipPlane(GL2ES1.GL_CLIP_PLANE2, clipPlane2, 0);
			gl.glClipPlane(GL2ES1.GL_CLIP_PLANE3, clipPlane4, 0);
			gl.glEnable(GL2ES1.GL_CLIP_PLANE2);
			gl.glEnable(GL2ES1.GL_CLIP_PLANE3);
		}
		gl.glPushMatrix();
		gl.glTranslatef(-originX, originY, 0);

	}

	private ElementLayout getScrollBarElement(boolean horizontal) {
		if (!horizontal)
			return get(1);
		else
			return ((ALayoutContainer) get(0)).get(0);
	}

	public void updateScrollBars() {
		final float cw = content.getSizeScaledX();
		final float ch = content.getSizeScaledY();

		final float ww = wrapper.getSizeScaledX();
		final float wh = wrapper.getSizeScaledY();

		boolean invalidate = false;
		if (cw <= ww) {
			ElementLayout hs = getScrollBarElement(true);
			if (!hs.isHidden()) {
				invalidate = true;
				hs.setHidden(true);
				originX = 0;
			}
		} else { // compute scrollbar
			ElementLayout hs = getScrollBarElement(true);
			if (hs.isHidden()) {
				hs.setHidden(false);
				invalidate = true;
			}
			final int pageSize = (int) (ww / cw * (cw - ww) * 10000);
			final int maxValue = (int) ((cw - ww) * 10000);
			if (maxValue != hScrollBar.getMaxValue() || pageSize != hScrollBar.getPageSize()) {
				hScrollBar.setMaxValue(maxValue);
				hScrollBar.setPageSize(pageSize);
				hs.setRenderingDirty();
			}
		}
		if (ch <= wh) {
			ElementLayout vs = getScrollBarElement(false);
			if (!vs.isHidden()) {
				invalidate = true;
				vs.setHidden(true);
				originY = 0;
			}
		} else { // compute scrollbar
			ElementLayout vs = getScrollBarElement(false);
			if (vs.isHidden()) {
				invalidate = true;
				vs.setHidden(false);
			}

			final int maxValue = (int) ((ch - wh) * 10000);
			final int pageSize = (int) (wh / ch * (ch - wh) * 10000);
			if (maxValue != vScrollBar.getMaxValue() || pageSize != vScrollBar.getPageSize()) {
				vScrollBar.setMaxValue(maxValue);
				vScrollBar.setSelection(vScrollBar.getMaxValue());
				vScrollBar.setPageSize(pageSize);
				invalidate = true;
			}
		}
		if (invalidate) {
			this.layoutManager.updateLayout();
		}
	}

	void endScrolling(GL2 gl) {
		gl.glPopMatrix();
		gl.glPopAttrib();
		dndController.handleDragging(gl, view.getGLMouseListener());
	}

	@Override
	public void handleScrollBarUpdate(ScrollBar scrollBar) {
		if (scrollBar == hScrollBar) {
			this.originX = scrollBar.getSelection() / 10000.f;
			// float zoomCenterX = pixelGLConverter.getGLWidthForPixelWidth(scrollBar.getSelection());
			// float viewTranslateX = (viewFrustum.getWidth() / 2.0f) - zoomCenterX - (currentZoomScale - 1) *
			// zoomCenterX;
			//
			// if (viewTranslateX > 0)
			// viewTranslateX = 0;
			// if (viewTranslateX < -(viewFrustum.getWidth() * (currentZoomScale - 1)))
			// viewTranslateX = -(viewFrustum.getWidth() * (currentZoomScale - 1));
			//
			// originX = viewTranslateX / viewFrustum.getWidth();
		}
		if (scrollBar == vScrollBar) {
			this.originY = (scrollBar.getMaxValue() - scrollBar.getSelection()) / 10000.f;
			// float zoomCenterY = pixelGLConverter.getGLHeightForPixelHeight(scrollBar.getSelection());
			// float viewTranslateY = (viewFrustum.getHeight() / 2.0f) - zoomCenterY - (currentZoomScale - 1)
			// * zoomCenterY;
			//
			// if (viewTranslateY > 0)
			// viewTranslateY = 0;
			// if (viewTranslateY < -(viewFrustum.getHeight() * (currentZoomScale - 1)))
			// viewTranslateY = -(viewFrustum.getHeight() * (currentZoomScale - 1));
			//
			// originY = viewTranslateY / viewFrustum.getHeight();
		}
	}
}
