/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.dnd;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * a special {@link IDragInfo} for transferring files
 *
 * @author Samuel Gratzl
 *
 */
public final class FileDragInfo implements IDragInfo, Iterable<File> {
	private final Collection<File> files;

	public FileDragInfo(Collection<File> files) {
		this.files = ImmutableList.copyOf(files);
	}

	public FileDragInfo(String[] fileNames) {
		Builder<File> builder = ImmutableList.builder();
		for (String fileName : fileNames) {
			builder.add(new File(fileName));
		}
		files = builder.build();
	}

	@Override
	public Iterator<File> iterator() {
		return files.iterator();
	}

	/**
	 * @return the files, see {@link #files}
	 */
	public Collection<File> getFiles() {
		return files;
	}

	@Override
	public String getLabel() {
		return files.isEmpty() ? "" : (files.size() == 1 ? files.iterator().next().getAbsolutePath() : StringUtils
				.join(files, ", "));
	}

	/**
	 * @return
	 */
	public String[] getFileNames() {
		String[] r = new String[files.size()];
		int i = 0;
		for (File f : files)
			r[i++] = f.getAbsolutePath();
		return r;
	}
}
