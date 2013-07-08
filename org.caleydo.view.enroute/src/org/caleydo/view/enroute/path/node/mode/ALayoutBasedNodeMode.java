/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.enroute.path.node.mode;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.view.enroute.path.APathwayPathRenderer;

/**
 * Base class for all nodes that make use of layouts.
 *
 * @author Christian
 *
 */
public abstract class ALayoutBasedNodeMode extends ALinearizeableNodeMode {

	protected LayoutManager layoutManager;

	protected PixelGLConverter pixelGLConverter;

	/**
	 * @param view
	 */
	public ALayoutBasedNodeMode(AGLView view, APathwayPathRenderer pathwayPathRenderer) {
		super(view, pathwayPathRenderer);
		this.pixelGLConverter = view.getPixelGLConverter();
		layoutManager = new LayoutManager(new ViewFrustum(), pixelGLConverter);
		layoutManager.setUseDisplayLists(false);
	}

	@Override
	public void render(GL2 gl, GLU glu) {
		float width = pixelGLConverter.getGLWidthForPixelWidth(node.getWidthPixels());
		float height = pixelGLConverter.getGLHeightForPixelHeight(node.getHeightPixels());

		Vec3f position = node.getPosition();

		gl.glPushMatrix();
		gl.glTranslatef(position.x() - width / 2.0f, position.y() - height / 2.0f, position.z());
		layoutManager.setViewFrustum(new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, width, 0, height, -1, 20));

		layoutManager.render(gl);
		gl.glPopMatrix();
	}

	@Override
	public void destroy() {
		GL2 gl = view.getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2();
		layoutManager.destroy(gl);
		super.destroy();
	}

}
