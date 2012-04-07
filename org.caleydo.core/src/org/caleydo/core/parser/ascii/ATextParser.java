package org.caleydo.core.parser.ascii;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

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
	protected String fileName = "";

	/**
	 * Contains the number of lines of the number of lines in the file to be
	 * parsed, after {@link #calculateNumberOfLinesInFile()} was called.
	 */
	int numberOfLinesInFile = -1;

	/**
	 * Defines numbers of lines to skip in the read file. This is, e.g., useful
	 * to ignore headers.
	 */
	protected int parsingStartLine = 0;

	/** Defines at which line to stop parsing */
	protected int stopParsingAtLine = Integer.MAX_VALUE;

	// protected int lineInFile = 0;

	protected SWTGUIManager swtGuiManager;

	/**
	 * Constructor.
	 */
	public ATextParser(final String fileName) {
		this.fileName = fileName;
		this.swtGuiManager = GeneralManager.get().getSWTGUIManager();
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

	public final void setStartParsingStopParsingAtLine(int startParsingAtLine,
			int stopParsingAtLine) {

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

		numberOfLinesInFile = stopParsingAtLine - parsingStartLine + 1;

	}

	/**
	 * Reads the file and counts the numbers of lines to be read.
	 */
	protected final int calculateNumberOfLinesInFile() {
		try {
			LineNumberReader lnr = new LineNumberReader(new FileReader(fileName));
			lnr.skip(Long.MAX_VALUE);
			numberOfLinesInFile = lnr.getLineNumber();
			lnr.close();

		} catch (IOException ioe) {
			throw new IllegalStateException("Could not read from file: " + fileName);
		}

		return numberOfLinesInFile;
	}

	public boolean loadData() {

		try {

			Logger.log(new Status(IStatus.INFO, GeneralManager.PLUGIN_ID,
					"Start loading file " + fileName + "..."));

			BufferedReader reader = new BufferedReader(new FileReader(fileName));

			this.parseFile(reader);

			if (reader != null) {
				reader.close();
			}
		} catch (Exception e) {
			Logger.log(new Status(IStatus.ERROR, this.toString(),
					"Could not read data file.", e));
			throw new IllegalStateException(
					"Could not read data file '" + fileName + "'", e);
		}

		Logger.log(new Status(IStatus.INFO, toString(), "File " + fileName
				+ " successfully loaded."));

		return true;
	}

	protected abstract void parseFile(BufferedReader reader) throws IOException;

	// protected abstract void setArraysToDimensions();
}
