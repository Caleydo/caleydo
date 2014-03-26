/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage;

import org.caleydo.view.tourguide.spi.adapter.ITourGuideAdapter;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideAdapterFactory;

/**
 * @author Samuel Gratzl
 *
 */
public class EntourageStratificationAdapterFactory implements ITourGuideAdapterFactory {
	public static final String SECONDARY_ID = "entourage.stratification";

	@Override
	public String getSecondaryID() {
		return SECONDARY_ID;
	}
	@Override
	public ITourGuideAdapter create() {
		return new EntourageStratificationAdapter();
	}

}
