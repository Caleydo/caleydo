/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.data.importer.tcga;

import java.io.File;

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
	private final String archiveURL;
	/**
	 * The source file name within the archive.
	 */
	private final String sourceFileName;

	public TCGAFileInfo(File file, String archiveURL, String sourceFileName) {
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
	public String getArchiveURL() {
		return archiveURL;
	}

	/**
	 * @return the sourceFileName, see {@link #sourceFileName}
	 */
	public String getSourceFileName() {
		return sourceFileName;
	}

}
