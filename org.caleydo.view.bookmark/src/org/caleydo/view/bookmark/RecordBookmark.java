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
class RecordBookmark extends ABookmark {

	private ElementLayout layout;

	/**
	 * Constructor taking a textRenderer
	 *
	 * @param textRenderer
	 * @param id
	 */
	public RecordBookmark(GLBookmarkView manager,
			RecordBookmarkContainer parentContainer, IDType idType, Integer id,
			CaleydoTextRenderer textRenderer) {
		super(manager, parentContainer, idType, textRenderer);
		this.id = id;

		layout = new ElementLayout("ContentBookmark");

		layout.setRatioSizeX(1);

		layout.setRenderer(this);
		// float height = (float) textRenderer.getBounds("Text").getHeight();

		layout.setPixelSizeY(20);

	}

	@Override
	public ElementLayout getLayout() {
		return layout;
	}

	@Override
	public void renderContent(GL2 gl) {

		super.renderContent(gl);

		String label = manager.getDataDomain().getRecordLabel(idType, id);
		textRenderer.setColor(0, 0, 0, 1);

		gl.glPushName(manager.getBookmarkPickingIDManager().getPickingID(parentContainer,
				id));
		textRenderer.renderTextInBounds(gl, label, 0 + xSpacing, 0 + ySpacing, 0, x
				- xSpacing, y - 2 * ySpacing);
		gl.glPopName();

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
