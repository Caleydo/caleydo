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
