package org.caleydo.core.command;

import java.io.Serializable;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Design Pattern "Command" ; behavior pattern Is combined with Design Pattern
 * "IMemento" to provide Do-Undo Base interface.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public interface ICommand
	extends IUniqueObject, Serializable
{

	/**
	 * execute a command.
	 * 
	 * @throws PrometheusCommandException if an error occurs.
	 */
	public abstract void doCommand() throws CaleydoRuntimeException;

	/**
	 * Undo the command.
	 * 
	 * @throws PrometheusCommandException if an error occurs.
	 */
	public abstract void undoCommand() throws CaleydoRuntimeException;

	public abstract void setParameterHandler(IParameterHandler parameterHandler);

	/**
	 * Get type information on this command.
	 * 
	 * @return command type of this class
	 * @throws PrometheusCommandException
	 * @see org.caleydo.core.command.factory.CommandFactory.getCommandType()
	 */
	public abstract CommandQueueSaxType getCommandType();

	/**
	 * Method returns a description of the command. This is mainly used for the
	 * UNDO/REDO GUI component to show what the command is about.
	 * 
	 * @return
	 */
	public abstract String getInfoText();
}
