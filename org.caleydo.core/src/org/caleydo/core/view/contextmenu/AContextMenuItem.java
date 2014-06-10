/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.contextmenu;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.EventPublisher;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

/**
 * Abstract base class for items in the context menu. A item must be supplied with a string to display its function in
 * the context menu as well as with a list of event which can be triggered. The events are of type {@link AEvent} and
 * are published via the {@link EventPublisher}.
 *
 * @author Marc Streit
 */
public abstract class AContextMenuItem {

	private final List<AContextMenuItem> subMenuItems = new ArrayList<>();

	private String label = "<not set>";

	private final List<AEvent> events = new ArrayList<>();

	/**
	 * advanced context menu types: radio and check
	 */
	private EContextMenuType type = EContextMenuType.NORMAL;
	/**
	 * state of an advanced context menu type
	 */
	private boolean state;

	/**
	 * an optional input stream to a image, which should be shown
	 */
	private URL imageURL;

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public AContextMenuItem setType(EContextMenuType type) {
		this.type = type;
		return this;
	}

	/**
	 * @return the type, see {@link #type}
	 */
	public EContextMenuType getType() {
		return type;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public AContextMenuItem setState(boolean state) {
		this.state = state;
		return this;
	}

	/**
	 * @return the state, see {@link #state}
	 */
	public boolean isState() {
		return state;
	}

	/**
	 * Sets the event which is associated with the item. This event will be triggered when requested by the context
	 * menu. It is mandatory to set an event.
	 *
	 * @param event
	 *            the event triggered when requested by the context menu
	 */
	public void registerEvent(AEvent event) {
		this.events.add(event);
	}

	/**
	 * Triggers the supplied event via the event publishing system
	 */
	public void triggerEvent() {
		if (events != null && events.size() > 0) {
			for (AEvent event : events) {
				EventPublisher.INSTANCE.triggerEvent(event);
			}
		}
	}

	protected void clearEvents() {
		if (events != null) {
			events.clear();
		}
	}

	public void addSubItem(AContextMenuItem contextMenuItem) {
		subMenuItems.add(contextMenuItem);
	}

	/**
	 * @return the subMenuItems
	 */
	public List<AContextMenuItem> getSubMenuItems() {
		return subMenuItems;
	}

	public enum EContextMenuType {
		NORMAL, RADIO, CHECK
	}

	/**
	 * @param imageURL
	 *            setter, see {@link imageURL}
	 */
	public void setImageURL(URL imageURL) {
		this.imageURL = imageURL;
	}

	public Image getImage(Device device) {
		if (imageURL == null)
			return null;
		try (InputStream in = imageURL.openStream()) {
			return new Image(device, in);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
