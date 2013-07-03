/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.event;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

/**
 * Event that changes the trend highlight mode for StratomeX.
 *
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class ConnectionsModeEvent
	extends AEvent {

	private boolean connectionsShowOnlySelected;
	private boolean connectionsHighlightDynamic;

	/**
	 * Determines the focus highlight dynamically in a range between 0 and 1
	 */
	private float connectionsFocusFactor;

	public ConnectionsModeEvent(boolean connectionsShowOnlySelected, boolean connectionsHighlighDynamic, float focusFactor) {
		this.connectionsShowOnlySelected = connectionsShowOnlySelected;
		this.connectionsHighlightDynamic = connectionsHighlighDynamic;
		this.connectionsFocusFactor = focusFactor;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	public boolean isConnectionsHighlightDynamic() {
		return connectionsHighlightDynamic;
	}

	public boolean isConnectionsShowOnlySelected() {
		return connectionsShowOnlySelected;
	}

	public float getFocusFactor() {
		return connectionsFocusFactor;
	}
}
