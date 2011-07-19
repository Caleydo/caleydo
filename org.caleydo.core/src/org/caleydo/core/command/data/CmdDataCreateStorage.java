package org.caleydo.core.command.data;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.data.collection.storage.AStorage;
import org.caleydo.core.manager.data.storage.StorageManager;
import org.caleydo.core.manager.id.EManagedObjectType;

/**
 * Command creates a new storage.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class CmdDataCreateStorage
	extends ACmdCreational<AStorage> {
	private EManagedObjectType storageType;

	/**
	 * Constructor.
	 */
	public CmdDataCreateStorage() {
		super(CommandType.CREATE_STORAGE);
	}

	@Override
	public void doCommand() {
		StorageManager storageManager = generalManager.getStorageManager();
		createdObject = storageManager.createStorage(storageType);

		generalManager.getIDCreator().mapInternalToExternalID(createdObject.getID(), externalID);

		// generalManager.getLogger().log(new Status(Status.INFO, GeneralManager.PLUGIN_ID,
		// "Created Storage with ID: " + createdObject.getID()));
	}

	@Override
	public void undoCommand() {
	}

	public void setAttributes(EManagedObjectType stroageType) {
		this.storageType = stroageType;
	}
}
