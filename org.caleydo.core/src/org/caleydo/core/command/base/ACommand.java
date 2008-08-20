package org.caleydo.core.command.base;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Abstract base class for all commands. Supports serialization for exporting
 * commands.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class ACommand
	extends AUniqueObject
	implements ICommand
{
	/**
	 * Reference to ICommandManager
	 */
	protected transient ICommandManager commandManager;

	protected transient IGeneralManager generalManager;
	
	private ECommandType cmdType;

	/**
	 * Constructor.
	 */
	public ACommand(final ECommandType cmdType)
	{
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.COMMAND));

		this.generalManager = GeneralManager.get();
		this.commandManager = generalManager.getCommandManager();
		this.cmdType = cmdType;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#getCommandType()
	 */
	public final ECommandType getCommandType()
	{
		return cmdType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
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
		return cmdType.getInfoText() + " [" + this.getID() + "]";
	}
}
