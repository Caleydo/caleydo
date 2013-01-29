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
package org.caleydo.core.view;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;

/**
 * Interface for creators of remotely rendered {@link ALayoutRenderer}s.
 *
 * @author Christian Partl
 *
 */
public interface IRemoteRendererCreator {
	/**
	 * Subclasses shall create an object of the View to be created.
	 * 
	 * @param remoteRenderingView
	 *            View that remote-renders the created renderer.
	 * @param tablePerspectives
	 *            List of {@link TablePerspective} objects that shall be displayed in the renderer.
	 * @return Instance of the renderer.
	 */
	public ALayoutRenderer createRemoteView(AGLView remoteRenderingView, List<TablePerspective> tablePerspectives);
}
