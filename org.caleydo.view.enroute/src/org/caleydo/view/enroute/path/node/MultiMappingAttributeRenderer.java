/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.path.node;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.enroute.path.APathwayPathRenderer;

/**
 * @author Christian
 *
 */
public class MultiMappingAttributeRenderer extends ANodeAttributeRenderer {

	private static final int GLYPH_SIZE = 8;

	/**
	 * @param view
	 * @param node
	 * @param pathwayPathRenderer
	 */
	public MultiMappingAttributeRenderer(AGLView view, ANode node, APathwayPathRenderer pathwayPathRenderer) {
		super(view, node, pathwayPathRenderer);
	}

	@Override
	public void render(GL2 gl) {
		Vec3f nodePos = node.getPosition();
		float nodeHeight = node.getHeight();
		float nodeWidth = node.getWidth();
		float size = pixelGLConverter.getGLHeightForGLWidth(GLYPH_SIZE);
		gl.glColor4f(0.3f, 0.3f, 0.3f, 1f);
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glVertex3f(nodePos.x() - nodeWidth / 2.0f, nodePos.y() + nodeHeight / 2.0f, nodePos.z() + 0.1f);
		gl.glVertex3f(nodePos.x() - (nodeWidth / 2.0f) + size, nodePos.y() + nodeHeight / 2.0f, nodePos.z() + 0.1f);
		gl.glVertex3f(nodePos.x() - nodeWidth / 2.0f, nodePos.y() + nodeHeight / 2.0f - size,
				nodePos.z() + 0.1f);
		gl.glEnd();
	}

	@Override
	protected void registerPickingListeners() {
	}

	@Override
	public void unregisterPickingListeners() {

	}

}
