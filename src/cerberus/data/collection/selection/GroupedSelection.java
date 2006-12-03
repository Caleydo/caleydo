package cerberus.data.collection.selection;

import java.util.ArrayList;
import java.util.Iterator;

import cerberus.command.data.CmdDataCreateSet;
import cerberus.command.data.CmdDataCreateStorage;
import cerberus.command.data.CmdDataCreateVirtualArray;
import cerberus.command.event.CmdEventCreateMediator;
import cerberus.data.collection.IGroupedSelection;
import cerberus.data.collection.IStorage;
import cerberus.manager.IGeneralManager;
import cerberus.manager.event.EventPublisher;
import cerberus.manager.view.ViewJoglManager;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.AViewRep.ViewType;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.xml.parser.parameter.ParameterHandler;

/**
 * Selection objects are created for each view that
 * needs to send selections to other views.
 * The selection will be created when the first user selection
 * takes place in the view.
 * 
 * @author Marc Streit
 *
 */
public class GroupedSelection
implements IGroupedSelection {

	protected IGeneralManager refGeneralManager;
	
	protected int iParentContainerId;
	
	//TODO: retrieve new generated IDs instead of static ones
	protected int iSelectionMediatorId = 95201;
	protected int iSelectionSetId = 75101;
	protected int iSelectionVirtualArrayId = 45201;
	protected int iSelectionIdStorageId = 45301;
	protected int iSelectionGroupStorageId = 55301;
	protected int iSelectionOptionalStorageId = 65301;

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
	public GroupedSelection(IGeneralManager refGeneralManager,
			int iParentContainerId,
			int[] iArSelectionId,
			int[] iArSelectionGroup,
			int[] iArSelectionOptionalData ) {
		
		this.refGeneralManager = refGeneralManager;
		this.iParentContainerId = iParentContainerId;
		
		createSelectionVirtualArray();
		createSelectionIdStorage(iArSelectionId);
		createSelectionGroupStorage(iArSelectionGroup);
		createSelectionOptionalStorage(iArSelectionOptionalData);
		createSelectionSet();

		setSelectionIdArray(iArSelectionId);
		setGroupArray(iArSelectionGroup);
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
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_CMD_ID.getXmlKey(), 
				"12345", 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_CMD_ID.getDefault());

		// Label
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_LABEL.getXmlKey(), 
				"Pathway Selection Virtual Array", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_LABEL.getDefault());
		
		// TargetID
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_TARGET_ID.getXmlKey(), 
				Integer.toString(iSelectionVirtualArrayId), 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_TARGET_ID.getDefault());
		
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey(), 
				"3 0 0 1", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getDefault());

		CmdDataCreateVirtualArray createdVirtualArrayCommand = 
			new CmdDataCreateVirtualArray(refGeneralManager, refParameterHandler);
		createdVirtualArrayCommand.doCommand();

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
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_CMD_ID.getXmlKey(), 
				"12345", 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_CMD_ID.getDefault());

		// Label
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_LABEL.getXmlKey(), 
				"Pathway Selection Storage", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_LABEL.getDefault());
		
		// TargetID
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_TARGET_ID.getXmlKey(), 
				Integer.toString(iSelectionIdStorageId), 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_TARGET_ID.getDefault());
		
		//FIXME: the attributes are overwritten afterwards. so they should get a  proper default value.
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey(), 
				"INT", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getDefault());

		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey(), 
				"123 123 123", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getDefault());
		
		CmdDataCreateStorage createdStorageDataCommand = 
			new CmdDataCreateStorage(refGeneralManager, refParameterHandler, true);
		createdStorageDataCommand.doCommand();
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
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_CMD_ID.getXmlKey(), 
				"12345", 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_CMD_ID.getDefault());

		// Label
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_LABEL.getXmlKey(), 
				"Pathway Selection Neighbor Storage", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_LABEL.getDefault());
		
		// TargetID
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_TARGET_ID.getXmlKey(), 
				Integer.toString(iSelectionOptionalStorageId), 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_TARGET_ID.getDefault());
		
		//FIXME: the attributes are overwritten afterwards. so they should get a  proper default value.
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey(), 
				"INT", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getDefault());

		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey(), 
				"123 123 123", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getDefault());
		
		CmdDataCreateStorage createdOptionalStorageCommand = 
			new CmdDataCreateStorage(refGeneralManager, refParameterHandler, true);
		createdOptionalStorageCommand.doCommand();
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
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_CMD_ID.getXmlKey(), 
				"12345", 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_CMD_ID.getDefault());

		// Label
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_LABEL.getXmlKey(), 
				"Pathway Selection Set", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_LABEL.getDefault());
				
		// TargetID (SetID)
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_TARGET_ID.getXmlKey(), 
				Integer.toString(iSelectionSetId), 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_TARGET_ID.getDefault());
		
		// VirtualArrayIDs
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey(), 
				Integer.toString(iSelectionVirtualArrayId), 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getDefault());
		
		// StorageIDs
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey(), 
				Integer.toString(iSelectionIdStorageId) + " " + 
				Integer.toString(iSelectionGroupStorageId) + " " + 
				Integer.toString(iSelectionOptionalStorageId), 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getDefault());
		
		// Detail
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_DETAIL.getXmlKey(), 
				"CREATE_SET_PLANAR", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_DETAIL.getDefault());
		
		CmdDataCreateSet createdSetCommand = new CmdDataCreateSet(refGeneralManager, refParameterHandler, true);
		createdSetCommand.doCommand();
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
			viewManager.getViewByType(ViewType.DATA_EXPLORER);
		
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
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_CMD_ID.getXmlKey(), 
				"12345", 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_CMD_ID.getDefault());

		// Label
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_LABEL.getXmlKey(), 
				"Pathway Selection Mediator", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_LABEL.getDefault());
		
		// TargetID
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_TARGET_ID.getXmlKey(), 
				Integer.toString(iSelectionMediatorId), 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_TARGET_ID.getDefault());		 
		
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey(), 
				Integer.toString(iParentContainerId), 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getDefault());

		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey(), 
				strDataExplorerConcatenation + " " +"79401", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getDefault());
		
		refParameterHandler.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_DETAIL.getXmlKey(), 
				"SELECTION_MEDIATOR", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_DETAIL.getDefault());
		
		CmdEventCreateMediator createdCommand =
			new CmdEventCreateMediator(refGeneralManager, refParameterHandler);	
	
		createdCommand.doCommand();

	}

	public void setSelectionIdArray(int[] iArSelectionId) {

		((IStorage)refGeneralManager.getItem(iSelectionIdStorageId)).
			setArrayInt(iArSelectionId);
	}

	public void setGroupArray(int[] iArSelectionGroup) {

		((IStorage)refGeneralManager.getItem(iSelectionGroupStorageId)).
			setArrayInt(iArSelectionGroup);
	}

	public void setOptionalDataArray(int[] iArSelectionOptionalData) {

		((IStorage)refGeneralManager.getItem(iSelectionOptionalStorageId)).
			setArrayInt(iArSelectionOptionalData);	
	}
}
