/*
 * @(#)GraphTilt.java	1.2 01.02.2003
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
package org.jgraph.plugins.gpgraph;

import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Map;

import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.pad.util.Utilities;

public class GraphTilt extends GPGraphAction {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		int magnitude = 100;
		Object[] v = getCurrentGPGraph().getVertices(getCurrentGPGraph().getAll());
		CellView[] views = getCurrentGraphLayoutCache().getMapping(v);
		if (views != null && views.length > 0) {
			Map GPAttributeMap = new Hashtable();
			for (int i = 0; i < views.length; i++) {
				Rectangle2D bounds = (Rectangle2D) (views[i].getBounds()).clone();
				int dx = Utilities.rnd(magnitude);
				int dy = Utilities.rnd(magnitude);
				double x = Math.max(0, bounds.getX() + dx - magnitude / 2);
				double y = Math.max(0, bounds.getY() + dy - magnitude / 2);
				bounds.setFrame(x, y, bounds.getWidth(), bounds.getHeight());
				Map attributes = new Hashtable();
				GraphConstants.setBounds(attributes, bounds);
				GPAttributeMap.put(v[i], attributes);
			}
			getCurrentGraphLayoutCache().edit(GPAttributeMap, null, null, null);
		}
	}
}
