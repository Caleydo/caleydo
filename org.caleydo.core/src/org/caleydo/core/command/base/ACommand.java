package org.caleydo.core.command.base;

import org.caleydo.core.command.CommandManager;
import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.ParameterHandler;

/**
 * Abstract base class for all commands. Supports serialization for exporting commands.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class ACommand
	extends AUniqueObject
	implements ICommand {
	/**
	 * Reference to ICommandManager
	 */
	protected transient CommandManager commandManager;

	protected transient GeneralManager generalManager;

	private CommandType cmdType;

	/**
	 * Constructor.
	 */
	public ACommand(final CommandType cmdType) {
		super(GeneralManager.get().getIDCreator().createID(EManagedObjectType.COMMAND));

		this.generalManager = GeneralManager.get();
		this.commandManager = generalManager.getCommandManager();
		this.cmdType = cmdType;
	}

	@Override
	public final CommandType getCommandType() {
		return cmdType;
	}

	@Override
	public void setParameterHandler(final ParameterHandler phHandler) {

	}

	@Override
	public String getInfoText() {
		return cmdType.getInfoText() + " [" + this.getID() + "]";
	}
}
