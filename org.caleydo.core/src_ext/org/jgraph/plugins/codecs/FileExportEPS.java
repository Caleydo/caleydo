/*
 * @(#)FileExportJPG.java	1.2 01.02.2003
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
package org.jgraph.plugins.codecs;

import java.awt.event.ActionEvent;

import org.jgraph.pad.coreframework.actions.AbstractActionFile;
//import org.jibble.epsgraphics.EpsGraphics2D;

public class FileExportEPS extends AbstractActionFile {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		/*try {
			String file =
				saveDialog(
					Translator.getString("Component.FileSave.Text") + " eps",
					"eps",
					"Encapsulated Postscript");
			JGraph graph = getCurrentGraph();
			Object[] cells = graph.getDescendants(graph.getRoots());
			if (cells.length > 0 && file != null && file.length() > 0) {
				// File Output stream
				FileOutputStream fos = new FileOutputStream(file);
				EpsGraphics2D graphics = new EpsGraphics2D();
				graphics.setColor(graph.getBackground());
				Rectangle2D bounds = graph.getCellBounds(cells);
				graph.toScreen(bounds);
				Dimension d = bounds.getBounds().getSize();
				graphics.fillRect(0, 0, d.width+10, d.height+10);
				graphics.translate(-bounds.getX() + 5, -bounds.getY() + 5);

				Object[] selection = graph.getSelectionCells();
				boolean gridVisible = graph.isGridVisible();
				boolean doubleBuffered = graph.isDoubleBuffered();
				graph.setGridVisible(false);
				graph.setDoubleBuffered(false);
				graph.clearSelection();

				// TODO: Remove the unsupported method stack trace
				graph.paint(graphics);

				graph.setSelectionCells(selection);
				graph.setGridVisible(gridVisible);
				graph.setDoubleBuffered(doubleBuffered);
				
				fos.write(graphics.toString().getBytes());
				fos.close();
			}
		} catch (IOException ex) {
			graphpad.error(ex.getMessage());
		}*/
	}
	
}
