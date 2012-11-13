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
package org.caleydo.core.event;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.manager.GeneralManager;

/**
 * utility class to hold a list of event listeners to register and remove them all in an convinced way
 *
 * @author Samuel Gratzl
 *
 */
public final class EventListeners {
	private static final EventPublisher EVENT_PUBLISHER = GeneralManager.get().getEventPublisher();

	private final Collection<AEventListener<?>> listeners = new ArrayList<>();

	public <T extends AEventListener<?>> void register(Class<? extends AEvent> event, AEventListener<?> listener) {
		listeners.add(listener);
		EVENT_PUBLISHER.addListener(event, listener);
	}

	public void unregisterAll() {
		for (AEventListener<?> listener : listeners) {
			EVENT_PUBLISHER.removeListener(listener);
		}
		listeners.clear();
	}

}
