package org.caleydo.core.command.data;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabel;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Command, creates a new storage.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class CmdDataCreateStorage
	extends ACmdCreate_IdTargetLabel
{

	EManagerObjectType storageType;

	/**
	 * Constructor.
	 */
	public CmdDataCreateStorage(final IGeneralManager generalManager,
			final ICommandManager commandManager, final CommandQueueSaxType commandQueueSaxType)
	{

		super(generalManager, commandManager, commandQueueSaxType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{

		IStorageManager storageManager = generalManager.getStorageManager();

		IStorage storage = (IStorage) storageManager.createStorage(storageType);

		storage.setId(iUniqueId);
		storage.setLabel(sLabel);

		storageManager.registerItem(storage, storage.getId());

		generalManager.getLogger().log(Level.INFO, "Created Storage with ID: " + iUniqueId);
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
	 * @see
	 * org.caleydo.core.command.base.ACmdCreate_IdTargetLabel#setParameterHandler
	 * (org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{

		super.setParameterHandler(parameterHandler);

		String sAttrib1 = parameterHandler.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE1
				.getXmlKey());

		if (sAttrib1.length() > 0)
		{
			if (sAttrib1.equalsIgnoreCase("String"))
				storageType = EManagerObjectType.STORAGE_NOMINAL;
			else if (sAttrib1.equalsIgnoreCase("int") || sAttrib1.equalsIgnoreCase("float"))
				storageType = EManagerObjectType.STORAGE_NUMERICAL;
		}

	}

	public void setAttributes(int iStorageID, EManagerObjectType stroageType)
	{

		iUniqueId = iStorageID;
		this.storageType = stroageType;
	}
}
