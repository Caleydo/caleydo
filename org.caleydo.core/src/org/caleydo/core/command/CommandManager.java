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

import org.caleydo.core.command.data.parser.CmdParseIDMapping;
import org.caleydo.core.parser.parameter.ParameterHandler;

/**
 * Manager for creating commands.
 * 
 * @author Marc Streit
 */
public class CommandManager {

	/**
	 * Create a new command. Calls createCommandByType(CommandType) internal.
	 * 
	 * @param phAttributes
	 *            Define several attributes and assign them in new Command
	 * @return new Command with attributes defined in phAttributes
	 */
	public ICommand createCommand(final ParameterHandler phAttributes) {

		CommandType cmdType =
			CommandType.valueOf(phAttributes.getValueString(CommandType.TAG_TYPE.getXmlKey()));

		ICommand createdCommand = createCommandByType(cmdType);

		if (phAttributes != null) {
			createdCommand.setParameterHandler(phAttributes);
		}

		createdCommand.doCommand();

		return createdCommand;
	}

	/**
	 * Create a new command assigned to a cmdType.
	 * 
	 * @param cmdType
	 *            specify the ICommand to be created.
	 * @return new ICommand
	 */
	public ICommand createCommandByType(final CommandType cmdType) {
		ICommand createdCommand = null;

		switch (cmdType) {

			case CREATE_ID_CATEGORY: {
				createdCommand = new CmdCreateIDCategory();
				break;
			}
			case CREATE_ID_TYPE: {
				createdCommand = new CmdCreateIDType();
				break;
			}
			case PARSE_ID_MAPPING: {
				createdCommand = new CmdParseIDMapping();
				break;
			}
			default:
				throw new IllegalStateException("Unsupported CommandQueue key= [" + cmdType + "]");
		}

		return createdCommand;
	}
}
