/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.image;

import java.io.File;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.caleydo.datadomain.image.wizard.LoadImageSetPage;

/**
 * Group files by common prefixes in their names. One filename needs to be a
 * full prefix of another files name to build up a group.
 *
 * @author Thomas Geymayer
 */
public class FilePrefixGrouper {

	protected SortedMap<String, File> files = null;
	protected SortedMap<String, SortedSet<String>> groups;

	public FilePrefixGrouper() {
		files = new TreeMap<String, File>();
		groups = new TreeMap<String, SortedSet<String>>();
	}

	public FilePrefixGrouper(FilePrefixGrouper other) {
		files = new TreeMap<String, File>(other.files);
		groups = new TreeMap<String, SortedSet<String>>();
		for (Entry<String, SortedSet<String>> e : other.groups.entrySet())
			groups.put(e.getKey(), new TreeSet<String>(e.getValue()));
	}

	public SortedMap<String, File> getFiles() {
		return files;
	}

	public SortedMap<String, SortedSet<String>> getGroups() {
		return groups;
	}

	public void add(File file) {
		if (file.isDirectory()) {
			for (File dir : file.listFiles())
				add(dir);
			return;
		}

		int ext_sep = file.getName().lastIndexOf('.');
		if (ext_sep < 0)
			return;

		String ext = file.getName().substring(ext_sep + 1).toLowerCase();
		if (!LoadImageSetPage.EXTENSIONS.contains(ext))
			return;

		String base_name = file.getName().substring(0, ext_sep);
		System.out.println("add " + ext + " (" + base_name + ") "
				+ file.getAbsolutePath());
		files.put(base_name, file);
	}

	public void remove(String name) {
		files.remove(name);
	}

	public boolean isEmpty() {
		return files.isEmpty();
	}

	public void refreshGroups() {
		groups.clear();

		SortedSet<String> ungrouped = new TreeSet<String>(files.keySet());

		// Check every file if it is a base of other file(s)
		for (String base : files.keySet()) {
			SortedSet<String> group = null;

			for (String other : files.keySet()) {
				if (other.compareToIgnoreCase(base) == 0
						|| !other.startsWith(base))
					continue;

				if (group == null) {
					group = new TreeSet<String>();
					groups.put(base, group);
				}

				group.add(other);
				ungrouped.remove(other);
			}

			if (group != null)
				ungrouped.remove(base);
		}

		for (String name : ungrouped)
			groups.put(name, new TreeSet<String>());
	}

}
