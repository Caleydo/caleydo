/*
 * @(#)JGraphGXLCodec.java 1.0 12-MAY-2004
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
 */
package org.jgraph.plugins.codecs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;

import org.jgraph.JGraph;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.pad.util.JGraphUtilities;


/**
 * @author Gaudenz Alder
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class JGraphSimpleCodec {

	//
	// Simple Format a:b[:edgelabel]
	//
	// Eg. a triange:
	// a:b             /\B 
	// b:c            /  \
	// c:a:ac       A/_ac_\C
	//

	public static void decode(
			JGraph graph,
			InputStream fstream,
			String delim,
			String edgeLabel) throws Exception
	{
		// Convert our input stream to a
		// BufferedReader
		BufferedReader in = new BufferedReader(new InputStreamReader(fstream));

		// Continue to read lines while
		// there are still some left to read
		// Map from keys to vertices
		Hashtable map = new Hashtable();
		// Link to Existing Vertices!
		Object[] items = JGraphUtilities.getVertices(graph.getModel(), DefaultGraphModel.getAll(graph.getModel()));
		if (items != null) {
			for (int i = 0; i < items.length; i++)
				if (items[i] != null && items[i].toString() != null) {
					map.put(items[i].toString(), items[i]);
				}
		}
		// Vertices and Edges to insert
		Hashtable adj = new Hashtable();
		java.util.List insert = new ArrayList();
		ConnectionSet cs = new ConnectionSet();
		while (in.ready()) {
			// Print file line to screen
			String s = in.readLine();
			StringTokenizer st = new StringTokenizer(s, delim);
			if (st.hasMoreTokens()) {
				String srckey = st.nextToken().trim();
				// Get or create source vertex
				Object source = getVertexForKey(map, srckey);
				if (!graph.getModel().contains(source)
					&& !insert.contains(source))
					insert.add(source);
				if (st.hasMoreTokens()) {
					String tgtkey = st.nextToken().trim();
					// Get or create source vertex
					Object target = getVertexForKey(map, tgtkey);
					if (!graph.getModel().contains(target)
						&& !insert.contains(target))
						insert.add(target);
					// Create and insert Edge
					Set neighbours = (Set) adj.get(srckey);
					if (neighbours == null) {
						neighbours = new HashSet();
						adj.put(srckey, neighbours);
					}
					String label =
						(st.hasMoreTokens())
							? st.nextToken().trim()
							: edgeLabel;
					if (!(neighbours.contains(tgtkey))) {
						Object edge =
							new DefaultEdge(label);
						Object sourcePort =
							graph.getModel().getChild(source, 0);
						Object targetPort =
							graph.getModel().getChild(target, 0);
						if (sourcePort != null && targetPort != null) {
							cs.connect(edge, sourcePort, targetPort);
							insert.add(edge);
							neighbours.add(tgtkey);
						}
					}
				}
			}
		}
		in.close();
		graph.getModel().insert(insert.toArray(), null, cs, null, null);
	}

	public static Object getVertexForKey(Hashtable map, String key) {
		Object cell = map.get(key);
		if (cell == null) {
			DefaultGraphCell dgc =
				new DefaultGraphCell(key);
			dgc.add(new DefaultPort());
			cell = dgc;
			map.put(key, cell);
		}
		return cell;
	}
	
}
