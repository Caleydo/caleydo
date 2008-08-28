package org.caleydo.core.command.data.parser;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACommand;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.parser.ascii.tabular.TabularAsciiDataReader;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.system.StringConversionTool;

/**
 * Command loads data from file using a token pattern and a target ISet. Use
 * AMicroArrayLoader to load data set.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdLoadFileNStorages
	extends ACommand
{

	protected String sFileName;

	protected String sTokenPattern;

	protected int iStartParseFileAtLine = 0;

	/**
	 * Default is -1 indicating read till end of file.
	 */
	protected int iStopParseFileAtLine = -1;

	protected ArrayList<Integer> iAlStorageIDs;

	/**
	 * Constructor.
	 * 
	 * @param cmdType
	 */
	public CmdLoadFileNStorages(final ECommandType cmdType)
	{
		super(cmdType);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		this.sFileName = parameterHandler.getValueString(ECommandType.TAG_DETAIL.getXmlKey());
		this.sTokenPattern = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE1
				.getXmlKey());

		StringTokenizer tokenizer = new StringTokenizer(parameterHandler
				.getValueString(ECommandType.TAG_ATTRIBUTE2.getXmlKey()),
				IGeneralManager.sDelimiter_Parser_DataItems);

		iAlStorageIDs = new ArrayList<Integer>();

		while (tokenizer.hasMoreTokens())
		{
			iAlStorageIDs.add(StringConversionTool.convertStringToInt(tokenizer.nextToken(),
					-1));
		}

		// Convert external IDs from XML file to internal IDs
		iAlStorageIDs = GeneralManager.get().getIDManager().convertExternalToInternalIDs(
				iAlStorageIDs);

		int[] iArrayStartStop = StringConversionTool.convertStringToIntArrayVariableLength(
				parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE3.getXmlKey()), " ");

		if (iArrayStartStop.length > 0)
		{
			iStartParseFileAtLine = iArrayStartStop[0];

			if (iArrayStartStop.length > 1)
			{

				if ((iArrayStartStop[0] > iArrayStartStop[1]) && (iArrayStartStop[1] != -1))
				{
					generalManager.getLogger().log(
							Level.SEVERE,
							"Ignore stop inde=" + iArrayStartStop[1]
									+ " because it is maller that start index="
									+ iArrayStartStop[0]);

					return;
				}
				iStopParseFileAtLine = iArrayStartStop[1];
			} // if ( iArrayStartStop.length > 0 )
		} // if ( iArrayStartStop.length > 0 )
	}

	public void setAttributes(final ArrayList<Integer> iAlStorageId, final String sFileName,
			final String sTokenPattern, final int iStartParseFileAtLine,
			final int iStopParseFileAtLine)
	{

		iAlStorageIDs = iAlStorageId;
		this.sFileName = sFileName;
		this.sTokenPattern = sTokenPattern;
		this.iStartParseFileAtLine = iStartParseFileAtLine;
		this.iStopParseFileAtLine = iStopParseFileAtLine;
	}

	@Override
	public void doCommand() throws CaleydoRuntimeException
	{
		generalManager.getLogger().log(
				Level.INFO,
				"Loading data from file " + sFileName + " using token pattern "
						+ sTokenPattern + ". Data is stored in Storage with ID "
						+ iAlStorageIDs.toString());

		TabularAsciiDataReader loader = null;

		try
		{
			loader = new TabularAsciiDataReader(sFileName);

			loader.setTokenPattern(sTokenPattern);
			loader.setTargetStorages(iAlStorageIDs);
			loader.setStartParsingStopParsingAtLine(iStartParseFileAtLine,
					iStopParseFileAtLine);

			loader.loadData();

			commandManager.runDoCommand(this);

		} // try
		catch (Exception e)
		{
			generalManager.getLogger().log(Level.SEVERE,
					"Error during parsing data file " + sFileName);
			e.printStackTrace();
		} // catch
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{

	}
}
