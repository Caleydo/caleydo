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

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.enroute.path.node.ALinearizableNode;

/**
 * Renders a vertical path of nodes with constant spacings. No branches are rendered.
 *
 * @author Christian Partl
 *
 */
public class VerticalPathRenderer extends APathwayPathRenderer {

	// protected static final int TOP_SPACING_PIXELS = 60;
	// protected static final int BOTTOM_SPACING_PIXELS = 60;
	// protected static final int NODE_SPACING_PIXELS = 60;
	// public final static int PATHWAY_TITLE_COLUMN_WIDTH_PIXELS = 20;
	// protected final static int PATHWAY_TITLE_TEXT_HEIGHT_PIXELS = 16;

	protected boolean isPathSelection = false;

	/**
	 * @param view
	 * @param tablePerspectives
	 */
	public VerticalPathRenderer(AGLView view, List<TablePerspective> tablePerspectives, boolean isPathSelectable) {
		super(view, tablePerspectives, isPathSelectable);
		minWidthPixels = sizeConfig.rectangleNodeWidth + 2 * sizeConfig.pathwayTitleAreaWidth;
	}

	@Override
	protected void updateLayout() {

		float currentPositionY = y - pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.pathStartSpacing);
		float nodeSpacing = pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.minNodeSpacing);

		if (pathNodes.size() == 0) {
			minHeightPixels = 0;
		} else {
			minHeightPixels = sizeConfig.pathStartSpacing + sizeConfig.pathEndSpacing + (pathNodes.size() - 1)
					* sizeConfig.minNodeSpacing;
		}
		if (pixelGLConverter.getGLHeightForPixelHeight(minHeightPixels) > y) {
			nodeSpacing = Math.max(
					(y - pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.pathStartSpacing
							+ sizeConfig.pathEndSpacing))
							/ (pathNodes.size() - 1),
					pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.rectangleNodeHeight));
		}

		for (ALinearizableNode node : pathNodes) {
			node.setPosition(new Vec3f(x / 2.0f, currentPositionY, 0));
			currentPositionY -= nodeSpacing;
		}

	}

	@Override
	protected void renderContent(GL2 gl) {
		GLU glu = new GLU();

		renderPathwayBorders(gl);

		for (int i = 0; i < pathNodes.size(); i++) {
			ALinearizableNode node = pathNodes.get(i);

			node.render(gl, glu);
		}

		renderEdges(gl, pathNodes);
	}

	protected void renderPathwayBorders(GL2 gl) {

		float topPathwayTitleLimit = y - pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.pathStartSpacing);
		float halfBorderWidth = pixelGLConverter
				.getGLHeightForPixelHeight((int) (sizeConfig.pathwayBorderWidth / 2.0f));
		int segmentIndex = 0;
		for (int i = 0; i < pathNodes.size(); i++) {
			ALinearizableNode node = pathNodes.get(i);

			if (node.getVertexReps().size() > 1) {
				float nodePositionY = node.getPosition().y();

				gl.glColor3f(0.95f, 0.95f, 0.95f);
				gl.glBegin(GL2.GL_QUADS);

				gl.glVertex3f(0, nodePositionY - halfBorderWidth, 0);
				gl.glVertex3f(x, nodePositionY - halfBorderWidth, 0);
				// gl.glColor3f(1f, 1f, 1f);
				gl.glVertex3f(x, nodePositionY + halfBorderWidth, 0);
				gl.glVertex3f(0, nodePositionY + halfBorderWidth, 0);
				gl.glEnd();

				gl.glColor3f(0.5f, 0.5f, 0.5f);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(x, nodePositionY + halfBorderWidth, 0);
				gl.glVertex3f(0, nodePositionY + halfBorderWidth, 0);
				gl.glVertex3f(x, nodePositionY - halfBorderWidth, 0);
				gl.glVertex3f(0, nodePositionY - halfBorderWidth, 0);
				gl.glEnd();

				renderPathwayTitle(gl, segmentIndex, topPathwayTitleLimit, nodePositionY);
				// gl.glPopMatrix();

				topPathwayTitleLimit = nodePositionY;
				segmentIndex++;
			}
			if (segmentIndex == pathSegments.size() - 1) {
				renderPathwayTitle(gl, segmentIndex, topPathwayTitleLimit,
						y - pixelGLConverter.getGLHeightForPixelHeight(minHeightPixels));
				segmentIndex++;
			}
		}
	}

	private void renderPathwayTitle(GL2 gl, int pathSegmentIndex, float topPathwayTitleLimit,
			float bottomPathwayTitleLimit) {
		float pathwayTitlePositionX = pixelGLConverter.getGLWidthForPixelWidth(sizeConfig.pathwayTitleAreaWidth
				- (int) ((sizeConfig.pathwayTitleAreaWidth - sizeConfig.pathwayTextHeight) / 2.0f));
		float pathwayTitleTextHeight = pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.pathwayTextHeight);
		String text = pathSegments.get(pathSegmentIndex).get(0).getPathway().getTitle();
		float maxTextWidth = topPathwayTitleLimit - bottomPathwayTitleLimit;
		float requiredTextWidth = textRenderer.getRequiredTextWidthWithMax(text, pathwayTitleTextHeight, maxTextWidth);
		gl.glPushMatrix();
		gl.glTranslatef(pathwayTitlePositionX, bottomPathwayTitleLimit + (maxTextWidth - requiredTextWidth) / 2.0f, 0);
		gl.glRotatef(90, 0, 0, 1);
		// gl.glColor3f(0, 0, 0);
		// textRenderer.setColor(0, 0, 0, 1);
		textRenderer.renderTextInBounds(gl, text, 0, 0, 0, maxTextWidth, pathwayTitleTextHeight);
		gl.glPopMatrix();
	}

	protected void renderEdges(GL2 gl, List<ALinearizableNode> pathNodes) {
		for (int i = 0; i < pathNodes.size() - 1; i++) {
			ALinearizableNode node1 = pathNodes.get(i);
			ALinearizableNode node2 = pathNodes.get(i + 1);
			EdgeRenderUtil.renderEdge(gl, node1, node2, node1.getBottomConnectionPoint(),
					node2.getTopConnectionPoint(), 0.2f, true, pixelGLConverter, textRenderer, sizeConfig);
		}
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}

}
