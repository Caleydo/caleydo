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
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.ParentMap;

public class GroupingRemoving {

	public static void main(String[] args) {

		// Construct Model and Graph
		GraphModel model = new DefaultGraphModel();
		GraphLayoutCache cache = new GraphLayoutCache(model, new DefaultCellViewFactory(), true);
		JGraph graph = new JGraph(model, cache);
		int numGroupedCells = 10;
		DefaultGraphCell[] cells = new DefaultGraphCell[numGroupedCells];

		for (int i=0; i < numGroupedCells; i++) {
			cells[i] = createVertex("Hello", 20, 20, 40, 20, null, false);
		}
		DefaultGraphCell group = createVertex("Hello", 20, 20, 40, 20, null, false);

		ParentMap pm = new ParentMap();

        for (int i = 0; i < cells.length; i++) {
            pm.addEntry(cells[i], group);
        }
        // Insert the cells via the cache, so they are visible
        graph.getGraphLayoutCache().insert(new Object[] { group }, null, null, pm, null);
        
        // Set some of the children are hidden
        graph.getGraphLayoutCache().hideCells(new Object[] { cells[3], cells [7] }, true);

		// Control-drag should clone selection
		graph.setCloneable(true);

		// Enable edit without final RETURN keystroke
		graph.setInvokesStopCellEditing(true);

		// When over a cell, jump to its default port (we only have one, anyway)
		graph.setJumpToDefaultPort(true);

		// Show in Frame
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JScrollPane(graph));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
		// Wait a bit and then remove the cells
        try {
        	Thread.sleep(5000);
        } catch (InterruptedException e){
        	
        }
        model.remove(DefaultGraphModel.getDescendants(model, DefaultGraphModel.getRoots(model)).toArray());
        System.out.println("model.roots.size() = " + DefaultGraphModel.getRoots(model).length);
        System.out.println("cell views size = " + cache.getCellViews().length);
        System.out.println("hidden views size = " + cache.getHiddenCellViews().length);
        System.out.println("visible set size = " + cache.getVisibleSet().size());
        System.out.println("cache roots size = " + cache.getRoots().length);
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