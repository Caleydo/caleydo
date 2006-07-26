/**
 * 
 */
package cerberus.command.queue;

import cerberus.command.CommandInterface;
import cerberus.command.CommandType;
import cerberus.command.base.AbstractManagedCommand;
import cerberus.command.queue.CommandQueueInterface;
import cerberus.manager.GeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Runs a command queue.
 * 
 * @author kalkusch
 *
 */
public class CmdSystemRunCmdQueue 
extends AbstractManagedCommand 
implements CommandInterface {

	protected int iCommandQueueId;
	
	/**
	 * @param iSetCmdCollectionId
	 */
	public CmdSystemRunCmdQueue(int iSetCmdId, 			
			final GeneralManager setGeneralManager,
			final int iCommandQueueId ) {
		super(iSetCmdId, setGeneralManager);
		
		this.iCommandQueueId = iCommandQueueId;
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		CommandQueueInterface cmdQueue = 
			this.refGeneralManager.getSingelton().getCommandManager().getCommandQueueByCmdQueueId(iCommandQueueId);
		
		if ( cmdQueue == null ) {
			throw new CerberusRuntimeException("CmdSystemRunCmdQueue::doCommand() cmdQueue==null !");
		}
		
		cmdQueue.doCommand();
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		
		CommandQueueInterface cmdQueue = 
			this.refGeneralManager.getSingelton().getCommandManager().getCommandQueueByCmdQueueId(iCommandQueueId);
		
		if ( cmdQueue == null ) {
			throw new CerberusRuntimeException("CmdSystemRunCmdQueue::doCommand() cmdQueue==null !");
		}
		
		cmdQueue.doCommand();
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		return CommandType.COMMAND_QUEUE_RUN;
	}

}
