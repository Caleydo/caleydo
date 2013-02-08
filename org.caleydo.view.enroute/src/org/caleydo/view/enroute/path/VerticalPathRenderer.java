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
package org.caleydo.view.enroute.path;

import gleem.linalg.Vec3f;

import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.ANode;

/**
 * Renders a vertical path of nodes with constant spacings. No branches are rendered.
 *
 * @author Christian Partl
 *
 */
public class VerticalPathRenderer extends APathwayPathRenderer {

	protected static final int TOP_SPACING_PIXELS = 20;
	protected static final int BOTTOM_SPACING_PIXELS = 20;
	protected static final int NODE_SPACING_PIXELS = 60;

	/**
	 * @param view
	 * @param tablePerspectives
	 */
	public VerticalPathRenderer(AGLView view, List<TablePerspective> tablePerspectives) {
		super(view, tablePerspectives);
		minWidthPixels = ANode.DEFAULT_WIDTH_PIXELS;
	}

	@Override
	protected void updateLayout() {

		float currentPositionY = y - pixelGLConverter.getGLHeightForPixelHeight(TOP_SPACING_PIXELS);
		float nodeSpacing = pixelGLConverter.getGLHeightForPixelHeight(NODE_SPACING_PIXELS);

		for (ALinearizableNode node : pathNodes) {
			node.setPosition(new Vec3f(x / 2.0f, currentPositionY, 0));
			currentPositionY -= nodeSpacing;
		}
		if (pathNodes.size() == 0) {
			minHeightPixels = 0;
		} else {
			minHeightPixels = TOP_SPACING_PIXELS + BOTTOM_SPACING_PIXELS + (pathNodes.size() - 1) * NODE_SPACING_PIXELS;
		}
	}

	@Override
	protected void renderContent(GL2 gl) {
		GLU glu = new GLU();

		for (int i = 0; i < pathNodes.size(); i++) {
			ALinearizableNode node = pathNodes.get(i);
			node.render(gl, glu);
		}

		renderEdges(gl, pathNodes);
	}

	private void renderEdges(GL2 gl, List<ALinearizableNode> pathNodes) {
		for (int i = 0; i < pathNodes.size() - 1; i++) {
			ALinearizableNode node1 = pathNodes.get(i);
			ALinearizableNode node2 = pathNodes.get(i + 1);
			EdgeRenderUtil.renderEdge(gl, node1, node2, node1.getBottomConnectionPoint(),
					node2.getTopConnectionPoint(), 0.2f, true, pixelGLConverter, textRenderer);
		}
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}

}
