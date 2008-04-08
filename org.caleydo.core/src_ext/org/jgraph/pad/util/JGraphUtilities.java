/*
 * @(#)JGraphUtilities.java 1.0 12-MAY-2004
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
package org.jgraph.pad.util;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.SwingConstants;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

/**
 * @author Gaudenz Alder
 */
public class JGraphUtilities {

	public static final int ALIGN_LEFT = SwingConstants.LEFT;

	public static final int ALIGN_RIGHT = SwingConstants.RIGHT;

	public static final int ALIGN_TOP = SwingConstants.TOP;

	public static final int ALIGN_BOTTOM = SwingConstants.BOTTOM;

	public static final int ALIGN_CENTER = SwingConstants.CENTER;

	public static final int ALIGN_MIDDLE = SwingConstants.NEXT;

	/**
	 * @return Returns all vertices in <code>cells</code>.
	 */
	public static Object[] getVertices(GraphModel model, Object[] cells) {
		if (cells != null) {
			ArrayList result = new ArrayList();
			for (int i = 0; i < cells.length; i++) {
				if (!model.isPort(cells[i]) && !model.isEdge(cells[i])
						&& !isGroup(model, cells[i]))
					result.add(cells[i]);
			}
			return result.toArray();
		}
		return null;
	}

	/**
	 * @return Returns all edges in <code>model</code>.
	 */
	public static Object[] getEdges(GraphModel model) {
		Object[] cells = DefaultGraphModel.getAll(model);
		if (cells != null) {
			ArrayList result = new ArrayList();
			for (int i = 0; i < cells.length; i++)
				if (model.isEdge(cells[i]))
					result.add(cells[i]);
			return result.toArray();
		}
		return null;
	}

	/**
	 * Returns all edges in <code>cells</code>. Note: Use
	 * DefaultGraphModel.getEdges() to get all <strong>connected</code> edges.
	 * 
	 * @return array of edges in specified cells
	 */
	public static Object[] getEdges(JGraph graph, Object[] cells) {
		if (cells != null) {
			ArrayList result = new ArrayList();
			GraphModel model = graph.getModel();
			for (int i = 0; i < cells.length; i++) {
				if (model.isEdge(cells[i]))
					result.add(cells[i]);
			}
			return result.toArray();
		}
		return null;
	}

	/**
	 * @return whether or not specified cell is a vertex
	 */
	public static boolean isVertex(JGraph graph, Object cell) {
		return !graph.getModel().isEdge(cell) && !graph.getModel().isPort(cell)
				&& !isGroup(graph, cell);
	}

	/**
	 * @return whether or not specified cell is a group
	 */
	public static boolean isGroup(JGraph graph, Object cell) {
		CellView view = graph.getGraphLayoutCache().getMapping(cell, false);
		if (view != null)
			return !view.isLeaf();
		return false;
	}

	/**
	 * @return  whether or not specified cell is a vertex
	 */
	public static boolean isVertex(GraphModel model, Object cell) {
		return !model.isEdge(cell) && !model.isPort(cell)
				&& !isGroup(model, cell);
	}

	/**
	 * @return whether or not specified cell is a group
	 */
	public static boolean isGroup(GraphModel model, Object cell) {
		for (int i = 0; i < model.getChildCount(cell); i++) {
			if (!model.isPort(model.getChild(cell, i)))
				return true;
		}
		return false;
	}

	public static void alignCells(JGraph graph, Object[] cells, int constraint) {
		Rectangle2D bounds = graph.getCellBounds(cells);
		GraphLayoutCache layoutCache = graph.getGraphLayoutCache();
		Map nested = new Hashtable();
		for (int i = 0; i < cells.length; i++) {
			CellView cellView = layoutCache.getMapping(cells[i], false);
			Rectangle2D cellBounds = GraphConstants.getBounds(cellView
					.getAllAttributes());
			if (bounds != null && cellBounds != null) {
				Map attrs = new Hashtable();
				Rectangle2D newBounds = align(constraint,
						(Rectangle2D) cellBounds.clone(), bounds);
				GraphConstants.setBounds(attrs, newBounds);
				nested.put(cellView.getCell(), attrs);
			}
		}
		if (!nested.isEmpty())
			layoutCache.edit(nested, null, null, null);
	}

	/**
	 * @return the new cell bounds after alignment
	 */
	public static Rectangle2D align(int constraint, Rectangle2D cellBounds,
			Rectangle2D bounds) {
		switch (constraint) {
		case ALIGN_LEFT:
			cellBounds.setFrame(bounds.getX(), cellBounds.getY(), cellBounds
					.getWidth(), cellBounds.getHeight());
			break;
		case ALIGN_TOP:
			cellBounds.setFrame(cellBounds.getX(), bounds.getY(), cellBounds
					.getWidth(), cellBounds.getHeight());
			break;
		case ALIGN_RIGHT:
			cellBounds.setFrame(bounds.getX() + bounds.getWidth()
					- cellBounds.getWidth(), cellBounds.getY(), cellBounds
					.getWidth(), cellBounds.getHeight());
			break;
		case ALIGN_BOTTOM:
			cellBounds.setFrame(cellBounds.getX(), bounds.getY()
					+ bounds.getHeight() - cellBounds.getHeight(), cellBounds
					.getWidth(), cellBounds.getHeight());
			break;
		case ALIGN_CENTER:
			double cx = bounds.getWidth() / 2;
			cellBounds.setFrame(bounds.getX() + cx - cellBounds.getWidth() / 2,
					cellBounds.getY(), cellBounds.getWidth(), cellBounds
							.getHeight());
			break;
		case ALIGN_MIDDLE:
			double cy = bounds.getHeight() / 2;
			cellBounds.setFrame(cellBounds.getX(), bounds.getY() + cy
					- cellBounds.getHeight() / 2, cellBounds.getWidth(),
					cellBounds.getHeight());
			break;
		}
		return cellBounds;
	}
}