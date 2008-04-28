package org.caleydo.core.command.base;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.data.AUniqueManagedObject;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class ACommand 
extends AUniqueManagedObject
implements ICommand {

	/**
	 * Reference ot ICommandManager
	 * 
	 * Used to
	 * 
	 * @see org.caleydo.core.manager.ICommandManager#runDoCommand(ICommand)
	 * @see org.caleydo.core.manager.ICommandManager#runUndoCommand(ICommand)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 * 
	 */
	protected final ICommandManager refCommandManager;
	
	private CommandQueueSaxType refCommandQueueSaxType;
	
	
	public ACommand(final int iSetCollectionId,
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		
		super( iSetCollectionId, refGeneralManager );
		
		this.refCommandManager = refCommandManager;
		this.refCommandQueueSaxType = refCommandQueueSaxType;
	}

	
	/**
	 * @see prometheus.command.ICommand#isEqualType(prometheus.command.ICommand)
	 *  * @see base.AManagedCmd#isEqualType(org.caleydo.core.command.ICommand)
	 */
	public final boolean isEqualType(ICommand compareToObject) {
		if ( compareToObject.getCommandType() == this.getCommandType() ) { 
			return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.IUniqueManagedObject#getBaseType()
	 */
	public final ManagerObjectType getBaseType() {
		return ManagerObjectType.COMMAND;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#getCommandType()
	 */
	public final CommandQueueSaxType getCommandType() {
		return refCommandQueueSaxType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler( IParameterHandler phHandler) {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#getInfoText()
	 */
	public String getInfoText() {
		
		return refCommandQueueSaxType.getInfoText() + " [" + this.getId() + "]";
	}
	
	protected final void setCommandQueueSaxType( final CommandQueueSaxType refCommandQueueSaxType) {
		this.refCommandQueueSaxType  =refCommandQueueSaxType;
	}
}
