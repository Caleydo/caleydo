package org.caleydo.core.parser.ascii;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.gui.SWTGUIManager;

/**
 * Loader for raw data data sets in text format.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AbstractLoader
	implements IParserObject
{

	protected final IGeneralManager generalManager;

	protected final ISWTGUIManager swtGuiManager;

	/**
	 * File name
	 */
	private String sFileName = "";

	/**
	 * Defines the number of lines to be read from a file. only useful, if
	 * loadData_TestLinesToBeRead() was called before reading the file.
	 * 
	 * @see org.caleydo.core.parser.ascii.AbstractLoader#computeNumberOfLinesInFile(BufferedReader)
	 * @see org.caleydo.core.parser.ascii.AbstractLoader#loadData_TestLinesToBeRead(String)
	 */
	private int iLinesInFileToBeRead = -1;

	/**
	 * Stores the current position of the progress bar.
	 * 
	 * @see org.caleydo.core.parser.ascii.AbstractLoader#progressBarCurrentPosition()
	 */
	private int iProgressBarCurrentPosition;

	/**
	 * Stores the current position of the progress bar after calling
	 * 
	 * @see org.caleydo.core.parser.ascii.AbstractLoader#progressBarSetStoreInitTitle(String,
	 *      int, int)
	 * @see org.caleydo.core.parser.ascii.AbstractLoader#progressBarStoredIncrement()
	 * @see org.caleydo.core.parser.ascii.AbstractLoader#fProgressBarInc
	 */
	private float fProgressBarIndex;

	/**
	 * Increments progress bar index. Call progressBarIncrement() increment the
	 * progress bar.
	 * 
	 * @see org.caleydo.core.parser.ascii.AbstractLoader#progressBarStoredIncrement()
	 * @see org.caleydo.core.parser.ascii.AbstractLoader#fProgressBarIndex
	 */
	private float fProgressBarInc;

	/**
	 * Define numbers of lines to skip as assumed to be the header of a file.
	 * Defines how many lines are part of the header file. By default these
	 * lines are skipped during parsing. Default is 32, because gpr files have a
	 * header of that size!
	 */
	protected int iStartParsingAtLine = 0;

	/**
	 * Define numbers of lines to skip as assumed to be the header of a file.
	 * Default is -1 which means until the end of file.
	 */
	protected int iStopParsingAtLine = Integer.MAX_VALUE;

	/**
	 * Define the separator TAB is the default token.
	 */
	protected String sTokenSeperator = IGeneralManager.sDelimiter_Parser_DataItems_Tab;

	protected int iLineInFile = 0;

	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 * @param setFileName
	 */
	public AbstractLoader(final IGeneralManager generalManager, final String setFileName)
	{

		this.generalManager = generalManager;

		swtGuiManager = (SWTGUIManager) generalManager.getSWTGUIManager();

		assert generalManager != null : "null-pointer in constructor";

		this.sFileName = setFileName;

		init();
	}

	/**
	 * Set the current token separator.
	 * 
	 * @param token current token separator
	 */
	public final void setTokenSeperator(final String token)
	{

		if (token.equals("\\t"))
			sTokenSeperator = "\t";
		else
			sTokenSeperator = token;
	}

	/**
	 * Get the current token separator.
	 * 
	 * @return current token separator
	 */
	public final String getTokenSeperator()
	{

		return sTokenSeperator;
	}

	/**
	 * Set the current file name.
	 * 
	 * @param setFileName set current file name
	 */
	public final void setFileName(String setFileName)
	{

		this.sFileName = setFileName;
	}

	/**
	 * Get the filename for the current file.
	 * 
	 * @return current file name
	 */
	public final String getFileName()
	{

		return this.sFileName;
	}

	public final void setStartParsingStopParsingAtLine(final int iStartParsingAtLine,
			final int iStopParsingAtLine)
	{

		this.iStartParsingAtLine = iStartParsingAtLine;

		if (iStopParsingAtLine < 0)
		{
			this.iStopParsingAtLine = Integer.MAX_VALUE;
			return;
		}

		if (iStartParsingAtLine > iStopParsingAtLine)
		{
			this.iStopParsingAtLine = Integer.MAX_VALUE;
			// generalManager.logMsg(
			// "AMicroArrayLoader.setStartParsingStopParsingAtLine() stop index is smaller than start index. set stop index to end of file!"
			// ,
			// LoggerType.MINOR_ERROR );
			return;
		}
		this.iStopParsingAtLine = iStopParsingAtLine;
	}

	/**
	 * Get the line the parser starts to read from the file.
	 * 
	 * @return first line to be read
	 */
	public final int getStartParsingAtLine()
	{

		return this.iStartParsingAtLine;
	}

	/**
	 * Return line to stop parsing.
	 * 
	 * @return last line to be parses
	 */
	public final int getStopParsingAtLine()
	{

		return this.iStopParsingAtLine;
	}

	/**
	 * Reads the file and counts the numbers of lines to be read.
	 */
	protected final int computeNumberOfLinesInFile(String sFileName) throws IOException
	{

		int iCountLinesToBeRead = 0;
		int iCountLines = 0;

		try
		{
			BufferedReader brFile = null;

			if (this.getClass().getClassLoader().getResourceAsStream(sFileName) != null)
			{
				brFile = new BufferedReader(new InputStreamReader(this.getClass()
						.getClassLoader().getResourceAsStream(sFileName)));
			}
			else
			{
				brFile = new BufferedReader(new FileReader(sFileName));
			}

			while (((brFile.readLine()) != null) && (iCountLines <= iStopParsingAtLine))
			{
				if (iCountLines > this.iStartParsingAtLine)
					iCountLinesToBeRead++;

				iCountLines++;
			}

			brFile.close();
		}
		catch (IOException ioe)
		{
			// TODO
		}
		catch (Exception ex)
		{
			// TODO
		}

		iLinesInFileToBeRead = iCountLinesToBeRead;

		if (iStopParsingAtLine == Integer.MAX_VALUE)
			iStopParsingAtLine = iLinesInFileToBeRead;

		return iCountLinesToBeRead;
	}

	public boolean loadData()
	{

		BufferedReader brFile = null;

		if (this.getClass().getClassLoader().getResourceAsStream(sFileName) != null)
		{
			brFile = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader()
					.getResourceAsStream(sFileName)));
		}
		else
		{
			try
			{
				brFile = new BufferedReader(new FileReader(sFileName));
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}

		generalManager.getLogger().log(Level.INFO, "Start loading file " + sFileName + "...");

		try
		{
			this.loadDataParseFile(brFile, computeNumberOfLinesInFile(sFileName));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		if (brFile != null)
		{
			try
			{
				brFile.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		generalManager.getLogger().log(Level.INFO,
				"File " + sFileName + " successfully loaded.");

		setArraysToStorages();

		return true;
	}

//	/**
//	 * Sets the progress bar to iPosition [0..199] and define the number of
//	 * increments iStepsTill100_Percent [>0] needed to the the progressbar to
//	 * iMaxProgressBarPosition Call progressBarIncrement() to increments the
//	 * progressbar using the settings. Use progressBarResetTitle() to reset the
//	 * progressbar to the previous position. set fProgressBarIndex = iPosition
//	 * set fProgressBarInc = (200 - iPosition) / iStepsTill100_Percent
//	 * 
//	 * @param sText new text for progress bar
//	 * @param iPosition range [0..200]
//	 * @see org.caleydo.core.parser.ascii.AbstractLoader#fProgressBarInc
//	 * @see org.caleydo.core.parser.ascii.AbstractLoader#fProgressBarIndex
//	 * @see org.caleydo.core.parser.ascii.AbstractLoader#progressBarStoredIncrement()
//	 * @see org.caleydo.core.parser.ascii.AbstractLoader#progressBarResetTitle()
//	 */
//	protected final void progressBarSetStoreInitTitle(final String sText,
//			final int iCurrentProgressBarPosition, final int iMaxProgressBarPosition,
//			final int iStepsTill100_Percent)
//	{
//		swtGuiManager.setLoadingProgressBarPercentage(iCurrentProgressBarPosition);
//		swtGuiManager.setLoadingProgressBarText("Load " + this.getFileName());
//
//		iProgressBarCurrentPosition = iCurrentProgressBarPosition;
//		fProgressBarIndex = iCurrentProgressBarPosition;
//		fProgressBarInc = (float) (100 - iCurrentProgressBarPosition)
//				/ (float) iStepsTill100_Percent;
//	}
//
//	/**
//	 * @param sText new text
//	 * @param iCurrentProgressBarPosition new progress bar position
//	 * @param iStepsTill100_Percent number of incremtens to reach 100 %
//	 * @see org.caleydo.core.parser.ascii.AbstractLoader#progressBarSetStoreInitTitle(String,
//	 *      int, int, int)
//	 */
//	protected final void progressBarSetStoreInitTitle(final String sText,
//			final int iCurrentProgressBarPosition, final int iStepsTill100_Percent)
//	{
//
//		progressBarSetStoreInitTitle(sText, iCurrentProgressBarPosition,
//				100, iStepsTill100_Percent);
//	}
//
//	public final void progressBarStoredIncrement()
//	{
//
//		fProgressBarIndex += fProgressBarInc;
//
//		if ((int) fProgressBarIndex != iProgressBarCurrentPosition)
//		{
//			iProgressBarCurrentPosition = (int) fProgressBarIndex;
//			swtGuiManager.setLoadingProgressBarPercentage(iProgressBarCurrentPosition);
//		}
//	}
//
//	/**
//	 * @param iTicks must be in the range of: currentPercentage - [0..200]
//	 */
//	protected final void progressBarIncrement(int iTicks)
//	{
//		iProgressBarCurrentPosition += iTicks;
//		swtGuiManager.setLoadingProgressBarPercentage(iProgressBarCurrentPosition);
//	}

	protected abstract void loadDataParseFile(BufferedReader brFile,
			final int iNumberOfLinesInFile) throws IOException;

	protected abstract void setArraysToStorages();

}
