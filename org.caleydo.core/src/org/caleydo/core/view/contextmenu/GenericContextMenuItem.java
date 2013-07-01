/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.contextmenu;

import java.util.List;

import org.caleydo.core.event.AEvent;

/**
 * generic implementation of a context menu item
 *
 * @author Samuel Gratzl
 *
 */
public class GenericContextMenuItem extends AContextMenuItem {
	public GenericContextMenuItem(String label, AEvent event) {
		setLabel(label);
		registerEvent(event);
	}

	public GenericContextMenuItem(String label, List<AEvent> events) {
		setLabel(label);
		for (AEvent event : events) {
			registerEvent(event);
		}
	}

	public GenericContextMenuItem(String label, EContextMenuType type, AEvent event) {
		setLabel(label);
		setType(type);
		registerEvent(event);
	}
}
