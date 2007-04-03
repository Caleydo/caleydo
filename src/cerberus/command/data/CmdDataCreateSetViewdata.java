/**
 * 
 */
package cerberus.command.data;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;


/**
 * @author Michael Kalkusch
 *
 */
public class CmdDataCreateSetViewdata
extends ACmdCreate_IdTargetLabelAttrDetail 
implements ICommand {

	/**
	 * @param refGeneralManager
	 * @param refCommandManager
	 * @param refCommandQueueSaxType
	 */
	public CmdDataCreateSetViewdata(IGeneralManager refGeneralManager,
			ICommandManager refCommandManager,
			CommandQueueSaxType refCommandQueueSaxType) {

		super(refGeneralManager, refCommandManager, refCommandQueueSaxType);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {

		// TODO Auto-generated method stub

	}

}
