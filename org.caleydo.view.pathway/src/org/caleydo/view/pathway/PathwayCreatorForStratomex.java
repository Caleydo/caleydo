/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.pathway;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.IRemoteViewCreator;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.datadomain.pathway.listener.PathwayMappingEvent;

/**
 * @author Christian
 *
 */
public class PathwayCreatorForStratomex implements IRemoteViewCreator {

	@Override
	public AGLView createRemoteView(AGLView remoteRenderingView, List<TablePerspective> tablePerspectives,
			String embeddingEventSpace) {
		GLPathway pathwayView = (GLPathway) ViewManager.get()
				.createGLView(GLPathway.class, remoteRenderingView.getParentGLCanvas(),
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1, -1, 1));
		TablePerspective tablePerspective = null;
		if (tablePerspectives.size() > 0) {
			tablePerspective = tablePerspectives.get(0);
			if (!(tablePerspective instanceof PathwayTablePerspective)) {
				throw new IllegalArgumentException(
						"The provided table perspective must be of type PathwayTablePerspective.");
			}

			pathwayView.setRemoteRenderingGLView((IGLRemoteRenderingView) remoteRenderingView);
			pathwayView.addTablePerspective(tablePerspective);
		}
		pathwayView.setPathwayPathEventSpace(embeddingEventSpace);
		pathwayView.setMinHeightPixels(120);
		pathwayView.setMinWidthPixels(120);
		pathwayView.setDynamicDetail(true);
		pathwayView.setShowStdDevBars(false);
		// pathwayView.setRenderTemplate(new BrickHeatMapTemplate(heatMap));
		pathwayView.initialize();
		PathwayMappingEvent event = new PathwayMappingEvent(tablePerspective);
		event.to(pathwayView);
		pathwayView.onMapTablePerspective(event);
		return pathwayView;
	}

}
