/*
 * @(#)JGraphEllipseView.java 1.0 12-MAY-2004
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
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

/**
 * @author Gaudenz Alder
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class JGraphEllipseView extends VertexView {

	/**
	 */
	public static transient JGraphEllipseRenderer renderer = new JGraphEllipseRenderer();

	/**
	 */
	public JGraphEllipseView() {
		super();
	}

	/**
	 */
	public JGraphEllipseView(Object cell) {
		super(cell);
	}

	/**
	 */
	public CellViewRenderer getRenderer() {
		return renderer;
	}

	/**
	 */
	public static class JGraphEllipseRenderer extends VertexRenderer {

		/**
		 * Return a slightly larger preferred size than for a rectangle.
		 */
		public Dimension getPreferredSize() {
			Dimension d = super.getPreferredSize();
			d.width += d.width / 8;
			d.height += d.height / 2;
			return d;
		}
		
		/**
		 * Returns the intersection of the bounding rectangle and the
		 * straight line between the source and the specified point p.
		 * The specified point is expected not to intersect the bounds.
		 */
		public Point2D getPerimeterPoint(VertexView edgeView, Point2D source, Point2D p) {
			Rectangle2D r = edgeView.getBounds();

			double x = r.getX();
			double y = r.getY();
			double a = (r.getWidth() + 1) / 2;
			double b = (r.getHeight() + 1) / 2;

			// x0,y0 - center of ellipse
			double x0 = x + a;
			double y0 = y + b;

			// x1, y1 - point
			double x1 = p.getX();
			double y1 = p.getY();

			// Calculates straight line equation through point and ellipse center
			// y = d * x + h
			double dx = x1 - x0;
			double dy = y1 - y0;

			if (dx == 0)
				return new Point((int) x0, (int) (y0 + b * dy / Math.abs(dy)));

			double d = dy / dx;
			double h = y0 - d * x0;

			// Calculates intersection
			double e = a * a * d * d + b * b;
			double f = -2 * x0 * e;
			double g = a * a * d * d * x0 * x0 + b * b * x0 * x0 - a * a * b * b;

			double det = Math.sqrt(f * f - 4 * e * g);

			// Two solutions (perimeter points)
			double xout1 = (-f + det) / (2 * e);
			double xout2 = (-f - det) / (2 * e);
			double yout1 = d * xout1 + h;
			double yout2 = d * xout2 + h;

			double dist1 = Math.sqrt(Math.pow((xout1 - x1), 2)
					+ Math.pow((yout1 - y1), 2));
			double dist2 = Math.sqrt(Math.pow((xout2 - x1), 2)
					+ Math.pow((yout2 - y1), 2));

			// Correct solution
			double xout, yout;

			if (dist1 < dist2) {
				xout = xout1;
				yout = yout1;
			} else {
				xout = xout2;
				yout = yout2;
			}

			return new Point2D.Double(xout, yout);
		}


		/**
		 */
		public void paint(Graphics g) {
			int b = borderWidth;
			Graphics2D g2 = (Graphics2D) g;
			Dimension d = getSize();
			boolean tmp = selected;
			if (super.isOpaque()) {
				g.setColor(super.getBackground());
				if (gradientColor != null && !preview) {
					setOpaque(false);
					g2.setPaint(new GradientPaint(0, 0, getBackground(),
							getWidth(), getHeight(), gradientColor, true));
				}
				g.fillOval(b - 1, b - 1, d.width - b, d.height - b);
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
				g.drawOval(b - 1, b - 1, d.width - b, d.height - b);
			}
			if (selected) {
				g2.setStroke(GraphConstants.SELECTION_STROKE);
				g.setColor(highlightColor);
				g.drawOval(b - 1, b - 1, d.width - b, d.height - b);
			}
		}
	}

}