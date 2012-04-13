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
package org.caleydo.view.visbricks.brick.ui;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.view.visbricks.brick.ViewLabelProvider;

/**
 * Renderer for a line of text for kaplan meier summary.
 * 
 * @author Marc Streit
 * 
 */
public class KaplanMeierSummaryRenderer extends LabelRenderer {

	public KaplanMeierSummaryRenderer(AGLView view, String caption, String pickingType,
			int id) {
		super(view, new ViewLabelProvider(view), pickingType, id);
	}

	@Override
	public int getMinHeightPixels() {
		return 20;
	}

	@Override
	public int getMinWidthPixels() {
		return 110;
	}

}
