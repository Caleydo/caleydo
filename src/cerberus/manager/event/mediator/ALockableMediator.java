/*
 * Project: GenView
 *  
 */

package cerberus.manager.event.mediator;

import cerberus.manager.event.mediator.MediatorUpdateType;

/**
 * Abstract class for the mediator that belongs to the event mechanism.
 * 
 * @author Micheal Kalkusch
 * @author Marc Streit
 */
public abstract class ALockableMediator 
extends ALockableMediatorReceiver
implements IMediator {
	
	private final MediatorUpdateType mediatorUpdateType;
		
	public final int iMediatorId;
	
	/**
	 * 
	 * @param iMediatorId
	 * @param mediatorUpdateType if ==NULL, MediatorUpdateType.MEDIATOR_DEFAULT is used as default 
	 */
	protected ALockableMediator(int iMediatorId,
			final MediatorUpdateType mediatorUpdateType) {
		
		super();
		
		this.iMediatorId = iMediatorId;
		
		if ( mediatorUpdateType == null ) 
		{
			this.mediatorUpdateType = MediatorUpdateType.MEDIATOR_DEFAULT;
		}
		else 
		{
			this.mediatorUpdateType = mediatorUpdateType;
		}
	}

	public final MediatorUpdateType getMediatorUpdateTypeType() {
		return mediatorUpdateType;
	}
	
	/**
	 * Implement cleanup inside this function.
	 * 
	 * @see cerberus.manager.event.mediator.ALockableMediator#destroyMediator(IMediatorSender)
	 * 
	 * @param sender callling object 
	 */
	protected abstract void destroyMediatorDerivedObject( 
			final IMediatorSender sender );

	
	/**
	 * Test if caller is creator and calls destroyMediatorObject(IMediatorSender).
	 * 
	 * @see cerberus.manager.event.mediator.IMediator#destroyMediator()
	 * @see cerberus.manager.event.mediator.ALockableMediator#destroyMediatorDerivedObject(IMediatorSender)
	 */
	public final void destroyMediator( final IMediatorSender sender )
	{
//		if ( sender != this.refSender ) {
//			throw new CerberusRuntimeException("IMediator.destroyMediator() may only be callled by its creator!");
//		}
//
//		destroyMediatorDerivedObject( sender );
	}
	

}
