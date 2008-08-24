package org.caleydo.core.parser.ascii.tabular;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.collection.INominalStorage;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.parser.ascii.AbstractLoader;
import org.caleydo.core.parser.ascii.IParserObject;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.util.system.StringConversionTool;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Loader for tabular data.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class TabularAsciiDataReader
	extends AbstractLoader
	implements IParserObject
{

	/**
	 * Imports data from file to this set. uses first storage and overwrites
	 * first selection.
	 */
	protected ArrayList<IStorage> alTargetStorages;

	protected ArrayList<EStorageType> alColumnDataTypes;

	private ArrayList<int[]> alIntBuffers;

	private ArrayList<float[]> alFloatBuffers;

	private ArrayList<ArrayList<String>> alStringBuffers;

	/**
	 * Constructor.
	 * 
	 */
	public TabularAsciiDataReader(final String sFileName)
	{
		super(sFileName);

		alTargetStorages = new ArrayList<IStorage>();
		alColumnDataTypes = new ArrayList<EStorageType>();

		alIntBuffers = new ArrayList<int[]>();
		alFloatBuffers = new ArrayList<float[]>();
		alStringBuffers = new ArrayList<ArrayList<String>>();

		init();
	}

	/**
	 * Defines a pattern for parsing
	 */
	public final boolean setTokenPattern(final String tokenPattern)
	{

		boolean bAllTokensProper = true;

		StringTokenizer tokenizer = new StringTokenizer(tokenPattern);

		final String sTokenPatternParserSeperator = IGeneralManager.sDelimiter_Parser_DataType;

		while (tokenizer.hasMoreTokens())
		{
			String sBuffer = tokenizer.nextToken(sTokenPatternParserSeperator);

			if (sBuffer.equalsIgnoreCase("abort"))
			{
				alColumnDataTypes.add(EStorageType.ABORT);

				return bAllTokensProper;
			}
			else if (sBuffer.equalsIgnoreCase("skip"))
			{
				alColumnDataTypes.add(EStorageType.SKIP);
			}
			else if (sBuffer.equalsIgnoreCase("int"))
			{
				alColumnDataTypes.add(EStorageType.INT);

			}
			else if (sBuffer.equalsIgnoreCase("float"))
			{
				alColumnDataTypes.add(EStorageType.FLOAT);

			}
			else if (sBuffer.equalsIgnoreCase("string"))
			{
				alColumnDataTypes.add(EStorageType.STRING);
			}
			else
			{
				bAllTokensProper = false;

				GeneralManager.get().getLogger().log(Level.WARNING,
						"Unknown column data type: " + tokenPattern);
			}

		} // end of while

		return bAllTokensProper;
	}

	public void setTargetStorages(final ArrayList<Integer> iAlTargetStorageId)
	{
		for (int iStorageId : iAlTargetStorageId)
		{
			alTargetStorages.add((IStorage) GeneralManager.get().getStorageManager().getItem(
					iStorageId));
		}
	}
	
	protected void allocateStorageBufferForTokenPattern()
	{

		for (EStorageType storageType : alColumnDataTypes)
		{
			switch (storageType)
			{
				case INT:
					alIntBuffers.add(new int[iStopParsingAtLine - iStartParsingAtLine + 1]);
					break;
				case FLOAT:
					alFloatBuffers
							.add(new float[iStopParsingAtLine - iStartParsingAtLine + 1]);
					break;
				case STRING:
					alStringBuffers.add(new ArrayList<String>(iStopParsingAtLine
							- iStartParsingAtLine + 1));
					break;
				case SKIP:
					break;
				case ABORT:
					return;
				default:
					throw new CaleydoRuntimeException("Unknown token pattern detected: "
							+ storageType.toString(), CaleydoRuntimeExceptionType.DATAHANDLING);
			}
		}
	}

	@Override
	protected void loadDataParseFile(BufferedReader brFile, final int iNumberOfLinesInFile)
			throws IOException
	{

		allocateStorageBufferForTokenPattern();

		// Init progress bar
		swtGuiManager.setProgressBarText("Load data file " + this.getFileName());
		
		String sLine;

		int iColumnIndex = 0;
		float fProgressBarFactor = 100f / iStopParsingAtLine;

		while (((sLine = brFile.readLine()) != null) && (iLineInFile < iStopParsingAtLine))
		{
			// Check if line should be ignored
			if (iLineInFile < this.iStartParsingAtLine)
			{
				iLineInFile++;
				continue;
			}

			// StringTokenizer strTokenText = new StringTokenizer(sLine, "\"");
			// strLineBuffer.setLength(0);
			// int iCountTokens = strTokenText.countTokens();
			// if ((iCountTokens % 2) == 0)
			// {
			// strTokenText = new StringTokenizer(sLine.replace("\"\"",
			// "\" \""), "\"");
			// }

			// System.out.println("Line: " +iLineInFile);
			// System.out.println(" I:" + sLine );

			StringTokenizer strTokenLine = new StringTokenizer(sLine, sTokenSeperator);

			iColumnIndex = 0;

			for (EStorageType columnDataType : alColumnDataTypes)
			{
				if(strTokenLine.hasMoreTokens())
				{
					switch (columnDataType)
					{
						case INT:
							
//							try
//							{
								alIntBuffers.get(iColumnIndex)[iLineInFile - iStartParsingAtLine] = StringConversionTool
										.convertStringToInt(strTokenLine.nextToken(), -1);
								iColumnIndex++;
//							}
//							catch (NumberFormatException nfe) 
//							{
//						        MessageBox messageBox = new MessageBox(new Shell(), SWT.ABORT | SWT.IGNORE);
//						        messageBox.setText("Problem during parsing");
//						        messageBox.setMessage("Cannot convert input in line " +iLineInFile);
//						        int state = messageBox.open();
//								switch (state)
//								{
//									case SWT.ABORT:
//										break;
//									case SWT.IGNORE:
//										valString = "SWT.IGNORE";
//										break;
//								}
//							}
							
							break;
						case FLOAT:
							alFloatBuffers.get(iColumnIndex)[iLineInFile - iStartParsingAtLine] = StringConversionTool
									.convertStringToFloat(strTokenLine.nextToken(), -1);
							iColumnIndex++;
							break;
						case STRING:
							alStringBuffers.get(iColumnIndex).add(strTokenLine.nextToken());
							iColumnIndex++;
							break;
						case SKIP: // do nothing
							strTokenLine.nextToken();
							break;
						case ABORT:
							iColumnIndex = alColumnDataTypes.size();
							break;
						default:
							throw new CaleydoRuntimeException(
									"Unknown token pattern detected: "
											+ columnDataType.toString(),
									CaleydoRuntimeExceptionType.DATAHANDLING);
					}
						
					// Check if the line is finished or early aborted
					if (iColumnIndex == alColumnDataTypes.size())
						continue;
				}
			}

			iLineInFile++;
			
			// Update progress bar only on each 100th line
			if (iLineInFile % 1000 == 0)
			{
				swtGuiManager.setProgressBarPercentage(
						(int)(fProgressBarFactor * iLineInFile));
			}
		}
	}

	@Override
	protected void setArraysToStorages()
	{

		int iIntArrayIndex = 0;
		int iFloatArrayIndex = 0;
		int iStringArrayIndex = 0;
		int iStorageIndex = 0;

		for (EStorageType storageType : alColumnDataTypes)
		{
			switch (storageType)
			{
				case INT:
					alTargetStorages.get(iStorageIndex).setRawData(
							alIntBuffers.get(iIntArrayIndex));
					iIntArrayIndex++;
					iStorageIndex++;
					break;
				case FLOAT:
					alTargetStorages.get(iStorageIndex).setRawData(
							alFloatBuffers.get(iFloatArrayIndex));
					iFloatArrayIndex++;
					iStorageIndex++;
					break;
				case STRING:
					((INominalStorage<String>) alTargetStorages.get(iStorageIndex))
							.setRawNominalData(alStringBuffers.get(iStringArrayIndex));
					alStringBuffers.add(new ArrayList<String>(iStopParsingAtLine
							- iStartParsingAtLine + 1));
					iStringArrayIndex++;
					iStorageIndex++;
					break;
				case SKIP: // do nothing
					break;
				case ABORT:
					return;
				default:
					throw new CaleydoRuntimeException("Unknown token pattern detected: "
							+ storageType.toString(), CaleydoRuntimeExceptionType.DATAHANDLING);
			}
		}
	}

	/**
	 * Init data structures. Use this to reset the state also!
	 * 
	 * @see org.caleydo.core.parser.ascii.IParserObject#init()
	 */
	public void init()
	{

		iLineInFile = 0;
	}
}
