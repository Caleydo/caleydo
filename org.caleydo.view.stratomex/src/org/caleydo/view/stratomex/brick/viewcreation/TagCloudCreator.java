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

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.stratomex.brick.GLBrick;

/**
 * Creator for a remote rendered {@link GLTagCloud}.
 * 
 * @author Christian Partl
 * 
 */
public class TagCloudCreator implements IRemoteViewCreator {

	public TagCloudCreator() {

	}

	@Override
	public AGLView createRemoteView(GLBrick remoteRenderingView, GL2 gl,
			GLMouseListener glMouseListener) {
//		GLTagCloud tagCloud = (GLTagCloud) GeneralManager
//				.get()
//				.getViewManager()
//				.createGLView(
//						GLTagCloud.class,
//						remoteRenderingView.getParentGLCanvas(),
//						remoteRenderingView.getParentComposite(),
//						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
//								-1, 1));
//
//		tagCloud.setRemoteRenderingGLView(remoteRenderingView);
//		tagCloud.setTablePerspective(remoteRenderingView.getTablePerspective());
//		tagCloud.setDataDomain(remoteRenderingView.getDataDomain());
//		tagCloud.initialize();
//		tagCloud.initRemote(gl, remoteRenderingView, glMouseListener);
//		tagCloud.setDetailLevel(EDetailLevel.LOW);

//		return tagCloud;
		
		return null;
	}

}
