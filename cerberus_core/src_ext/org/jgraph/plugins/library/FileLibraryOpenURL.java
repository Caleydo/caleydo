/*
 * @(#)FileLibraryOpen.java	1.2 29.01.2003
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

package org.jgraph.plugins.library;

import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.swing.JOptionPane;

import org.jgraph.pad.coreframework.actions.AbstractActionFile;
import org.jgraph.pad.resources.Translator;

public class FileLibraryOpenURL extends AbstractActionFile {

	public void actionPerformed(ActionEvent e) {
		String name =
			JOptionPane.showInputDialog(Translator.getString("URLDialog", new Object[]{"test.lib"}));
		if (name != null) {
			try {
				URL location = new URL(name);
				InputStream f = location.openStream();
				f = new GZIPInputStream(f);
				ObjectInputStream in = new ObjectInputStream(f);
				LibraryDecorator library = (LibraryDecorator) getCurrentDocument().getPluginsMap().get(LibraryDecorator.LIBRARY_PLUGIN);
				library.getLibraryPanel().openLibrary(
					in.readObject());
				in.close();
				// Display Library
				library.getSplitPane().resetToPreferredSizes();
			} catch (Exception ex) {
				graphpad.error(ex.toString());
			} finally {
				graphpad.repaint();
			}
		}
	}

}
