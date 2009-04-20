package org.caleydo.core.manager.event;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.manager.IEventPublisher;

/**
 * <p>
 * Interface for a container for information that are passed on an event basis
 * </p>
 * <p>
 * This interface has to be implemented to be used with the the
 * {@link IEventPublisher#triggerEvent(EMediatorType, IUniqueObject, IEventContainer)} or the
 * {@link IMediator#triggerEvent(IUniqueObject, IEventContainer)}
 * </p>
 * <p>
 * You should use {@link AEventContainer} instead of implementing this interface directly
 * </p>
 * <p>
 * The event type should be set in the constructor
 * </p>
 * 
 * @author Alexander Lex
 */
public interface IEventContainer {

	/**
	 * Returns the type of event
	 * 
	 * @return the type of event
	 */
	public EEventType getEventType();
}
