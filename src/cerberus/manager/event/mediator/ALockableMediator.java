/*
 * Project: GenView
 *  
 */

package cerberus.manager.event.mediator;

import cerberus.util.exception.CerberusRuntimeException;

/**
 * @author kalkusch
 *
 */
public abstract class ALockableMediator 
extends ALockableMediatorReceiver
implements IMediator
{

	protected final IMediatorSender refSender;
	
	/**
	 * 
	 */
	protected ALockableMediator( final IMediatorSender sender )
	{
		super();
		
		refSender = sender;
	}

	/**
	 * Implement cleanup inside this function.
	 * 
	 * @see cerberus.manager.event.mediator.ALockableMediator#destroyMediator(IMediatorSender)
	 * 
	 * @param sender callling object 
	 */
	protected abstract void destroyMediatorDerivedObject( final IMediatorSender sender );

	
	/**
	 * Test if caller is creator and calls destroyMediatorObject(IMediatorSender).
	 * 
	 * @see cerberus.manager.event.mediator.IMediator#destroyMediator()
	 * @see cerberus.manager.event.mediator.ALockableMediator#destroyMediatorDerivedObject(IMediatorSender)
	 */
	public final void destroyMediator( final IMediatorSender sender )
	{
		if ( sender != this.refSender ) {
			throw new CerberusRuntimeException("IMediator.destroyMediator() may only be callled by its creator!");
		}

		destroyMediatorDerivedObject( sender );
	}
	

}
