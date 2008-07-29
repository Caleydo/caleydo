package org.caleydo.core.command.data;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.selection.Selection;
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
public class CmdDataCreateSelection 
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
	public CmdDataCreateSelection(
			final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType) {

		super(generalManager,
				commandManager,
				commandQueueSaxType);
	}

	public void doCommand() throws CaleydoRuntimeException {
		
		generalManager.getSelectionManager().createSelection(
				generalManager.getSelectionManager().createId(ManagerObjectType.SELECTION));
		
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
}
