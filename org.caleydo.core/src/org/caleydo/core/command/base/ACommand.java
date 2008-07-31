package org.caleydo.core.command.base;

import java.io.Serializable;
import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.data.AManagedObject;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Abstract base class for all commands. Supports serialization for exporting
 * commands.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class ACommand
	extends AManagedObject
	implements ICommand, Serializable
{

	/**
	 * Reference to ICommandManager
	 */
	protected transient ICommandManager commandManager;

	private CommandQueueSaxType commandQueueSaxType;

	/**
	 * Constructor.
	 */
	public ACommand(final int iSetCollectionId, final IGeneralManager generalManager,
			final ICommandManager commandManager, final CommandQueueSaxType commandQueueSaxType)
	{

		super(iSetCollectionId, generalManager);

		this.commandManager = commandManager;
		this.commandQueueSaxType = commandQueueSaxType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.IUniqueManagedObject#getBaseType()
	 */
	public final EManagerObjectType getBaseType()
	{

		return EManagerObjectType.COMMAND;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#getCommandType()
	 */
	public final CommandQueueSaxType getCommandType()
	{

		return commandQueueSaxType;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.command.ICommand#setParameterHandler(org.caleydo.core
	 * .parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler phHandler)
	{

	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#getInfoText()
	 */
	public String getInfoText()
	{

		return commandQueueSaxType.getInfoText() + " [" + this.getId() + "]";
	}

	protected final void setCommandQueueSaxType(final CommandQueueSaxType commandQueueSaxType)
	{

		this.commandQueueSaxType = commandQueueSaxType;
	}

	/**
	 * Overrides setGeneralManager and sets command manager after serialization.
	 */
	public void setGeneralManager(final IGeneralManager generalManager)
	{

		this.generalManager = generalManager;
		this.commandManager = generalManager.getCommandManager();
	}
}
