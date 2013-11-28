/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.state;


/**
 * simple implementation of a {@link IState}
 *
 * @author Samuel Gratzl
 *
 */
public class SimpleState implements IState {
	private final String label;

	public SimpleState(String label) {
		this.label = label;
	}

	/**
	 * @return the label, see {@link #label}
	 */
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void onEnter() {

	}

	@Override
	public void onLeave() {

	}

}
