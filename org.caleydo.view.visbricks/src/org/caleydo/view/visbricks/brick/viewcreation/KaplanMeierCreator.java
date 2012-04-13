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
package org.caleydo.view.visbricks.brick.viewcreation;

import javax.media.opengl.GL2;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.kaplanmeier.GLKaplanMeier;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Creator for a remote rendered {@link GLKaplanMeier}.
 * 
 * @author Alexander Lex
 * 
 */
public class KaplanMeierCreator implements IRemoteViewCreator {

	public KaplanMeierCreator() {
	}

	@Override
	public AGLView createRemoteView(GLBrick remoteRenderingView, GL2 gl,
			GLMouseListener glMouseListener) {

		GLKaplanMeier kaplanMeier = (GLKaplanMeier) GeneralManager
				.get()
				.getViewManager()
				.createGLView(
						GLKaplanMeier.class,
						remoteRenderingView.getParentGLCanvas(),
						remoteRenderingView.getParentComposite(),
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
								-1, 1));

		kaplanMeier.setRemoteRenderingGLView(remoteRenderingView);
		kaplanMeier.setDataContainer(remoteRenderingView.getDataContainer());
		kaplanMeier.setDataDomain(remoteRenderingView.getDataDomain());

		kaplanMeier.initialize();
		kaplanMeier.initRemote(gl, remoteRenderingView, glMouseListener);

		return kaplanMeier;
	}
}
