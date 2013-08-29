/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.spi.score;

import java.util.Collection;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;

/**
 * @author Samuel Gratzl
 *
 */
public interface IGroupSelector {
	Group select(IScore score, IComputeElement elem, Collection<Group> groups);
}
