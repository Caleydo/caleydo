/*
 * @(#)ShapeDefaultPorts.java	1.2 01.02.2003
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

import org.jgraph.graph.ConnectionSet;

public class ShapeDefaultPorts extends GPGraphAction {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent ev) {
			Object[] all = getCurrentGraph().getDescendants(getCurrentGraph().getSelectionCells());
			Object[] e = getCurrentGPGraph().getEdges(all);
			if (e != null && e.length > 0) {
				ConnectionSet cs = new ConnectionSet();
				for (int i = 0; i < e.length; i++) {
					Object s = getCurrentGraph().getModel().getSource(e[i]);
					Object sv = getCurrentGraph().getModel().getParent(s);
					Object t = getCurrentGraph().getModel().getTarget(e[i]);
					Object tv = getCurrentGraph().getModel().getParent(t);
					if (sv != null) {
						s = getCurrentGraph().getModel().getChild(sv, 0);
						cs.connect(e[i], s, true);
					}
					if (tv != null) {
						t = getCurrentGraph().getModel().getChild(tv, 0);
						cs.connect(e[i], t, false);
					}
				}
				getCurrentGraph().getModel().insert(null, null, cs, null, null);
			}
	}

}
