/*
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

package org.jgraph.pad.coreframework.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.JFileChooser;

import org.jgraph.pad.coreframework.GPAbstractActionDefault;

public abstract class AbstractActionFile extends GPAbstractActionDefault {

	/** If the library files should be compressed (zipped)
	 */
	public static final boolean COMPRESS_FILES = true;

	/** Shows a file open dialog and returns the filename. */
	public String openDialog(String message, String extension, String desc) {
		return dialog(message, true, extension, desc);
	}

	/** Shows a file save dialog and returns the filename. */
	public String saveDialog(String message, String extension, String desc) {
		return dialog(message, false, extension, desc);
	}

	/** Opens a dialog and return the filename.
	 *
	 *  Returns <tt>null</tt> if cancelled.
	 * */
	protected String dialog(
		String message,
		boolean open,
		String extension,
		String desc) {
		//FileDialog f = new FileDialog(getFrame(), message, mode);
		JFileChooser f = new JFileChooser();
		f.setDialogTitle(message);
		String ext = (extension != null) ? "." + extension.toLowerCase() : "";
		if (extension != null)
			f.setFileFilter(new MyFileFilter(extension, desc));
		if (open)
			f.showOpenDialog(graphpad.getFrame());
		else
			f.showSaveDialog(graphpad.getFrame());
		if (f.getSelectedFile() != null) {
			String tmp = f.getSelectedFile().getAbsolutePath();
			if (extension != null && !tmp.toLowerCase().endsWith(ext))
				tmp += ext;
			return tmp;
		}
		return null;
	}

	/** Create an object input stream.
	 * */
	public static ObjectInputStream createInputStream(
		String filename,
		boolean compressed)
		throws Exception {
		InputStream f = new FileInputStream(filename);
		if (COMPRESS_FILES && compressed)
			f = new GZIPInputStream(f);
		return new ObjectInputStream(f);
	}

	/** Create an object output stream.
	 * */
	public static ObjectOutputStream createOutputStream(
		String filename,
		boolean compressed)
		throws Exception {
		OutputStream f = new FileOutputStream(filename);
		if (COMPRESS_FILES && compressed)
			f = new GZIPOutputStream(f);
		return new ObjectOutputStream(f);
	}

	/** Create an object input stream.
	 * */
	public static ObjectInputStream createInputStream(String filename)
		throws Exception {
		return createInputStream(filename, true);
	}

	/** Create an object output stream.
	 * */
	public static ObjectOutputStream createOutputStream(String filename)
		throws Exception {
		return createOutputStream(filename, true);
	}

	/**
	 * Filter for the jgraphpad file format (*.pad or *.lib)
	 */
	protected class MyFileFilter extends javax.swing.filechooser.FileFilter {

		/** Extension for this file format.
		 */
		protected String ext;
		/** Full extension for this file format (the Point and the extension)
		 */
		protected String fullExt;
		/** Descrption of the File format
		 */
		protected String desc;

		/** Constructor for the Graphpad specific file format
		 *
		 */
		public MyFileFilter(String extension, String description) {
			ext = extension.toLowerCase();
			fullExt = "." + ext;
			desc = description;
		}

		/** Returns true if the file ends with the full extension or
		 *  if the file is a directory
		 *
		 */
		public boolean accept(File file) {
			return file.isDirectory()
				|| file.getName().toLowerCase().endsWith(fullExt);
		}

		/** returns the desc
		 */
		public String getDescription() {
			return desc;
		}

	}

}
