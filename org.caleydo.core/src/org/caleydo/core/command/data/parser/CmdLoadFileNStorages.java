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
import org.caleydo.core.util.system.StringConversionTool;

/**
 * Command loads data from file using a token pattern and a target ISet. Use AMicroArrayLoader to load data
 * set.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdLoadFileNStorages
	extends ACommand {
	private String sFileName;
	private String sTokenPattern;
	private String sTokenSeparator = "";

	private int iStartParseFileAtLine = 0;

	/**
	 * Default is -1 indicating read till end of file.
	 */
	private int iStopParseFileAtLine = -1;

	private ArrayList<Integer> iAlStorageIDs;

	/**
	 * Constructor.
	 */
	public CmdLoadFileNStorages(final ECommandType cmdType) {
		super(cmdType);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);

		this.sFileName = parameterHandler.getValueString(ECommandType.TAG_DETAIL.getXmlKey());
		this.sTokenPattern = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE1.getXmlKey());

		StringTokenizer tokenizer =
			new StringTokenizer(parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE2.getXmlKey()),
				IGeneralManager.sDelimiter_Parser_DataItems);

		iAlStorageIDs = new ArrayList<Integer>();

		while (tokenizer.hasMoreTokens()) {
			iAlStorageIDs.add(Integer.valueOf(tokenizer.nextToken()).intValue());
		}

		// Convert external IDs from XML file to internal IDs
		iAlStorageIDs = GeneralManager.get().getIDManager().convertExternalToInternalIDs(iAlStorageIDs);

		int[] iArrayStartStop =
			StringConversionTool.convertStringToIntArray(parameterHandler
				.getValueString(ECommandType.TAG_ATTRIBUTE3.getXmlKey()), " ");

		if (iArrayStartStop.length > 0) {
			iStartParseFileAtLine = iArrayStartStop[0];

			if (iArrayStartStop.length > 1) {

				if (iArrayStartStop[0] > iArrayStartStop[1] && iArrayStartStop[1] != -1) {
					generalManager.getLogger().log(
						Level.SEVERE,
						"Ignore stop inde=" + iArrayStartStop[1] + " because it is maller that start index="
							+ iArrayStartStop[0]);

					return;
				}
				iStopParseFileAtLine = iArrayStartStop[1];
			} // if ( iArrayStartStop.length > 0 )
		} // if ( iArrayStartStop.length > 0 )
	}

	public void setAttributes(final ArrayList<Integer> iAlStorageId, final String sFileName,
		final String sTokenPattern, final String sTokenSeparator, final int iStartParseFileAtLine,
		final int iStopParseFileAtLine) {
		
		this.iAlStorageIDs = iAlStorageId;
		this.sFileName = sFileName;
		this.sTokenPattern = sTokenPattern;
		this.iStartParseFileAtLine = iStartParseFileAtLine;
		this.iStopParseFileAtLine = iStopParseFileAtLine;
		this.sTokenSeparator = sTokenSeparator;
	}

	@Override
	public void doCommand() {
		generalManager.getLogger().log(
			Level.INFO,
			"Loading data from file " + sFileName + " using token pattern " + sTokenPattern
				+ ". Data is stored in Storage with ID " + iAlStorageIDs.toString());

		TabularAsciiDataReader loader = new TabularAsciiDataReader(sFileName);
		loader.setTokenPattern(sTokenPattern);
		loader.setTargetStorages(iAlStorageIDs);
		loader.setStartParsingStopParsingAtLine(iStartParseFileAtLine, iStopParseFileAtLine);

		if (!sTokenSeparator.isEmpty()) {
			loader.setTokenSeperator(sTokenSeparator);
		}

		loader.loadData();

		generalManager.getGUIBridge().setFileNameCurrentDataSet(sFileName);

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() {

	}
}
