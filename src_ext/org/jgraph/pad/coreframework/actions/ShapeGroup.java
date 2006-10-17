/*
 * Copyright (C) 2001-2004 Gaudenz Alder
 * 
 * 6/01/2006: I, Raphpael Valyi, changed back the header of this file to LGPL
 * because this action now invoke the JGraph LGPL built in method.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jgraph.pad.coreframework.actions;

import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.pad.coreframework.GPAbstractActionDefault;
import org.jgraph.pad.coreframework.GPUserObject;

/**
 * Action that groups the current selection.
 */
public class ShapeGroup extends GPAbstractActionDefault {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		JGraph graph = getCurrentGraph();
		if (!graph.isSelectionEmpty()) {
			DefaultGraphCell group = new DefaultGraphCell(new GPUserObject());
			Object[] cells = graph.order(graph.getSelectionCells());
			Rectangle2D bounds = graph.getCellBounds(cells);
			if (bounds != null) {
				bounds = new Rectangle2D.Double(bounds.getX()
						+ bounds.getWidth() / 4, bounds.getY()
						+ bounds.getHeight() / 4, bounds.getWidth() / 2, bounds
						.getHeight() / 2);
				GraphConstants.setBounds(group.getAttributes(), bounds);
			}
			getCurrentGraph().getGraphLayoutCache().insertGroup(group, cells);
		}
	}
}