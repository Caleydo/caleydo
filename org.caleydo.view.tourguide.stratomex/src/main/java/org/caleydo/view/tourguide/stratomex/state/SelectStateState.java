/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex.state;

import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.state.SimpleState;

/**
 * @author Samuel Gratzl
 *
 */
public class SelectStateState extends SimpleState {
	private final EDataDomainQueryMode mode;

	public SelectStateState(String label, EDataDomainQueryMode mode) {
		super(label);
		this.mode = mode;
	}

	/**
	 * @return the mode, see {@link #mode}
	 */
	public EDataDomainQueryMode getMode() {
		return mode;
	}



}
