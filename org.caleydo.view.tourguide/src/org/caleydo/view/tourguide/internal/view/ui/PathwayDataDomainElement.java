/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui;

import org.caleydo.view.tourguide.internal.model.PathwayDataDomainQuery;

public class PathwayDataDomainElement extends ADataDomainElement {

	public PathwayDataDomainElement(PathwayDataDomainQuery model) {
		super(model);
	}

	@Override
	public PathwayDataDomainQuery getModel() {
		return (PathwayDataDomainQuery) super.getModel();
	}

	@Override
	protected void onFilterEdit(boolean isStartEditing, Object payload) {

	}

	@Override
	protected String getLabel() {
		return getModel().getType().getName();
	}
}
