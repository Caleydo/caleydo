package org.caleydo.core.manager.command.factory;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.command.data.CmdDataCreateSelection;
import org.caleydo.core.command.data.CmdDataCreateSet;
import org.caleydo.core.command.data.CmdDataCreateStorage;
import org.caleydo.core.command.data.CmdDataCreateVirtualArray;
import org.caleydo.core.command.data.filter.CmdDataFilterMath;
import org.caleydo.core.command.data.filter.CmdDataFilterMinMax;
import org.caleydo.core.command.data.parser.CmdLoadFileLookupTable;
import org.caleydo.core.command.data.parser.CmdLoadFileNStorages;
import org.caleydo.core.command.event.CmdEventCreateMediator;
import org.caleydo.core.command.event.CmdEventMediatorAddObject;
import org.caleydo.core.command.queue.CmdSystemRunCmdQueue;
import org.caleydo.core.command.queue.CommandQueueVector;
import org.caleydo.core.command.system.CmdFetchPathwayData;
import org.caleydo.core.command.system.CmdLoadGlyphDefinition;
import org.caleydo.core.command.system.CmdLoadPathwayData;
import org.caleydo.core.command.system.CmdSystemExit;
import org.caleydo.core.command.system.path.CmdSetPathwayDatabasePath;
import org.caleydo.core.command.view.opengl.CmdCreateGLEventListener;
import org.caleydo.core.command.view.opengl.CmdGlObjectPathway3D;
import org.caleydo.core.command.view.rcp.CmdExternalActionTrigger;
import org.caleydo.core.command.view.rcp.CmdExternalFlagSetter;
import org.caleydo.core.command.view.rcp.CmdViewCreateRcpGLCanvas;
import org.caleydo.core.command.view.swt.CmdViewCreateDataEntitySearcher;
import org.caleydo.core.command.view.swt.CmdViewCreateDataExchanger;
import org.caleydo.core.command.view.swt.CmdViewCreateDataExplorer;
import org.caleydo.core.command.view.swt.CmdViewCreateGlyphConfiguration;
import org.caleydo.core.command.view.swt.CmdViewCreateHTMLBrowser;
import org.caleydo.core.command.view.swt.CmdViewCreateImage;
import org.caleydo.core.command.view.swt.CmdViewCreateMixer;
import org.caleydo.core.command.view.swt.CmdViewCreatePathway;
import org.caleydo.core.command.view.swt.CmdViewCreateSetEditor;
import org.caleydo.core.command.view.swt.CmdViewCreateSwtGLCanvas;
import org.caleydo.core.command.view.swt.CmdViewCreateUndoRedo;
import org.caleydo.core.command.view.swt.CmdViewLoadURLInHTMLBrowser;
import org.caleydo.core.command.window.swt.CmdContainerCreate;
import org.caleydo.core.command.window.swt.CmdWindowCreate;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Class is responsible for creating the commands. The commands are created
 * according to the command type.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CommandFactory
	implements ICommandFactory
{
	private ICommand lastCommand;

	protected final IGeneralManager generalManager;

	protected final ICommandManager commandManager;

	/**
	 * Constructor.
	 */
	public CommandFactory()
	{
		this.generalManager = GeneralManager.get();
		this.commandManager = generalManager.getCommandManager();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.command.factory.ICommandFactory#createCommandByType(org.caleydo.core.command.CommandType)
	 */
	public ICommand createCommandByType(final CommandType cmdType)
	{
		ICommand createdCommand = null;

		switch (cmdType)
		{

			/**
			 * ---------------------- DATA LOADING ----------------------
			 */
			case LOAD_LOOKUP_TABLE_FILE:
			{
				createdCommand = new CmdLoadFileLookupTable(cmdType);
				break;
			}
			case LOAD_DATA_FILE:
			{
				createdCommand = new CmdLoadFileNStorages(cmdType);
				break;
			}
			case LOAD_URL_IN_BROWSER:
			{
				createdCommand = new CmdViewLoadURLInHTMLBrowser(cmdType);
				break;
			}

			/**
			 * ---------------------- DATA CONTAINERS ----------------------
			 */
			case CREATE_STORAGE:
			{
				createdCommand = new CmdDataCreateStorage(cmdType);
				break;
			}
			case CREATE_VIRTUAL_ARRAY:
			{
				createdCommand = new CmdDataCreateVirtualArray(cmdType);
				break;
			}
			case CREATE_SET_DATA:
			{
				createdCommand = new CmdDataCreateSet(cmdType);
				break;
			}
//			case CREATE_SELECTION:
//			{
//				createdCommand = new CmdDataCreateSelection(cmdType);
//				break;
//			}

			/**
			 * ---------------------- SWT ----------------------
			 */
			case CREATE_SWT_WINDOW:
			{
				createdCommand = new CmdWindowCreate(cmdType);
				break;
			}
			case CREATE_SWT_CONTAINER:
			{
				createdCommand = new CmdContainerCreate(cmdType);
				break;
			}

			/**
			 * ---------------------- VIEW ----------------------
			 */
			case CREATE_VIEW_SWT_GLCANVAS:
			{
				createdCommand = new CmdViewCreateSwtGLCanvas(cmdType);
				break;
			}
			case CREATE_VIEW_RCP_GLCANVAS:
			{
				createdCommand = new CmdViewCreateRcpGLCanvas(cmdType);
				break;
			}
			case CREATE_VIEW_DATA_EXPLORER:
			{
				createdCommand = new CmdViewCreateDataExplorer(cmdType);
				break;
			}
			case CREATE_VIEW_DATA_EXCHANGER:
			{
				createdCommand = new CmdViewCreateDataExchanger(cmdType);
				break;
			}

			case CREATE_VIEW_SET_EDITOR:
			{
				createdCommand = new CmdViewCreateSetEditor(cmdType);
				break;
			}
			case CREATE_VIEW_PATHWAY:
			{
				createdCommand = new CmdViewCreatePathway(cmdType);
				break;
			}
			case CREATE_VIEW_MIXER:
			{
				createdCommand = new CmdViewCreateMixer(cmdType);
				break;
			}
			case CREATE_VIEW_GLYPHCONFIG:
			{
				createdCommand = new CmdViewCreateGlyphConfiguration(cmdType);
				break;
			}
			case CREATE_VIEW_BROWSER:
			{
				createdCommand = new CmdViewCreateHTMLBrowser(cmdType);
				break;
			}
			case CREATE_VIEW_IMAGE:
			{
				createdCommand = new CmdViewCreateImage(cmdType);
				break;
			}
			case CREATE_VIEW_UNDO_REDO:
			{
				createdCommand = new CmdViewCreateUndoRedo(cmdType);
				break;
			}
			case CREATE_VIEW_DATA_ENTITY_SEARCHER:
			{
				createdCommand = new CmdViewCreateDataEntitySearcher(cmdType);
				break;
			}

			/**
			 * ---------------------- OPEN GL ----------------------
			 */
			case CREATE_GL_PATHWAY_3D:
			{
				createdCommand = new CmdGlObjectPathway3D(cmdType);
				break;
			}
			case CREATE_GL_HEAT_MAP_3D:
			case CREATE_GL_GLYPH:
			case CREATE_GL_GLYPH_SLIDER:
			case CREATE_GL_PARALLEL_COORDINATES_3D:
			case CREATE_GL_BUCKET_3D:
			case CREATE_GL_JUKEBOX_3D:
			case CREATE_GL_WII_TEST:
			case CREATE_GL_REMOTE_GLYPH:
			{
				createdCommand = new CmdCreateGLEventListener(cmdType);
				break;
			}
			case EXTERNAL_FLAG_SETTER:
			{
				createdCommand = new CmdExternalFlagSetter(cmdType);
				break;
			}
			case EXTERNAL_ACTION_TRIGGER:
			{
				createdCommand = new CmdExternalActionTrigger(cmdType);
				break;
			}
			
			/**
			 * ---------------------- EVENT - SYSTEM ----------------------
			 */
			case CREATE_EVENT_MEDIATOR:
			{
				createdCommand = new CmdEventCreateMediator(cmdType);
				break;
			}
			case EVENT_MEDIATOR_ADD_OBJECT:
			{
				createdCommand = new CmdEventMediatorAddObject(cmdType);
				break;
			}
			case SYSTEM_SHUT_DOWN:
			{
				createdCommand = new CmdSystemExit(cmdType);
				break;
			}
			case SET_SYSTEM_PATH_PATHWAYS:
			{
				createdCommand = new CmdSetPathwayDatabasePath(cmdType);
				break;
			}
			case LOAD_GLYPH_DEFINITIONS:
			{
				createdCommand = new CmdLoadGlyphDefinition(cmdType);
				break;
			}
			case LOAD_PATHWAY_DATA:
			{
				createdCommand = new CmdLoadPathwayData(cmdType);
				break;
			}
			case FETCH_PATHWAY_DATA:
			{
				createdCommand = new CmdFetchPathwayData(cmdType);
				break;
			}
			
			/**
			 * ---------------------- DATA FILTER ----------------------
			 */
			case DATA_FILTER_MATH:
			{
				createdCommand = new CmdDataFilterMath(cmdType);
				break;
			}
			case DATA_FILTER_MIN_MAX:
			{
				createdCommand = new CmdDataFilterMinMax(cmdType);
				break;
			}
			default:
				throw new CaleydoRuntimeException(
						"Unsupported CommandQueue key= ["
								+ cmdType + "]", CaleydoRuntimeExceptionType.COMMAND);
		} // end switch

		return createdCommand;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.command.factory.ICommandFactory#createCommandQueue
	 * (java.lang.String, java.lang.String, int, int, int, int)
	 */
	public ICommand createCommandQueue(final String sCmdType, final String sProcessType,
			final int iCmdId, final int iCmdQueueId, final int sQueueThread,
			final int sQueueThreadWait)
	{

		CommandType queueType;

		/**
		 * Create a new uniqueId if necessary
		 */
		int iNewUniqueId = iCmdId;
		if (iCmdId < 0)
		{
			//TODO: review when implementing ID management
			iNewUniqueId = -1; //commandManager.createId(null);
		}
		/**
		 * End: Create a new uniqueId if necessary
		 */

		try
		{
			queueType = CommandType.valueOf(sCmdType);
		}
		catch (IllegalArgumentException iae)
		{
			throw new CaleydoRuntimeException(
					"Undefined CommandQueue key= [" + sCmdType + "]",
					CaleydoRuntimeExceptionType.SAXPARSER);
		}

		switch (queueType)
		{
			case COMMAND_QUEUE_OPEN:
			{
				ICommand cmdQueue = new CommandQueueVector(queueType, iCmdQueueId);
				return cmdQueue;
			}

			case COMMAND_QUEUE_RUN:
				return new CmdSystemRunCmdQueue(queueType, iCmdQueueId);

			default:
				throw new CaleydoRuntimeException("Unsupported CommandQueue key= [" + sCmdType
						+ "]", CaleydoRuntimeExceptionType.COMMAND);
		}

	}

	/**
	 * Since the last created command is stored its reference is returned. Note:
	 * be carefully with this method, because maybe the command was already
	 * executed or destroyed, or a new command was created meanwhile
	 * 
	 * @return reference to last created command
	 */
	protected ICommand getLastCreatedCommand()
	{
		return lastCommand;
	}
}
