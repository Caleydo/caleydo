package org.caleydo.core.manager.event.mediator;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.mediator.MediatorUpdateType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Abstract class for the mediator that belongs to the event mechanism.
 * 
 * @see org.caleydo.core.manager.event.mediator.IMediator
 * 
 * @author Micheal Kalkusch
 * @author Marc Streit
 */
public abstract class ALockableMediator 
extends ALockableMediatorReceiver {
	
	protected final IEventPublisher refEventPublisher;
	
	private final MediatorUpdateType mediatorUpdateType;
		
	public final int iMediatorId;
	
	/**
	 * Constructor.
	 * 
	 * @param iMediatorId
	 * @param mediatorUpdateType if ==NULL, MediatorUpdateType.MEDIATOR_DEFAULT is used as default 
	 */
	protected ALockableMediator(final IEventPublisher refEventPublisher,
			int iMediatorId,
			final MediatorUpdateType mediatorUpdateType) {
		
		super();
		
		this.refEventPublisher = refEventPublisher;
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
	 * @see org.caleydo.core.manager.event.mediator.ALockableMediator#destroyMediator(IEventPublisher)
	 * 
	 * @param sender callling object 
	 */
	protected abstract void destroyMediatorDerivedObject( 
			final IEventPublisher sender );

	
	/**
	 * Test if caller is creator and calls destroyMediatorObject(IMediatorSender).
	 * 
	 * @see org.caleydo.core.manager.event.mediator.IMediator#destroyMediator()
	 * @see org.caleydo.core.manager.event.mediator.ALockableMediator#destroyMediatorDerivedObject(IMediatorSender)
	 */
	public final void destroyMediator( final IEventPublisher sender )
	{
		if ( ! refEventPublisher.equals(sender)) {
			throw new CaleydoRuntimeException("IMediator.destroyMediator() may only be callled by its creator!");
		}

		destroyMediatorDerivedObject( sender );
	}
	

	/**
	 * @see org.caleydo.core.data.IUniqueObject#getId()
	 */
	public final int getId() {

		return iMediatorId;
	}

	/**
	 * Since the MediatorId is final this method must not be called.
	 * 
	 * @see org.caleydo.core.data.IUniqueObject#setId(int)
	 */
	public final void setId(int isetId) {
		
		throw new CaleydoRuntimeException("setId() must not be called.",
				CaleydoRuntimeExceptionType.OBSERVER);
		
		//this.iMediatorId = isetId;
	}
}
