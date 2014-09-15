/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import org.caleydo.core.event.AEvent;

import com.google.common.base.Predicate;

/**
 * @author Christian
 *
 */
public class UpdateDataCellSelectionValidatorEvent extends AEvent {

	private final Predicate<DataCellInfo> validator;

	public UpdateDataCellSelectionValidatorEvent(Predicate<DataCellInfo> validator) {
		this.validator = validator;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @return the validator, see {@link #validator}
	 */
	public Predicate<DataCellInfo> getValidator() {
		return validator;
	}

}
