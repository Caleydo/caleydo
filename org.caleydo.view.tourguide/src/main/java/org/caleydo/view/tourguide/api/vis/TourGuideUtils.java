/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.api.vis;

import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.OpenViewHandler;

/**
 * @author Samuel Gratzl
 *
 */
public class TourGuideUtils {
	/**
	 * triggers to open tour guide
	 *
	 * @param mode
	 * @return
	 */
	public static ITourGuideView showTourGuide(final EDataDomainQueryMode mode) {
		return OpenViewHandler.showTourGuide(mode).getView();
	}

}
