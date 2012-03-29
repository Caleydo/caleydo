package org.caleydo.core.parser.ascii;

import java.io.BufferedReader;
import java.io.IOException;
import org.caleydo.core.gui.SWTGUIManager;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Base class for text parsers.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public abstract class ATextParser {

	public static final String SPACE = " ";
	public static final String SEMICOLON = ";";
	public static final String TAB = "\t";

	/** The path of the file to parse */
	private String fileName = "";

	/**
	 * Defines the number of lines to be read from a file. only useful, if loadData_TestLinesToBeRead() was
	 * called before reading the file.
	 */
	int nrLinesToRead = -1;
	int nrLinesToReadWithClusterInfo = -1;

	/** Defines numbers of lines to skip in the read file. This is, e.g., useful to ignore headers. */
	protected int parsingStartLine = 0;

	/** Defines at which line to stop parsing */
	protected int stopParsingAtLine = Integer.MAX_VALUE;

	/** Defines the token separator. TAB is default. */
	protected String tokenSeperator = TAB;

	protected int lineInFile = 0;

	protected SWTGUIManager swtGuiManager;

	/**
	 * Constructor.
	 */
	public ATextParser(final String fileName) {
		this.fileName = fileName;
		this.swtGuiManager = GeneralManager.get().getSWTGUIManager();
	}

	/**
	 * Set the current token separator.
	 * 
	 * @param tokenSeparator
	 */
	public final void setTokenSeperator(final String tokenSeparator) {
		if (tokenSeparator.equals("\\t")) {
			tokenSeperator = "\t";
		}
		else {
			tokenSeperator = tokenSeparator;
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
	 * @param fileName
	 *            set current file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Get the filename for the current file.
	 * 
	 * @return current file name
	 */
	public String getFileName() {
		return this.fileName;
	}

	public final void setStartParsingStopParsingAtLine(int startParsingAtLine, int stopParsingAtLine) {

		this.parsingStartLine = startParsingAtLine;

		if (stopParsingAtLine < 0) {
			this.stopParsingAtLine = Integer.MAX_VALUE;
			return;
		}

		if (startParsingAtLine > stopParsingAtLine) {
			this.stopParsingAtLine = Integer.MAX_VALUE;
			return;
		}
		this.stopParsingAtLine = stopParsingAtLine;

		nrLinesToRead = stopParsingAtLine - parsingStartLine + 1;
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

			while (brFile.readLine() != null && iCountLines <= stopParsingAtLine) {
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

		if (stopParsingAtLine == Integer.MAX_VALUE) {
			stopParsingAtLine = nrLinesToRead;
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

		return true;
	}

	protected abstract void loadDataParseFile(BufferedReader brFile, final int iNumberOfLinesInFile)
		throws IOException;

	// protected abstract void setArraysToDimensions();
}
