package org.caleydo.core.net.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.ListenerMap;
import org.caleydo.core.net.EventFilterBridge;
import org.caleydo.core.serialize.SerializationManager;

public class ConfigureableEventBridge
	implements IConfigureableEventList {

	EventFilterBridge bridge;

	EventPublisher publisher;

	public ConfigureableEventBridge(EventFilterBridge bridge, EventPublisher publisher) {
		this.bridge = bridge;
		this.publisher = publisher;
	}

	@Override
	public Collection<Class<? extends AEvent>> getAllEventTypes() {
		return SerializationManager.getSerializeableEventTypes();
	}

	@Override
	public Collection<Class<? extends AEvent>> getSelectedEventTypes() {
		Collection<Class<? extends AEvent>> selectedEventTypes = new ArrayList<Class<? extends AEvent>>();
		ListenerMap listenerMap = publisher.getListenerMap();
		for (Map.Entry<Class<? extends AEvent>, Collection<AEventListener<?>>> entry : listenerMap.entrySet()) {
			Collection<AEventListener<?>> listeners = entry.getValue();
			if (listeners.contains(bridge)) {
				selectedEventTypes.add(entry.getKey());
			}
		}
		return selectedEventTypes;
	}

	public EventFilterBridge getBridge() {
		return bridge;
	}

	public void setBridge(EventFilterBridge bridge) {
		this.bridge = bridge;
	}

	public IEventPublisher getPublisher() {
		return publisher;
	}

	public void setPublisher(EventPublisher publisher) {
		this.publisher = publisher;
	}

}
