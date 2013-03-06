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
package org.caleydo.view.subgraph;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.IRemoteRendererCreator;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;

/**
 * @author Christian
 *
 */
public class Level4Creator implements IRemoteRendererCreator {

	@Override
	public ALayoutRenderer createRemoteView(AGLView remoteRenderingView, List<TablePerspective> tablePerspectives,
			String embeddingEventSpace) {

		if (tablePerspectives.size() > 0) {
			TablePerspective tablePerspective = tablePerspectives.get(0);
			if (!(tablePerspective instanceof PathwayTablePerspective)) {
				throw new IllegalArgumentException(
						"The provided table perspective must be of type PathwayTablePerspective.");
			}

			return new Level4PathwayRenderer(((PathwayTablePerspective) tablePerspective).getPathway());
		}
		return null;
	}

}
