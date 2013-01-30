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
						remoteRenderingView.getParentComposite(),
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
