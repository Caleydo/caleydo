/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.spi.compute;

import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.spi.score.IStratificationScore;

/**
 * declares that the given {@link IScore} must be computed on a stratification base
 *
 * @author Samuel Gratzl
 *
 */
public interface IComputedReferenceStratificationScore extends IComputedStratificationScore, IStratificationScore {
}
