/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.stratomex.t;

import org.caleydo.view.tourguide.spi.adapter.ITourGuideAdapter;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideAdapterFactory;

/**
 * @author Samuel Gratzl
 *
 */
public class PathwayStratomexAdapterFactory implements ITourGuideAdapterFactory {
	@Override
	public ITourGuideAdapter create() {
		return new PathwayStratomexAdapter();
	}

	@Override
	public String getSecondaryID() {
		return "PATHWAYS";
	}
}
