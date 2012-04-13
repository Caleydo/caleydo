/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.net.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.ListenerMap;
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
		for (Map.Entry<Class<? extends AEvent>, HashMap<String, Collection<AEventListener<?>>>> entry : listenerMap
			.entrySet()) {
			Collection<AEventListener<?>> listeners = new ArrayList<AEventListener<?>>();
			HashMap<String, Collection<AEventListener<?>>> dataDomainToListenersMap = entry.getValue();
			for (Collection<AEventListener<?>> subListeners : dataDomainToListenersMap.values())
				listeners.addAll(subListeners);
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

	public EventPublisher getPublisher() {
		return publisher;
	}

	public void setPublisher(EventPublisher publisher) {
		this.publisher = publisher;
	}

}
