package org.caleydo.core.command.data.parser;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACommand;
import org.caleydo.core.data.collection.set.LoadDataParameters;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.parser.ascii.tabular.TabularAsciiDataReader;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.system.StringConversionTool;
import org.eclipse.core.runtime.Status;

/**
 * Command loads data from file using a token pattern and a target ISet. Use AMicroArrayLoader to load data
 * set.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdLoadFileNStorages
	extends ACommand {

	private ArrayList<Integer> iAlStorageIDs;

	private LoadDataParameters loadDataParameters;

	private boolean bParsingOK = false;

	EDataDomain dataDomain;

	/**
	 * Constructor.
	 */
	public CmdLoadFileNStorages(final ECommandType cmdType) {
		super(cmdType);
		loadDataParameters = new LoadDataParameters();
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);

		loadDataParameters.setFileName(parameterHandler.getValueString(ECommandType.TAG_DETAIL.getXmlKey()));
		// this.sTreeFileName = parameterHandler.getValueString(ECommandType.TAG_DETAIL.getXmlKey());
		loadDataParameters.setInputPattern(parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE1
			.getXmlKey()));

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
			loadDataParameters.setStartParseFileAtLine(iArrayStartStop[0]);

			if (iArrayStartStop.length > 1) {

				if (iArrayStartStop[0] > iArrayStartStop[1] && iArrayStartStop[1] != -1) {
					generalManager.getLogger().log(
						new Status(Status.ERROR, GeneralManager.PLUGIN_ID, "Ignore stop inde="
							+ iArrayStartStop[1] + " because it is maller that start index="
							+ iArrayStartStop[0]));

					return;
				}
				loadDataParameters.setStopParseFileAtLine(iArrayStartStop[1]);
			} // if ( iArrayStartStop.length > 0 )
		} // if ( iArrayStartStop.length > 0 )

		String dataDomainString = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE4.getXmlKey());
		dataDomain = EDataDomain.valueOf(dataDomainString);
		loadDataParameters.setDataDomain(dataDomain);

	}

	public void setAttributes(final ArrayList<Integer> iAlStorageId,
		final LoadDataParameters loadDataParameters) {
		this.dataDomain = loadDataParameters.getDataDomain();
		this.iAlStorageIDs = iAlStorageId;
		this.loadDataParameters = loadDataParameters;
	}

	@Override
	public void doCommand() {
		generalManager.getLogger().log(
			new Status(Status.INFO, GeneralManager.PLUGIN_ID, "Loading data from file "
				+ loadDataParameters.getFileName() + " using token pattern "
				+ loadDataParameters.getInputPattern() + ". Data is stored in Storage with ID "
				+ iAlStorageIDs.toString()));

		TabularAsciiDataReader loader =
			new TabularAsciiDataReader(loadDataParameters.getFileName(), dataDomain);
		loader.setTokenPattern(loadDataParameters.getInputPattern());
		loader.setTargetStorages(iAlStorageIDs);
		if (loadDataParameters.isUseExperimentClusterInfo())
			loader.enableExperimentClusterInfo();
		loader.setStartParsingStopParsingAtLine(loadDataParameters.getStartParseFileAtLine(),
			loadDataParameters.getStopParseFileAtLine());

		if (loadDataParameters.getDelimiter() != null && !loadDataParameters.getDelimiter().isEmpty()) {
			loader.setTokenSeperator(loadDataParameters.getDelimiter());
		}

		bParsingOK = loader.loadData();

		generalManager.getGUIBridge().setFileNameCurrentDataSet(loadDataParameters.getFileName());

		GeneralManager.get().getUseCase(dataDomain).setLoadDataParameters(loadDataParameters);
		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() {

	}

	public boolean isParsingOK() {
		return bParsingOK;
	}
}
