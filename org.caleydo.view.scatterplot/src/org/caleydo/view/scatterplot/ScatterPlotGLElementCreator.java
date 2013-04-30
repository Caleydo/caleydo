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
package org.caleydo.view.scatterplot;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.ARemoteGLElementCreator;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.scatterplot.utils.ScatterplotDataUtils;

/**
 * @author Samuel Gratzl
 *
 */
public class ScatterPlotGLElementCreator extends ARemoteGLElementCreator {
	@Override
	protected GLElement create(List<TablePerspective> tablePerspectives) {
		if (tablePerspectives.isEmpty())
			return null;
		else
		{
			GLElement element = new ScatterplotElement(tablePerspectives.get(0), ScatterplotDataUtils.buildDefaultDataSelection());
			return element;
		}
		
	}

}

