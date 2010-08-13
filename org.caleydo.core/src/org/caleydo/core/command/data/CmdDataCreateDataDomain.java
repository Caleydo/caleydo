package org.caleydo.core.command.data;

import java.util.StringTokenizer;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Command creates a new data domain
 * 
 * @author Alexander Lex
 */
public class CmdDataCreateDataDomain
	extends ACmdCreational<IDataDomain> {
	private String dataDomainType;

	/**
	 * Constructor.
	 */
	public CmdDataCreateDataDomain(final ECommandType cmdType) {
		super(cmdType);

		dataDomainType = "unspecified";
	}

	/**
	 * Load data from file using a token pattern.
	 */
	public void doCommand() {
		createdObject = createDataDomain(dataDomainType);
	}

	private IDataDomain createDataDomain(String dataDomainType) {

		return DataDomainManager.getInstance().createDataDomain(dataDomainType);
	}

	@Override
	public void undoCommand() {
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

		while (strToken_StorageBlock.hasMoreTokens()) {
			/**
			 * Separate "id1 id2 .."
			 */
			StringTokenizer dataDomainToken =
				new StringTokenizer(strToken_StorageBlock.nextToken(),
					IGeneralManager.sDelimiter_Parser_DataItems);

			while (dataDomainToken.hasMoreTokens()) {
				dataDomainType = dataDomainToken.nextToken();
			}
		}
	}

	public void setAttributes(String dataDomainType) {
		this.dataDomainType = dataDomainType;
	}
}
