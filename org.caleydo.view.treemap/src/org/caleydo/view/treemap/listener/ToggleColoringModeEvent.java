/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.treemap.listener;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

/**
 * Event for toggling coloring mode.
 * 
 * @author Michael Lafer
 */

@XmlRootElement
@XmlType
public class ToggleColoringModeEvent
	extends AEvent {

	private boolean bCalculateColor;

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	public void setCalculateColor(boolean flag) {
		bCalculateColor = flag;
	}

	public boolean isCalculateColor() {
		return bCalculateColor;
	}

}
