/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.impl;

import org.caleydo.vis.lineup.model.mapping.EStandardMappings;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;

/**
 * @author Samuel Gratzl
 *
 */
public class Utils {

	/**
	 * @return
	 */
	public static PiecewiseMapping createPValueMapping() {
		PiecewiseMapping m = new PiecewiseMapping(Float.NaN, 1);
		EStandardMappings.P_Q_VALUE.apply(m);
		return m;
	}

}
