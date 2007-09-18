/*
 * @(#)ShapeCloneSize.java	1.2 01.02.2003
 *
 * Copyright (C) 2001-2004 Gaudenz Alder
 * Copyright (C) 2005 David Benson
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
package org.jgraph.pad.coreframework.actions;

import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Map;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.pad.coreframework.GPAbstractActionDefault;

/**
 * Action for copying the size of one cell onto other cells
 */
public class ShapeCloneSize extends GPAbstractActionDefault {

	/**
	 * Takes the size of the first cell selected and applies it to all the
	 * other selected cells
	 */
	public void actionPerformed(ActionEvent e) {
		GraphLayoutCache layoutCache = getCurrentGraphLayoutCache();
		Object[] cells = getCurrentGraph().getSelectionCells();
		if (cells != null) {
			Object cell = getCurrentGraph().getSelectionCell();
			Rectangle2D rect = (getCurrentGraph().getCellBounds(cell))
			.getBounds2D();
			Map viewMap = new Hashtable();
			for (int i = 0; i < cells.length; i++) {
				CellView view = layoutCache.getMapping(cells[i], false);
				AttributeMap map = (AttributeMap)view.getAllAttributes().clone();
				Rectangle2D bounds = GraphConstants.getBounds(map);
				if (bounds != null) {
					bounds.setRect( bounds.getX(),
									bounds.getY(),
									rect.getWidth(),
									rect.getHeight());
					viewMap.put(cells[i], map);
				}
			}
			layoutCache.edit(viewMap, null, null, null);
		}
	}
}
