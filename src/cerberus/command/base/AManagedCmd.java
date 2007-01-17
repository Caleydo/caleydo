/**
 * 
 */
package cerberus.command.base;

import cerberus.command.ICommand;
import cerberus.command.ICommandManaged;
//import cerberus.command.CommandType;
import cerberus.data.AUniqueManagedObject;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;

/**
 * @author kalkusch
 *
 */
public abstract class AManagedCmd 
extends AUniqueManagedObject 
implements ICommandManaged {

	/**
	 * @param iSetCollectionId
	 * @param setGeneralManager
	 */
	protected AManagedCmd(int iSetCollectionId,
			IGeneralManager setGeneralManager) {
		super(iSetCollectionId, setGeneralManager);
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


}
