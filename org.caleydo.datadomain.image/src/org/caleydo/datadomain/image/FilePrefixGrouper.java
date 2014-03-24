/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Set;
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
		files = new TreeMap<>();
		groups = new TreeMap<>();
	}

	public FilePrefixGrouper(FilePrefixGrouper other) {
		files = new TreeMap<>(other.files);
		groups = new TreeMap<>();
		for (Entry<String, SortedSet<String>> e : other.groups.entrySet())
			groups.put(e.getKey(), new TreeSet<>(e.getValue()));
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
		String base_name = file.getName().substring(0, ext_sep);

		if (LoadImageSetPage.EXTENSIONS_CFG.contains(ext))
			base_name += "_" + ext;
		else if (!LoadImageSetPage.EXTENSIONS_IMG.contains(ext))
			return;

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

		List<String> fileNames = new ArrayList<>(files.keySet());
		Set<String> ungrouped = new TreeSet<>(files.keySet());

		// Check every file if it is a base of other file(s)
		ListIterator<String> base = fileNames.listIterator();
		while (base.hasNext()) {
			String baseName = base.next();

			if (!ungrouped.contains(baseName))
				continue;

			SortedSet<String> group = null;

			ListIterator<String> other = fileNames.listIterator(base.nextIndex());
			while (other.hasNext()) {
				String otherName = other.next();

				if (otherName.compareToIgnoreCase(baseName) == 0 || !otherName.startsWith(baseName))
					continue;

				if (group == null) {
					group = new TreeSet<>();
					groups.put(baseName, group);
				}

				group.add(otherName);
				ungrouped.remove(otherName);
			}

			if (group != null)
				ungrouped.remove(baseName);
		}

		for (String name : ungrouped)
			groups.put(name, new TreeSet<String>());
	}

	public static String stringSuffix(String str, char separator) {
		int suffixStart = str.lastIndexOf('_');
		if (suffixStart < 0 || suffixStart >= str.length())
			return "";
		return str.substring(suffixStart + 1);
	}

}
