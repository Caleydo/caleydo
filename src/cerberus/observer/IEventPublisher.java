package cerberus.observer;

import cerberus.observer.mediator.IMediatorReceiver;
import cerberus.observer.mediator.IMediatorSender;

public interface IEventPublisher
{
	
	public void register( IMediatorSender sender,
			IMediatorReceiver receiver );
	
	public void register( IMediatorSender senderExisting,
			IMediatorSender senderNew );
	

	
	public void unregister( IMediatorSender sender,
			IMediatorReceiver receiver );
	
	public void unregister( IMediatorSender senderExisting,
			IMediatorSender senderRemove );
	
	/**
	 * Called onyl by Sender.
	 * 
	 * @param sender
	 */
	public void removeMediator( IMediatorSender sender );
	
	public boolean hasRelation( IMediatorSender sender,
			IMediatorReceiver receiver );
}
