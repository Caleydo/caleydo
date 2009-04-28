package org.caleydo.core.manager;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.EEventType;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IEventListener;
import org.caleydo.core.manager.event.IMediator;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.event.IMediatorSender;

/**
 * <p>
 * This class manages mediators. There are two basic types of Mediators: private and public, system-wide ones.
 * </p>
 * <p>
 * The private mediators are created by calling {@link #getPrivateMediator()} and are not stored or managed
 * any further by the publisher.
 * </p>
 * <p>
 * The public mediators are unique system-wide. The possible mediators are listed in {@link EMediatorType}
 * </p>
 * <p>
 * The publisher provides a complete abstraction of the public mediators, so that no access to the concrete
 * mediators is needed.
 * </p>
 * <p>
 * Object that can receive updates form the mediator have to implement {@link IMediatorReceiver}, those that
 * want to send updates have to implement {@link IMediatorSender}
 * </p>
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IEventPublisher {

	/**
	 * Get a mediator that is not stored anywhere, for your private use only.
	 * 
	 * @return
	 */
	public IMediator getPrivateMediator();

	/**
	 * Trigger an update concerning selections. The details about what to do with the update are specified in
	 * the delta.
	 * 
	 * @param eMediatorType
	 *            for which mediator
	 * @param eventTrigger
	 *            the caller
	 * @param selectionDelta
	 *            the delta containing all operations to be executed
	 * @param colSelectionCommand
	 *            a command to be executed on the selection manager (can be null if not necessary)
	 */
	// public void triggerSelectionUpdate(EMediatorType eMediatorType,
	// IUniqueObject eventTrigger, ISelectionDelta selectionDelta,
	// Collection<SelectionCommand> colSelectionCommand);
	//
	// /**
	// * Trigger an update concerning virtual arrays. The details about what to
	// do
	// * with the update are specified in the delta.
	// *
	// * @param eMediatorType for which mediator
	// * @param eventTrigger the caller
	// * @param delta the delta containing all operations to be executed
	// */
	// public void triggerVAUpdate(EMediatorType eMediatorType, IUniqueObject
	// eventTrigger,
	// IVirtualArrayDelta delta, Collection<SelectionCommand>
	// colSelectionCommand);
	/**
	 * Triggers an event, signals that something has happened and sends data along
	 * 
	 * @param eventTrigger
	 *            the caller
	 * @param eventContainer
	 *            containing the information on the type of the event {@link EEventType} and possibly data
	 *            associated
	 */
	public void triggerEvent(EMediatorType eMediatorType, IMediatorSender eventTrigger,
		IEventContainer eventContainer);

	/**
	 * Adds a sender to the mediator specified in eMediatorType
	 * 
	 * @param eMediatorType
	 *            The mediator that is used to pass this type of events
	 * @param sender
	 *            the sender to be registered
	 */
	public void addSender(EMediatorType eMediatorType, IMediatorSender sender);

	/**
	 * Adds a receiver to the mediator specified in eMediatorType
	 * 
	 * @param eMediatorType
	 *            the type of the mediator
	 * @param receiver
	 *            the receiver to be registered
	 */
	public void addReceiver(EMediatorType eMediatorType, IMediatorReceiver receiver);

	/**
	 * Removes a sender from the mediator specified in eMediatorType
	 * 
	 * @param eMediatorType
	 *            the type of the mediator
	 * @param sender
	 *            the sender to be removed
	 */
	public void removeSender(EMediatorType eMediatorType, IMediatorSender sender);

	/**
	 * Removes a receiver from the mediator specified in eMediatorType
	 * 
	 * @param eMediatorType
	 *            the type of the mediator
	 * @param reveiver
	 *            the receiver to be removed
	 */
	public void removeReceiver(EMediatorType eMediatorType, IMediatorReceiver receiver);

	/**
	 * Removes a sender from all public mediators
	 * 
	 * @param sender
	 *            the sender to be removed
	 */
	public void removeSenderFromAllGroups(IMediatorSender sender);

	/**
	 * Removes a receiver from all public mediators
	 * 
	 * @param receiver
	 *            the receiver to be removed
	 */
	public void removeReceiverFromAllGroups(IMediatorReceiver receiver);
	
	/**
	 * adds a receiver to the list of event handlers
	 * @param eventClass event type to register the handler to
	 * @param listener IMediatorReceiver to handle events
	 */
	public void addListener(Class<? extends AEvent> eventClass, IEventListener listener);
	
	/**
	 * removes a contained receiver from the list of event handlers
	 * @param eventClass event type to remove the handler from 
	 * @param listener IMediatorReceiver to handle events
	 */
	public void removeListener(Class<? extends AEvent> eventClass, IEventListener listener);

	/**
	 * removes a listener from all events in this event-publisher
	 * @param listener listener to remove
	 */
	public void removeListener(IEventListener listener);
	
	/**
	 * New central event handling and distribution method
	 * @param event event to distribute to the listeners
	 */
	public void triggerEvent(AEvent event);

}
