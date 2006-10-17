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
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ObjectInputStream;

import org.jgraph.pad.coreframework.actions.AbstractActionFile;
import org.jgraph.pad.resources.Translator;

/**
 * Action that opens a library from a file.
 */
public class FileLibraryOpen extends AbstractActionFile {

	public void actionPerformed(ActionEvent e) {
		String libraryExtension = Translator.getString("LibraryExtension");
		String name =
			openDialog(
				Translator.getString("openLabel"),
				libraryExtension,
				Translator.getString(
					"JGraphpadLibrary",
					new Object[] { libraryExtension }));
		if (name != null) {
			Object s = null;
			try {
				//Boolean compress = new Boolean(Translator.getString("compressLibraries"));
				ObjectInputStream in = createInputStream(name, true);
				XMLDecoder dec = new XMLDecoder(new BufferedInputStream(in));
				s = dec.readObject();
				dec.close();
			} catch (Exception ex) {
				try {
					ObjectInputStream in = createInputStream(name, false);
					XMLDecoder dec = new XMLDecoder(new BufferedInputStream(in));
					s = dec.readObject();
					dec.close();
				} catch (Exception exe) {
					graphpad.error(ex.toString());
					ex.printStackTrace();
				}
			}
			if (s != null){
				LibraryDecorator library = (LibraryDecorator) getCurrentDocument().getPluginsMap().get(LibraryDecorator.LIBRARY_PLUGIN);
				library.getLibraryPanel().openLibrary(s);
				library.getSplitPane().resetToPreferredSizes();
			}

			graphpad.repaint();
		}
	}

}
