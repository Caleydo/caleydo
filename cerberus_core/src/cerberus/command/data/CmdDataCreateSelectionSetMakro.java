package cerberus.command.data;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class creates a selection set 
 * including the three storages
 * and the virtual array.
 * This commands act as a MAKRO.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdDataCreateSelectionSetMakro 
extends ACmdCreate_IdTargetLabelAttrDetail 
implements ICommand {
	
//	ISet refSelectionSet;
	
	protected int iSelectionVirtualArrayId = -1;
	protected int iSelectionIdStorageId = -1;
	protected int iSelectionGroupStorageId = -1;
	protected int iSelectionOptionalStorageId = -1;
	
	/**
	 * Constructor. 
	 * 
	 * @param refGeneralManager
	 * @param refCommandManager
	 * @param refCommandQueueSaxType
	 */
	public CmdDataCreateSelectionSetMakro(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {

		super(refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
	}

	public void doCommand() throws GeneViewRuntimeException {
		
		// First the storages and virtual arrays are created.
		createSelectionVirtualArray();
		createSelectionIdStorage();
		createSelectionGroupStorage();
		createSelectionOptionalStorage();
		
		// Here the selection set itself are created
		// and the storages and VA is assigned to it.
		createSelectionSet();
		
		refCommandManager.runDoCommand(this);
	}
	
	public void undoCommand() throws GeneViewRuntimeException {
		
		refCommandManager.runUndoCommand(this);		
	}
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
		
		// Nothing else to do here because the command only
		// needs an target Set ID, which is already
		// read by the super class.

	}

	public void setAttributes(int iSelectionSetId) {
		
		this.iUniqueTargetId = iSelectionSetId;
	}
		
	public String getInfoText() {
		return super.getInfoText() + " -> " + this.iUniqueTargetId + ": " + this.sLabel;
	}
	
	/**
	 * Creates a selection virtual array.
	 * Usually the selection will start with 0
	 * and is of the same size as the storage.
	 *
	 */
	protected void createSelectionVirtualArray() {
		
		// Retrieve new ID
		iSelectionVirtualArrayId = refGeneralManager.getSingelton().getVirtualArrayManager()
			.createId(ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK);
		
		CmdDataCreateVirtualArray createdCommand = 
			(CmdDataCreateVirtualArray) refGeneralManager.getSingelton().getCommandManager()
				.createCommandByType(CommandQueueSaxType.CREATE_VIRTUAL_ARRAY);
		
		createdCommand.setAttributes(iSelectionVirtualArrayId, 3, 0, 0, 1);	
		createdCommand.doCommand();
	}
	
	/**
	 * Creates the selection storage in which the IDs of all
	 * selected vertices are stored.
	 */
	protected void createSelectionIdStorage() {

		// Retrieve new ID
		iSelectionIdStorageId = refGeneralManager.getSingelton().getStorageManager()
			.createId(ManagerObjectType.STORAGE_FLAT);
				
		CmdDataCreateStorage createdCommand = 
			(CmdDataCreateStorage) refGeneralManager.getSingelton().getCommandManager()
				.createCommandByType(CommandQueueSaxType.CREATE_STORAGE);

		// The storage will be empty after creation.
		// The real data values are filled in afterwards.
		createdCommand.setAttributes(iSelectionIdStorageId, "INT", "0");
		createdCommand.doCommand();
	}
	
	/**
	 * Creates a selection storage that is intended
	 * to be used four selections grouping information.
	 */
	protected void createSelectionGroupStorage() {
	
		// Retrieve new ID
		iSelectionGroupStorageId = refGeneralManager.getSingelton().getStorageManager()
			.createId(ManagerObjectType.STORAGE_FLAT);
			
		CmdDataCreateStorage createdCommand = 
			(CmdDataCreateStorage) refGeneralManager.getSingelton().getCommandManager()
				.createCommandByType(CommandQueueSaxType.CREATE_STORAGE);

		// The storage will be empty after creation.
		// The real data values are filled in afterwards.
		createdCommand.setAttributes(iSelectionGroupStorageId, "INT", "0");
		createdCommand.doCommand();
	}

	/**
	 * Creates a selection storage that can be used for all kind of
	 * additional selection data.
	 * Therefore the purpose of this object is not restricted.
	 */
	protected void createSelectionOptionalStorage() {

		// Retrieve new ID
		iSelectionOptionalStorageId = refGeneralManager.getSingelton().getStorageManager()
			.createId(ManagerObjectType.STORAGE_FLAT);
			
		CmdDataCreateStorage createdCommand = 
			(CmdDataCreateStorage) refGeneralManager.getSingelton().getCommandManager()
				.createCommandByType(CommandQueueSaxType.CREATE_STORAGE);

		// The storage will be empty after creation.
		// The real data values are filled in afterwards.
		createdCommand.setAttributes(iSelectionOptionalStorageId, "INT", "0");
		createdCommand.doCommand();
	}

	/**
	 * Creates the selection set.
	 * A selection set contains of the 3 storages 
	 * and a virtual array.
	 * 
	 */
	protected void createSelectionSet() {
			
		CmdDataCreateSet createdCommand = 
			(CmdDataCreateSet) refGeneralManager.getSingelton().getCommandManager()
				.createCommandByType(CommandQueueSaxType.CREATE_SET_DATA);

		StringBuffer stBuffer = new StringBuffer();
		
		stBuffer.append(Integer.toString(iSelectionIdStorageId));
		stBuffer.append(" "); 
		stBuffer.append(Integer.toString(iSelectionGroupStorageId));
		stBuffer.append(" ");
		stBuffer.append(Integer.toString(iSelectionOptionalStorageId));
		
		String sVirtualArrayIDs = Integer.toString(iSelectionVirtualArrayId);
		
		createdCommand.setAttributes(iUniqueTargetId, 
				sVirtualArrayIDs, 
				stBuffer.toString(),
				null);  // use null because we what to create a SelectionSet! 
		
		createdCommand.doCommand();
	}
}
