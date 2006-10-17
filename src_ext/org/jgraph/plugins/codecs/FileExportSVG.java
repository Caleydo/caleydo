/*
 * @(#)FileExportJPG.java 1.2 01.02.2003
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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.jgraph.JGraph;
import org.jgraph.pad.coreframework.actions.AbstractActionFile;
import org.jgraph.pad.resources.Translator;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class FileExportSVG extends AbstractActionFile {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		/*try {
			String file = saveDialog(Translator
					.getString("Component.FileSave.Text")
					+ " svg", "svg", "svg");
			JGraph graph = getCurrentGraph();
			Object[] cells = graph.getDescendants(graph.getRoots());
			if (cells.length > 0 && file != null && file.length() > 0) {
				Rectangle2D bounds = graph.getCellBounds(cells);
				graph.toScreen(bounds);
				Dimension d = bounds.getBounds().getSize();

				Object[] selection = graph.getSelectionCells();
				boolean gridVisible = graph.isGridVisible();
				boolean doubleBuffered = graph.isDoubleBuffered();
				graph.setGridVisible(false);
				graph.setDoubleBuffered(false);
				graph.clearSelection();

				FileOutputStream fos = new FileOutputStream(file);
				// Created writer with UTF-8 encoding
				Writer out = new OutputStreamWriter(fos, "UTF-8");
				// Get a DOMImplementation
				DOMImplementation domImpl =
					GenericDOMImplementation.getDOMImplementation();
				// Create an instance of org.w3c.dom.Document
				Document document = domImpl.createDocument(null, "svg", null);
				// Create an instance of the SVG Generator
				SVGGraphics2D g2 = new SVGGraphics2D(document);
				// Render into the SVG Graphics2D implementation
				graph.paint(g2);
				// Use CSS style attribute
				boolean useCSS = true;
				// Finally, stream out SVG to the writer
				g2.stream(out, useCSS);

				g2.setColor(graph.getBackground());
				g2.fillRect(0, 0, d.width + 10, d.height + 10);
				g2.translate(-bounds.getX() + 5, -bounds.getY() + 5);

				graph.paint(g2);

				graph.setSelectionCells(selection);
				graph.setGridVisible(gridVisible);
				graph.setDoubleBuffered(doubleBuffered);

				// Close the file output stream
				fos.flush();
				fos.close();
			}
		} catch (IOException ex) {
			graphpad.error(ex.getMessage());
		}*/
	}

}