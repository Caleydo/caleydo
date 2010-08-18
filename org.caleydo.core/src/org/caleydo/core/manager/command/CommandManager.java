package org.caleydo.core.manager.command;

import org.caleydo.core.command.CmdCreateIDCategory;
import org.caleydo.core.command.CmdCreateIDType;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.command.data.CmdDataCreateDataDomain;
import org.caleydo.core.command.data.CmdDataCreateSet;
import org.caleydo.core.command.data.CmdDataCreateStorage;
import org.caleydo.core.command.data.CmdDataCreateVirtualArray;
import org.caleydo.core.command.data.CmdSetDataRepresentation;
import org.caleydo.core.command.data.filter.CmdDataFilterMinMax;
import org.caleydo.core.command.data.parser.CmdLoadFileLookupTable;
import org.caleydo.core.command.data.parser.CmdLoadFileNStorages;
import org.caleydo.core.command.system.CmdSystemExit;
import org.caleydo.core.command.view.CmdCreateView;
import org.caleydo.core.command.view.CmdViewCreateRcpGLCanvas;
import org.caleydo.core.parser.parameter.IParameterHandler;

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
	public ICommand createCommand(final IParameterHandler phAttributes) {

		ECommandType cmdType =
			ECommandType.valueOf(phAttributes.getValueString(ECommandType.TAG_TYPE.getXmlKey()));

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
	public ICommand createCommandByType(final ECommandType cmdType) {
		ICommand createdCommand = null;

		switch (cmdType) {

			case CREATE_ID_CATEGORY: {
				createdCommand = new CmdCreateIDCategory(cmdType);
				break;
			}
			case CREATE_ID_TYPE: {
				createdCommand = new CmdCreateIDType(cmdType);
				break;
			}
			case LOAD_LOOKUP_TABLE_FILE: {
				createdCommand = new CmdLoadFileLookupTable(cmdType);
				break;
			}
			case LOAD_DATA_FILE: {
				createdCommand = new CmdLoadFileNStorages(cmdType);
				break;
			}
			case CREATE_DATA_DOMAIN: {
				createdCommand = new CmdDataCreateDataDomain(cmdType);
				break;
			}
			case CREATE_STORAGE: {
				createdCommand = new CmdDataCreateStorage(cmdType);
				break;
			}
			case CREATE_VIRTUAL_ARRAY: {
				createdCommand = new CmdDataCreateVirtualArray(cmdType);
				break;
			}
			case CREATE_SET_DATA: {
				createdCommand = new CmdDataCreateSet(cmdType);
				break;
			}
			case CREATE_VIEW_RCP_GLCANVAS: {
				createdCommand = new CmdViewCreateRcpGLCanvas(cmdType);
				break;
			}
			case CREATE_GL_VIEW: {
				createdCommand = new CmdCreateView(cmdType);
				break;
			}
			case SYSTEM_SHUT_DOWN: {
				createdCommand = new CmdSystemExit(cmdType);
				break;
			}
			case SET_DATA_REPRESENTATION: {
				createdCommand = new CmdSetDataRepresentation(cmdType);
				break;
			}
			case DATA_FILTER_MIN_MAX: {
				createdCommand = new CmdDataFilterMinMax(cmdType);
				break;
			}
			default:
				throw new IllegalStateException("Unsupported CommandQueue key= [" + cmdType + "]");
		}

		return createdCommand;
	}
}
