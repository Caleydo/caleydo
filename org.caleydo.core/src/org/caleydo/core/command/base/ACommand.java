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
package org.caleydo.core.command.base;

import org.caleydo.core.command.CommandManager;
import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.id.ManagedObjectType;
import org.caleydo.core.io.parser.parameter.ParameterHandler;
import org.caleydo.core.manager.GeneralManager;

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
		super(GeneralManager.get().getIDCreator().createID(ManagedObjectType.COMMAND));

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
