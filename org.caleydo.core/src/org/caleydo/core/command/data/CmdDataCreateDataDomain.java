package org.caleydo.core.command.data;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;

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
	public CmdDataCreateDataDomain() {
		super(CommandType.CREATE_DATA_DOMAIN);

		dataDomainType = "unspecified";
	}

	/**
	 * Load data from file using a token pattern.
	 */
	@Override
	public void doCommand() {
		createdObject = createDataDomain(dataDomainType);
	}

	private IDataDomain createDataDomain(String dataDomainType) {

		return DataDomainManager.get().createDataDomain(dataDomainType);
	}

	@Override
	public void undoCommand() {
	}

	public void setAttributes(String dataDomainType) {
		this.dataDomainType = dataDomainType;
	}
}
