package org.caleydo.core.manager.command.factory;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.command.data.CmdDataCreatePathwayStorage;
import org.caleydo.core.command.data.CmdDataCreateSelectionSetMakro;
import org.caleydo.core.command.data.CmdDataCreateSet;
import org.caleydo.core.command.data.CmdDataCreateSetViewdata;
import org.caleydo.core.command.data.CmdDataCreateStorage;
import org.caleydo.core.command.data.CmdDataCreateVirtualArray;
import org.caleydo.core.command.data.filter.CmdDataFilterMath;
import org.caleydo.core.command.data.filter.CmdDataFilterMinMax;
import org.caleydo.core.command.event.CmdEventCreateMediator;
import org.caleydo.core.command.event.CmdEventMediatorAddObject;
import org.caleydo.core.command.queue.CmdSystemRunCmdQueue;
import org.caleydo.core.command.queue.CommandQueueVector;
import org.caleydo.core.command.system.CmdSystemExit;
import org.caleydo.core.command.system.CmdSystemLoadFileLookupTable;
import org.caleydo.core.command.system.CmdSystemLoadFileNStorages;
import org.caleydo.core.command.system.CmdSystemLoadFileViaImporter;
import org.caleydo.core.command.system.path.CmdSetPathwayDatabasePath;
import org.caleydo.core.command.view.opengl.CmdGlObjectBucket3D;
import org.caleydo.core.command.view.opengl.CmdGlObjectGlyph;
import org.caleydo.core.command.view.opengl.CmdGlObjectHeatMap3D;
import org.caleydo.core.command.view.opengl.CmdGlObjectHistogram2D;
import org.caleydo.core.command.view.opengl.CmdGlObjectJukebox3D;
import org.caleydo.core.command.view.opengl.CmdGlObjectParCoords3D;
import org.caleydo.core.command.view.opengl.CmdGlObjectPathway3D;
import org.caleydo.core.command.view.opengl.CmdGlObjectPathway3DJukebox;
import org.caleydo.core.command.view.opengl.CmdGlObjectRemoteGlyph;
import org.caleydo.core.command.view.opengl.CmdGlObjectWiiTest;
import org.caleydo.core.command.view.rcp.CmdExternalActionTrigger;
import org.caleydo.core.command.view.rcp.CmdExternalFlagSetter;
import org.caleydo.core.command.view.rcp.CmdViewCreateRcpGLCanvas;
import org.caleydo.core.command.view.swt.CmdViewCreateDataEntitySearcher;
import org.caleydo.core.command.view.swt.CmdViewCreateDataExchanger;
import org.caleydo.core.command.view.swt.CmdViewCreateDataExplorer;
import org.caleydo.core.command.view.swt.CmdViewCreateGears;
import org.caleydo.core.command.view.swt.CmdViewCreateHTMLBrowser;
import org.caleydo.core.command.view.swt.CmdViewCreateImage;
import org.caleydo.core.command.view.swt.CmdViewCreateMixer;
import org.caleydo.core.command.view.swt.CmdViewCreatePathway;
import org.caleydo.core.command.view.swt.CmdViewCreateProgressBar;
import org.caleydo.core.command.view.swt.CmdViewCreateSelectionSlider;
import org.caleydo.core.command.view.swt.CmdViewCreateSetEditor;
import org.caleydo.core.command.view.swt.CmdViewCreateStorageSlider;
import org.caleydo.core.command.view.swt.CmdViewCreateSwtGLCanvas;
import org.caleydo.core.command.view.swt.CmdViewCreateUndoRedo;
import org.caleydo.core.command.view.swt.CmdViewLoadURLInHTMLBrowser;
import org.caleydo.core.command.window.swt.CmdContainerCreate;
import org.caleydo.core.command.window.swt.CmdWindowCreate;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Class is responsible for creating the commands.
 * The commands are created according to the command type.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CommandFactory 
implements ICommandFactory {

	private ICommand lastCommand;
	
	protected final IGeneralManager generalManager;

	protected final ICommandManager commandManager;
	
	
	/**
	 * Constructor
	 * 
	 * @param setRefGeneralManager reference to IGeneralManager
	 * @param setCommandType may be null if no command shall be created by the constructor
	 */
	public CommandFactory(  final IGeneralManager setRefGeneralManager,
			final ICommandManager refCommandManager) {
		
		assert setRefGeneralManager != null:"Can not create CommandFactory from null-pointer to IGeneralManager";
		
		this.generalManager = setRefGeneralManager;		
		this.commandManager = refCommandManager;
	}

	
	public ICommand createCommandByType(final CommandQueueSaxType cmdType) {
		
		ICommand createdCommand = null;
		
		switch ( cmdType ) {
		
		/*
		 * ----------------------
		 *        LAODING...
		 * ----------------------
		 */
		case LOAD_LOOKUP_TABLE_FILE: 
		{
			createdCommand = new CmdSystemLoadFileLookupTable( 
						generalManager,
						commandManager,
						cmdType);
			break;
		}	
		case LOAD_DATA_FILE: 
		{
			createdCommand = new CmdSystemLoadFileViaImporter(
						generalManager,
						commandManager,
						cmdType);
			break;
		}
		case LOAD_DATA_FILE_N_STORAGES:
		{
			createdCommand = new CmdSystemLoadFileNStorages( 
						generalManager,
						commandManager,
						cmdType);
			break;
		}
		case LOAD_URL_IN_BROWSER:
		{
			createdCommand =
				new CmdViewLoadURLInHTMLBrowser(
						generalManager,
						commandManager,
						cmdType);
			break;
		}
		
		/*
		 * ----------------------
		 *    DATA CONTAINERS
		 * ----------------------
		 */
		case CREATE_STORAGE:
		{					
			createdCommand =
				new CmdDataCreateStorage(
						generalManager,
						commandManager,
						cmdType,
						true);
			break;
		}
		case CREATE_PATHWAY_STORAGE:
		{					
			createdCommand =
				new CmdDataCreatePathwayStorage(
						generalManager,
						commandManager,
						cmdType);
			break;
		}
		case CREATE_VIRTUAL_ARRAY:
		{
			createdCommand =
				new CmdDataCreateVirtualArray(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}
		case CREATE_SET_DATA:
		{
			createdCommand =
				new CmdDataCreateSet(
						generalManager,
						commandManager,
						cmdType,
						true);
			break;
		}
		case CREATE_SET_VIEW:
		{
			createdCommand =
				new CmdDataCreateSetViewdata(
						generalManager,
						commandManager,
						cmdType);
			break;
		}
		case CREATE_SET_SELECTION_MAKRO:
		{
			createdCommand =
				new CmdDataCreateSelectionSetMakro(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}
		
		/*
		 * ----------------------
		 *        SWT
		 * ----------------------
		 */
		case CREATE_SWT_WINDOW:
		{
			createdCommand =
				new CmdWindowCreate(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}
		case CREATE_SWT_CONTAINER:
		{
			createdCommand =
				new CmdContainerCreate(						
						generalManager,
						commandManager,
						cmdType);
			break;
		}
	
		/*
		 * ----------------------
		 *        VIEW
		 * ----------------------
		 */
		case CREATE_VIEW_GEARS:
		{
			createdCommand =
				new CmdViewCreateGears(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}
		case CREATE_VIEW_SWT_GLCANVAS:
		{
			createdCommand =
				new CmdViewCreateSwtGLCanvas(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}
		case CREATE_VIEW_RCP_GLCANVAS:
		{
			createdCommand =
				new CmdViewCreateRcpGLCanvas(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}
		case CREATE_VIEW_DATA_EXPLORER:
		{
			createdCommand =
				new CmdViewCreateDataExplorer(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}
		case CREATE_VIEW_DATA_EXCHANGER:
		{
			createdCommand =
				new CmdViewCreateDataExchanger(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}
		
		case CREATE_VIEW_SET_EDITOR:
		{
			createdCommand =
				new CmdViewCreateSetEditor(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}
		case CREATE_VIEW_PROGRESSBAR:
		{
			createdCommand =
				new CmdViewCreateProgressBar(
						generalManager,
						commandManager,
						cmdType);			
			break;
		}
		case CREATE_VIEW_PATHWAY:
		{
			createdCommand =
				new CmdViewCreatePathway(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}
		case CREATE_VIEW_STORAGE_SLIDER:
		{
			createdCommand =
				new CmdViewCreateStorageSlider(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}
		case CREATE_VIEW_SELECTION_SLIDER:
		{
			createdCommand =
				new CmdViewCreateSelectionSlider(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}
		case CREATE_VIEW_MIXER:
		{
			createdCommand =
				new CmdViewCreateMixer(
						generalManager,
						commandManager,
						cmdType);
			break;
		}
		case CREATE_VIEW_BROWSER:
		{
			createdCommand =
				new CmdViewCreateHTMLBrowser(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}
		case CREATE_VIEW_IMAGE:
		{
			createdCommand =
				new CmdViewCreateImage(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}	
		case CREATE_VIEW_UNDO_REDO:
		{
			createdCommand =
				new CmdViewCreateUndoRedo(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}		
		case CREATE_VIEW_DATA_ENTITY_SEARCHER:
		{
			createdCommand =
				new CmdViewCreateDataEntitySearcher(
						generalManager,
						commandManager,
						cmdType);		
			break;			
		}
		
		/*
		 * ----------------------
		 *        OPEN GL
		 * ----------------------
		 */	
		case CREATE_GL_HEAT_MAP_3D:
		{
			createdCommand =
				new CmdGlObjectHeatMap3D(
						generalManager,
						commandManager,
						cmdType);	
			break;
		}
		case CREATE_GL_HISTOGRAM2D:
		{
 			createdCommand =
				new CmdGlObjectHistogram2D(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}
		case CREATE_GL_JUKEBOX_PATHWAY_3D:
		{
 			createdCommand =
				new CmdGlObjectPathway3DJukebox(
						generalManager,
						commandManager,
						cmdType);	
			break;
		}		
		case CREATE_GL_PATHWAY_3D:
		{
 			createdCommand =
				new CmdGlObjectPathway3D(
						generalManager,
						commandManager,
						cmdType);	
			break;
		}
		case CREATE_GL_GLYPH:
		{
 			createdCommand =
				new CmdGlObjectGlyph(
						generalManager,
						commandManager,
						cmdType);	
			break;
		}
		case CREATE_GL_PARALLEL_COORDINATES_3D:
		{
 			createdCommand =
				new CmdGlObjectParCoords3D(
						generalManager,
						commandManager,
						cmdType);	
			break;
		}
		case CREATE_GL_BUCKET_3D:
		{
 			createdCommand =
				new CmdGlObjectBucket3D(
						generalManager,
						commandManager,
						cmdType);	
			break;
		}
		case CREATE_GL_JUKEBOX_3D:
		{
 			createdCommand =
				new CmdGlObjectJukebox3D(
						generalManager,
						commandManager,
						cmdType);	
			break;
		}
		case CREATE_GL_WII_TEST:
		{
 			createdCommand =
				new CmdGlObjectWiiTest(
						generalManager,
						commandManager,
						cmdType);	
			break;
		}
		case CREATE_GL_REMOTE_GLYPH:
		{
 			createdCommand =
				new CmdGlObjectRemoteGlyph(
						generalManager,
						commandManager,
						cmdType);	
			break;
		}
//		case CREATE_GL_MINMAX_SCATTERPLOT2D:
//		{
//			createdCommand =
//				new CmdGlObjectMinMaxScatterPlot2D(
//						generalManager,
//						commandManager,
//						cmdType);
//			break;
//		}
//		case CREATE_GL_WIDGET:
//		{
//			createdCommand =
//				new CmdGlObjectWidget(
//						generalManager,
//						commandManager,
//						cmdType);
//			break;
//		}
//		case CREATE_GL_MINMAX_SCATTERPLOT3D:
//		{
//			createdCommand =
//				new CmdGlObjectMinMaxScatterPlot3D(
//						generalManager,
//						commandManager,
//						cmdType);
//			break;
//		}
//		case CREATE_GL_ISOSURFACE3D:
//		{
// 			createdCommand =
//				new CmdGlObjectIsosurface3D(
//						generalManager,
//						commandManager,
//						cmdType);		
//			break;
//		}
//		case CREATE_GL_SCATTERPLOT2D:
//		{
//			createdCommand =
//				new CmdGlObjectScatterPlot2D(
//						generalManager,
//						commandManager,
//						cmdType);		
//			break;
//		}
//		case CREATE_GL_TEXTURE2D:
//		{
//			createdCommand =
//				new CmdGlObjectTexture2D(
//						generalManager,
//						commandManager,
//						cmdType);		
//			break;
//		}
		case EXTERNAL_FLAG_SETTER:
		{
			createdCommand =
				new CmdExternalFlagSetter(
						generalManager,
						commandManager,
						cmdType);
			break;
		}
		case EXTERNAL_ACTION_TRIGGER:
		{
			createdCommand =
				new CmdExternalActionTrigger(
						generalManager,
						commandManager,
						cmdType);
			break;
		}
		
		/*
		 * ----------------------
		 *        SYSTEM
		 * ----------------------
		 */
		
		/*
		 * ----------------------
		 *     EVENT - SYSTEM
		 * ----------------------
		 */
		case CREATE_EVENT_MEDIATOR:
		{
			createdCommand =
				new CmdEventCreateMediator(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}	
		case EVENT_MEDIATOR_ADD_OBJECT:
		{
			createdCommand =
				new CmdEventMediatorAddObject(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}
		case SYSTEM_SHUT_DOWN:
		{
			createdCommand = new CmdSystemExit(generalManager);
			break;
		}
		case  SET_SYSTEM_PATH_PATHWAYS:
		{
			createdCommand =
				new CmdSetPathwayDatabasePath(
						generalManager,
						commandManager,
						cmdType);
			break;
		}	
		
		/*
		 * ----------------------
		 *     DATA FILTER
		 * ----------------------
		 */
		case DATA_FILTER_MATH:
		{
			createdCommand =
				new CmdDataFilterMath(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}
		case DATA_FILTER_MIN_MAX:
		{
			createdCommand =
				new CmdDataFilterMinMax(
						generalManager,
						commandManager,
						cmdType);		
			break;
		}
		default: 
			throw new CaleydoRuntimeException("CommandFactory::createCommand() Unsupported CommandQueue key= [" + 
					cmdType + "]",
					CaleydoRuntimeExceptionType.SAXPARSER);
		} // end switch	
		
		return createdCommand;
	}
	
	
	public ICommand createCommandQueue( final String sCmdType,
			final String sProcessType,
			final int iCmdId,
			final int iCmdQueueId,
			final int sQueueThread,
			final int sQueueThreadWait ) {
		
		CommandQueueSaxType queueType;
		
		/**
		 * Create a new uniqueId if necessary
		 */
		int iNewUniqueId = iCmdId;		
		if ( iCmdId < 0 ) {
			iNewUniqueId = commandManager.createId( null );
		}
		/**
		 * End: Create a new uniqueId if necessary
		 */
		
		try 
		{
			queueType =CommandQueueSaxType.valueOf( sCmdType );
		}
		catch ( IllegalArgumentException iae ) 
		{
			throw new CaleydoRuntimeException("Undefined CommandQueue key= [" + sCmdType + "]",
					CaleydoRuntimeExceptionType.SAXPARSER);
		}
			
		switch (queueType) 
		{
		case COMMAND_QUEUE_OPEN: {
			ICommand cmdQueue = new CommandQueueVector(iNewUniqueId, 
					generalManager,
					commandManager,
					queueType,
					iCmdQueueId);				
			return cmdQueue;
		}
			
		case COMMAND_QUEUE_RUN:
			return new CmdSystemRunCmdQueue(iNewUniqueId,
					generalManager,
					commandManager,
					queueType,
					iCmdQueueId);
			
			default:
				throw new CaleydoRuntimeException("Unsupported CommandQueue key= [" + sCmdType + "]",
						CaleydoRuntimeExceptionType.SAXPARSER);
		}
		
	}
	
	
	/**
	 * Since the last created command is stored its reference is returned.
	 * Note: be carefull with this method, becaus maybe the commadn was already executed or distryed, or a new command was created meanwhile
	 * @return reference to last created command
	 */
	protected ICommand getLastCreatedCommand() {
		return lastCommand;
	}
	
	
	/**
	 * Call doCommand() inside a try catch block.
	 * 
	 * @param refCommand
	 * @throws CaleydoRuntimeException
	 */
	public static final void doCommandSafe(ICommand refCommand) throws CaleydoRuntimeException {
		try {
			refCommand.doCommand();
		} catch (CaleydoRuntimeException pe) {
			throw new CaleydoRuntimeException( refCommand.getClass().getName() +
					"doCommand() failed with "+
					pe.toString(),
					CaleydoRuntimeExceptionType.COMMAND );
		} catch (Exception e) {
			throw new CaleydoRuntimeException( refCommand.getClass().getName() +
					"doCommand() failed with "+
					e.toString(),
					CaleydoRuntimeExceptionType.COMMAND );
		}
	}


	/**
	 * Call undoCommand() inside a try catch block.
	 * 
	 * @param refCommand
	 * @throws CaleydoRuntimeException
	 */
	public static final void undoCommandSafe(ICommand refCommand) throws CaleydoRuntimeException {
		try {
			refCommand.undoCommand();
		} catch (CaleydoRuntimeException pe) {
			throw new CaleydoRuntimeException( refCommand.getClass().getName() +
					"undoCommand() failed with "+
					pe.toString(),
					CaleydoRuntimeExceptionType.COMMAND );
		} catch (Exception e) {
			throw new CaleydoRuntimeException( refCommand.getClass().getName() +
					"undoCommand() failed with "+
					e.toString(),
					CaleydoRuntimeExceptionType.COMMAND );
		}
	}
}
