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
package org.caleydo.view.heatmap.v2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;

/**
 * @author Samuel Gratzl
 *
 */
public class BasicBlockRenderer implements IBlockRenderer {
	public static final BasicBlockRenderer INSTANCE = new BasicBlockRenderer();

	private BasicBlockRenderer() {

	}
	@Override
	public void render(GLGraphics g, int recordID, int dimensionID, ATableBasedDataDomain dataDomain, Rect bounds, boolean deSelected) {
		float value = dataDomain.getTable().getNormalizedValue(dimensionID, recordID);
		float[] color = dataDomain.getColorMapper().getColor(value);
		float opacity = deSelected ? 0.3f : 1.0f;

		g.color(color[0], color[1], color[2], opacity);
		g.fillRect(bounds);
	}

}
