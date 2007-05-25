/*
 * @(#)ShapeBestPorts.java	1.2 30.01.2003
 *
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.jgraph.plugins.gpgraph;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.Port;
import org.jgraph.graph.PortView;

/**
 * Action that sets the selections fill color using a color dialog.
 */
public class ShapeBestPorts extends GPGraphAction {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */

		public void actionPerformed(ActionEvent e) {
			GPGraph graph = getCurrentGPGraph();
			Object[] all = graph.getDescendants(graph.getSelectionCells());
			Object[] edges = graph.getEdges(all);
			if (edges != null && edges.length > 0) {
				ConnectionSet cs = new ConnectionSet();
				for (int i = 0; i < edges.length; i++) {
					EdgeView view =
						(EdgeView) graphpad.getCurrentDocument().getGraphLayoutCache().getMapping(edges[i], false);
					if (view != null) {
						Object orig = graph.getModel().getSource(edges[i]);
						if (orig != null) {
							Point2D to2d = view.getPoint(1);
							Point to = new Point((int)to2d.getX(),(int)to2d.getY());
							Object source = graph.getModel().getSource(edges[i]);
							Port port = findClosestPort(to, source);
							if (port != orig)
								cs.connect(edges[i], port, true);
						}
						orig = graph.getModel().getTarget(edges[i]);
						if (orig != null) {
							Point2D to2d = view.getPoint(view.getPointCount() - 1);
							Point to = new Point((int)to2d.getX(),(int)to2d.getY());
							Object target = graph.getModel().getTarget(edges[i]);
							Port port = findClosestPort(to, target);
							if (port != orig)
								cs.connect(edges[i], port, false);
						}
					}
				} // end for buttonImage
				graph.getModel().edit(null, cs, null, null);
			}
		}

	/* Return the port of the given vertex that is closest to the given point. */
	public Port findClosestPort(Point p, Object vertex) {
		GPGraph graph = getCurrentGPGraph();
		Port port = null;
		double min = Double.MAX_VALUE;
		for (int i = 0; i < graph.getModel().getChildCount(vertex); i++) {
			Object child = graph.getModel().getChild(vertex, i);
			if (child instanceof Port) {
				PortView view = (PortView) getCurrentGraphLayoutCache().getMapping(child, false);
				double t = p.distance(view.getLocation());
				if (port == null || t < min) {
					port = (Port) child;
					min = t;
				}
			}
		}
		return port;
	}

}
