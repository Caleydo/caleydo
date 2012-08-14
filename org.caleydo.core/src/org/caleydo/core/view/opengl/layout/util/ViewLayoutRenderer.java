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

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

/**
 * A sub-class for {@link LayoutRenderer} intended to render whole
 * {@link AGLView}s. The main contract here, is that the view renders within its
 * view frustum, which is updated according to the size of the layout in the
 * {@link #setLimits(float, float)} method.
 * 
 * @author Alexander Lex
 */
public class ViewLayoutRenderer
	extends LayoutRenderer {

	protected AGLView view;

	/**
	 * Constructor taking an {@link AGLView} to be rendered by this renderer.
	 * 
	 * @param view
	 */
	public ViewLayoutRenderer(AGLView view) {
		this.view = view;
	}

	@Override
	public void setLimits(float x, float y) {
		super.setLimits(x, y);
		ViewFrustum viewFrustum = view.getViewFrustum();

		if (viewFrustum == null) {
			viewFrustum = new ViewFrustum();
			viewFrustum.setProjectionMode(CameraProjectionMode.ORTHOGRAPHIC);
		}

		viewFrustum.setLeft(0);
		viewFrustum.setBottom(0);
		viewFrustum.setRight(x);
		viewFrustum.setTop(y);
		view.setFrustum(viewFrustum);
		view.setDisplayListDirty();
	}

	/**
	 * @param view setter, see {@link #view}
	 */
	public void setView(AGLView view) {
		this.view = view;
	}

	/**
	 * @return the view, see {@link #view}
	 */
	public AGLView getView() {
		return view;
	}

	@Override
	public int getMinHeightPixels() {
		return view.getMinPixelHeight();
	}

	@Override
	public int getMinWidthPixels() {
		return view.getMinPixelWidth();
	}

	@Override
	protected void renderContent(GL2 gl) {
		view.displayRemote(gl);
	}

	@Override
	protected boolean permitsDisplayLists() {
		return false;
	}

}
