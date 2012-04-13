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
package org.caleydo.core.view.swt.collab;

import java.util.List;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.net.EventFilterBridge;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

/**
 * <p>
 * Saves the configuration of an {@link EventFilterBridge}.\
 * </p>
 * <p>
 * Saving is done by adding the related {@link EventFilterBridge} as listener to all of the selected events.
 * </p>
 * 
 * @author Werner Puff
 */
public class SaveEventBridgeConfigurationListener
	implements SelectionListener {

	/** Te {@link EventFilterBridge} to be configured */
	EventFilterBridge bridge;

	/** The {@link EventPublisher} the {@link EventFilterBridge} is listening to */
	EventPublisher publisher;

	/** The list of checkbox-style buttons, one for each possible event */
	List<Button> eventButtonList;

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void widgetSelected(SelectionEvent e) {
		publisher.removeListener(bridge);

		for (Button button : eventButtonList) {
			if (button.getSelection()) {
				Class<? extends AEvent> eventType;
				eventType = (Class<? extends AEvent>) button.getData(CollabView.ITEM_DATA_EVENT_TYPE);
				publisher.addListener(eventType, bridge);
			}
		}
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

	public List<Button> getEventButtonList() {
		return eventButtonList;
	}

	public void setEventButtonList(List<Button> eventButtonList) {
		this.eventButtonList = eventButtonList;
	}

}
