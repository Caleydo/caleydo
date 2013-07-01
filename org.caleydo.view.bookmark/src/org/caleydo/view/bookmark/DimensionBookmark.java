/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
