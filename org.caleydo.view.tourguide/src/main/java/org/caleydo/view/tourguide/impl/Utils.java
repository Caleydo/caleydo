/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.impl;

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
		m.fromJavaScript("if (value < 0.0 || value > 1.0) return NaN\n"
				+ "return linear(0.0, -log(Math.max(10e-10,value_min)), -log(Math.max(10e-10,value)), 0.0, 1.0)");
		return m;
	}

}
