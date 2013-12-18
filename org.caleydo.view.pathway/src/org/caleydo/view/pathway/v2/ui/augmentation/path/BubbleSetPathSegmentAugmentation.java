/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui.augmentation.path;

import gleem.linalg.Vec2f;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Line;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.util.spline.ITesselatedPolygon;
import org.caleydo.core.view.opengl.util.spline.TesselatedPolygons;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.graph.PathSegment;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
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
public class BubbleSetPathSegmentAugmentation extends GLElement {

	protected IPathwayRepresentation pathwayRepresentation;
	protected boolean initialized = false;
	protected TextureRenderer texRenderer = null;

	protected SetOutline setOutline;
	protected AbstractShapeGenerator shaper;
	protected CanvasComponent bubblesetCanvas;

	protected PathSegment pathSegment;
	protected Color color = new Color();

	public BubbleSetPathSegmentAugmentation(IPathwayRepresentation pathwayRepresentation) {
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
		texRenderer.setSize(1, 1);
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
		if (g.isPickingPass() && pickingID >= 0)
			g.popName();
		for (PathwayVertexRep vertex : pathwayRepresentation.getPathway().vertexSet()) {
			g.fillRect(pathwayRepresentation.getVertexRepBounds(vertex));
		}
		if (g.isPickingPass() && pickingID >= 0)
			g.pushName(pickingID);

		gl.glColorMask(true, true, true, true);
		gl.glDepthMask(true);
		gl.glStencilMask(0x00);
		// draw where stencil's value is 0
		gl.glStencilFunc(GL.GL_EQUAL, 0, 0xFF);

		List<Vec2f> points = calcBubbleSet();
		Rect pathwayBounds = pathwayRepresentation.getPathwayBounds();
		PathwayGraph pathway = pathwayRepresentation.getPathway();

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		gl.glPushMatrix();
		gl.glPushAttrib(GL2.GL_LINE_BIT);

		g.move(pathwayBounds.x(), pathwayBounds.y());
		gl.glScalef(pathwayBounds.width() / pathway.getWidth(), pathwayBounds.height() / pathway.getHeight(), 1f);
		ITesselatedPolygon polygon = TesselatedPolygons.polygon2(points);
		g.incZ();
		g.color(color).fillPolygon(polygon).lineWidth(2).drawPath(polygon);
		g.decZ();

		gl.glPopAttrib();
		gl.glPopMatrix();

		gl.glDisable(GL.GL_STENCIL_TEST);
		// repaint();

	}

	protected List<Vec2f> calcBubbleSet() {

		Rect previousBounds = null;
		List<Rect> positions = new ArrayList<>();
		List<Line> edges = new ArrayList<>();
		// boolean updatedParameters = false;

		for (PathwayVertexRep v : pathSegment) {

			Rect bounds = new Rect(v.getLowerLeftCornerX(), v.getLowerLeftCornerY(), v.getWidth(), v.getHeight());
			positions.add(bounds);

			// if (!updatedParameters) {
			// float scaling = 1; // (bounds.height() / 10f) / v.getHeight();
			// // ((BubbleSet) setOutline).setParameter(1, 1, 1, 0.5, 1.5, 0.5, 0.5, 0.5, 1);
			// ((BubbleSet) setOutline).setParameter(100, 20, 3, scaling * 10.0, scaling * 7.0, scaling * 0.5,
			// scaling * 2.5, scaling * 15.0, 8);
			// updatedParameters = true;
			// }

			if (previousBounds != null) {
				edges.add(new Line(previousBounds.x(), previousBounds.y(), bounds.x(), bounds.y()));
			}
			previousBounds = bounds;
		}
		setGroup(positions, edges, color);
		return getOutlinePoints();

		// Rect previousBounds = null;
		// List<Rect> positions = new ArrayList<>();
		// List<Line> edges = new ArrayList<>();
		// boolean updatedParameters = false;
		//
		// for (PathwayVertexRep v : pathSegment) {
		// Rect vertexBounds = pathwayRepresentation.getVertexRepBounds(v);
		// Rect bounds = new Rect(vertexBounds.x() + vertexBounds.width() / 20.0f, vertexBounds.y()
		// + vertexBounds.height() / 20.0f, vertexBounds.width() / 10.0f, vertexBounds.height() / 10.0f);
		// positions.add(bounds);
		//
		// if (!updatedParameters) {
		// float scaling = (bounds.height() / 10f) / v.getHeight();
		// // ((BubbleSet) setOutline).setParameter(1, 1, 1, 0.5, 1.5, 0.5, 0.5, 0.5, 1);
		// ((BubbleSet) setOutline).setParameter(100, 20, 3, scaling * 10.0, scaling * 7.0, scaling * 0.5,
		// scaling * 2.5, scaling * 15.0, 8);
		// updatedParameters = true;
		// }
		//
		// if (previousBounds != null) {
		// edges.add(new Line(previousBounds.x() + previousBounds.width() / 2.0f, previousBounds.y()
		// + previousBounds.height() / 2.0f, bounds.x() + bounds.width() / 2.0f, bounds.y()
		// + bounds.height() / 2.0f));
		// }
		// previousBounds = bounds;
		// }
		// setGroup(positions, edges, color);
		// return getOutlinePoints();
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		renderImpl(g, w, h);
	}

	protected void setGroup(List<Rect> positions, List<Line> edges, Color color) {
		if (positions == null || positions.isEmpty())
			return;

		bubblesetCanvas.removeAllGroups();
		// new group
		bubblesetCanvas.addGroup(color.getAWTColor(), 2, true);

		for (Rect item : positions) {
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

	/**
	 * @param pathSegment
	 *            setter, see {@link pathSegment}
	 */
	public void setPathSegment(PathSegment pathSegment) {
		this.pathSegment = pathSegment;
		repaintAll();
	}

	/**
	 * @return the pathSegment, see {@link #pathSegment}
	 */
	public PathSegment getPathSegment() {
		return pathSegment;
	}

	/**
	 * @param color
	 *            setter, see {@link color}
	 */
	public void setColor(Color color) {
		this.color = color;
		repaint();
	}

}
