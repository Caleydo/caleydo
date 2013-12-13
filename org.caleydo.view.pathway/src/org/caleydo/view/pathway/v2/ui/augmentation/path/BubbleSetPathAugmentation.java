/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui.augmentation.path;

import gleem.linalg.Vec2f;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Line;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.util.spline.ITesselatedPolygon;
import org.caleydo.core.view.opengl.util.spline.TesselatedPolygons;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

import setvis.SetOutline;
import setvis.bubbleset.BubbleSet;
import setvis.gui.CanvasComponent;
import setvis.shape.AbstractShapeGenerator;
import setvis.shape.BSplineShapeGenerator;

import com.jogamp.opengl.util.awt.TextureRenderer;

/**
 * @author Christian
 *
 */
public class BubbleSetPathAugmentation extends GLElement {

	protected IPathwayRepresentation pathwayRepresentation;
	protected boolean initialized = false;
	protected TextureRenderer texRenderer = null;

	protected SetOutline setOutline;
	protected AbstractShapeGenerator shaper;
	protected CanvasComponent bubblesetCanvas;

	public BubbleSetPathAugmentation(IPathwayRepresentation pathwayRepresentation) {
		this.pathwayRepresentation = pathwayRepresentation;
		setOutline = new BubbleSet(100, 20, 3, 10.0, 7.0, 0.5, 2.5, 15.0, 8);
		((BubbleSet) setOutline).useVirtualEdges(false);
		shaper = new BSplineShapeGenerator(setOutline);
		bubblesetCanvas = new CanvasComponent(shaper);
		bubblesetCanvas.setDefaultView();

		setVisibility(EVisibility.PICKABLE);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (!initialized) {
			texRenderer = new TextureRenderer((int) w, (int) h, true);
			initialized = true;
		}
		texRenderer.setSize((int) w, (int) h);
		PathwayGraph pathway = pathwayRepresentation.getPathway();
		Set<PathwayVertexRep> vertexReps = pathway.vertexSet();
		PathwayVertexRep v = null;
		for (PathwayVertexRep vertex : vertexReps) {
			if (vertex.getType() == EPathwayVertexType.map) {
				v = vertex;
			}
		}
		GL2 gl = g.gl;

		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glColorMask(false, false, false, false);
		gl.glDepthMask(false);
		gl.glStencilFunc(GL.GL_NEVER, 1, 0xFF);
		gl.glStencilOp(GL.GL_REPLACE, GL.GL_KEEP, GL.GL_KEEP); // draw 1s on test fail (always)

		// draw stencil pattern
		gl.glStencilMask(0xFF);
		gl.glClear(GL.GL_STENCIL_BUFFER_BIT); // needs mask=0xFF

		for (PathwayVertexRep vertex : vertexReps) {
			g.fillRect(pathwayRepresentation.getVertexRepBounds(vertex));
		}

		gl.glColorMask(true, true, true, true);
		gl.glDepthMask(true);
		gl.glStencilMask(0x00);
		// draw where stencil's value is 0
		gl.glStencilFunc(GL.GL_EQUAL, 0, 0xFF);
		Rect vertexBounds = pathwayRepresentation.getVertexRepBounds(v);

		Rect bounds = new Rect(vertexBounds.x() + vertexBounds.width() / 2.0f, vertexBounds.y() + vertexBounds.height()
				/ 2.0f, vertexBounds.width(), vertexBounds.height());
		Color selColor = SelectionType.SELECTION.getColor();
		Color color = new Color(selColor.r, selColor.g, selColor.b, 0.5f);

		setGroup(Arrays.asList(bounds), null, SelectionType.SELECTION.getColor());

		List<Vec2f> points = getOutlinePoints();
		ITesselatedPolygon polygon = TesselatedPolygons.polygon2(points);
		g.color(color).fillPolygon(polygon);

		gl.glDisable(GL.GL_STENCIL_TEST);
		// repaint();

	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		renderImpl(g, w, h);
	}

	protected void setGroup(List<Rect> items, List<Line> edges, Color color) {
		if (items == null)
			return;

		bubblesetCanvas.removeAllGroups();
		// new group
		bubblesetCanvas.addGroup(color.getAWTColor(), 2, true);

		for (Rect item : items) {
			bubblesetCanvas.addItem(0, item.x(), item.y(), item.width(), item.height());
		}
		if (edges != null) {
			for (Line edge : edges) {
				bubblesetCanvas.addEdge(0, edge.x1(), edge.y1(), edge.x2(), edge.y2());
			}
		}
	}

	protected List<Vec2f> getOutlinePoints() {
		List<Vec2f> points = new ArrayList<>();
		Graphics2D g2d = texRenderer.createGraphics();
		points = bubblesetCanvas.getShapePoints(g2d);
		g2d.dispose();
		return points;
	}

}
