/*
 * @(#)FileExportGraphviz.java	1.2 01.02.2003
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

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jgraph.pad.coreframework.actions.AbstractActionFile;
import org.jgraph.pad.resources.Translator;

public class FileExportGraphviz extends AbstractActionFile {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		FileDialog f =
			new FileDialog(graphpad.getFrame(), Translator.getString("DOTFile"/*#Finished:Original="DOT File"*/), FileDialog.SAVE);
		f.setVisible(true);
		if (f.getFile() == null)
			return;

		try {
			Object[] cells = getCurrentGraph().getDescendants(getCurrentGraph().getRoots());

			if (cells != null && cells.length > 0) {
				// Create temp file
				String tmpFile = f.getDirectory() + f.getFile();
				//File tmpFile = File.createTempFile("TODOT", ".dot");
				//tmpFile.deleteOnExit();

				// File Output stream
				FileOutputStream fos = new FileOutputStream(tmpFile);
				fos.write(JGraphGraphvizEncoder.encode(getCurrentGraph(), cells).getBytes());

				// Write to file
				fos.flush();
				fos.close();

			};
		} catch (IOException ex) {
			graphpad.error(ex.toString());
		}
	}

}
