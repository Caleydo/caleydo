package org.caleydo.core.command;

import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Command creates a new ID type.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class CmdCreateIDType
	extends ACmdCreational<IDType> {

	private String typeName;
	private EStorageType storageType;
	private IDCategory idCategory;

	/**
	 * Constructor.
	 */
	public CmdCreateIDType(final ECommandType cmdType) {
		super(cmdType);

	}

	/**
	 * Load data from file using a token pattern.
	 */
	@Override
	public void doCommand() {

		createdObject = IDType.registerType(typeName, idCategory, storageType);

		generalManager.getLogger().log(
			new Status(IStatus.INFO, GeneralManager.PLUGIN_ID, "Created ID Type " + createdObject));
	}

	@Override
	public void undoCommand() {
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);

		typeName = detail;
		idCategory = IDCategory.getIDCategory(attrib1);
		storageType = EStorageType.valueOf(attrib2);

	}

	public void setAttributes(String typeName, IDCategory idCategory, EStorageType storageType) {
		this.typeName = typeName;
		this.idCategory = idCategory;
		this.storageType = storageType;
	}
}
