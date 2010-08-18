package org.caleydo.core.parser.ascii;

import java.io.BufferedReader;
import java.io.IOException;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.gui.SWTGUIManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Loader for raw data in text format.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AbstractLoader {
	/**
	 * File name
	 */
	private String sFileName = "";

	/**
	 * Defines the number of lines to be read from a file. only useful, if loadData_TestLinesToBeRead() was
	 * called before reading the file.
	 * 
	 * @see org.caleydo.core.parser.ascii.AbstractLoader#computeNumberOfLinesInFile(BufferedReader)
	 * @see org.caleydo.core.parser.ascii.AbstractLoader#loadData_TestLinesToBeRead(String)
	 */
	private int iLinesInFileToBeRead = -1;

	/**
	 * Define numbers of lines to skip as assumed to be the header of a file. Defines how many lines are part
	 * of the header file. By default these lines are skipped during parsing. Default is 32, because gpr files
	 * have a header of that size!
	 */
	protected int iStartParsingAtLine = 0;

	/**
	 * Define numbers of lines to skip as assumed to be the header of a file. Default is -1 which means until
	 * the end of file.
	 */
	protected int iStopParsingAtLine = Integer.MAX_VALUE;

	/**
	 * Define the separator TAB is the default token.
	 */
	protected String sTokenSeperator = GeneralManager.sDelimiter_Parser_DataItems_Tab;

	protected int iLineInFile = 0;

	protected SWTGUIManager swtGuiManager;

	/**
	 * Constructor.
	 */
	public AbstractLoader(final String sFileName) {
		this.sFileName = sFileName;
		this.swtGuiManager = GeneralManager.get().getSWTGUIManager();
	}
	
	/**
	 * Set the current token separator.
	 * 
	 * @param sTokenSeparator
	 *            current token separator
	 */
	public final void setTokenSeperator(final String sTokenSeparator) {

		if (sTokenSeparator.equals("\\t")) {
			sTokenSeperator = "\t";
		}
		else {
			sTokenSeperator = sTokenSeparator;
		}
	}

	/**
	 * Get the current token separator.
	 * 
	 * @return current token separator
	 */
	public final String getTokenSeperator() {

		return sTokenSeperator;
	}

	/**
	 * Set the current file name.
	 * 
	 * @param setFileName
	 *            set current file name
	 */
	public final void setFileName(String setFileName) {

		this.sFileName = setFileName;
	}

	/**
	 * Get the filename for the current file.
	 * 
	 * @return current file name
	 */
	public final String getFileName() {

		return this.sFileName;
	}

	public final void setStartParsingStopParsingAtLine(final int iStartParsingAtLine,
		final int iStopParsingAtLine) {

		this.iStartParsingAtLine = iStartParsingAtLine;

		if (iStopParsingAtLine < 0) {
			this.iStopParsingAtLine = Integer.MAX_VALUE;
			return;
		}

		if (iStartParsingAtLine > iStopParsingAtLine) {
			this.iStopParsingAtLine = Integer.MAX_VALUE;
			return;
		}
		this.iStopParsingAtLine = iStopParsingAtLine;
	}

	/**
	 * Get the line the parser starts to read from the file.
	 * 
	 * @return first line to be read
	 */
	public final int getStartParsingAtLine() {

		return this.iStartParsingAtLine;
	}

	/**
	 * Return line to stop parsing.
	 * 
	 * @return last line to be parses
	 */
	public final int getStopParsingAtLine() {

		return this.iStopParsingAtLine;
	}

	/**
	 * Reads the file and counts the numbers of lines to be read.
	 */
	protected final int computeNumberOfLinesInFile(String sFileName) throws IOException {

		int iCountLinesToBeRead = 0;
		int iCountLines = 0;

		try {
			BufferedReader brFile = GeneralManager.get().getResourceLoader().getResource(sFileName);

			while (brFile.readLine() != null && iCountLines <= iStopParsingAtLine) {
				if (iCountLines > this.iStartParsingAtLine) {
					iCountLinesToBeRead++;
				}

				iCountLines++;
			}

			brFile.close();
		}
		catch (IOException ioe) {
			throw new RuntimeException();
		}

		iLinesInFileToBeRead = iCountLinesToBeRead + iStartParsingAtLine;

		if (iStopParsingAtLine == Integer.MAX_VALUE) {
			iStopParsingAtLine = iLinesInFileToBeRead;
		}

		return iLinesInFileToBeRead;
	}

	public boolean loadData() {

		try {

			GeneralManager.get().getLogger()
				.log(
					new Status(IStatus.INFO, GeneralManager.PLUGIN_ID, "Start loading file " + sFileName
						+ "..."));
			BufferedReader brFile = GeneralManager.get().getResourceLoader().getResource(sFileName);

			this.loadDataParseFile(brFile, computeNumberOfLinesInFile(sFileName));

			if (brFile != null) {
				brFile.close();
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Could not read data file '" + sFileName + "'", e);
		}

		GeneralManager.get().getLogger().log(
			new Status(IStatus.WARNING, GeneralManager.PLUGIN_ID, "File " + sFileName
				+ " successfully loaded."));

		setArraysToStorages();

		return true;
	}

	protected abstract void loadDataParseFile(BufferedReader brFile, final int iNumberOfLinesInFile)
		throws IOException;

	protected abstract void setArraysToStorages();
}
