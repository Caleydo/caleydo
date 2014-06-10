/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.heatmap;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.IRemoteViewCreator;
import org.caleydo.core.view.ViewManager;
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
	public AGLView createRemoteView(AGLView remoteRenderingView, List<TablePerspective> tablePerspectives,
			String embeddingEventSpace) {
		GeneralManager r = GeneralManager
						.get();
		GLHeatMap heatMap = (GLHeatMap) ViewManager.get()
				.createGLView(GLHeatMap.class, remoteRenderingView.getParentGLCanvas(),
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
