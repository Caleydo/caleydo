/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.specific;

import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;

/**
 * @author Samuel Gratzl
 *
 */
public class DataDomainModeSpecifics {
	/**
	 * @param mode
	 * @return
	 */
	public static IDataDomainQueryModeSpecfics of(EDataDomainQueryMode mode) {
		switch (mode) {
		case OTHER:
			return new NumericalSpecifics();
		case PATHWAYS:
			return new PathwaySpecifics();
		case STRATIFICATIONS:
			return new StratificationSpecifics();
		}
		throw new IllegalStateException();
	}
}
