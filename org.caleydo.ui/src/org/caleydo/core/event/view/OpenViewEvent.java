/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.view;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

/**
 * Event to create views from its view type (String).
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class OpenViewEvent
	extends AEvent {

	/** serialized form of the view to create */
	private String viewType;

	@Override
	public boolean checkIntegrity() {
		if (viewType == null) {
			throw new IllegalStateException("the targetApplicationID has not been set");
		}

		return true;
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}
}
