/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 * Creation date: 18-05-2005
 *  
 */
package cerberus.manager.command.factory;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.CommandType;
import cerberus.command.ICommand;
import cerberus.command.data.CmdDataCreatePathwayStorage;
import cerberus.command.data.CmdDataCreateSelectionSetMakro;
import cerberus.command.data.CmdDataCreateSetViewdata;
import cerberus.command.data.CmdDataCreateVirtualArray;
import cerberus.command.data.CmdDataCreateSet;
import cerberus.command.data.CmdDataCreateStorage;
import cerberus.command.event.CmdEventCreateMediator;
import cerberus.command.event.CmdEventMediatorAddObject;
import cerberus.command.view.opengl.CmdGlObjectHeatmap;
import cerberus.command.view.opengl.CmdGlObjectHistogram2D;
import cerberus.command.view.opengl.CmdGlObjectMinMaxScatterPlot2D;
import cerberus.command.view.opengl.CmdGlObjectMinMaxScatterPlot3D;
import cerberus.command.view.opengl.CmdGlObjectPathway3DLayered;
import cerberus.command.view.opengl.CmdGlObjectPathway3DPanel;
import cerberus.command.view.opengl.CmdGlObjectScatterPlot2D;
import cerberus.command.view.opengl.CmdGlObjectTexture2D;
import cerberus.command.view.opengl.CmdGlObjectTriangleTest;
import cerberus.command.view.opengl.CmdGlObjectIsosurface3D;
import cerberus.command.view.opengl.CmdGlObjectHeatmap2D;
import cerberus.command.view.opengl.CmdGlObjectWidget;
import cerberus.command.view.swt.CmdViewCreateDataExchanger;
import cerberus.command.view.swt.CmdViewCreateSetEditor;
import cerberus.command.view.swt.CmdViewCreateDataExplorer;
import cerberus.command.view.swt.CmdViewCreateGears;
import cerberus.command.view.swt.CmdViewCreateImage;
import cerberus.command.view.swt.CmdViewCreateMixer;
import cerberus.command.view.swt.CmdViewCreatePathway;
import cerberus.command.view.swt.CmdViewCreateProgressBar;
import cerberus.command.view.swt.CmdViewCreateSelectionSlider;
import cerberus.command.view.swt.CmdViewCreateStorageSlider;
import cerberus.command.view.swt.CmdViewCreateSwtGLCanvas;
import cerberus.command.view.swt.CmdViewCreateUndoRedo;
import cerberus.command.view.swt.CmdViewLoadURLInHTMLBrowser;
import cerberus.command.view.swt.CmdViewCreateHTMLBrowser;
import cerberus.command.window.swt.CmdWindowCreate;
import cerberus.command.window.swt.CmdContainerCreate;
import cerberus.command.queue.CmdSystemRunCmdQueue;
import cerberus.command.queue.CommandQueueVector;
import cerberus.command.system.CmdSystemExit;
import cerberus.command.system.CmdSystemNewFrame;
import cerberus.command.system.CmdSystemLoadFileViaImporter;
import cerberus.command.system.CmdSystemLoadFileNStorages;
import cerberus.command.system.CmdSystemLoadFileLookupTable;
import cerberus.command.system.path.CmdSetPathwayPaths;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeExceptionType;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Class is responsible for creating the commands.
 * The commands are created according to the command type.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CommandFactory 
	implements  ICommandFactory {

	private ICommand refLastCommand;
	
	protected final IGeneralManager refGeneralManager;

	protected final ICommandManager refCommandManager;
	
	
	/**
	 * Constructor
	 * 
	 * @param setRefGeneralManager reference to IGeneralManager
	 * @param setCommandType may be null if no command shall be created by the constructor
	 */
	public CommandFactory(  final IGeneralManager setRefGeneralManager,
			final ICommandManager refCommandManager) {
		
		assert setRefGeneralManager != null:"Can not create CommandFactory from null-pointer to IGeneralManager";
		
		this.refGeneralManager = setRefGeneralManager;		
		this.refCommandManager = refCommandManager;
		
	}
	
//	/**
//	 * @deprecated
//	 */
//	public ICommand createCommand( 
//			final CommandType createCommandByType, 
//			final String details ) {
//		
//		assert createCommandByType != null:"Can not create command from null-pointer.";
//		
//		switch ( createCommandByType.getGroup()) {
//		
//		case APPLICATION:
//			break;
//			
//		case DATASET:
//			return createDatasetCommand(createCommandByType,details);
//			
//		case DATA_COLLECTION:
//			return createDatasetCommand(createCommandByType,details);
//		
//		case HOST:
//			break;
//			
//		case SELECT:
//			return createSelectionCommand(createCommandByType,details);
//			
//		case SELECT_VALUE:
//			return createSelectionValueCommand(createCommandByType,details);
//			
//		case SERVER:
//			break;
//			
//		case SYSTEM:
//			return createSystemCommand(createCommandByType,details);	
//			
//		case WINDOW:
//			return createWindowCommand(createCommandByType,details);
//			
//		default:
//			System.err.println("CommandFactory(CommandType) failed, because CommandTypeGroup ["+
//					createCommandByType.getGroup() +"] is not known by factory.");
//			refLastCommand = null;
//			return null;			
//		}
//		
//		return null;
//		
//	}
	

	
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
			createdCommand =
				new CmdSystemLoadFileLookupTable( 
						refGeneralManager,
						refCommandManager,
						cmdType);
			break;
		}
		
		case LOAD_DATA_FILE: 
		{
			createdCommand =
				new CmdSystemLoadFileViaImporter(
						refGeneralManager,
						refCommandManager,
						cmdType);
			break;
		}
		
		case LOAD_DATA_FILE_N_STORAGES:
		{
			createdCommand =
				new CmdSystemLoadFileNStorages( 
						refGeneralManager,
						refCommandManager,
						cmdType);
			break;
		}
		
		case LOAD_URL_IN_BROWSER:
		{
			createdCommand =
				new CmdViewLoadURLInHTMLBrowser(
						refGeneralManager,
						refCommandManager,
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
						refGeneralManager,
						refCommandManager,
						cmdType,
						true);
			break;
		}
		
		case CREATE_PATHWAY_STORAGE:
		{					
			createdCommand =
				new CmdDataCreatePathwayStorage(
						refGeneralManager,
						refCommandManager,
						cmdType);
			break;
		}				

		case CREATE_VIRTUAL_ARRAY:
		{
			createdCommand =
				new CmdDataCreateVirtualArray(
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}
		
		case CREATE_SET_DATA:
		{
			createdCommand =
				new CmdDataCreateSet(
						refGeneralManager,
						refCommandManager,
						cmdType,
						true);
			break;
		}
		
		case CREATE_SET_VIEW:
		{
			createdCommand =
				new CmdDataCreateSetViewdata(
						refGeneralManager,
						refCommandManager,
						cmdType);
			break;
		}
		

		case CREATE_SET_SELECTION_MAKRO:
		{
			createdCommand =
				new CmdDataCreateSelectionSetMakro(
						refGeneralManager,
						refCommandManager,
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
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}
		
		case CREATE_SWT_CONTAINER:
		{
			createdCommand =
				new CmdContainerCreate(						
						refGeneralManager,
						refCommandManager,
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
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}
		
		case CREATE_VIEW_SWT_GLCANVAS:
		{
			createdCommand =
				new CmdViewCreateSwtGLCanvas(
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}
		
		case CREATE_VIEW_DATA_EXPLORER:
		{
			createdCommand =
				new CmdViewCreateDataExplorer(
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}
		
		case CREATE_VIEW_DATA_EXCHANGER:
		{
			createdCommand =
				new CmdViewCreateDataExchanger(
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}
		
		case CREATE_VIEW_SET_EDITOR:
		{
			createdCommand =
				new CmdViewCreateSetEditor(
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}
		
		case CREATE_VIEW_PROGRESSBAR:
		{
			createdCommand =
				new CmdViewCreateProgressBar(
						refGeneralManager,
						refCommandManager,
						cmdType);			
			break;
		}
		
		case CREATE_VIEW_PATHWAY:
		{
			createdCommand =
				new CmdViewCreatePathway(
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}

		case CREATE_VIEW_STORAGE_SLIDER:
		{
			createdCommand =
				new CmdViewCreateStorageSlider(
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}
		
		case CREATE_VIEW_SELECTION_SLIDER:
		{
			createdCommand =
				new CmdViewCreateSelectionSlider(
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}
		
		case CREATE_VIEW_MIXER:
		{
			createdCommand =
				new CmdViewCreateMixer(
						refGeneralManager,
						refCommandManager,
						cmdType);
			break;
		}
		
		case CREATE_VIEW_BROWSER:
		{
			createdCommand =
				new CmdViewCreateHTMLBrowser(
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}
		
		case CREATE_VIEW_IMAGE:
		{
			createdCommand =
				new CmdViewCreateImage(
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}	
		
		case CREATE_VIEW_UNDO_REDO:
		{
			createdCommand =
				new CmdViewCreateUndoRedo(
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}		
		
		/*
		 * ----------------------
		 *        OPEN GL
		 * ----------------------
		 */
		
		
		case CREATE_GL_TRIANGLE_TEST:
		{
			createdCommand =
				new CmdGlObjectTriangleTest(
						refGeneralManager,
						refCommandManager,
						cmdType);			
			break;
		}
		
		case CREATE_GL_HEATMAP:
		{
			createdCommand =
				new CmdGlObjectHeatmap(
						refGeneralManager,
						refCommandManager,
						cmdType);	
			break;
		}
		
		case CREATE_GL_HISTOGRAM2D:
		{
 			createdCommand =
				new CmdGlObjectHistogram2D(
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}
		
		case CREATE_GL_ISOSURFACE3D:
		{
 			createdCommand =
				new CmdGlObjectIsosurface3D(
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}
		
		case CREATE_GL_SCATTERPLOT2D:
		{
			createdCommand =
				new CmdGlObjectScatterPlot2D(
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}
		
		case CREATE_GL_TEXTURE2D:
		{
			createdCommand =
				new CmdGlObjectTexture2D(
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}
		
		case CREATE_GL_HEATMAP2D:
		{
			createdCommand =
				new CmdGlObjectHeatmap2D(
						refGeneralManager,
						refCommandManager,
						cmdType);			
			break;
		}
		
		case CREATE_GL_LAYERED_PATHWAY_3D:
		{
 			createdCommand =
				new CmdGlObjectPathway3DLayered(
						refGeneralManager,
						refCommandManager,
						cmdType);	
			break;
		}
		
		case CREATE_GL_PANEL_PATHWAY_3D:
		{
 			createdCommand =
				new CmdGlObjectPathway3DPanel(
						refGeneralManager,
						refCommandManager,
						cmdType);	
			break;
		}
		
		case CREATE_GL_MINMAX_SCATTERPLOT2D:
		{
			createdCommand =
				new CmdGlObjectMinMaxScatterPlot2D(
						refGeneralManager,
						refCommandManager,
						cmdType);
			break;
		}
		
		case CREATE_GL_WIDGET:
		{
			createdCommand =
				new CmdGlObjectWidget(
						refGeneralManager,
						refCommandManager,
						cmdType);
			break;
		}
				
		
		case CREATE_GL_MINMAX_SCATTERPLOT3D:
		{
			createdCommand =
				new CmdGlObjectMinMaxScatterPlot3D(
						refGeneralManager,
						refCommandManager,
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
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}
		
		case EVENT_MEDIATOR_ADD_OBJECT:
		{
			createdCommand =
				new CmdEventMediatorAddObject(
						refGeneralManager,
						refCommandManager,
						cmdType);		
			break;
		}
		
		
		
		/**
		 * Set path for pathway XML files, images and imagemaps.
		 */
		case  SET_SYSTEM_PATH_PATHWAYS:
		{
			createdCommand =
				new CmdSetPathwayPaths(
						refGeneralManager,
						refCommandManager,
						cmdType);
			break;
		}	
		
		
		
		default: 
			throw new CerberusRuntimeException("CommandFactory::createCommand() Unsupported CommandQueue key= [" + 
					cmdType + "]",
					CerberusRuntimeExceptionType.SAXPARSER);
		} // end switch
		
//		/**
//		 * Create a new uniqueId if nessecary
//		 */
//		int iNewUniqueId = iData_CmdId;		
//		if ( iData_CmdId < 0 ) {
//			iNewUniqueId = refCommandManager.createNewId( null );
//		}
//		createdCommand.setId( iNewUniqueId );
//		/**
//		 * End: Create a new uniqueId if nessecary
//		 */			
		
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
			iNewUniqueId = refCommandManager.createId( null );
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
			throw new CerberusRuntimeException("Undefined CommandQueue key= [" + sCmdType + "]",
					CerberusRuntimeExceptionType.SAXPARSER);
		}
			
		switch (queueType) 
		{
		case COMMAND_QUEUE_OPEN: {
			ICommand cmdQueue = new CommandQueueVector(iNewUniqueId, 
					refGeneralManager,
					refCommandManager,
					queueType,
					iCmdQueueId);				
			return cmdQueue;
		}
			
		case COMMAND_QUEUE_RUN:
			return new CmdSystemRunCmdQueue(iNewUniqueId,
					refGeneralManager,
					refCommandManager,
					queueType,
					iCmdQueueId);
			
			default:
				throw new CerberusRuntimeException("Unsupported CommandQueue key= [" + sCmdType + "]",
						CerberusRuntimeExceptionType.SAXPARSER);
		}
		
	}
	
//	protected ICommand createDatasetCommand( 
//			final CommandType setCommandType,
//			final String details ) {
//		switch (setCommandType) {
//			case DATA_COLLECTION_LOAD :
//				break;
//			case DATA_COLLECTION_SAVE :
//				break;	
//			case DATASET_RELOAD :
//				break;
//			case DATASET_LOAD :
//				break;
//			case DATASET_SAVE :
//				break;
//				
//			default:
//				System.err.println("CommandFactory(CommandType) failed, because CommandType ["+
//						setCommandType +"] is not known by factory.");
//				refLastCommand = null;
//			return null;
//		}
//		
//		return null;
//	}
	
	protected ICommand createSelectionCommand( 
			final CommandType setCommandType,
			final String details ) {
		switch (setCommandType) {
			case SELECT_NEW :
				break;
			case SELECT_DEL :
				break;	
			case SELECT_ADD :
				break;
			case SELECT_LOAD :
				break;
			case SELECT_SAVE :
				break;
				
			default:
				System.err.println("CommandFactory(CommandType) failed, because CommandType ["+
						setCommandType +"] is not known by factory.");
				refLastCommand = null;
			return null;
		}
		
		return null;
	}
	
	protected ICommand createSystemCommand( 
			final CommandType setCommandType,
			final String details ) {
		switch (setCommandType) {
			case SYSTEM_EXIT :
				refLastCommand = new CmdSystemExit(refGeneralManager);
				return refLastCommand;
				
			case SYSTEM_NEW_FRAME:
				return new CmdSystemNewFrame( refGeneralManager,
						refCommandManager, null);
				
			default:
				System.err.println("CommandFactory(CommandType) failed, because CommandType ["+
						setCommandType +"] is not known by factory.");
				refLastCommand = null;
		}
		
		return null;
	}
	
	protected ICommand createWindowCommand( 
			final CommandType setCommandType,
			final String details ) {
		
//		int iWorkspaceTargetId = -1;
//		//JComponent refJComponent = null;
//		DDesktopPane refDDesktopPane = null;
//		
//		if ( details != null ) {
//			try {
//				iWorkspaceTargetId = Integer.valueOf( details );
//				WorkspaceSwingFrame targetFrame = 
//					this.refGeneralManager.getSingelton().getViewCanvasManager().getItemWorkspace( iWorkspaceTargetId );
//				refDDesktopPane = targetFrame.getDesktopPane();
//				
//			} catch (NumberFormatException nfe ) {
//				assert false:"Can not handle detail [" +
//				details +
//				"] in createWindowCommand()";
//			}
//		}
//		
//		switch (setCommandType) {
//			case WINDOW_POPUP_CREDITS :
//				refCommand = new CmdWindowPopupCredits();
//				return refCommand;
//				
//			case WINDOW_POPUP_INFO :
//				refCommand = new CmdWindowPopupInfo(details);
//				return refCommand;
//				
//			case WINDOW_NEW_INTERNAL_FRAME:
//				return  new CmdWindowNewInternalFrame(refGeneralManager,
//						iWorkspaceTargetId, 
//						details );
//				
//			case WINDOW_IFRAME_OPEN_HEATMAP2D:
//				return new CmdWindowNewIFrameHeatmap2D(refGeneralManager, iWorkspaceTargetId);
//				
//			case WINDOW_IFRAME_OPEN_HISTOGRAM2D:
//				return new CmdWindowNewIFrameHistogram2D(refGeneralManager,iWorkspaceTargetId);
//				
//			case WINDOW_IFRAME_OPEN_SCATTERPLOT2D:
//				return new CmdWindowNewIFrameScatterplot2D();
//				
//			case WINDOW_IFRAME_OPEN_SELECTION:
//				return new CmdWindowNewIFrameSelection(refGeneralManager,iWorkspaceTargetId);
//			
//			case WINDOW_IFRAME_OPEN_STORAGE:
//				return new CmdWindowNewIFrameStorage(refGeneralManager,iWorkspaceTargetId);
//				
//			case WINDOW_IFRAME_OPEN_JOGL_CANVAS:
//				
//				return new CmdWindowNewIFrameJoglCanvas(refGeneralManager, 
//						null, 
//						iWorkspaceTargetId,
//						details );
//				
//			case WINDOW_IFRAME_OPEN_JOGL_HISTOGRAM:
//				return new CmdWindowNewIFrameJoglHistogram( refGeneralManager, 
//						iWorkspaceTargetId,
//						null, 
//						null );
//				
//			case WINDOW_IFRAME_OPEN_JOGL_HEATMAP:
//				return new CmdWindowNewIFrameJoglHeatmap( refGeneralManager, 
//						iWorkspaceTargetId, 
//						null, 						
//						null );
//			
//			case WINDOW_IFRAME_OPEN_JOGL_SCATTERPLOT:
//				return new CmdWindowNewIFrameJoglScatterplot( refGeneralManager, 
//						iWorkspaceTargetId, 
//						null, 						
//						null );
//				
//			case WINDOW_SET_ACTIVE_FRAME:
//				return new CmdWindowSetActiveFrame( refGeneralManager, details );
//				
//			case WINDOW_IFRAME_OPEN_SET:
//				//return new CmdWindowNewIFrameSet(refGeneralManager);
//				
//			
//			default:
//				System.err.println("CommandFactory(CommandType) failed, because CommandType ["+
//						setCommandType +"] is not known by factory.");
//				refCommand = null;
//		}
		
		return null;
	}
	
	
	
	protected ICommand createSelectionValueCommand( 
			final CommandType setCommandType,
			final String details ) {
		switch (setCommandType) {
			case SELECT_SHOW:
				break;
			case SELECT_HIDE:
				break;
			case SELECT_LOCK:
				break;
			case SELECT_UNLOCK:
				break;
				
			default:
				System.err.println("CommandFactory(CommandType) failed, because CommandType ["+
						setCommandType +"] is not known by factory.");
				refLastCommand = null;
			return null;
		}
		
		return null;
	}
	
	/**
	 * Since the last created command is stored its reference is returned.
	 * Note: be carefull with this method, becaus maybe the commadn was already executed or distryed, or a new command was created meanwhile
	 * @return reference to last created command
	 */
	protected ICommand getLastCreatedCommand() {
		return refLastCommand;
	}
	
	
	/**
	 * Call doCommand() inside a try catch block.
	 * 
	 * @param refCommand
	 * @throws CerberusRuntimeException
	 */
	public static final void doCommandSafe(ICommand refCommand) throws CerberusRuntimeException {
		try {
			refCommand.doCommand();
		} catch (CerberusRuntimeException pe) {
			throw new CerberusRuntimeException( refCommand.getClass().getName() +
					"doCommand() failed with "+
					pe.toString(),
					CerberusRuntimeExceptionType.COMMAND );
		} catch (Exception e) {
			throw new CerberusRuntimeException( refCommand.getClass().getName() +
					"doCommand() failed with "+
					e.toString(),
					CerberusRuntimeExceptionType.COMMAND );
		}
	}


	/**
	 * Call undoCommand() inside a try catch block.
	 * 
	 * @param refCommand
	 * @throws CerberusRuntimeException
	 */
	public static final void undoCommandSafe(ICommand refCommand) throws CerberusRuntimeException {
		try {
			refCommand.undoCommand();
		} catch (CerberusRuntimeException pe) {
			throw new CerberusRuntimeException( refCommand.getClass().getName() +
					"undoCommand() failed with "+
					pe.toString(),
					CerberusRuntimeExceptionType.COMMAND );
		} catch (Exception e) {
			throw new CerberusRuntimeException( refCommand.getClass().getName() +
					"undoCommand() failed with "+
					e.toString(),
					CerberusRuntimeExceptionType.COMMAND );
		}
	}
	


}
