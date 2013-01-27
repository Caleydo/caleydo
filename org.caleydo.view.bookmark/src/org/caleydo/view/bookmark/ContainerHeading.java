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
package org.caleydo.view.bookmark;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;

public class ContainerHeading extends ALayoutRenderer implements ILayoutedElement {

	private ElementLayout layout;
	private GLBookmarkView manager;
	private String caption = "CAPTION";

	protected final static int Y_SPACING_PIXEL = 4;
	protected final static int X_SPACING_PIXEL = 5;

	public ContainerHeading(GLBookmarkView manager) {
		this.manager = manager;
		layout = new ElementLayout("ContainerHeading");
		layout.setRatioSizeX(1);
		layout.setPixelSizeY(20);
		layout.setRenderer(this);

	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@Override
	public ElementLayout getLayout() {
		return layout;
	}

	@Override
	protected void renderContent(GL2 gl) {

		PixelGLConverter pixelGLConverter = manager.getPixelGLConverter();
		float ySpacing = pixelGLConverter.getGLHeightForPixelHeight(Y_SPACING_PIXEL);
		float xSpacing = pixelGLConverter.getGLWidthForPixelWidth(X_SPACING_PIXEL);

		manager.getTextRenderer().setColor(0, 0, 0, 1);
		manager.getTextRenderer().renderTextInBounds(gl, caption, 0 + xSpacing,
				0 + ySpacing, 0, x - xSpacing, y - 2 * ySpacing);

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}
}
