package org.caleydo.core.command.data;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.caleydo.core.data.collection.SetType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

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
extends ACmdCreate_IdTargetLabelAttrDetail {
	
//	ISet selectionSet;
	
	protected int iSelectionVirtualArrayId = -1;
	protected int iSelectionIdStorageId = -1;
	protected int iSelectionGroupStorageId = -1;
	protected int iSelectionOptionalStorageId = -1;
	
	/**
	 * Constructor. 
	 * 
	 * @param generalManager
	 * @param commandManager
	 * @param commandQueueSaxType
	 */
	public CmdDataCreateSelectionSetMakro(
			final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType) {

		super(generalManager,
				commandManager,
				commandQueueSaxType);
	}

	public void doCommand() throws CaleydoRuntimeException {
		
		// First the storages and virtual arrays are created.
		createSelectionVirtualArray();
		createSelectionIdStorage();
		createSelectionGroupStorage();
		createSelectionOptionalStorage();
		
		// Here the selection set itself are created
		// and the storages and VA is assigned to it.
		createSelectionSet();
		
		commandManager.runDoCommand(this);
	}
	
	public void undoCommand() throws CaleydoRuntimeException {
		
		commandManager.runUndoCommand(this);		
	}
	
	public void setParameterHandler( final IParameterHandler parameterHandler ) {
		
		assert parameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(parameterHandler);	
		
		// Nothing else to do here because the command only
		// needs an target Set ID, which is already
		// read by the super class.

	}

	public void setAttributes(int iSelectionSetId) {
		
		this.iUniqueId = iSelectionSetId;
	}
		
	public String getInfoText() {
		return super.getInfoText() + " -> " + this.iUniqueId + ": " + this.sLabel;
	}
	
	/**
	 * Creates a selection virtual array.
	 * Usually the selection will start with 0
	 * and is of the same size as the storage.
	 *
	 */
	protected void createSelectionVirtualArray() {
		
		// Retrieve new ID
		iSelectionVirtualArrayId = generalManager.getVirtualArrayManager()
			.createId(ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK);
		
		CmdDataCreateVirtualArray createdCommand = 
			(CmdDataCreateVirtualArray) generalManager.getCommandManager()
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
		iSelectionIdStorageId = generalManager.getStorageManager()
			.createId(ManagerObjectType.STORAGE_FLAT);
				
		CmdDataCreateStorage createdCommand = 
			(CmdDataCreateStorage) generalManager.getCommandManager()
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
		iSelectionGroupStorageId = generalManager.getStorageManager()
			.createId(ManagerObjectType.STORAGE_FLAT);
			
		CmdDataCreateStorage createdCommand = 
			(CmdDataCreateStorage) generalManager.getCommandManager()
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
		iSelectionOptionalStorageId = generalManager.getStorageManager()
			.createId(ManagerObjectType.STORAGE_FLAT);
			
		CmdDataCreateStorage createdCommand = 
			(CmdDataCreateStorage) generalManager.getCommandManager()
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
			(CmdDataCreateSet) generalManager.getCommandManager()
				.createCommandByType(CommandQueueSaxType.CREATE_SET_DATA);

		StringBuffer stBuffer = new StringBuffer();
		
		stBuffer.append(Integer.toString(iSelectionIdStorageId));
		stBuffer.append(" "); 
		stBuffer.append(Integer.toString(iSelectionGroupStorageId));
		stBuffer.append(" ");
		stBuffer.append(Integer.toString(iSelectionOptionalStorageId));
		
		String sVirtualArrayIDs = Integer.toString(iSelectionVirtualArrayId);
		
		createdCommand.setAttributes(iUniqueId, 
				sVirtualArrayIDs, 
				stBuffer.toString(),
				SetType.SET_SELECTION); 
		
		createdCommand.doCommand();
	}
}
