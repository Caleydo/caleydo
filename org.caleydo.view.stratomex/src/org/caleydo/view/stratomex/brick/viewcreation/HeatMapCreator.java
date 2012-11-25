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
package org.caleydo.view.stratomex.brick.viewcreation;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.BrickHeatMapLayoutConfiguration;
import org.caleydo.view.stratomex.brick.GLBrick;

/**
 * Creator for a remote rendered {@link GLHeatMap}.
 * 
 * @author Christian Partl
 * 
 */
public class HeatMapCreator implements IRemoteViewCreator {

	public HeatMapCreator() {
	}

	@Override
	public AGLView createRemoteView(GLBrick remoteRenderingView, GL2 gl,
			GLMouseListener glMouseListener) {

		GLHeatMap heatMap = (GLHeatMap) GeneralManager
				.get()
				.getViewManager()
				.createGLView(
						GLHeatMap.class,
						remoteRenderingView.getParentGLCanvas(),
						remoteRenderingView.getParentComposite(),
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
								-1, 1));

		heatMap.setRemoteRenderingGLView(remoteRenderingView);
		heatMap.setTablePerspective(remoteRenderingView.getTablePerspective());
		heatMap.setDataDomain(remoteRenderingView.getDataDomain());
		BrickHeatMapLayoutConfiguration template = new BrickHeatMapLayoutConfiguration(heatMap);
		heatMap.setRenderTemplate(template);
		heatMap.initialize();
		heatMap.initRemote(gl, remoteRenderingView, glMouseListener);

		return heatMap;
	}
}
