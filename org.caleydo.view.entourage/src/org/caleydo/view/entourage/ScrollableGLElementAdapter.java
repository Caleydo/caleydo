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
package org.caleydo.view.entourage;

import gleem.linalg.Vec2f;

import javax.media.opengl.GL2ES1;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.listener.IMouseWheelHandler;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout2.GLElementAdapter;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * @author Christian
 *
 */
public class ScrollableGLElementAdapter extends GLElementAdapter implements IMouseWheelHandler {

	protected boolean isVScrollBarVisible = false;
	protected ALayoutRenderer renderer;
	protected float vScrollBarPosition = 0;
	protected float vScrollBarSize = 0;

	/**
	 * @param view
	 * @param renderer
	 * @param isZoomable
	 */
	public ScrollableGLElementAdapter(AGLView view, ALayoutRenderer renderer) {
		super(view, renderer, false);
		this.renderer = renderer;
		view.registerMouseWheelListener(this);
	}

	protected void updateScrollBarSize(float minWidth, float minHeight) {
		float vScrollBarSize = getSize().y() / minHeight * getSize().y();
		if (vScrollBarSize >= getSize().y())
			isVScrollBarVisible = false;
		isVScrollBarVisible = true;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		beginScrollBars(g, w, h);
		super.renderImpl(g, w, h);
		endScrollBars(g, w, h);
	}

	protected void beginScrollBars(GLGraphics g, float w, float h) {
		vScrollBarSize = (h / renderer.getMinHeightPixels()) * h;
		if (vScrollBarSize < h) {
			if (vScrollBarPosition < 0) {
				vScrollBarPosition = 0;
			}
			if (vScrollBarPosition + vScrollBarSize > h) {
				vScrollBarPosition = h - vScrollBarSize;
			}

			g.color(0.6f, 0.6f, 0.6f, 1f).fillRect(w - 10, vScrollBarPosition, 10, vScrollBarSize);

			double[] clipPlane1 = new double[] { 0.0, 1.0, 0.0, 0.0 };
			double[] clipPlane2 = new double[] { 1.0, 0.0, 0.0, 0.0 };
			double[] clipPlane3 = new double[] { -1.0, 0.0, 0.0, w };
			double[] clipPlane4 = new double[] { 0.0, -1.0, 0.0, h };

			g.gl.glClipPlane(GL2ES1.GL_CLIP_PLANE0, clipPlane1, 0);
			g.gl.glClipPlane(GL2ES1.GL_CLIP_PLANE1, clipPlane2, 0);
			g.gl.glClipPlane(GL2ES1.GL_CLIP_PLANE2, clipPlane3, 0);
			g.gl.glClipPlane(GL2ES1.GL_CLIP_PLANE3, clipPlane4, 0);
			g.gl.glEnable(GL2ES1.GL_CLIP_PLANE0);
			g.gl.glEnable(GL2ES1.GL_CLIP_PLANE1);
			g.gl.glEnable(GL2ES1.GL_CLIP_PLANE2);
			g.gl.glEnable(GL2ES1.GL_CLIP_PLANE3);

			g.gl.glPushMatrix();
			g.move(0, -vScrollBarPosition / h * renderer.getMinHeightPixels());

		} else {
			vScrollBarPosition = 0;
		}
	}

	protected void endScrollBars(GLGraphics g, float w, float h) {
		if (vScrollBarSize < h) {
			g.gl.glPopMatrix();
			g.gl.glDisable(GL2ES1.GL_CLIP_PLANE0);
			g.gl.glDisable(GL2ES1.GL_CLIP_PLANE1);
			g.gl.glDisable(GL2ES1.GL_CLIP_PLANE2);
			g.gl.glDisable(GL2ES1.GL_CLIP_PLANE3);
		}
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		beginScrollBars(g, w, h);
		super.renderPickImpl(g, w, h);
		endScrollBars(g, w, h);
	}


	@Override
	public void handleMouseWheel(int wheelAmount, Vec2f wheelPosition) {
		if ((wheelPosition.get(0) > getAbsoluteLocation().x() && wheelPosition.get(0) < getAbsoluteLocation().x()
				+ getSize().x())
				&& (wheelPosition.get(1) > getAbsoluteLocation().y() && wheelPosition.get(1) < getAbsoluteLocation()
						.y()
						+ getSize().y())) {
			vScrollBarPosition += -wheelAmount * vScrollBarSize * 0.5f;
		}

	}
}
