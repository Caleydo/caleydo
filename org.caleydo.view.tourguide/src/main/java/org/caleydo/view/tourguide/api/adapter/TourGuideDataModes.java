/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.api.adapter;

import org.caleydo.view.tourguide.internal.adapter.PathwayDataMode;
import org.caleydo.view.tourguide.internal.adapter.StratificationDataMode;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideDataMode;

/**
 * common {@link ITourGuideDataMode} modes
 * 
 * @author Samuel Gratzl
 * 
 */
public class TourGuideDataModes {
	public static final ITourGuideDataMode STRATIFICATIONS = new StratificationDataMode();
	public static final ITourGuideDataMode PATHWAYS = new PathwayDataMode();

	private TourGuideDataModes() {

	}

	/**
	 * @param mode
	 * @return whether the given mode represents the standard stratification mode
	 */
	public static boolean areStratificatins(ITourGuideDataMode mode) {
		return mode instanceof StratificationDataMode;
	}

	/**
	 * @param mode
	 * @return whether the given mode represents the standard pathway mode
	 */
	public static boolean arePathways(ITourGuideDataMode mode) {
		return mode instanceof PathwayDataMode;
	}

}
