package org.caleydo.core.command.data;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.ParameterHandler;

/**
 * Command creates a new storage.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class CmdDataCreateStorage
	extends ACmdCreational<IStorage> {
	private EManagedObjectType storageType;

	/**
	 * Constructor.
	 */
	public CmdDataCreateStorage(final ECommandType cmdType) {
		super(cmdType);
	}

	@Override
	public void doCommand() {
		IStorageManager storageManager = generalManager.getStorageManager();
		createdObject = storageManager.createStorage(storageType);

		generalManager.getIDCreator().mapInternalToExternalID(createdObject.getID(), externalID);

		// generalManager.getLogger().log(new Status(Status.INFO, GeneralManager.PLUGIN_ID,
		// "Created Storage with ID: " + createdObject.getID()));
	}

	@Override
	public void undoCommand() {
	}

	@Override
	public void setParameterHandler(final ParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);

		if (attrib1.length() > 0) {
			if (attrib1.equalsIgnoreCase("NOMINAL")) {
				storageType = EManagedObjectType.STORAGE_NOMINAL;
			}
			else if (attrib1.equalsIgnoreCase("NUMERICAL")) {
				storageType = EManagedObjectType.STORAGE_NUMERICAL;
			}
			else
				throw new IllegalArgumentException(
					"attrib1 of CREATE_STORAGE must be either NUMERICAL or NOMINAL, but was neither");
		}

	}

	public void setAttributes(EManagedObjectType stroageType) {
		this.storageType = stroageType;
	}
}
