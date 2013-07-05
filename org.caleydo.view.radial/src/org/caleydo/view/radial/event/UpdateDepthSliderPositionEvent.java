/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial.event;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

/**
 * This event specifies the position (the selected value) of the (depth-) slider in the Toolbox of Radial
 * Layout.
 * 
 * @author Christian Partl
 */
@XmlRootElement
@XmlType
public class UpdateDepthSliderPositionEvent
	extends AEvent {

	private int iDepthSliderPosition = -1;

	public int getDepthSliderPosition() {
		return iDepthSliderPosition;
	}

	public void setDepthSliderPosition(int iDepthSliderPosition) {
		this.iDepthSliderPosition = iDepthSliderPosition;
	}

	@Override
	public boolean checkIntegrity() {
		if (iDepthSliderPosition == -1)
			throw new IllegalStateException("iDepthSliderPosition was not set");

		return true;
	}

}
