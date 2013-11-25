/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.data.importer.tcga;

import java.io.File;
import java.net.URL;

/**
 * Info of a downloaded TCGA file.
 *
 * @author Christian
 *
 */
public class TCGAFileInfo {

	/**
	 * The locally stored file.
	 */
	private final File file;
	/**
	 * The url of the archive the file was extracted from.
	 */
	private final URL archiveURL;
	/**
	 * The source file name within the archive.
	 */
	private final String sourceFileName;

	public TCGAFileInfo(File file, URL archiveURL, String sourceFileName) {
		this.file = file;
		this.archiveURL = archiveURL;
		this.sourceFileName = sourceFileName;
	}

	/**
	 * @return the file, see {@link #file}
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @return the archiveURL, see {@link #archiveURL}
	 */
	public URL getArchiveURL() {
		return archiveURL;
	}

	/**
	 * @return the sourceFileName, see {@link #sourceFileName}
	 */
	public String getSourceFileName() {
		return sourceFileName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((archiveURL == null) ? 0 : archiveURL.hashCode());
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + ((sourceFileName == null) ? 0 : sourceFileName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TCGAFileInfo other = (TCGAFileInfo) obj;
		if (archiveURL == null) {
			if (other.archiveURL != null)
				return false;
		} else if (!archiveURL.equals(other.archiveURL))
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (sourceFileName == null) {
			if (other.sourceFileName != null)
				return false;
		} else if (!sourceFileName.equals(other.sourceFileName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TCGAFileInfo [");
		builder.append(archiveURL);
		builder.append("!");
		builder.append(sourceFileName);
		builder.append(" => ");
		builder.append(file);
		builder.append("]");
		return builder.toString();
	}

}
