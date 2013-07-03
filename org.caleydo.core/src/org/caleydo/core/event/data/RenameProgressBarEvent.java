/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

@XmlRootElement
@XmlType
public class RenameProgressBarEvent
	extends AEvent {

	private String progressbarTitle = null;

	public RenameProgressBarEvent() {
		// nothing to initialize here
	}

	public RenameProgressBarEvent(String stProgressBarTitle) {
		this.progressbarTitle = stProgressBarTitle;
	}

	public String getProgressbarTitle() {
		return progressbarTitle;
	}

	@Override
	public boolean checkIntegrity() {
		if (progressbarTitle == null)
			return false;
		return true;
	}

	public void setProgressbarTitle(String progressbarTitle) {
		this.progressbarTitle = progressbarTitle;
	}
}
