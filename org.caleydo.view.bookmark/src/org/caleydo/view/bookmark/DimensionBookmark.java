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

import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * A bookmark for a gene. The id used here is DAVID
 *
 * @author Alexander Lex
 */
class DimensionBookmark extends ABookmark {

	ElementLayout layout;

	/**
	 * Constructor taking a textRenderer
	 *
	 * @param textRenderer
	 * @param davidID
	 */
	public DimensionBookmark(GLBookmarkView manager,
			DimensionBookmarkContainer partentContainer, IDType idType,
			Integer experimentIndex, CaleydoTextRenderer textRenderer) {
		super(manager, partentContainer, idType, textRenderer);
		this.id = experimentIndex;

		layout = new ElementLayout();
		layout.setRatioSizeX(1);
		layout.setRenderer(this);
		layout.setPixelSizeY(20);

	}

	@Override
	public ElementLayout getLayout() {
		return layout;
	}

	@Override
	public void renderContent(GL2 gl) {

		super.renderContent(gl);
		String label = manager.getDataDomain().getDimensionLabel(id);
		gl.glPushName(manager.getBookmarkPickingIDManager().getPickingID(parentContainer,
				id));
		manager.getTextRenderer().renderTextInBounds(gl, label, 0 + xSpacing,
				0 + ySpacing, 0, x - xSpacing, y - 2 * ySpacing);
		gl.glPopName();

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
