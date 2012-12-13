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

import org.caleydo.core.util.color.Colors;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.scrollbar.IScrollBarUpdateHandler;
import org.caleydo.core.view.opengl.util.scrollbar.ScrollBar;
import org.caleydo.core.view.opengl.util.scrollbar.ScrollBarRenderer;

/**
 * TODO not working
 * 
 * @author Samuel Gratzl
 * 
 */
public final class ScrolledLayoutElement extends Row implements IScrollBarUpdateHandler {

	private final AGLView view;
	private final DragAndDropController dndController;

	private ScrollBar hScrollBar;
	private ScrollBar vScrollBar;

	private float originX;
	private float originY;

	private ElementLayout content;
	private ScrolledContent wrapper;

	public ScrolledLayoutElement(AGLView parentView) {
		this.view = parentView;
		dndController = new DragAndDropController(parentView);

		this.setLeftToRight(false);
		vScrollBar = new ScrollBar(0, 10, 5, 5, PickingType.ZOOM_SCROLLBAR, parentView.createNewScrollBarID(), this);
		ElementLayout vBar = ElementLayouts.wrap(new ScrollBarRenderer(vScrollBar, view, false, dndController), 10);
		vBar.setGrabY(true);
		this.add(vBar);

		Column tmp = new Column();
		tmp.setBottomUp(true);

		hScrollBar = new ScrollBar(0, 10, 5, 5, PickingType.ZOOM_SCROLLBAR, parentView.createNewScrollBarID(), this);
		ElementLayout hBar = ElementLayouts.wrap(new ScrollBarRenderer(hScrollBar, view, true, dndController), -1);
		hBar.setPixelSizeY(10);
		tmp.add(hBar);

		wrapper = new ScrolledContent();
		wrapper.setGrabX(true);
		wrapper.setGrabY(true);
		wrapper.addBackgroundRenderer(new ColorRenderer(Colors.RED.getRGBA()));
		tmp.add(wrapper);

		this.add(tmp);
	}

	/**
	 * @param content
	 *            setter, see {@link content}
	 */
	public void setContent(ElementLayout content) {
		this.content = content;
		this.content.addBackgroundRenderer(new ColorRenderer(Colors.BLUE.getRGBA()));
	}

	private class ScrolledContent extends ElementLayout {
		@Override
		void render(GL2 gl) {
			super.render(gl);
			if (this.isHidden)
				return;
			// render content
			if (content != null) {
				beginScrolling(gl);
				content.render(gl);
				endScrolling(gl);
			}
		}

		@Override
		void setRenderingDirty() {
			if (isHidden)
				return;
			super.setRenderingDirty();
			if (content != null)
				content.setRenderingDirty();
		}

		@Override
		void setLayoutManager(LayoutManager layoutManager) {
			super.setLayoutManager(layoutManager);
			if (content != null)
				content.setLayoutManager(layoutManager);
		}

		@Override
		public void destroy(GL2 gl) {
			super.destroy(gl);
			if (content != null)
				content.destroy(gl);
		}

		@Override
		public void updateSubLayout() {
			if (isHidden)
				return;
			calculateScales(totalWidth, totalHeight, dynamicSizeUnitsX, dynamicSizeUnitsY);
			updateSpacings();
			if (content != null) {
				content.calculateScales(totalWidth, totalHeight, dynamicSizeUnitsX, dynamicSizeUnitsY);
			}
		}

		@Override
		protected void updateSpacings() {
			if (isHidden)
				return;
			super.updateSpacings();
			if (content != null)
				content.updateSpacings();
		}

	}


	void beginScrolling(GL2 gl) {
		float width = getSizeScaledX();
		float height = getSizeScaledY();

		double[] clipPlane1 = new double[] { 0.0, 1.0, 0.0, 0.0 };
		double[] clipPlane2 = new double[] { 1.0, 0.0, 0.0, 0.0 };
		double[] clipPlane3 = new double[] { -1.0, 0.0, 0.0, width };
		double[] clipPlane4 = new double[] { 0.0, -1.0, 0.0, height };

		// gl.glClipPlane(GL2ES1.GL_CLIP_PLANE0, clipPlane1, 0);
		// gl.glClipPlane(GL2ES1.GL_CLIP_PLANE1, clipPlane2, 0);
		// gl.glClipPlane(GL2ES1.GL_CLIP_PLANE2, clipPlane3, 0);
		// gl.glClipPlane(GL2ES1.GL_CLIP_PLANE3, clipPlane4, 0);
		// gl.glEnable(GL2ES1.GL_CLIP_PLANE0);
		// gl.glEnable(GL2ES1.GL_CLIP_PLANE1);
		// gl.glEnable(GL2ES1.GL_CLIP_PLANE2);
		// gl.glEnable(GL2ES1.GL_CLIP_PLANE3);

		// viewportPositionX = pixelGLConverter.getPixelWidthForCurrentGLTransform(gl);
		// viewportPositionY = pixelGLConverter.getPixelHeightForCurrentGLTransform(gl);
		//
		//
		//
		// float x = getSizeScaledX();
		// float y = getSizeScaledY();
		//
		// float viewTranslateX = originX * x;
		// float viewTranslateY = originY * y;
		//
		// hScrollBar.setPageSize(pixelGLConverter.getPixelWidthForGLWidth((x - x / currentZoomScale) /
		// currentZoomScale));
		// hScrollBar.setMaxValue(pixelGLConverter.getPixelWidthForGLWidth(x - x / (currentZoomScale * 2.0f)));
		// hScrollBar.setMinValue(pixelGLConverter.getPixelWidthForGLWidth(x / (currentZoomScale * 2.0f)));
		// hScrollBar.setSelection(pixelGLConverter.getPixelWidthForGLWidth(zoomCenterX));
		//
		// vScrollBar.setPageSize(pixelGLConverter.getPixelWidthForGLWidth((y - y / currentZoomScale) /
		// currentZoomScale));
		// vScrollBar.setMaxValue(pixelGLConverter.getPixelWidthForGLWidth(y - y / (currentZoomScale * 2.0f)));
		// vScrollBar.setMinValue(pixelGLConverter.getPixelWidthForGLWidth(y / (currentZoomScale * 2.0f)));
		// vScrollBar.setSelection(pixelGLConverter.getPixelWidthForGLWidth(zoomCenterY));
		//
		gl.glPushMatrix();
		gl.glTranslatef(originX, originY, 0);

	}

	void endScrolling(GL2 gl) {
		// gl.glDisable(GL2ES1.GL_CLIP_PLANE0);
		// gl.glDisable(GL2ES1.GL_CLIP_PLANE1);
		// gl.glDisable(GL2ES1.GL_CLIP_PLANE2);
		// gl.glDisable(GL2ES1.GL_CLIP_PLANE3);
		//
		gl.glPopMatrix();
		dndController.handleDragging(gl, view.getGLMouseListener());
	}

	@Override
	public void handleScrollBarUpdate(ScrollBar scrollBar) {
		if (scrollBar == hScrollBar) {
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
