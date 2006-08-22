/**
 * 
 */
package cerberus.observer.mediator;

import java.util.Vector;

import cerberus.observer.mediator.IMediatorReceiver;
import cerberus.observer.mediator.IMediatorSender;

/**
 * Attention: Since Mediator is also a IMediatorReceiver care 
 * has to be taken when regsitering a Mediator as Receiver.
 * 
 * @version "Schieﬂ dir nicht ins Bein"
 * 
 * @author kalkusch
 *
 */
public class Mediator 
extends AMediator
implements IMediatorReceiver
{

	protected Vector <IMediatorReceiver> vecReceiver;
	protected Vector <IMediatorSender> vecSender;
	
	/**
	 * @param sender
	 */
	public Mediator( final IMediatorSender sender)
	{
		super(sender);
	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#register(cerberus.observer.mediator.IMediatorSender)
	 */
	public void register(IMediatorSender sender)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#register(cerberus.observer.mediator.IMediatorReceiver)
	 */
	public void register(IMediatorReceiver receiver)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#unregister(cerberus.observer.mediator.IMediatorSender)
	 */
	public void unregister(IMediatorSender sender)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#unregister(cerberus.observer.mediator.IMediatorReceiver)
	 */
	public void unregister(IMediatorReceiver receiver)
	{
		// TODO Auto-generated method stub

	}
	
	public void update( IMediatorSender eventTrigger ) {
		
	}

}
