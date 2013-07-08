/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.virtualarray.events;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.event.AEvent;

/**
 * Event that signals that the virtual array has changed. VA users have to load the new one from the UseCase
 * if only the vaType is provided, or use the va attached.
 *
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class ReplacePerspectiveEvent
	extends AEvent {

	private PerspectiveInitializationData data;
	private String perspectiveID = null;

	/**
	 * default no-arg constructor.
	 */
	public ReplacePerspectiveEvent() {
		// nothing to initialize here
	}

	/**
	 * If no set is specified, the use case should send this to all suitable sets
	 *
	 * @param idCategory
	 * @param perspectiveID
	 * @param virtualArray
	 */
	public ReplacePerspectiveEvent(String dataDomainID, String perspectiveID,
		PerspectiveInitializationData data) {
		this.eventSpace = dataDomainID;
		this.perspectiveID = perspectiveID;
		this.data = data;
	}

	public PerspectiveInitializationData getPerspectiveInitializationData() {
		return data;
	}

	@Override
	public boolean checkIntegrity() {
		if (eventSpace == null || perspectiveID == null)
			return false;

		return true;
	}

	public void setPerspectiveInitializationData(PerspectiveInitializationData data) {
		this.data = data;
	}

	public String getPerspectiveID() {
		return perspectiveID;
	}
}
