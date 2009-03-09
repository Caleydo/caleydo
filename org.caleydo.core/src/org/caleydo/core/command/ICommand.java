package org.caleydo.core.command;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Design Pattern "Command" ; behavior pattern Is combined with Design Pattern "IMemento" to provide Do-Undo
 * Base interface.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public interface ICommand
	extends IUniqueObject {
	/**
	 * Execute a command.
	 */
	public abstract void doCommand();

	/**
	 * Undo the command.
	 */
	public abstract void undoCommand();

	public abstract void setParameterHandler(IParameterHandler parameterHandler);

	/**
	 * Get type information on this command.
	 * 
	 * @return command type of this class
	 * @throws PrometheusCommandException
	 * @see org.caleydo.core.command.factory.CommandFactory.getCommandType()
	 */
	public abstract ECommandType getCommandType();

	/**
	 * Method returns a description of the command. This is mainly used for the UNDO/REDO GUI component to show
	 * what the command is about.
	 * 
	 * @return
	 */
	public abstract String getInfoText();
}
