/*
 * @(#)ShapeAlignCenter.java	1.2 01.02.2003
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

import java.awt.event.ActionEvent;

import org.jgraph.JGraph;
import org.jgraph.pad.coreframework.GPAbstractActionDefault;
import org.jgraph.pad.util.JGraphUtilities;

/**
 * Gets the coordinates (r) for all selected shapes and aligns the selected
 * shapes centermost to the coordinates (r).
 */
public class ShapeAlignCenter extends GPAbstractActionDefault {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		JGraph graph = getCurrentGraph();
		JGraphUtilities.alignCells(graph, graph.getSelectionCells(),
				JGraphUtilities.ALIGN_CENTER);
	}

}