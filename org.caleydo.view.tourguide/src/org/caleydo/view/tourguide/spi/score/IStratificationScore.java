/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.spi.score;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;


/**
 * score based on a stratification
 *
 * @author Samuel Gratzl
 *
 */
public interface IStratificationScore extends IScore {
	Perspective getStratification();

	IComputeElement asComputeElement();
}
