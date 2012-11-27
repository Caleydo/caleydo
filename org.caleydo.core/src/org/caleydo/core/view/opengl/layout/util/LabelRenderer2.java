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
package org.caleydo.core.view.opengl.layout.util;

import javax.media.opengl.GL2;

import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.color.IColor;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.Padding;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer.LabelAlignment;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * Renders a text within the given bounds of the ElementLayout. The text is
 * truncated if necessary.
 *
 * @author Partl
 */
public class LabelRenderer2 extends LayoutRenderer {
	private final ILabelProvider labelProvider;

	private String label = "Not set";

	private final CaleydoTextRenderer textRenderer;

	/**
	 * Specifies the alignment of the text.
	 */
	private final LabelAlignment alignment;

	private Padding padding;

	private final IColor textColor;

	/**
	 * The text of the label that was rendered in the last frame. This variable
	 * is used to detect whether a new display list has to be built.
	 */
	private String prevLabel = "";


	public LabelRenderer2(ILabelProvider labelProvider, Padding padding, IColor textColor, LabelAlignment alignment,
			CaleydoTextRenderer textRenderer) {
		this.labelProvider = labelProvider;
		this.padding = padding;
		this.textColor = textColor;
		this.alignment = alignment;
		this.textRenderer = textRenderer;
	}

	@Override
	protected void prepare() {
		if (labelProvider != null)
			label = labelProvider.getLabel();

		if (!prevLabel.equals(label)) {
			setDisplayListDirty();
		}
		prevLabel = label;
	}

	@Override
	protected void renderContent(GL2 gl) {
		if (labelProvider != null)
			label = labelProvider.getLabel();

		textRenderer.setColor(textColor.getRGBA());

		float[] padding = this.padding.resolve(layoutManager.getPixelGLConverter());

		float height = y - padding[1] + padding[3];
		float width = x - padding[0] - padding[2];
		float textWidth = textRenderer.getRequiredTextWidthWithMax(label, height, width);

		float yPosition = padding[3];
		float xPosition;
		switch (alignment) {
		case CENTER:
			xPosition = padding[0] + width * 0.5f - textWidth * 0.5f;
			break;
		case RIGHT:
			xPosition = x - padding[2] - textWidth;
			break;
		default:
			xPosition = padding[0];
			break;
		}
		textRenderer.renderTextInBounds(gl, label, xPosition, yPosition, 0.1f, width, height);
	}

	@Override
	protected boolean permitsDisplayLists() {
		return true;
	}
}
