/**
 * 
 */
package cerberus.command.queue;

import cerberus.command.ICommand;
import cerberus.command.CommandType;
import cerberus.command.base.AManagedCmd;
import cerberus.command.queue.ICommandQueue;
import cerberus.manager.IGeneralManager;
//import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Runs a command queue.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdSystemRunCmdQueue 
extends AManagedCmd 
implements ICommand {

	protected int iCommandQueueId;
	
	/**
	 * @param iSetCmdCollectionId
	 */
	public CmdSystemRunCmdQueue(int iSetCmdId, 			
			final IGeneralManager setGeneralManager,
			final int iCommandQueueId ) {
		super(iSetCmdId, setGeneralManager);
		
		this.iCommandQueueId = iCommandQueueId;
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		ICommandQueue cmdQueue = 
			this.refGeneralManager.getSingelton().getCommandManager().getCommandQueueByCmdQueueId(iCommandQueueId);
		
		if ( cmdQueue == null ) {
			throw new CerberusRuntimeException("CmdSystemRunCmdQueue::doCommand() cmdQueue==null !");
		}
		
		cmdQueue.doCommand();
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		
		ICommandQueue cmdQueue = 
			this.refGeneralManager.getSingelton().getCommandManager().getCommandQueueByCmdQueueId(iCommandQueueId);
		
		if ( cmdQueue == null ) {
			throw new CerberusRuntimeException("CmdSystemRunCmdQueue::doCommand() cmdQueue==null !");
		}
		
		cmdQueue.doCommand();
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		return CommandType.COMMAND_QUEUE_RUN;
	}

}
