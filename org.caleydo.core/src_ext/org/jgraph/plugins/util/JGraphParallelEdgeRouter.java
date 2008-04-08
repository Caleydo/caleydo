/*
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
 */
package org.jgraph.plugins.util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.PortView;

public class JGraphParallelEdgeRouter extends DefaultEdge.LoopRouting {

	protected static GraphModel emptyModel = new DefaultGraphModel();

	public static JGraphParallelEdgeRouter sharedInstance = new JGraphParallelEdgeRouter();

	/**
	 * The distance between the control point and the middle line. A larger
	 * number will lead to a more "bubbly" appearance of the bezier edges.
	 */
	public double edgeSeparation = 25;

	private JGraphParallelEdgeRouter() {
		// empty
	}

	/**
	 * Returns the array of parallel edges.
	 * 
	 * @param edge
	 * @return the array of parallel edges
	 */
	public Object[] getParallelEdges(EdgeView edge) {
		// FIXME: The model is stored in the cells only in the default
		// implementations. Otherwise we must use the real model here.
		return DefaultGraphModel.getEdgesBetween(emptyModel, edge.getSource()
				.getParentView().getCell(), edge.getTarget().getParentView()
				.getCell(), false);
	}

	public List route(EdgeView edge) {
		List newPoints = new ArrayList();
		if (edge.getSource() == null || edge.getTarget() == null
				|| edge.getSource().getParentView() == null
				|| edge.getTarget().getParentView() == null)
			return null;
		Object[] edges = getParallelEdges(edge);
		// Find the position of the current edge that we are currently routing
		if (edges == null)
			return null;
		int position = 0;
		for (int i = 0; i < edges.length; i++) {
			Object e = edges[i];
			if (e == edge.getCell()) {
				position = i;
			}
		}

		if (edges.length >= 2) {
			// Find the end point positions
			Point2D from = ((PortView) edge.getSource()).getLocation();
			Point2D to = ((PortView) edge.getTarget()).getLocation();

			if (from != null && to != null) {
				// calculate mid-point of the main edge
				double midX = Math.min(from.getX(), to.getX())
						+ Math.abs((from.getX() - to.getX()) / 2);
				double midY = Math.min(from.getY(), to.getY())
						+ Math.abs((from.getY() - to.getY()) / 2);

				// compute the normal slope. The normal of a slope is the negative
				// inverse of the original slope.
				double m = (from.getY() - to.getY())
						/ (from.getX() - to.getX());
				double theta = Math.atan(-1 / m);

				// modify the location of the control point along the axis of the
				// normal using the edge position
				double r = edgeSeparation * (Math.floor(position / 2) + 1);
				if ((position % 2) == 0) {
					r = -r;
				}

				// convert polar coordinates to cartesian and translate axis to the
				// mid-point
				double ex = r * Math.cos(theta) + midX;
				double ey = r * Math.sin(theta) + midY;
				Point2D controlPoint = new Point2D.Double(ex, ey);

				newPoints.add(controlPoint);
			}
		}
		newPoints.add(edge.getTarget());
		return newPoints;
	}

	/**
	 * @return Returns the edgeSeparation.
	 */
	public double getEdgeSeparation() {
		return edgeSeparation;
	}

	/**
	 * @param edgeSeparation
	 *            The edgeSeparation to set.
	 */
	public void setEdgeSeparation(double edgeSeparation) {
		this.edgeSeparation = edgeSeparation;
	}

	/**
	 * @return Returns the sharedInstance.
	 */
	public static JGraphParallelEdgeRouter getSharedInstance() {
		return sharedInstance;
	}
}