/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.kaplanmeier;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.IRemoteViewCreator;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;

/**
 * Creator for a remote rendered {@link GLKaplanMeier}.
 *
 * @author Christian Partl
 *
 */
public class KaplanMeierRemoteViewCreator implements IRemoteViewCreator {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.IRemoteViewCreator#createRemoteView(org.caleydo.core.view.opengl.canvas.AGLView,
	 * java.util.List)
	 */
	@Override
	public AGLView createRemoteView(AGLView remoteRenderingView, List<TablePerspective> tablePerspectives,
			String embeddingEventSpace) {

		GLKaplanMeier kaplanMeier = (GLKaplanMeier) GeneralManager
				.get()
				.getViewManager()
				.createGLView(GLKaplanMeier.class, remoteRenderingView.getParentGLCanvas(),
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1, -1, 1));
		if (tablePerspectives.size() > 0) {
			TablePerspective tablePerspective = tablePerspectives.get(0);
			kaplanMeier.setRemoteRenderingGLView((IGLRemoteRenderingView) remoteRenderingView);
			kaplanMeier.setTablePerspective(tablePerspective);
			kaplanMeier.setDataDomain(tablePerspective.getDataDomain());
			float maxTimeValue = 0;
			if (tablePerspective.getParentTablePerspective() != null) {
				maxTimeValue = GLKaplanMeier.calculateMaxAxisTime(tablePerspective.getParentTablePerspective());
			} else {
				maxTimeValue = GLKaplanMeier.calculateMaxAxisTime(tablePerspective);
			}
			kaplanMeier.setMaxAxisTime(maxTimeValue);
		}

		kaplanMeier.initialize();

		return kaplanMeier;
	}
}
