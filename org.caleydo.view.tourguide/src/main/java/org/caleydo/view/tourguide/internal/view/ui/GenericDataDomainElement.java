/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui;

import org.caleydo.view.tourguide.api.model.ADataDomainQuery;

/**
 * generic version of a {@link ADataDomainElement} without any special filter handling
 * 
 * @author Samuel Gratzl
 * 
 */
public class GenericDataDomainElement extends ADataDomainElement {

	public GenericDataDomainElement(ADataDomainQuery model) {
		super(model);
	}


	@Override
	protected void onFilterEdit(boolean isStartEditing, Object payload, int minSize) {

	}
}
