/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command;

import cerberus.util.exception.PrometheusCommandException;
import cerberus.data.UniqueInterface;

/**
 * Design Pattern "Command" ;behaviour pattern
 * 
 * Is combined with Design Pattern "Memento" to provide Do-Undo
 * 
 * Base interface.
 * 
 * @author Michael Kalkusch
 *
 */
public interface CommandInterface 
extends UniqueInterface {

	/**
	 * execute a command.
	 * 
	 * @throws PrometheusCommandException if an error occures.
	 */
	public abstract void doCommand() 
		throws PrometheusCommandException;
	
	/**
	 * Undo the command.
	 *
	 * @throws PrometheusCommandException if an error occures.
	 */
	public abstract void undoCommand() 
		throws PrometheusCommandException;
	
	/**
	 * Tests, if two commands are of the same type.
	 * 
	 * @param compareToObject
	 * @return TRUE if both commands are of the same type.
	 */
	abstract boolean isEqualType(CommandInterface compareToObject);
	
	/**
	 * Get type information on this command.
	 * 
	 * @return command type of this class
	 * 
	 * @throws PrometheusCommandException
	 * 
	 * @see cerberus.command.factory.CommandFactory.getCommandType()
	 */
	public abstract CommandType getCommandType() 
		throws PrometheusCommandException;
	
}
