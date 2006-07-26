/**
 * 
 */
package cerberus.command.base;

import cerberus.command.CommandInterface;
import cerberus.command.CommandManagedInterface;
import cerberus.command.CommandType;
import cerberus.data.UniqueManagedObject;
import cerberus.manager.GeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * @author kalkusch
 *
 */
public abstract class AbstractManagedCommand 
extends UniqueManagedObject 
implements CommandManagedInterface {

	/**
	 * @param iSetCollectionId
	 * @param setGeneralManager
	 */
	public AbstractManagedCommand(int iSetCollectionId,
			GeneralManager setGeneralManager) {
		super(iSetCollectionId, setGeneralManager);
	}




	/**
	 * @see cerberus.command.CommandInterface#isEqualType(cerberus.command.CommandInterface)
	 * @see base.AbstractCommand#isEqualType(cerberus.command.CommandInterface)
	 */
	public boolean isEqualType(CommandInterface compareToObject) {
		if ( compareToObject.getCommandType() == this.getCommandType() ) { 
			return true;
		}
		return false;
	}

	public final ManagerObjectType getBaseType() {
		return ManagerObjectType.COMMAND;
	}


}
