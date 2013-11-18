/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.api.vis;

import org.caleydo.view.tourguide.internal.OpenViewHandler;

/**
 * @author Samuel Gratzl
 * @author Christian Partl
 *
 */
public class TourGuideUtils {
	/**
	 * triggers to open tour guide
	 *
	 * @param mode
	 * @return
	 */
	public static ITourGuideView showTourGuide(final String secondaryID) {
		return OpenViewHandler.showTourGuide(secondaryID).getView();
	}

	/**
	 * Hides the tour guide instance with the specified mode.
	 *
	 * @param mode
	 */
	public static void hideTourGuide(final String secondaryID) {
		OpenViewHandler.hideTourGuide(secondaryID);
	}

}
