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

import java.awt.event.ActionEvent;

import org.jgraph.pad.coreframework.actions.AbstractActionFile;

/*import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;*/

public class FileExportPDF extends AbstractActionFile {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		/*Document document = new Document();
		try {
			String file = saveDialog(Translator
					.getString("Component.FileSave.Text")
					+ " pdf", "pdf", "PDF");
			JGraph graph = getCurrentGraph();
			Object[] cells = graph.getDescendants(graph.getRoots());
			if (cells.length > 0 && file != null && file.length() > 0) {
				PdfWriter writer = PdfWriter.getInstance(document,
						new FileOutputStream(file));
	            document.open();
	            
				Rectangle2D bounds = graph.getCellBounds(cells);
				graph.toScreen(bounds);
				Dimension d = bounds.getBounds().getSize();

				Object[] selection = graph.getSelectionCells();
				boolean gridVisible = graph.isGridVisible();
				boolean doubleBuffered = graph.isDoubleBuffered();
				graph.setGridVisible(false);
				graph.setDoubleBuffered(false);
				graph.clearSelection();

				PdfContentByte cb = writer.getDirectContent();
				cb.saveState();
				cb.concatCTM(1, 0, 0, 1, 50, 400);
				Graphics2D g2 = cb.createGraphics(d.width + 10, d.height + 10);
				
				g2.setColor(graph.getBackground());
				g2.fillRect(0, 0, d.width + 10, d.height + 10);
				g2.translate(-bounds.getX() + 5, -bounds.getY() + 5);

				graph.paint(g2);

				graph.setSelectionCells(selection);
				graph.setGridVisible(gridVisible);
				graph.setDoubleBuffered(doubleBuffered);

				g2.dispose();
				cb.restoreState();
			}
		} catch (IOException ex) {
			graphpad.error(ex.getMessage());
		} catch (DocumentException e2) {
			graphpad.error(e2.getMessage());
		}
		document.close();*/
	}

}