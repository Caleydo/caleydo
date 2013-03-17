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
package org.caleydo.view.subgraph;

import gleem.linalg.Vec2f;

import java.awt.geom.Rectangle2D;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.ShowNodeContextEvent;
import org.caleydo.view.subgraph.GLSubGraph.PathwayMultiFormInfo;

/**
 * Renders a single Link as augmentation.
 *
 * @author Christian Partl
 *
 */

// TODO: cleanup and refactor
public class LinkRenderer extends PickableGLElement {

	protected final Rectangle2D loc1;
	protected final Rectangle2D loc2;
	protected final PathwayMultiFormInfo info1;
	protected final PathwayMultiFormInfo info2;
	protected final boolean isLocation1Window;
	protected final boolean isLocation2Window;
	protected final float stubSize;
	protected final boolean drawLink;
	protected final boolean isContextLink;
	protected final boolean isPathLink;
	protected final PathwayVertexRep vertexRep1;
	protected final PathwayVertexRep vertexRep2;
	protected final GLSubGraph view;

	public LinkRenderer(GLSubGraph view, boolean drawLink, Rectangle2D loc1, Rectangle2D loc2,
			PathwayMultiFormInfo info1, PathwayMultiFormInfo info2, float stubSize, boolean isLocation1Window,
			boolean isLocation2Window, boolean isContextLink, boolean isPathLink, PathwayVertexRep vertexRep1,
			PathwayVertexRep vertexRep2) {
		this.drawLink = drawLink;
		this.loc1 = loc1;
		this.loc2 = loc2;
		this.info1 = info1;
		this.info2 = info2;
		this.stubSize = stubSize;
		this.isLocation1Window = isLocation1Window;
		this.isLocation2Window = isLocation2Window;
		this.isContextLink = isContextLink;
		this.isPathLink = isPathLink;
		this.vertexRep1 = vertexRep1;
		this.vertexRep2 = vertexRep2;
		this.view = view;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		render(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		render(g, w, h);
	}

	public void render(GLGraphics g, float w, float h) {
		g.incZ(0.5f);
		// g.gl.glBegin(GL.GL_LINES);
		// g.color(1, 0, 0, 1);
		// g.gl.glVertex2f((float) loc1.getCenterX(), (float) loc1.getCenterY());
		// g.color(1, 0, 0, 0);
		// g.gl.glVertex2f((float) loc2.getCenterX(), (float) loc2.getCenterY());
		// g.gl.glEnd();
		if (isPathLink) {
			g.color(0, 1, 0, 1f);
		} else if (isContextLink) {
			g.color(1, 0, 1, 1f);
		} else {
			g.color(1, 0, 0, 1f);
		}
		g.lineWidth(2);

		if (!drawLink) {
			Vec2f direction = new Vec2f((float) loc1.getCenterX() - (float) loc2.getCenterX(),
					(float) loc1.getCenterY() - (float) loc2.getCenterY());
			direction.normalize();
			if (!isLocation1Window) {
				Vec2f stub1End = new Vec2f((float) loc1.getCenterX() - 20 * direction.x() * stubSize,
						(float) loc1.getCenterY() - 20 * direction.y() * stubSize);
				g.drawLine((float) loc1.getCenterX(), (float) loc1.getCenterY(), stub1End.x(), stub1End.y());
			}
			if (!isLocation2Window) {
				Vec2f stub2End = new Vec2f((float) loc2.getCenterX() + 20 * direction.x() * stubSize,
						(float) loc2.getCenterY() + 20 * direction.y() * stubSize);
				g.drawLine((float) loc2.getCenterX(), (float) loc2.getCenterY(), stub2End.x(), stub2End.y());
			}
		} else {
			g.drawLine((float) loc1.getCenterX(), (float) loc1.getCenterY(), (float) loc2.getCenterX(),
					(float) loc2.getCenterY());
		}
		g.drawRect((float) loc1.getX(), (float) loc1.getY(), (float) loc1.getWidth(), (float) loc1.getHeight());
		g.drawRect((float) loc2.getX(), (float) loc2.getY(), (float) loc2.getWidth(), (float) loc2.getHeight());
		g.lineWidth(1);
		// // g.color(0, 1, 0, 1).fillCircle((float) loc1.getX(), (float) loc1.getY(), 50);
		// // g.color(0, 1, 0, 1).fillCircle((float) loc2.getX(), (float) loc2.getY(), 50);
		g.incZ(-0.5f);
	}

	@Override
	protected void onMouseOver(Pick pick) {
		// TODO: reset fade timer
	}

	@Override
	protected void onClicked(Pick pick) {
		promote(info1);
		promote(info2);
		ShowNodeContextEvent event = new ShowNodeContextEvent(vertexRep1);
		event.setEventSpace(view.getPathEventSpace());
		EventPublisher.INSTANCE.triggerEvent(event);
	}

	private void promote(PathwayMultiFormInfo info) {
		if (info.getCurrentEmbeddingID() == EEmbeddingID.PATHWAY_LEVEL3
				|| info.getCurrentEmbeddingID() == EEmbeddingID.PATHWAY_LEVEL4) {
			info.multiFormRenderer.setActive(info.embeddingIDToRendererIDs.get(EEmbeddingID.PATHWAY_LEVEL2).get(0));
			info.age = GLSubGraph.currentPathwayAge--;
			view.lastUsedRenderer = info.multiFormRenderer;
		} else if (info.getCurrentEmbeddingID() == EEmbeddingID.PATHWAY_LEVEL1) {
			view.lastUsedLevel1Renderer = info.multiFormRenderer;
		}
	}
}