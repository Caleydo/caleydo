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
package org.caleydo.view.stratomex.tourguide.internal;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.view.stratomex.tourguide.TourguideAdapter;

/**
 * @author Samuel Gratzl
 *
 */
public class AddAttachedLayoutRenderer extends ALayoutRenderer {
	private final AGLView view;
	private final int id;
	private final TourguideAdapter tourguide;
	private final boolean left;

	public AddAttachedLayoutRenderer(AGLView view, int id, TourguideAdapter tourguide, boolean left) {
		this.view = view;
		this.id = id;
		this.tourguide = tourguide;
		this.left = left;
	}

	@Override
	protected void renderContent(GL2 gl) {
		float hi = view.getPixelGLConverter().getGLHeightForPixelHeight(34);
		float wi = view.getPixelGLConverter().getGLWidthForPixelWidth(32);
		tourguide.renderAddDependentButton(gl, left ? (-wi - x * 0.05f) : (x * 1.05f), y * 0.8f, wi, hi, id * 2
				+ (left ? 1 : 0));
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}

}
