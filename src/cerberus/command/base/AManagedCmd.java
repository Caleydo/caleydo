/**
 * 
 */
package cerberus.command.base;

import cerberus.command.ICommand;
//import cerberus.command.CommandType;
import cerberus.data.AUniqueManagedObject;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * @author Micahel Kalkusch
 *
 */
public abstract class AManagedCmd 
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
	protected ICommandManager refCommandManager;
	
	/**
	 * @param iSetCollectionId
	 * @param setGeneralManager
	 */
	protected AManagedCmd(final int iSetCollectionId,
			final IGeneralManager setGeneralManager) {
		super(iSetCollectionId, setGeneralManager);
		
		refCommandManager = setGeneralManager.getSingelton().getCommandManager();
	}

	/**
	 * @see cerberus.command.ICommand#isEqualType(cerberus.command.ICommand)
	 * @see base.ACommand#isEqualType(cerberus.command.ICommand)
	 */
	public boolean isEqualType(ICommand compareToObject) {
		if ( compareToObject.getCommandType() == this.getCommandType() ) { 
			return true;
		}
		return false;
	}

	public final ManagerObjectType getBaseType() {
		return ManagerObjectType.COMMAND;
	}

	public void setParameterHandler( IParameterHandler phHandler) {
		assert false : "Call empty methode!";
	}
}
