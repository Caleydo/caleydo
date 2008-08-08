package org.caleydo.core.command.data;

import java.util.logging.Level;
import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabel;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Command creates a new storage.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class CmdDataCreateStorage
	extends ACmdCreate_IdTargetLabel
{
	private IStorage storage;
	
	private EManagedObjectType storageType;

	/**
	 * Constructor.
	 */
	public CmdDataCreateStorage(final CommandType cmdType)
	{
		super(cmdType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{
		IStorageManager storageManager = generalManager.getStorageManager();
		storage = (IStorage) storageManager.createStorage(storageType);
		storage.setLabel(sLabel);

		generalManager.getIDManager().mapInternalToExternalID(storage.getID(), iExternalID);
		
		generalManager.getLogger().log(Level.INFO, "Created Storage with ID: " + storage.getID());
		commandManager.runDoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_IdTargetLabel#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		String sAttrib1 = parameterHandler.getValueString(CommandType.TAG_ATTRIBUTE1
				.getXmlKey());

		if (sAttrib1.length() > 0)
		{
			if (sAttrib1.equalsIgnoreCase("NOMINAL"))
				storageType = EManagedObjectType.STORAGE_NOMINAL;
			else if (sAttrib1.equalsIgnoreCase("NUMERICAL"))
				storageType = EManagedObjectType.STORAGE_NUMERICAL;
			else
				throw new CaleydoRuntimeException(
						"attrib1 of CREATE_STORAGE must be either NUMERICAL or NOMINAL, but was neither",
						CaleydoRuntimeExceptionType.COMMAND);
		}

	}

	public void setAttributes(int iStorageID, EManagedObjectType stroageType)
	{
		iExternalID = iStorageID;
		this.storageType = stroageType;
	}
	
	public int getStorageID() 
	{
		return storage.getID();
	}
}
