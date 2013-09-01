/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.tourguide.internal;

import javax.media.opengl.GL;
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
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);

		tourguide.renderConfirmButton(gl, x * 1.05f, y * 0.8f + hi, wi, hi);
		tourguide.renderCancelButton(gl, x * 1.05f, y * 0.8f + hi * 2, wi, hi);
		tourguide.renderBackButton(gl, x * 1.05f, y * 0.8f, wi, hi);

		gl.glPopAttrib();
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
