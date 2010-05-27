package org.caleydo.core.command.data;

import java.util.StringTokenizer;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.EDataDomain;
import org.caleydo.core.manager.datadomain.UnspecifiedDataDomain;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Command creates a new data domain
 * 
 * @author Alexander Lex
 */
public class CmdDataCreateDataDomain
	extends ACmdCreational<IDataDomain> {
	private EDataDomain dataDomain;

	/**
	 * Constructor.
	 */
	public CmdDataCreateDataDomain(final ECommandType cmdType) {
		super(cmdType);

		dataDomain = EDataDomain.UNSPECIFIED;
	}

	/**
	 * Load data from file using a token pattern.
	 */
	public void doCommand() {
		createdObject = createUseCase(dataDomain);

		GeneralManager.get().addUseCase(createdObject);

		commandManager.runDoCommand(this);
	}

	private IDataDomain createUseCase(EDataDomain dataDomain) {
		switch (dataDomain) {
			case CLINICAL_DATA:
				return DataDomainManager.getInstance().createDataDomain("org.caleydo.datadomain.clinical.ClinicalDataDomain");
			case GENETIC_DATA:
				return DataDomainManager.getInstance().createDataDomain("org.caleydo.datadomain.genetic.GeneticDataDomain");
			case TISSUE_DATA:
				return DataDomainManager.getInstance().createDataDomain("org.caleydo.datadomain.tissue.TissueDataDomain");
			case PATHWAY_DATA:
				return DataDomainManager.getInstance().createDataDomain("org.caleydo.datadomain.pathway.PathwayDataDomain");
			case UNSPECIFIED:
				return new UnspecifiedDataDomain();
			default:
				throw new IllegalStateException("Unknow data domain type: " + dataDomain);
		}
	}

	@Override
	public void undoCommand() {
		commandManager.runUndoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);

		/**
		 * Read TAG_ATTRIBUTE2 "attrib2" for storage!
		 */

		/**
		 * Separate "text1@text2"
		 */
		StringTokenizer strToken_StorageBlock =
			new StringTokenizer(parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE1.getXmlKey()),
				IGeneralManager.sDelimiter_Paser_DataItemBlock);

		String dataDomainString = "";

		while (strToken_StorageBlock.hasMoreTokens()) {
			/**
			 * Separate "id1 id2 .."
			 */
			StringTokenizer dataDomainToken =
				new StringTokenizer(strToken_StorageBlock.nextToken(),
					IGeneralManager.sDelimiter_Parser_DataItems);

			while (dataDomainToken.hasMoreTokens()) {
				dataDomainString = dataDomainToken.nextToken();
			}
		}

		dataDomain = EDataDomain.valueOf(dataDomainString);

	}

	public void setAttributes(EDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}
}
