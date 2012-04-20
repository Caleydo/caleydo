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
package org.caleydo.view.linearizedpathway.mappeddataview;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * @author alexsb
 * 
 */
public class ColumnCaptionRenderer extends LayoutRenderer {

	private String label;
	private CaleydoTextRenderer textRenderer;
	private PixelGLConverter pixelGLConverter;

	public ColumnCaptionRenderer(CaleydoTextRenderer textRenderer,
			PixelGLConverter pixelGLConverter, String label) {
		this.textRenderer = textRenderer;
		this.pixelGLConverter = pixelGLConverter;
		this.label = label;
	}

	@Override
	public void render(GL2 gl) {
//		float sideSpacing = pixelGLConverter.getGLWidthForPixelWidth(8);
		float sideSpacing =0;
		
		float height = pixelGLConverter.getGLHeightForPixelHeight(15);

		float backgroundZ = 0;
		
		float[] evenColor = { 220f / 255f, 220f / 255, 220f / 255, 1f };

		
		gl.glColor4fv(evenColor, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, backgroundZ);
		gl.glVertex3f(0, y, backgroundZ);
		gl.glVertex3f(x, y, backgroundZ);
		gl.glVertex3f(x, 0, backgroundZ);
		gl.glEnd();

		
		textRenderer.renderTextInBounds(gl, label, sideSpacing, (y - height) / 2, 0.1f,
				x, height);
		
	}
}
