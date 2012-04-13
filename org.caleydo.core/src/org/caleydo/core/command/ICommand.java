/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.command;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.parser.parameter.ParameterHandler;

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

	public abstract void setParameterHandler(ParameterHandler parameterHandler);

	/**
	 * Get type information on this command.
	 * 
	 * @return command type of this class
	 * @throws PrometheusCommandException
	 * @see org.caleydo.core.command.factory.CommandFactory.getCommandType()
	 */
	public abstract CommandType getCommandType();

	/**
	 * Method returns a description of the command. This is mainly used for the UNDO/REDO GUI component to
	 * show what the command is about.
	 * 
	 * @return
	 */
	public abstract String getInfoText();
}
