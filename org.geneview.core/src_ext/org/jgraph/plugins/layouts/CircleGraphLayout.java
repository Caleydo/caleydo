/*
 * @(#)CircleLayoutAlgorithm.java 1.0 18-MAY-2004
 * 
 * Copyright (c) 2004-2005, Gaudenz Alder
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
package org.jgraph.plugins.layouts;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.VertexView;

/**
 * @author Gaudenz Alder
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CircleGraphLayout extends JGraphLayoutAlgorithm {
	
	/**
	 * Returns the name of this algorithm in human
	 * readable form.
	 */
	public String toString() {
		return "Simple Circle";
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
     * @param graph JGraph instance
     * @param dynamic_cells List of all nodes the layout should move
     * @param static_cells List of node the layout should not move but allow for
	 */
    public void run(JGraph graph, Object[] dynamic_cells, Object[] static_cells) {
		// Fetch All Views
		CellView[] views = graph.getGraphLayoutCache().getMapping(dynamic_cells);
		// Create list to hold vertices
		List vertices = new ArrayList();
		// Maximum width or height
		int max = 0;
		// Loop through all views
		for (int i = 0; i < views.length; i++) {
			// Add vertex to list
			if (views[i] instanceof VertexView) {
				vertices.add(views[i]);
				// Fetch Bounds
				Rectangle2D bounds = views[i].getBounds();
				// Update Maximum
				if (bounds != null)
					max = (int) Math.max(max, 
							Math.max(bounds.getWidth(), bounds.getHeight()));
			}
		}
		// Compute Radius
		int r = (int) Math.max(vertices.size() * max / Math.PI, 100);
		// Compute angle step
		double phi = 2 * Math.PI / vertices.size();
		// Arrange vertices in a circle
		for (int i = 0; i < vertices.size(); i++) {
			Rectangle2D bounds = ((CellView) vertices.get(i)).getBounds();
			// Update Location
			if (bounds != null)
				bounds.setFrame(
					r + r * Math.sin(i * phi),
					r + r * Math.cos(i * phi),
					bounds.getWidth(),
					bounds.getHeight());
		}
	}
	
}
