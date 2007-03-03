package cerberus.data.collection.selection;

import java.util.ArrayList;
import java.util.Iterator;

import cerberus.command.CommandQueueSaxType;
import cerberus.data.collection.IGroupedSelection;
import cerberus.data.collection.IStorage;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IEventPublisher.MediatorType;
import cerberus.manager.view.ViewJoglManager;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.ViewType;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.xml.parser.parameter.ParameterHandler;

/**
 * Selection objects are created for each view that
 * needs to send selections to other views.
 * The selection will be created when the first user selection
 * takes place in the view.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 * 
 * @deprecated use SetSelection
 */
public class SelectionHandler
implements IGroupedSelection {

	protected IGeneralManager refGeneralManager;
	
	protected int iParentContainerId;
	
	//TODO: retrieve new generated IDs instead of static ones
	protected int iSelectionMediatorId = 95201;
	protected int iSelectionSetId = 85101;
	protected int iSelectionVirtualArrayId = 55201;
	protected int iSelectionIdStorageId = 85301;
	protected int iSelectionGroupStorageId = 95301;
	protected int iSelectionOptionalStorageId = 85301;

	/**
	 * Constructor.
	 * 
	 * The inital storage arrays are set, the parent ID is given
	 * as an argument.
	 * Furthermore the set is created on the first call.
	 * 
	 * @param refGeneralManager
	 * @param iParentContainerId
	 * @param iArSelectionId
	 * @param iArSelectionGroup
	 * @param iArSelectionOptionalData
	 */
	public SelectionHandler(IGeneralManager refGeneralManager,
			int iParentContainerId,
			int[] iArSelectionId,
			int[] iArSelectionGroup,
			int[] iArSelectionOptionalData ) {
		
		this.refGeneralManager = refGeneralManager;
		this.iParentContainerId = iParentContainerId;
		
		createSelectionVirtualArray();
		createSelectionIdStorage(iArSelectionId);
		//createSelectionGroupStorage(iArSelectionGroup);
		createSelectionOptionalStorage(iArSelectionOptionalData);
		createSelectionSet();

		setSelectionIdArray(iArSelectionId);
		//setGroupArray(iArSelectionGroup);
		setOptionalDataArray(iArSelectionOptionalData);
		
		createSelectionMediator();
	}
	
	/**
	 * Creates a selection virtual array.
	 * Usually the selection will start with 0
	 * and is of the same size as the storage.
	 *
	 */
	protected void createSelectionVirtualArray() {
		
		IParameterHandler refParameterHandler = new ParameterHandler();
		
		// CmdId
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_CMD_ID.getXmlKey(), 
				"12345", 
				IParameterHandler.ParameterHandlerType.INT);

		// Type
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_TYPE.getXmlKey(),
				CommandQueueSaxType.CREATE_VIRTUAL_ARRAY.name(),
				IParameterHandler.ParameterHandlerType.STRING);
		
//		// Label
//		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_LABEL.getXmlKey(), 
//				"Pathway Selection Virtual Array", 
//				IParameterHandler.ParameterHandlerType.STRING);
		
		// TargetID
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_TARGET_ID.getXmlKey(), 
				Integer.toString(iSelectionVirtualArrayId), 
				IParameterHandler.ParameterHandlerType.INT);
		
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey(), 
				"3 0 0 1", 
				IParameterHandler.ParameterHandlerType.STRING);

		refGeneralManager.getSingelton().getCommandManager().
			createCommand(refParameterHandler).doCommand();
	}
	
	/**
	 * Creates the selection storage in which the IDs of all
	 * selected vertices is stored.
	 * 
	 * @param iArSelectedId
	 */
	protected void createSelectionIdStorage(int[] iArSelectionId) {

		IParameterHandler refParameterHandler = new ParameterHandler();
		
		// CmdId
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_CMD_ID.getXmlKey(), 
				"12345", 
				IParameterHandler.ParameterHandlerType.INT);

		// Type
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_TYPE.getXmlKey(),
				CommandQueueSaxType.CREATE_STORAGE.name(),
				IParameterHandler.ParameterHandlerType.STRING);
		
//		// Label
//		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_LABEL.getXmlKey(), 
//				"Pathway Selection Storage", 
//				IParameterHandler.ParameterHandlerType.STRING);
		
		// TargetID
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_TARGET_ID.getXmlKey(), 
				Integer.toString(iSelectionIdStorageId), 
				IParameterHandler.ParameterHandlerType.INT);
		
		//FIXME: the attributes are overwritten afterwards. so they should get a  proper default value.
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey(), 
				"INT", 
				IParameterHandler.ParameterHandlerType.STRING);

		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey(), 
				"123 123 123", 
				IParameterHandler.ParameterHandlerType.STRING);
		
		refGeneralManager.getSingelton().getCommandManager().
			createCommand(refParameterHandler).doCommand();

	}
	
	/**
	 * 
	 * @param iArSelectedGroup
	 */
	protected void createSelectionGroupStorage(int[] iArSelectedGroup) {
	
		// not implemented yet
	}

	/**
	 * Creates a selection storage that can be used for all kind of
	 * additional selection data.
	 * Therefore the purpose of this object is not restricted.
	 * 
	 * @param iArSelectedOptional
	 */
	protected void createSelectionOptionalStorage(int[] iArSelectionOptional) {
		
		IParameterHandler refParameterHandler = new ParameterHandler();
		
		// CmdId
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_CMD_ID.getXmlKey(), 
				"12345", 
				IParameterHandler.ParameterHandlerType.INT);

		// Type
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_TYPE.getXmlKey(),
				CommandQueueSaxType.CREATE_STORAGE.name(),
				IParameterHandler.ParameterHandlerType.STRING);
		
//		// Label
//		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_LABEL.getXmlKey(), 
//				"Pathway Selection Optional Data Storage", 
//				IParameterHandler.ParameterHandlerType.STRING);
		
		// TargetID
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_TARGET_ID.getXmlKey(), 
				Integer.toString(iSelectionOptionalStorageId), 
				IParameterHandler.ParameterHandlerType.INT);
		
		//FIXME: the attributes are overwritten afterwards. so they should get a  proper default value.
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey(), 
				"INT", 
				IParameterHandler.ParameterHandlerType.STRING);

		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey(), 
				"123 123 123", 
				IParameterHandler.ParameterHandlerType.STRING);
		
		refGeneralManager.getSingelton().getCommandManager().
			createCommand(refParameterHandler).doCommand();

	}

	/**
	 * Creates the selection set.
	 * A selection set contains of the 3 storages 
	 * and a virtual array.
	 * 
	 */
	protected void createSelectionSet() {
		
		IParameterHandler refParameterHandler = new ParameterHandler();
		
		// CmdId
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_CMD_ID.getXmlKey(), 
				"12345", 
				IParameterHandler.ParameterHandlerType.INT);

		// Type
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_TYPE.getXmlKey(),
				CommandQueueSaxType.CREATE_SET.name(),
				IParameterHandler.ParameterHandlerType.STRING);
		
		// Label
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_LABEL.getXmlKey(), 
				"Pathway Selection Set", 
				IParameterHandler.ParameterHandlerType.STRING);
				
		// TargetID (SetID)
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_TARGET_ID.getXmlKey(), 
				Integer.toString(iSelectionSetId), 
				IParameterHandler.ParameterHandlerType.INT);
		
		// VirtualArrayIDs
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey(), 
				Integer.toString(iSelectionVirtualArrayId), 
				IParameterHandler.ParameterHandlerType.STRING);
		
		// StorageIDs
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey(), 
				Integer.toString(iSelectionIdStorageId) + " " + 
//				Integer.toString(iSelectionGroupStorageId) + " " + 
				Integer.toString(iSelectionOptionalStorageId), 
				IParameterHandler.ParameterHandlerType.STRING);
		
		// Detail
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_DETAIL.getXmlKey(), 
				CommandQueueSaxType.CREATE_SET_PLANAR.name(), 
				IParameterHandler.ParameterHandlerType.STRING);

		refGeneralManager.getSingelton().getCommandManager().
			createCommand(refParameterHandler).doCommand();
	}
	
	/**
	 * Creates the mediator and register sender and known
	 * receivers. 
	 * Note: Further receivers can be added later on.
	 *
	 */
	protected void createSelectionMediator() {
		
		IParameterHandler refParameterHandler = new ParameterHandler();
		
		ViewJoglManager viewManager = 
			(ViewJoglManager) refGeneralManager.getSingelton().getViewGLCanvasManager();
		
		ArrayList<AViewRep> arDataExplorerViews = 
			viewManager.getViewByType(ViewType.SWT_DATA_EXPLORER);
		
		Iterator<AViewRep> iterDataExplorerViewRep = 
			arDataExplorerViews.iterator();
		
		String strDataExplorerConcatenation = ""; 
		
		while(iterDataExplorerViewRep.hasNext())
		{
			strDataExplorerConcatenation = strDataExplorerConcatenation.concat(
					Integer.toString(iterDataExplorerViewRep.next().getId()));
			
			//TODO: add space character
		}
		
		// CmdId
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_CMD_ID.getXmlKey(), 
				"12345", 
				IParameterHandler.ParameterHandlerType.INT);
		
		// Type
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_TYPE.getXmlKey(),
				CommandQueueSaxType.CREATE_EVENT_MEDIATOR.name(),
				IParameterHandler.ParameterHandlerType.STRING);

//		// Label
//		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_LABEL.getXmlKey(), 
//				"Pathway Selection Mediator", 
//				IParameterHandler.ParameterHandlerType.STRING);
		
		// TargetID
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_TARGET_ID.getXmlKey(), 
				Integer.toString(iSelectionMediatorId), 
				IParameterHandler.ParameterHandlerType.INT);		 
		
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey(), 
				Integer.toString(iParentContainerId), 
				IParameterHandler.ParameterHandlerType.STRING);

		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey(), 
				strDataExplorerConcatenation + " " +"79401", 
				IParameterHandler.ParameterHandlerType.STRING);
		
		refParameterHandler.setValueAndType(CommandQueueSaxType.TAG_DETAIL.getXmlKey(), 
				MediatorType.SELECTION_MEDIATOR.name(), 
				IParameterHandler.ParameterHandlerType.STRING);
		
		refGeneralManager.getSingelton().getCommandManager().
			createCommand(refParameterHandler).doCommand();
	}

	public void setSelectionIdArray(int[] iArSelectionId) {
		assert iArSelectionId != null : "setSelectionIdArray() called with null-pointer";
		
		((IStorage)refGeneralManager.getItem(iSelectionIdStorageId)).
			setArrayInt(iArSelectionId);
	}

	public void setGroupArray(int[] iArSelectionGroup) {
		assert iArSelectionGroup != null : "setGroupArray() called with null-pointer";
		
		((IStorage)refGeneralManager.getItem(iSelectionGroupStorageId)).
			setArrayInt(iArSelectionGroup);
	}

	public void setOptionalDataArray(int[] iArSelectionOptionalData) {
		assert iArSelectionOptionalData != null : "setOptionalDataArray() called with null-pointer";
		
		((IStorage)refGeneralManager.getItem(iSelectionOptionalStorageId)).
			setArrayInt(iArSelectionOptionalData);	
	}

	public final void setAllSelectionDataArrays(int[] iArSelectionId, int[] iArSelectionGroup, int[] iArSelectionOptionalData) {
				
		setSelectionIdArray(iArSelectionId);
		
		if ( iArSelectionGroup != null )
		{
			setGroupArray(iArSelectionGroup);
		}
		
		if ( iArSelectionOptionalData != null ) 
		{
			setOptionalDataArray(iArSelectionOptionalData);
		}
	}
}
