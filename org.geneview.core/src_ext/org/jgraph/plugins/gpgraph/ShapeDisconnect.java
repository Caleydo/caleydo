/*
 * @(#)ShapeDisconnect.java	1.2 01.02.2003
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
import java.util.HashSet;

/**
 * Action that disconnects all selected vertices.
 */
public class ShapeDisconnect extends GPGraphAction {

	 /**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
			Object[] v = getCurrentGPGraph().getSelectionVertices();
			if (v != null && v.length > 0) {
				HashSet result = new HashSet();
				for (int i = 0; i < v.length; i++) {
					for (int j = i + 1; j < v.length; j++) {
						Object[] e = getCurrentGPGraph().getEdgesBetween(v[i], v[j]);
						for (int k = 0; k < e.length; k++)
							result.add(e[k]);
					}
				}
				if (result.size() > 0)
					getCurrentGraph().getModel().remove(result.toArray());
			}
	}

}
