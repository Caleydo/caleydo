/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.spi.compute;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;

/**
 * defines whether for a given combination of stratification and group the score should be computed or not
 * 
 * @author Samuel Gratzl
 * 
 */
public interface IComputeScoreFilter {
	boolean doCompute(IComputeElement a, Group ag, IComputeElement reference, Group referenceGroup);
}
