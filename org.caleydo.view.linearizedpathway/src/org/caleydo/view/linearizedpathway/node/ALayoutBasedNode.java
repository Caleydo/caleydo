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
/**
 * 
 */
package org.caleydo.view.linearizedpathway.node;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;

/**
 * Base class for nodes that make use of layouts.
 * 
 * @author Christian
 * 
 */
public abstract class ALayoutBasedNode extends ANode {

	protected LayoutManager layoutManager;

	/**
	 * @param pixelGLConverter
	 * @param view
	 * @param nodeId
	 */
	public ALayoutBasedNode(PixelGLConverter pixelGLConverter, GLLinearizedPathway view,
			int nodeId) {
		super(pixelGLConverter, view, nodeId);
		layoutManager = new LayoutManager(new ViewFrustum(), pixelGLConverter);
		// ElementLayout baseLayout = setupLayout();
		// layoutManager.setBaseElementLayout(baseLayout);
	}

	// /**
	// * Subclasses are intended to setup their layout within this method, which
	// * is automatically called upon node creation.
	// *
	// * @return The base layout element of the node layout.
	// */
	// protected abstract ElementLayout setupLayout();

	@Override
	public void render(GL2 gl, GLU glu) {
		float width = pixelGLConverter.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter.getGLHeightForPixelHeight(getHeightPixels());

		gl.glPushMatrix();
		gl.glTranslatef(position.x() - width / 2.0f, position.y() - height / 2.0f, 0f);
		// layoutManager.setViewFrustum(new ViewFrustum(
		// ECameraProjectionMode.ORTHOGRAPHIC, x - spacingWidth, x
		// + spacingWidth, y - spacingHeight, y + spacingHeight,
		// -1, 20));
		layoutManager.setViewFrustum(new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC,
				0, width, 0, height, -1, 20));

		layoutManager.render(gl);
		gl.glPopMatrix();

	}

	/**
	 * Sets the base {@link ElementLayout} of the node.
	 * 
	 * @param baseLayout
	 */
	public void setBaseLayout(ElementLayout baseLayout) {
		layoutManager.setBaseElementLayout(baseLayout);
	}

}
