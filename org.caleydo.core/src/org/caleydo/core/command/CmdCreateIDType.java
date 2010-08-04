package org.caleydo.core.command;

import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Command creates a new ID type.
 * 
 * @author Marc Streit
 */
public class CmdCreateIDType
	extends ACmdCreational<IDType> {

	private String typeName;
	private EStorageType storageType;
	
	/**
	 * Constructor.
	 */
	public CmdCreateIDType(final ECommandType cmdType) {
		super(cmdType);

	}

	/**
	 * Load data from file using a token pattern.
	 */
	public void doCommand() {

		createdObject = IDType.registerType(typeName, storageType);

		generalManager.getLogger().log(
			new Status(IStatus.INFO, IGeneralManager.PLUGIN_ID, "Created ID Type " + createdObject));

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() {
		commandManager.runUndoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);

		typeName = detail;
		storageType = EStorageType.valueOf(attrib1);
		

	}

	public void setAttributes(String typeName, EStorageType storageType) {
		this.typeName = typeName;
		this.storageType = storageType;
	}
}
