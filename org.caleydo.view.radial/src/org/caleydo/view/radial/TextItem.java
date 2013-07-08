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
package org.caleydo.view.radial;

import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * Represents some text that can be drawn within a label. Note, that changes to
 * the size of a text item cannot be made by the methods setHeight and setWidth,
 * since the size of a text is determined by the font type, the font size, the
 * font style and the font scaling.
 * 
 * @author Christian Partl
 */
public class TextItem extends ALabelItem {

	private static final String sTextForHeightCalculation = "Text without characters below the bottom textline";

	private String sText;
	private CaleydoTextRenderer textRenderer;
	private float fTextScaling;

	/**
	 * Constructor.
	 * 
	 * @param sText
	 *            Text that should be displayed in the label.
	 */
	public TextItem(String sText) {
		this.sText = sText;
	}

	@Override
	public void draw(GL2 gl) {
		float[] text_color = RadialHierarchyRenderStyle.LABEL_TEXT_COLOR;
		textRenderer.setColor(text_color[0], text_color[1], text_color[2], text_color[3]);

		textRenderer.renderText(gl, sText, vecPosition.x(), vecPosition.y(), 0,
				fTextScaling, RadialHierarchyRenderStyle.LABEL_TEXT_MIN_SIZE);
		textRenderer.flush();
	}

	/**
	 * Sets the properties for rendering text which indirectly sets the size of
	 * the text item.
	 * 
	 * @param textRenderer
	 *            TextRenderer that shall be used for rendering the text.
	 * @param fTextScaling
	 *            Scaling factor of the text.
	 */
	public void setRenderingProperties(GL2 gl, CaleydoTextRenderer textRenderer,
			float fTextScaling) {
		this.textRenderer = textRenderer;
		this.fTextScaling = fTextScaling;

		Rectangle2D bounds = textRenderer.getScaledBounds(gl, sTextForHeightCalculation,
				fTextScaling, RadialHierarchyRenderStyle.LABEL_TEXT_MIN_SIZE);
		fHeight = (float) bounds.getHeight();
		bounds = textRenderer.getScaledBounds(gl, sText, fTextScaling,
				RadialHierarchyRenderStyle.LABEL_TEXT_MIN_SIZE);
		fWidth = (float) bounds.getWidth();
	}

	@Override
	public int getLabelItemType() {
		return LabelItemTypes.LABEL_ITEM_TYPE_TEXT;
	}
}
