/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 * 
 */
package org.caleydo.core.view.opengl.util.connectionline;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.List;

import javax.media.opengl.GL2;

/**
 * Renderer for an arrow at the end of a connection line.
 * 
 * @author Christian
 * 
 */
public class LineEndArrowRenderer extends ALineEndRenderer {

	protected AArrowRenderer arrowRenderer;

	/**
	 * @param isLineEnd1
	 */
	public LineEndArrowRenderer(boolean isLineEnd1, AArrowRenderer arrowRenderer) {
		super(isLineEnd1);
		this.arrowRenderer = arrowRenderer;
	}

	@Override
	public void render(GL2 gl, List<Vec3f> linePoints) {
		Vec3f arrowHead = isLineEnd1 ? linePoints.get(0) : linePoints.get(linePoints
				.size() - 1);
		Vec3f prevArrowHeadPoint = isLineEnd1 ? linePoints.get(1) : linePoints
				.get(linePoints.size() - 2);
		Vec2f direction = new Vec2f(arrowHead.x() - prevArrowHeadPoint.x(), arrowHead.y()
				- prevArrowHeadPoint.y());

		arrowRenderer.render(gl, arrowHead, direction);

	}
}
