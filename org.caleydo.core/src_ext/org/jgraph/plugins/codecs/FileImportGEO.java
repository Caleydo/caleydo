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
package org.jgraph.plugins.codecs;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;

import javax.swing.JOptionPane;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.pad.coreframework.GPAbstractActionDefault;
import org.jgraph.pad.resources.Translator;

public class FileImportGEO extends GPAbstractActionDefault {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		FileDialog f = new FileDialog(graphpad.getFrame(), Translator
				.getString("CustomFile"/* #Finished:Original="Custom File" */),
				FileDialog.LOAD);
		f.setVisible(true);
		if (f.getFile() == null)
			return;

		try {
			String file = f.getDirectory() + f.getFile();
			String smaxx = JOptionPane.showInputDialog(Translator
					.getString("Width"));
			String smaxy = JOptionPane.showInputDialog(Translator
					.getString("Height"));
			int maxx = Integer.parseInt(smaxx);
			int maxy = Integer.parseInt(smaxy);
			JGraph graph = getCurrentGraph();
			// Check in case there is no document available
			if ( null == graph ) {
				graphpad.addDocument(null, null);
				graph = getCurrentGraph();
			}
			JGraphGEOCodec.decode(graph, new DefaultGraphCell(),
					new DefaultPort(), new DefaultEdge(), new FileInputStream(
							file), maxx, maxy);
		} catch (Exception ex) {
			graphpad.error(ex.toString());
		}
	}

}

