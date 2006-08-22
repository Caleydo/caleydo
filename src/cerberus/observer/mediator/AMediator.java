/**
 * 
 */
package cerberus.observer.mediator;

import cerberus.observer.mediator.IMediatorSender;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * @author kalkusch
 *
 */
public abstract class AMediator implements IMediator
{

	protected final IMediatorSender refSender;
	
	/**
	 * 
	 */
	protected AMediator( final IMediatorSender sender )
	{
		refSender = sender;
	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#destroyMediator()
	 */
	public final void destroyMediator( final IMediatorSender sender )
	{
		if ( sender != this.refSender ) {
			throw new CerberusRuntimeException("IMediator.destroyMediator() may only be callled by its creator!");
		}

	}

}
