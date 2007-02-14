/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.base;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.data.AUniqueManagedObject;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.xml.parser.parameter.IParameterHandler;

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
	 * @see cerberus.manager.ICommandManager#runDoCommand(ICommand)
	 * @see cerberus.manager.ICommandManager#runUndoCommand(ICommand)
	 * @see cerberus.command.ICommand#doCommand()
	 * @see cerberus.command.ICommand#undoCommand()
	 * 
	 */
	protected final ICommandManager refCommandManager;
	
	private CommandQueueSaxType refCommandQueueSaxType;
	
	
	public ACommand(final int iSetCollectionId,
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager) {
		super( iSetCollectionId, refGeneralManager );
		
		this.refCommandManager = refCommandManager;
		
		/*
		 * TODO: remove next assignment!
		 */
		refCommandQueueSaxType = CommandQueueSaxType.NO_OPERATION;
	}

	
	/**
	 * @see prometheus.command.ICommand#isEqualType(prometheus.command.ICommand)
	 *  * @see base.AManagedCmd#isEqualType(cerberus.command.ICommand)
	 */
	public final boolean isEqualType(ICommand compareToObject) {
		if ( compareToObject.getCommandType() == this.getCommandType() ) { 
			return true;
		}
		return false;
	}
	
	public final ManagerObjectType getBaseType() {
		return ManagerObjectType.COMMAND;
	}
	
	public final CommandQueueSaxType getCommandType() {
		return refCommandQueueSaxType;
	}


	public void setParameterHandler( IParameterHandler phHandler) {
		
	}
	
	protected final void setCommandQueueSaxType( final CommandQueueSaxType refCommandQueueSaxType) {
		this.refCommandQueueSaxType  =refCommandQueueSaxType;
	}

}
