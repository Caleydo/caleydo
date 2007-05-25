/*
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * GPGraphpad is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * GPGraphpad is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPGraphpad; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.jgraph.plugins.gpgraph;

import java.awt.event.ActionEvent;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.pad.resources.Translator;

/**
 * Action that connects all selected vertices.
 */
public class ShapeConnect extends GPGraphAction {
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Object[] v = getCurrentGPGraph().getSelectionVertices();
		if (v != null && v.length < 20) {
			ConnectionSet cs = new ConnectionSet();
			for (int i = 0; i < v.length; i++) {
				for (int j = i + 1; j < v.length; j++) {
					if (!getCurrentGPGraph().isNeighbour(v[i], v[j])) {
						
						Object edge = new DefaultEdge("");
						
						Object sourcePort =
							getCurrentGraph().getModel().getChild(v[i], 0);
						Object targetPort =
							getCurrentGraph().getModel().getChild(v[j], 0);
						cs.connect(edge, sourcePort, targetPort);
					}
				}
			}
			if (!cs.isEmpty())
				getCurrentGraph().getModel().insert(
						cs.getChangedEdges().toArray(),
						null,
						cs,
						null,
						null);
		} else
			graphpad.error(Translator.getString("TooMany"));
	}
	
}
