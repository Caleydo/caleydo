/*
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * GPGraphpad is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * GPGraphpad is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPGraphpad; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.jgraph.pad.coreframework.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import org.jgraph.pad.JGraphpad;
import org.jgraph.pad.coreframework.GPAbstractActionDefault;
import org.jgraph.pad.coreframework.GPGraphpadFile;
import org.jgraph.pad.resources.Translator;
import org.jgraph.pad.util.ExtensionFilter;
import org.jgraph.pad.util.NamedInputStream;

public class FileOpen extends GPAbstractActionDefault {

	public void actionPerformed(ActionEvent e) {
		try {
			NamedInputStream namedInput = provideInput(Translator.getString("FileExtension"));
			InputStream in = namedInput.getInputStream();
			if (namedInput.getName().endsWith("gz"))
				in = new GZIPInputStream(in);
			if (in != null) {
				GPGraphpadFile file = GPGraphpadFile.read(in);
				if (file == null)
					return;
				// add the new document with the new graph and the new model
				graphpad.addDocument(namedInput.getName(), file);
				graphpad.update();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static NamedInputStream provideInput(String fileExtension) {
		PreferencesService preferences = PreferencesService
				.getInstance(JGraphpad.class);// TODO: check if that work!
		ExtensionFilter fileExtensionFilter = new ExtensionFilter(Translator
				.getString("DefaultFileName"), new String[] { fileExtension });

		ArrayList recentFiles = new ArrayList();
		File lastDir = new File(".");
		String recent = preferences.get("recent", "").trim();
		if (recent.length() > 0) {
			recentFiles.addAll(Arrays.asList(recent.split("[|]")));
			lastDir = new File((String) recentFiles.get(0)).getParentFile();
		}
		FileService fileService = FileService.getInstance(lastDir);
		try {
			FileService.Open open = fileService.open(null, null,
					fileExtensionFilter);
			NamedInputStream input = new NamedInputStream();
			input.setInputStream(open.getInputStream());
			input.setName(open.getName());
			return input;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Empty implementation. This Action should be available each time.
	 */
	public void update() {
	};
}
