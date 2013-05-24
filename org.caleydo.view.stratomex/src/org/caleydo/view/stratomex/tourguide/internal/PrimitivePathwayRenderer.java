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
