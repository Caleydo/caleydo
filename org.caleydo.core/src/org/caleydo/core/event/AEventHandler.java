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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlTransient;

import org.caleydo.core.util.collection.Pair;

/**
 * <p>
 * Base class for event handlers which can not process their event in a pre-existing loop (as views do).
 * </p>
 * <p>
 * All classes of this instance must be started in this fashion:
 * </p>
 *
 * <pre>
 * AEventHandler handler = new EventHandler();
 * Thread thread = new Thread(handler, &quot;thread name&quot;);
 * thread.start();
 * </pre>
 * <p>
 * This process calls the {@link IListenerOwner#registerEventListeners()} method. It's finalize calls the
 * {@link IListenerOwner#unregisterEventListeners()}
 * </p>
 *
 * @author Alexander Lex
 */
public abstract class AEventHandler
	implements Runnable, IListenerOwner {

	/**
	 * The queue which holds the events
	 */
	@XmlTransient
	private BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue = new LinkedBlockingQueue<>();

	@Override
	public synchronized void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {

		try {
			queue.put(new Pair<AEventListener<? extends IListenerOwner>, AEvent>(listener, event));
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void processEvents() {
		while (!Thread.interrupted()) {
			try {
				Pair<AEventListener<? extends IListenerOwner>, AEvent> eventPair =
					queue.poll(Long.MAX_VALUE, TimeUnit.DAYS);
				eventPair.getFirst().handleEvent(eventPair.getSecond());
			}
			catch (InterruptedException e) {
				e.printStackTrace();
				break; // stop listening
			}
		}
	}

	@Override
	public void run() {
		registerEventListeners();
		processEvents();
		unregisterEventListeners();
	}
}
