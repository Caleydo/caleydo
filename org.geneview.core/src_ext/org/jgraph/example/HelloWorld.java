/*
 * @(#)HelloWorld.java 3.3 23-APR-04
 * 
 * Copyright (c) 2001-2004, Gaudenz Alder All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *  
 */
package org.jgraph.example;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;

public class HelloWorld {

	public static void main(String[] args) {

		// Construct Model and Graph
		GraphModel model = new DefaultGraphModel();
		JGraph graph = new JGraph(model);

		// Control-drag should clone selection
		graph.setCloneable(true);

		// Enable edit without final RETURN keystroke
		graph.setInvokesStopCellEditing(true);

		// When over a cell, jump to its default port (we only have one, anyway)
		graph.setJumpToDefaultPort(true);

		// Insert all three cells in one call, so we need an array to store them
		DefaultGraphCell[] cells = new DefaultGraphCell[3];

		// Create Hello Vertex
		cells[0] = createVertex("Hello", 20, 20, 40, 20, null, false);

		// Create World Vertex
		cells[1] = createVertex("World", 140, 140, 40, 20, Color.ORANGE, true);

		// Create Edge
		DefaultEdge edge = new DefaultEdge();
		// Fetch the ports from the new vertices, and connect them with the edge
		edge.setSource(cells[0].getChildAt(0));
		edge.setTarget(cells[1].getChildAt(0));
		cells[2] = edge;

		// Set Arrow Style for edge
		int arrow = GraphConstants.ARROW_CLASSIC;
		GraphConstants.setLineEnd(edge.getAttributes(), arrow);
		GraphConstants.setEndFill(edge.getAttributes(), true);

		// Insert the cells via the cache, so they get selected
		graph.getGraphLayoutCache().insert(cells);

		// Show in Frame
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JScrollPane(graph));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	public static DefaultGraphCell createVertex(String name, double x,
			double y, double w, double h, Color bg, boolean raised) {

		// Create vertex with the given name
		DefaultGraphCell cell = new DefaultGraphCell(name);

		// Set bounds
		GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(
				x, y, w, h));

		// Set fill color
		if (bg != null) {
			GraphConstants.setGradientColor(cell.getAttributes(), bg);
			GraphConstants.setOpaque(cell.getAttributes(), true);
		}

		// Set raised border
		if (raised)
			GraphConstants.setBorder(cell.getAttributes(), BorderFactory
					.createRaisedBevelBorder());
		else
			// Set black border
			GraphConstants.setBorderColor(cell.getAttributes(), Color.black);

		// Add a Floating Port
		cell.addPort();

		return cell;
	}

}