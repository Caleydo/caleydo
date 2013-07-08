/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.util;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.datadomain.pathway.PathwayDataDomain;

/**
 * @author Samuel Gratzl
 *
 */
public class PathwayOracle {
	/**
	 * checks whether the given source can be the underlying of a Pathway
	 * 
	 * @param source
	 * @return
	 */
	public static boolean canBeUnderlying(TablePerspective source) {
		return source.getDataDomain().hasIDCategory(
				((PathwayDataDomain) DataDomainManager.get().getDataDomainByType(
PathwayDataDomain.DATA_DOMAIN_TYPE))
						.getDavidIDType());
	}

}
