package org.caleydo.core.manager.command.factory;

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
import org.caleydo.core.command.system.CmdLoadGlyphDefinition;
import org.caleydo.core.command.system.CmdLoadPathwayData;
import org.caleydo.core.command.system.CmdSystemExit;
import org.caleydo.core.command.view.opengl.CmdCreateView;
import org.caleydo.core.command.view.rcp.CmdViewCreateRcpGLCanvas;
import org.caleydo.core.command.view.swt.CmdViewCreateGlyphConfiguration;
import org.caleydo.core.command.view.swt.CmdViewCreateSwtGLCanvas;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;

/**
 * Class is responsible for creating the commands. The commands are created according to the command type.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CommandFactory
	implements ICommandFactory {
	private ICommand lastCommand;

	protected final IGeneralManager generalManager;

	protected final ICommandManager commandManager;

	/**
	 * Constructor.
	 */
	public CommandFactory() {
		this.generalManager = GeneralManager.get();
		this.commandManager = generalManager.getCommandManager();
	}

	@Override
	public ICommand createCommandByType(final ECommandType cmdType) {
		ICommand createdCommand = null;

		switch (cmdType) {
			
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
			case CREATE_VIEW_SWT_GLCANVAS: {
				createdCommand = new CmdViewCreateSwtGLCanvas(cmdType);
				break;
			}
			case CREATE_VIEW_RCP_GLCANVAS: {
				createdCommand = new CmdViewCreateRcpGLCanvas(cmdType);
				break;
			}
			case CREATE_VIEW_GLYPHCONFIG: {
				createdCommand = new CmdViewCreateGlyphConfiguration(cmdType);
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
			case LOAD_GLYPH_DEFINITIONS: {
				createdCommand = new CmdLoadGlyphDefinition(cmdType);
				break;
			}
			case LOAD_PATHWAY_DATA: {
				createdCommand = new CmdLoadPathwayData(cmdType);
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
		} // end switch

		return createdCommand;
	}

	/**
	 * Since the last created command is stored its reference is returned. Note: be carefully with this
	 * method, because maybe the command was already executed or destroyed, or a new command was created
	 * meanwhile
	 * 
	 * @return reference to last created command
	 */
	protected ICommand getLastCreatedCommand() {
		return lastCommand;
	}
}
