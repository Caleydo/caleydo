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
package org.caleydo.view.stratomex.column;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;

public class BrickColumnGlowRenderer
	extends ColorRenderer
{

	private BrickColumn dimensionGroup;
	private boolean renderWhenDimensionGroupIsCollapsed;

	public BrickColumnGlowRenderer(float[] color, BrickColumn dimensionGroup,
			boolean renderWhenDimensionGroupIsCollapsed)
	{
		super(color);
		this.dimensionGroup = dimensionGroup;
		this.renderWhenDimensionGroupIsCollapsed = renderWhenDimensionGroupIsCollapsed;
	}

	@Override
	public void renderContent(GL2 gl)
	{
		if (dimensionGroup.isCollapsed() != renderWhenDimensionGroupIsCollapsed)
			return;

		if (renderWhenDimensionGroupIsCollapsed)
		{
			PixelGLConverter pixelGLConverter = dimensionGroup.getPixelGLConverter();
			float overlapX = pixelGLConverter.getGLWidthForPixelWidth(10);
			float overlapY = pixelGLConverter.getGLWidthForPixelWidth(10);

			gl.glBegin(GL2.GL_QUADS);
			gl.glColor4f(color[0], color[1], color[2], 1);
			gl.glVertex3f(0, y, 0);
			gl.glVertex3f(x, y, 0);
			gl.glColor4f(color[0], color[1], color[2], 0);
			gl.glVertex3f(x + overlapX, y + overlapY, 0);
			gl.glVertex3f(-overlapX, y + overlapY, 0);

			gl.glColor4f(color[0], color[1], color[2], 1);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, y, 0);
			gl.glColor4f(color[0], color[1], color[2], 0);
			gl.glVertex3f(-overlapX, y + overlapY, 0);
			gl.glVertex3f(-overlapX, -overlapY, 0);

			gl.glColor4f(color[0], color[1], color[2], 1);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(x, 0, 0);
			gl.glColor4f(color[0], color[1], color[2], 0);
			gl.glVertex3f(x + overlapX, -overlapY, 0);
			gl.glVertex3f(-overlapX, -overlapY, 0);

			gl.glColor4f(color[0], color[1], color[2], 1);
			gl.glVertex3f(x, 0, 0);
			gl.glVertex3f(x, y, 0);
			gl.glColor4f(color[0], color[1], color[2], 0);
			gl.glVertex3f(x + overlapX, y + overlapY, 0);
			gl.glVertex3f(x + overlapX, -overlapY, 0);

			gl.glEnd();
		}
		else
		{
			gl.glBegin(GL2.GL_QUADS);
			gl.glColor4f(color[0], color[1], color[2], 0);
			gl.glVertex3f(-x / 3.0f, 0, 0);
			gl.glColor4f(color[0], color[1], color[2], 1);
			gl.glVertex3f(x / 3.0f, 0, 0);
			gl.glVertex3f(x / 3.0f, y, 0);
			gl.glColor4f(color[0], color[1], color[2], 0);
			gl.glVertex3f(-x / 3.0f, y, 0);

			gl.glColor4f(color[0], color[1], color[2], 1);
			gl.glVertex3f(x / 3.0f, 0, 0);
			gl.glVertex3f(2.0f * x / 3.0f, 0, 0);
			gl.glVertex3f(2.0f * x / 3.0f, y, 0);
			gl.glVertex3f(x / 3.0f, y, 0);

			gl.glVertex3f(2.0f * x / 3.0f, 0, 0);
			gl.glColor4f(color[0], color[1], color[2], 0);
			gl.glVertex3f(x + x / 3.0f, 0, 0);
			gl.glVertex3f(x + x / 3.0f, y, 0);
			gl.glColor4f(color[0], color[1], color[2], 1);
			gl.glVertex3f(2.0f * x / 3.0f, y, 0);

			gl.glEnd();
		}

	}
	
	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
