/*
 * @(#)FileExportGXL.java 1.2 01.02.2003
 * 
 * Copyright (C) 2001-2004 Gaudenz Alder
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 *  
 */
package org.jgraph.pad.coreframework.actions;

import java.awt.event.ActionEvent;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.pad.coreframework.GPUserObject;
import org.jgraph.pad.util.ICellBuisnessObject;
import org.jgraph.pad.util.JGraphImageMapEncoder;

public class FileExportImageMap extends AbstractActionFile {

	/**
	 * Shared encoder instance that uses the GPUserObject for
	 * creating the URL for a cell. See GPUserObject.keyURI
	 */
	public static JGraphImageMapEncoder myEncoder = new JGraphImageMapEncoder() {
		public String getURL(JGraph graph, Object cell) {
			if (cell instanceof DefaultGraphCell) {
				Object userObject = ((DefaultGraphCell) cell)
						.getUserObject();
				if (userObject instanceof GPUserObject) {
					Object url = ((ICellBuisnessObject) userObject)
							.getProperty(GPUserObject.keyURI);
					if (isURL(url))
						return url.toString();
				}
			}
			return super.getURL(graph, cell);
		}
	};

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		System.out.println("-----Copy here-----");
		System.out.println("<img src=\"yourimage.png\""
			+ " border=\"0\" ismap usemap=\"#map\">\n");
		System.out.println(myEncoder.encode(getCurrentGraph(), "map"));
		System.out.println("-----End Copy-----");
	}

}