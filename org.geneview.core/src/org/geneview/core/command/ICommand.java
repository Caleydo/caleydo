/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.command;

import org.geneview.core.data.IUniqueObject;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;


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
	 * @throws PrometheusCommandException if an error occurs.
	 */
	public abstract void doCommand() 
		throws GeneViewRuntimeException;
	
	/**
	 * Undo the command.
	 *
	 * @throws PrometheusCommandException if an error occurs.
	 */
	public abstract void undoCommand() 
		throws GeneViewRuntimeException;
	
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
	 * @see org.geneview.core.command.factory.CommandFactory.getCommandType()
	 */
	public abstract CommandQueueSaxType getCommandType();	
	
	/**
	 * Method returns a description of the command.
	 * This is mainly used for the UNDO/REDO GUI component 
	 * to show what the command is about.
	 * 
	 * @return
	 */
	public abstract String getInfoText();

}
