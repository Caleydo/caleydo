/*
 *
 * Copyright (C) 2003-2005 David Benson
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

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

public class JGraphRoundRectView extends VertexView {
	public static transient ActivityRenderer renderer = new ActivityRenderer();

	public JGraphRoundRectView() {
		super();
	}

	public JGraphRoundRectView(Object cell) {
		super(cell);
	}

	/**
	 * getArcSize calculates an appropriate arc for the corners of the rectangle
	 * for boundary size cases of width and height
	 */
	public static int getArcSize(int width, int height) {
		int arcSize;

		// The arc width of a activity rectangle is 1/5th of the larger
		// of the two of the dimensions passed in, but at most 1/2
		// of the smaller of the two. 1/5 because it looks nice and 1/2
		// so the arc can complete in the given dimension

		if (width <= height) {
			arcSize = height / 5;
			if (arcSize > (width / 2)) {
				arcSize = width / 2;
			}
		} else {
			arcSize = width / 5;
			if (arcSize > (height / 2)) {
				arcSize = height / 2;
			}
		}

		return arcSize;
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public static class ActivityRenderer extends VertexRenderer {

		/**
		 * Return a slightly larger preferred size than for a rectangle.
		 */
		public Dimension getPreferredSize() {
			Dimension d = super.getPreferredSize();
			d.width += d.height / 5;
			return d;
		}

		public void paint(Graphics g) {
			int b = borderWidth;
			Graphics2D g2 = (Graphics2D) g;
			Dimension d = getSize();
			boolean tmp = selected;
			int roundRectArc = JGraphRoundRectView.getArcSize(d.width - b,
					d.height - b);
			if (super.isOpaque()) {
				g.setColor(super.getBackground());
				if (gradientColor != null && !preview) {
					setOpaque(false);
					g2.setPaint(new GradientPaint(0, 0, getBackground(),
							getWidth(), getHeight(), gradientColor, true));
				}
				g.fillRoundRect(b / 2, b / 2, d.width - (int) (b * 1.5),
						d.height - (int) (b * 1.5), roundRectArc, roundRectArc);
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
				g.drawRoundRect(b / 2, b / 2, d.width - (int) (b * 1.5),
						d.height - (int) (b * 1.5), roundRectArc, roundRectArc);
			}
			if (selected) {
				g2.setStroke(GraphConstants.SELECTION_STROKE);
				g.setColor(highlightColor);
				g.drawRoundRect(b / 2, b / 2, d.width - (int) (b * 1.5),
						d.height - (int) (b * 1.5), roundRectArc, roundRectArc);
			}
		}
	}

}