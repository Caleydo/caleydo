/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.spi.compute;

import org.caleydo.view.tourguide.spi.score.IGroupScore;
import org.caleydo.view.tourguide.spi.score.IScore;


/**
 * declares that the given {@link IScore} must be computed on a group base
 *
 * @author Samuel Gratzl
 *
 */
public interface IComputedReferenceGroupScore extends IComputedGroupScore, IGroupScore {
}
