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

import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * @author alexsb
 * 
 */
public class ColumnCaptionRenderer extends SelectableRenderer {

	private Group group;
	private CaleydoTextRenderer textRenderer;
	private PixelGLConverter pixelGLConverter;
	private String label;

	public ColumnCaptionRenderer(AGLView parentView, MappedDataRenderer parent,
			Group group) {
		super(parentView, parent);
		this.textRenderer = parentView.getTextRenderer();
		this.pixelGLConverter = parentView.getPixelGLConverter();
		this.group = group;
		this.label = group.getClusterNode().getLabel();
		
		topBarColor = MappedDataRenderer.CAPTION_BACKGROUND_COLOR;
		bottomBarColor = topBarColor;
	}

	@Override
	public void render(GL2 gl) {
		// float sideSpacing = pixelGLConverter.getGLWidthForPixelWidth(8);
		float sideSpacing = 0;

		float height = pixelGLConverter.getGLHeightForPixelHeight(15);

		float backgroundZ = 0;

		ArrayList<SelectionType> selectionTypes = parent.sampleGroupSelectionManager
				.getSelectionTypes(group.getID());
		calculateColors(selectionTypes);

		gl.glPushName(parentView.getPickingManager().getPickingID(parentView.getID(),
				PickingType.SAMPLE_GROUP.name(), group.getID()));
		gl.glColor4fv(topBarColor, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, backgroundZ);
		gl.glVertex3f(x, 0, backgroundZ);
		gl.glColor4fv(bottomBarColor, 0);
		gl.glVertex3f(x, y, backgroundZ);
		gl.glVertex3f(0, y, backgroundZ);
		
		gl.glEnd();
		gl.glPopName();

		float width = textRenderer.getRequiredTextWidth(label, height);

		float textXOffset = sideSpacing;

		if (width < x) {
			textXOffset = (x - width) / 2;
		}

		textRenderer.renderTextInBounds(gl, label, textXOffset, (y - height) / 2, 0.1f,
				x, height);

	}

	protected void calculateColors(ArrayList<SelectionType> selectionTypes) {

		if (selectionTypes.size() != 0
				&& !selectionTypes.get(0).equals(SelectionType.NORMAL)) {
			topBarColor = selectionTypes.get(0).getColor();

			if (selectionTypes.size() > 1
					&& !selectionTypes.get(1).equals(SelectionType.NORMAL)) {
				bottomBarColor = selectionTypes.get(1).getColor();
			} else {
				bottomBarColor = topBarColor;
			}
		}
	}
}
