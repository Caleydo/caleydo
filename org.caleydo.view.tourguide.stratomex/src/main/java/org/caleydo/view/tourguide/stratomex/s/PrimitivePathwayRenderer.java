/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex.s;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.pathway.GLPathwayTextureManager;

/**
 * @author Samuel Gratzl
 *
 */
public class PrimitivePathwayRenderer extends ALayoutRenderer {
	private final GLPathwayTextureManager texture = new GLPathwayTextureManager();

	private final PathwayGraph pathway;

	private final AGLView view;

	public PrimitivePathwayRenderer(PathwayGraph pathway, AGLView view) {
		this.pathway = pathway;
		this.view = view;
	}

	@Override
	protected void renderContent(GL2 gl) {
		gl.glPushMatrix();
		final PixelGLConverter converter = view.getPixelGLConverter();
		float pw = pathway.getWidth();
		float ph = pathway.getHeight();

		float w = converter.getPixelWidthForGLWidth(x);
		float h = converter.getPixelHeightForGLHeight(y);

		float s = Math.max(pw / w, ph / h); // select scale
		pw /= s; // get scaled pathway size
		ph /= s;

		// convert to target scale
		w = converter.getGLWidthForPixelWidth(Math.round(pw));
		h = converter.getGLHeightForPixelHeight(Math.round(ph));

		gl.glTranslatef((x - w) * .5f, (y - h) * 0.5f, 0);
		// scale as the pathway texture renderer works with gl widths
		gl.glScalef(w / converter.getGLWidthForPixelWidth(pathway.getWidth()),
				h / converter.getGLHeightForPixelHeight(pathway.getHeight()), 1);

		texture.renderPathway(gl, view, pathway, 1.f, false);
		gl.glPopMatrix();
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}

	@Override
	public void destroy(GL2 gl) {
		texture.clear(gl);
		super.destroy(gl);
	}

}
