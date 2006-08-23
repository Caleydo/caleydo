package cerberus.manager;

import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;

public interface IEventPublisher extends IGeneralManager
{
	
	public void registerSenderToReceiver( IMediatorSender sender,
			IMediatorReceiver receiver );
	
	public void registerSenderToReceiver( int iMediatorSenderId,
			int iMediatorReceiverId );
	
	public void registerSenderToSender( IMediatorSender senderExisting,
			IMediatorSender senderNew );
		
	public void unregister( IMediatorSender sender,
			IMediatorReceiver receiver );
	
	public void unregister( IMediatorSender senderExisting,
			IMediatorSender senderRemove );
	
	/**
	 * Called only by Sender.
	 * 
	 * @param sender
	 */
	public void removeMediator( IMediatorSender sender );
	
	public boolean hasRelation( IMediatorSender sender,
			IMediatorReceiver receiver );
}
