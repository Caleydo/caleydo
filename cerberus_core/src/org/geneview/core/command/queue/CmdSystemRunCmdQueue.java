/**
 * 
 */
package org.geneview.core.command.queue;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACommand;
import org.geneview.core.command.queue.ICommandQueue;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
//import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.util.exception.GeneViewRuntimeException;

/**
 * Runs a command queue.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdSystemRunCmdQueue 
extends ACommand {

	protected int iCommandQueueId;
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdSystemRunCmdQueue(int iSetCmdId, 			
			final IGeneralManager setGeneralManager,
			final ICommandManager setCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType,
			final int iCommandQueueId ) {
		
		super(iSetCmdId, 
				setGeneralManager, 
				setCommandManager,
				refCommandQueueSaxType);
		
		this.iCommandQueueId = iCommandQueueId;
		
		setCommandQueueSaxType(CommandQueueSaxType.COMMAND_QUEUE_RUN);
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {
		ICommandQueue cmdQueue = 
			this.refGeneralManager.getSingelton().getCommandManager().getCommandQueueByCmdQueueId(iCommandQueueId);
		
		if ( cmdQueue == null ) {
			throw new GeneViewRuntimeException("CmdSystemRunCmdQueue::doCommand() cmdQueue==null !");
		}
		
		cmdQueue.doCommand();
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {
		
		ICommandQueue cmdQueue = 
			this.refGeneralManager.getSingelton().getCommandManager().getCommandQueueByCmdQueueId(iCommandQueueId);
		
		if ( cmdQueue == null ) {
			throw new GeneViewRuntimeException("CmdSystemRunCmdQueue::doCommand() cmdQueue==null !");
		}
		
		cmdQueue.doCommand();
	}

}
