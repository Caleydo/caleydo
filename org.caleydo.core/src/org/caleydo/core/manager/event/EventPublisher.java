package org.caleydo.core.manager.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.caleydo.core.manager.IEventPublisher;

/**
 * Implementation of {@link IEventPublisher}
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class EventPublisher
	implements IEventPublisher {
	private HashMap<EMediatorType, IMediator> hashMediatorType2Mediator;
	
	private ListenerMap listenerMap;

	/**
	 * Constructor.
	 */
	public EventPublisher() {
		hashMediatorType2Mediator = new HashMap<EMediatorType, IMediator>();
		listenerMap = new ListenerMap();
	}

	@Deprecated
	@Override
	public IMediator getPrivateMediator() {
		return new Mediator();
	}

	@Deprecated
	@Override
	public void addSender(EMediatorType eMediatorType, IMediatorSender sender) {
		// Lazy mediator creation
		if (!hashMediatorType2Mediator.containsKey(eMediatorType)) {
			hashMediatorType2Mediator.put(eMediatorType, new Mediator(eMediatorType));
		}

		hashMediatorType2Mediator.get(eMediatorType).addSender(sender);

	}

	@Deprecated
	@Override
	public void addReceiver(EMediatorType eMediatorType, IMediatorReceiver receiver) {
		// Lazy mediator creation
		if (!hashMediatorType2Mediator.containsKey(eMediatorType)) {
			hashMediatorType2Mediator.put(eMediatorType, new Mediator(eMediatorType));
		}

		hashMediatorType2Mediator.get(eMediatorType).addReceiver(receiver);

	}

	@Deprecated
	@Override
	public void triggerEvent(EMediatorType eMediatorType, IMediatorSender eventTrigger,
		IEventContainer eventContainer) {

		if (eMediatorType == EMediatorType.ALL_REGISTERED) {

			for (EMediatorType eTempMediatorType : hashMediatorType2Mediator.keySet()) {
				IMediator tempMediator = hashMediatorType2Mediator.get(eTempMediatorType);
				if (tempMediator.hasSender((IMediatorSender) eventTrigger)) {
					tempMediator.triggerEvent(eventTrigger, eventContainer);
				}
			}
		}
		else {
			IMediator tempMediator = hashMediatorType2Mediator.get(eMediatorType);
			if (tempMediator.hasSender((IMediatorSender) eventTrigger)) {
				tempMediator.triggerEvent(eventTrigger, eventContainer);
			}
		}
	}
	
	@Deprecated
	@Override
	public void removeSender(EMediatorType eMediatorType, IMediatorSender sender) {
		if (!hashMediatorType2Mediator.containsKey(eMediatorType))
			return;

		hashMediatorType2Mediator.get(eMediatorType).removeSender(sender);
	}

	@Deprecated
	@Override
	public void removeReceiver(EMediatorType eMediatorType, IMediatorReceiver receiver) {
		if (!hashMediatorType2Mediator.containsKey(eMediatorType))
			return;

		hashMediatorType2Mediator.get(eMediatorType).removeReceiver(receiver);
	}

	@Deprecated
	@Override
	public void removeSenderFromAllGroups(IMediatorSender sender) {
		for (IMediator mediator : hashMediatorType2Mediator.values()) {
			mediator.removeSender(sender);
		}
	}

	@Deprecated
	@Override
	public void removeReceiverFromAllGroups(IMediatorReceiver receiver) {
		for (IMediator mediator : hashMediatorType2Mediator.values()) {
			mediator.removeReceiver(receiver);
		}
	}

	@Override
	public void addListener(Class<? extends AEvent> eventClass, IEventListener listener) {
		Collection<IEventListener> listeners = listenerMap.get(eventClass);
		if (listeners == null) {
			listeners = new ArrayList<IEventListener>();
			listenerMap.put(eventClass, listeners);
		}
		listeners.add(listener);
	}

	@Override
	public void removeListener(Class<? extends AEvent> eventClass, IEventListener listener) {
		Collection<IEventListener> listeners = listenerMap.get(eventClass);
		listeners.remove(listener);
	}

	@Override
	public void removeListener(IEventListener listener) {
		for (Collection<IEventListener> listeners : listenerMap.values()) {
			listeners.remove(listener);			
		}
	}

	@Override
	public void triggerEvent(AEvent event) {
		Collection<IEventListener> listeners = listenerMap.get(event.getClass());
		if (listeners != null) {
			for (IEventListener receiver : listeners) {
				receiver.handleEvent(event);
			}
		}
	}
}
