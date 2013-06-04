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
public class WizardActionsLayoutRenderer extends ALayoutRenderer {
	private final AGLView view;
	private final TourguideAdapter tourguide;

	public WizardActionsLayoutRenderer(AGLView view, TourguideAdapter tourguide) {
		this.view = view;
		this.tourguide = tourguide;
	}

	@Override
	protected void renderContent(GL2 gl) {
		float hi = view.getPixelGLConverter().getGLHeightForPixelHeight(34);
		float wi = view.getPixelGLConverter().getGLWidthForPixelWidth(32);
		tourguide.renderConfirmButton(gl, x * 1.05f, y * 0.8f + hi, wi, hi);
		tourguide.renderCancelButton(gl, x * 1.05f, y * 0.8f + hi * 2, wi, hi);
		tourguide.renderBackButton(gl, x * 1.05f, y * 0.8f, wi, hi);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
