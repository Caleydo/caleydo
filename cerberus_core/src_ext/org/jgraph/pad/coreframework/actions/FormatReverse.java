/*
 * @(#)FormatReverse.java	1.2 31.01.2003
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
package org.jgraph.pad.coreframework.actions;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Map;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.pad.coreframework.GPAbstractActionDefault;

/**
 * Reverse the selected cells
 */
public class FormatReverse extends GPAbstractActionDefault {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
			Object[] cells = getCurrentGraph().getSelectionCells();
			if (cells != null) {
				CellView[] views = getCurrentGraphLayoutCache().getMapping(cells);
				Map viewMap = new Hashtable();
				for (int i = 0; i < views.length; i++) {
					AttributeMap map = (AttributeMap)views[i].getAttributes().clone();
					if (getCurrentGraph().getModel().isEdge(views[i].getCell())) {
						int style = GraphConstants.getLineBegin(map);
						int size = GraphConstants.getBeginSize(map);
						boolean fill = GraphConstants.isBeginFill(map);
						GraphConstants.setLineBegin(
							map,
							GraphConstants.getLineEnd(map));
						GraphConstants.setBeginSize(
							map,
							GraphConstants.getEndSize(map));
						GraphConstants.setBeginFill(
							map,
							GraphConstants.isEndFill(map));
						GraphConstants.setLineEnd(map, style);
						GraphConstants.setEndSize(map, size);
						GraphConstants.setEndFill(map, fill);
						viewMap.put(cells[i], map);
					}
					Rectangle bounds = (Rectangle)GraphConstants.getBounds(map);
					if (bounds != null) {
						bounds.setSize(bounds.height, bounds.width);
						viewMap.put(views[i].getCell(), map);
					}
				} // for
				getCurrentGraphLayoutCache().edit(viewMap, null, null, null);
			}
	}

}
