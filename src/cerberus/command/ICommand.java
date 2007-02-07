/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command;

import cerberus.util.exception.CerberusRuntimeException;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.data.IUniqueObject;

/**
 * Design Pattern "Command" ;behaviour pattern
 * 
 * Is combined with Design Pattern "IMemento" to provide Do-Undo
 * 
 * Base interface.
 * 
 * @author Michael Kalkusch
 *
 */
public interface ICommand 
extends IUniqueObject {

	/**
	 * execute a command.
	 * 
	 * @throws PrometheusCommandException if an error occures.
	 */
	public abstract void doCommand() 
		throws CerberusRuntimeException;
	
	/**
	 * Undo the command.
	 *
	 * @throws PrometheusCommandException if an error occures.
	 */
	public abstract void undoCommand() 
		throws CerberusRuntimeException;
	
	public abstract void setParameterHandler( IParameterHandler refParameterHandler);
	
	//public abstract void setAttribute( String, int, boolean);
	
	/**
	 * Tests, if two commands are of the same type.
	 * 
	 * @param compareToObject
	 * @return TRUE if both commands are of the same type.
	 */
	abstract boolean isEqualType(ICommand compareToObject);
	
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
		throws CerberusRuntimeException;	

}
