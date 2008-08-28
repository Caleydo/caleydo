package org.caleydo.core.command.data;

import java.util.logging.Level;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdCreational;
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
	extends ACmdCreational<IStorage>
{
	private EManagedObjectType storageType;

	/**
	 * Constructor.
	 */
	public CmdDataCreateStorage(final ECommandType cmdType)
	{
		super(cmdType);
	}

	@Override
	public void doCommand() throws CaleydoRuntimeException
	{
		IStorageManager storageManager = generalManager.getStorageManager();
		createdObject = storageManager.createStorage(storageType);
		createdObject.setLabel(sLabel);

		generalManager.getIDManager().mapInternalToExternalID(createdObject.getID(),
				iExternalID);

		generalManager.getLogger().log(Level.INFO,
				"Created Storage with ID: " + createdObject.getID());
		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		if (sAttribute1.length() > 0)
		{
			if (sAttribute1.equalsIgnoreCase("NOMINAL"))
				storageType = EManagedObjectType.STORAGE_NOMINAL;
			else if (sAttribute1.equalsIgnoreCase("NUMERICAL"))
				storageType = EManagedObjectType.STORAGE_NUMERICAL;
			else
				throw new CaleydoRuntimeException(
						"attrib1 of CREATE_STORAGE must be either NUMERICAL or NOMINAL, but was neither",
						CaleydoRuntimeExceptionType.COMMAND);
		}

	}

	public void setAttributes(EManagedObjectType stroageType)
	{
		this.storageType = stroageType;
	}
}
