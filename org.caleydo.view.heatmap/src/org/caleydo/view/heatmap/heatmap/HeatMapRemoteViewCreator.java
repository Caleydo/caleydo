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
package org.caleydo.view.heatmap.heatmap;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.IRemoteViewCreator;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.view.heatmap.heatmap.template.BrickHeatMapLayoutConfiguration;

/**
 * Creator for a remote rendered {@link GLHeatMap}.
 *
 * @author Christian Partl
 *
 */
public class HeatMapRemoteViewCreator implements IRemoteViewCreator {

	public HeatMapRemoteViewCreator() {
	}

	@Override
	public AGLView createRemoteView(AGLView remoteRenderingView, List<TablePerspective> tablePerspectives) {
		GLHeatMap heatMap = (GLHeatMap) GeneralManager
				.get()
				.getViewManager()
				.createGLView(GLHeatMap.class, remoteRenderingView.getParentGLCanvas(),
						remoteRenderingView.getParentComposite(),
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1, -1, 1));

		heatMap.setRemoteRenderingGLView((IGLRemoteRenderingView) remoteRenderingView);
		if (tablePerspectives.size() > 0) {
			TablePerspective tablePerspective = tablePerspectives.get(0);
			heatMap.setTablePerspective(tablePerspective);
			heatMap.setDataDomain(tablePerspective.getDataDomain());
			BrickHeatMapLayoutConfiguration template = new BrickHeatMapLayoutConfiguration(heatMap);
			heatMap.setRenderTemplate(template);
		}
		heatMap.initialize();

		return heatMap;
	}
}
