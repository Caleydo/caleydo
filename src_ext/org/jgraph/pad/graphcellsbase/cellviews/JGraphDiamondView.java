/*
 * @(#)JGraphDiamondView.java 1.1 14-JAN-2005
 *
 * Copyright (c) 2001-2005, Gaudenz Alder
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package org.jgraph.pad.graphcellsbase.cellviews;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;

import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

/**
 * Creates a diamond shaped graph cell. Correctly calculates perimeter, and
 * manage a shape with different height and width parameters.
 *
 */
public class JGraphDiamondView extends VertexView {

	public static transient JGraphDiamondRenderer renderer = new JGraphDiamondRenderer();

	public JGraphDiamondView() {
		super();
	}

	public JGraphDiamondView(Object cell) {
		super(cell);
	}
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		Point2D center = AbstractCellView.getCenterPoint(this);
		double halfwidth = getBounds().getWidth() / 2;
		double halfheight = getBounds().getHeight() / 2;
		Point2D top = new Point2D.Double(center.getX(), center.getY() - halfheight);
		Point2D bottom = new Point2D.Double(center.getX(), center.getY() + halfheight);
		Point2D left = new Point2D.Double(center.getX() - halfwidth, center.getY());
 		Point2D right = new Point2D.Double(center.getX() + halfwidth, center.getY());
		// Special case for intersecting the diamond's points
		if (center.getX() == p.getX()) {
			if (center.getY() > p.getY()) // top point
				return (top);
			return bottom;
		}
		if (center.getY() == p.getY()) {
			if (center.getX() > p.getX()) // left point
				return (left);
				// right point
			return right;
		}
		// In which quadrant will the intersection be?
		// set the slope and offset of the border line accordingly
		Point2D i;
		if (p.getX() < center.getX())
			if (p.getY() < center.getY())
				i = intersection(p, center, top, left);
			else
				i = intersection(p, center, bottom, left);
		else if (p.getY() < center.getY())
			i = intersection(p, center, top, right);
		else
			i = intersection(p, center, bottom, right);
		return i;
	}

	/**
	 * Find the point of intersection of two straight lines (which follow the
	 * equation y=mx+b) one line is an incoming edge and the other is one side
	 * of the diamond.
	 */
	private Point2D intersection(Point2D lineOneStart, Point2D lineOneEnd,
									Point2D lineTwoStart, Point2D lineTwoEnd) {
		// m = delta y / delta x, the slope of a line
		// b = y - mx, the axis intercept
		double m1 = (lineOneEnd.getY() - lineOneStart.getY())
					/ (lineOneEnd.getX() - lineOneStart.getX());
		double b1 = lineOneStart.getY() - m1 * lineOneStart.getX();
		double m2 = (lineTwoEnd.getY() - lineTwoStart.getY())
					/ (lineTwoEnd.getX() - lineTwoStart.getX());
		double b2 = lineTwoStart.getY() - m2 * lineTwoStart.getX();
		double xinter = (b1 - b2) / (m2 - m1);
		double yinter = m1 * xinter + b1;
		Point2D intersection = getAttributes().createPoint(xinter, yinter);
		return intersection;
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public static class JGraphDiamondRenderer extends VertexRenderer {

		JGraphDiamondRenderer() {
			super();
		}

		public void paint(Graphics g) {
			// TODO this doesn't draw the border
			int b = borderWidth;
			Graphics2D g2 = (Graphics2D) g;
			Dimension d = getSize();
			boolean tmp = selected;
			// construct the diamond
			int width = d.width - b; // allow for border
			int height = d.height - b; // allow for border
			int halfWidth = (d.width - b) / 2;
			int halfHeight = (d.height - b) / 2;
			int[] xpoints = {halfWidth, width, halfWidth, 0};
			int[] ypoints = {0, halfHeight, height, halfHeight};
			Polygon diamond = new Polygon(xpoints, ypoints, 4);
			if (super.isOpaque()) {
				g.setColor(super.getBackground());
				if (gradientColor != null && !preview) {
					setOpaque(false);
					g2.setPaint(new GradientPaint(0, 0, getBackground(), getWidth(),
                     		getHeight(), gradientColor, true));
				}
				g.fillPolygon(diamond);
			}
			try {
				setBorder(null);
				setOpaque(false);
				selected = false;
				super.paint(g);
			} finally {
				selected = tmp;
			}
			if (bordercolor != null) {
				g.setColor(bordercolor);
				g2.setStroke(new BasicStroke(b));
				g.drawPolygon(diamond);
			}
			if (selected) {
				g2.setStroke(GraphConstants.SELECTION_STROKE);
				g.setColor(highlightColor);
				g.drawPolygon(diamond);
			}
		}

		protected void paintBorder(Graphics g) {
			// TODO This needs to be implemented to paint a non-rectangular
			// border
			super.paintBorder(g);
		}
	} // end of DiamondRenderer
}
