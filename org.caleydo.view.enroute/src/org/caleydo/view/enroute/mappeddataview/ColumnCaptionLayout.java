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
package org.caleydo.view.enroute.mappeddataview;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.enroute.EPickingType;

/**
 * @author alexsb
 *
 */
public class ColumnCaptionLayout extends Column {

	AGLView parentView;
	MappedDataRenderer parent;
	Group group;

	// private static final int abstractModePixelWidth = 40;
	//
	// private float dymanicWidth;

	/**
	 *
	 */
	public ColumnCaptionLayout(AGLView parentView, MappedDataRenderer parent) {
		this.parentView = parentView;
		this.parent = parent;
		this.isBottomUp = false;
		// dymanicWidth = ratioSizeX;

	}

	public void init(Group group, Perspective samplePerspective, ATableBasedDataDomain dataDomain) {
		this.group = group;
		ElementLayout caption = new ElementLayout();
		this.append(caption);
		ColumnCaptionRenderer renderer = new ColumnCaptionRenderer(parentView, parent, group, samplePerspective,
				dataDomain);
		caption.setRenderer(renderer);

		Button button = new Button(EPickingType.SAMPLE_GROUP_VIEW_MODE.name(), group.getID(),
				EIconTextures.ABSTRACT_BAR_ICON);
		ButtonRenderer buttonRender = new ButtonRenderer.Builder(parentView, button).build();

		ElementLayout spacing = new ElementLayout();
		spacing.setPixelSizeY(2);

		append(spacing);
		ElementLayout buttonLayout = new ElementLayout();
		buttonLayout.setPixelSizeX(20);
		buttonLayout.setPixelSizeY(20);
		buttonLayout.setRenderer(buttonRender);
		this.append(buttonLayout);
	}
}
