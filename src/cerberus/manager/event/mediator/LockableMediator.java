/**
 * 
 */
package cerberus.manager.event.mediator;

import java.util.Vector;
import java.util.Iterator;


/**
 * Attention: Since Mediator is also a IMediatorReceiver care 
 * has to be taken when regsitering a Mediator as Receiver.
 * 
 * @version "Schie� dir nicht ins Bein"
 * 
 * @author kalkusch
 *
 */
public class LockableMediator 
extends ALockableMediator 
implements IMediator
{

	protected Vector <IMediatorReceiver> vecReceiver;
	
	protected Vector <IMediatorSender>   vecSender;
	
	/**
	 * 
	 */
	public LockableMediator( IMediatorSender sender )
	{
		super( sender );
		
		vecReceiver = new Vector <IMediatorReceiver> ();
		vecSender   = new Vector <IMediatorSender> ();
	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#destroyMediator(cerberus.observer.mediator.IMediatorSender)
	 */
	protected void destroyMediatorDerivedObject( final IMediatorSender sender )
	{
		updateStall();
		
		this.vecReceiver.clear();
		this.vecSender.clear();
	}

	
	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.AThreadedMediatorReceiver#updateReceiver(cerberus.observer.mediator.IMediatorSender)
	 */
	@Override
	public void updateReceiver(Object eventTrigger)
	{
		Iterator <IMediatorReceiver> iter = vecReceiver.iterator();
		
		while ( iter.hasNext() ) {
			iter.next().update( eventTrigger );
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#register(cerberus.observer.mediator.IMediatorSender)
	 */
	public boolean register(IMediatorSender sender)
	{
		if ( vecSender.contains( sender ) ) {
			//throw new CerberusRuntimeException("LockableMediator.register() receiver that is already registered!");
			return false;
		}
		
		vecSender.addElement( sender );
		
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#register(cerberus.observer.mediator.IMediatorReceiver)
	 */
	public boolean register(IMediatorReceiver receiver)
	{
		if ( vecReceiver.contains( receiver ) ) {
			//throw new CerberusRuntimeException("LockableMediator.register() receiver that is already registered!");
			return false;
		}
		
		vecReceiver.addElement( receiver );
		
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#unregister(cerberus.observer.mediator.IMediatorSender)
	 */
	public boolean unregister(IMediatorSender sender)
	{
		return vecSender.remove( sender );
	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#unregister(cerberus.observer.mediator.IMediatorReceiver)
	 */
	public boolean unregister(IMediatorReceiver receiver)
	{
		return vecReceiver.remove( receiver );
	}


}
