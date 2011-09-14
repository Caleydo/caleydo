package org.caleydo.core.parser.ascii;

import java.io.BufferedReader;
import java.io.IOException;

import org.caleydo.core.gui.SWTGUIManager;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Loader for raw data in text format.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class ATextParser {
	
	public static final String SPACE = " ";
	public static final String SEMICOLON = ";";
	public static final String TAB = "\t";
	
	/**
	 * File name
	 */
	private String fileName = "";

	/**
	 * Defines the number of lines to be read from a file. only useful, if loadData_TestLinesToBeRead() was
	 * called before reading the file.
	 */
	int nrLinesToRead = -1;
	int nrLinesToReadWithClusterInfo = -1;

	/**
	 * Define numbers of lines to skip as assumed to be the header of a file. Defines how many lines are part
	 * of the header file. By default these lines are skipped during parsing. Default is 32, because gpr files
	 * have a header of that size!
	 */
	protected int parsingStartLine = 0;

	/**
	 * Define numbers of lines to skip as assumed to be the header of a file. Default is -1 which means until
	 * the end of file.
	 */
	protected int iStopParsingAtLine = Integer.MAX_VALUE;

	/**
	 * Define the separator TAB is the default token.
	 */
	protected String tokenSeperator = TAB;

	protected int lineInFile = 0;

	protected SWTGUIManager swtGuiManager;

	/**
	 * Constructor.
	 */
	public ATextParser(final String sFileName) {
		this.fileName = sFileName;
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
			tokenSeperator = "\t";
		}
		else {
			tokenSeperator = sTokenSeparator;
		}
	}

	/**
	 * Get the current token separator.
	 * 
	 * @return current token separator
	 */
	public final String getTokenSeperator() {

		return tokenSeperator;
	}

	/**
	 * Set the current file name.
	 * 
	 * @param setFileName
	 *            set current file name
	 */
	public final void setFileName(String setFileName) {

		this.fileName = setFileName;
	}

	/**
	 * Get the filename for the current file.
	 * 
	 * @return current file name
	 */
	public final String getFileName() {

		return this.fileName;
	}

	public final void setStartParsingStopParsingAtLine(final int iStartParsingAtLine,
		final int iStopParsingAtLine) {

		this.parsingStartLine = iStartParsingAtLine;

		if (iStopParsingAtLine < 0) {
			this.iStopParsingAtLine = Integer.MAX_VALUE;
			return;
		}

		if (iStartParsingAtLine > iStopParsingAtLine) {
			this.iStopParsingAtLine = Integer.MAX_VALUE;
			return;
		}
		this.iStopParsingAtLine = iStopParsingAtLine;

		nrLinesToRead = iStopParsingAtLine - parsingStartLine + 1;
		nrLinesToReadWithClusterInfo = nrLinesToRead - 2;
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
				if (iCountLines > this.parsingStartLine) {
					iCountLinesToBeRead++;
				}

				iCountLines++;
			}

			brFile.close();
		}
		catch (IOException ioe) {
			throw new RuntimeException();
		}

		nrLinesToRead = iCountLinesToBeRead + parsingStartLine;

		if (iStopParsingAtLine == Integer.MAX_VALUE) {
			iStopParsingAtLine = nrLinesToRead;
		}

		nrLinesToReadWithClusterInfo = nrLinesToRead - 2;
		return nrLinesToRead;
	}

	public boolean loadData() {

		try {

			Logger.log(new Status(IStatus.INFO, GeneralManager.PLUGIN_ID, "Start loading file " + fileName
				+ "..."));

			BufferedReader brFile = GeneralManager.get().getResourceLoader().getResource(fileName);

			this.loadDataParseFile(brFile, computeNumberOfLinesInFile(fileName));

			if (brFile != null) {
				brFile.close();
			}
		}
		catch (Exception e) {
			Logger.log(new Status(IStatus.ERROR, this.toString(), "Could not read data file.", e));
			throw new RuntimeException("Could not read data file '" + fileName + "'", e);
		}

		Logger.log(new Status(IStatus.INFO, toString(), "File " + fileName + " successfully loaded."));

		setArraysToDimensions();

		return true;
	}

	protected abstract void loadDataParseFile(BufferedReader brFile, final int iNumberOfLinesInFile)
		throws IOException;

	protected abstract void setArraysToDimensions();
}
