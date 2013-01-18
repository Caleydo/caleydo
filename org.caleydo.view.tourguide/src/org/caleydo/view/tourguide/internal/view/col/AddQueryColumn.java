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
package org.caleydo.view.tourguide.internal.view.col;

import static org.caleydo.core.view.opengl.layout.ElementLayouts.createButton;

import java.util.List;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.tourguide.api.query.ScoreQuery;
import org.caleydo.view.tourguide.api.query.ScoringElement;
import org.caleydo.view.tourguide.internal.view.ScoreQueryUI;

public class AddQueryColumn extends ATableColumn {
	public AddQueryColumn(AGLView view) {
		super(view);
		this.setPixelSizeX(16);
		this.init();
	}

	@Override
	protected ElementLayout createHeader() {
		return createButton(view, new Button(ScoreQueryUI.ADD_COLUMN, 1, EIconTextures.GROUPER_COLLAPSE_PLUS));
	}

	@Override
	public void setData(List<ScoringElement> data, ScoreQuery query) {
		// nothing todo
	}

	@Override
	protected int getMinWidth() {
		return 16;
	}
}