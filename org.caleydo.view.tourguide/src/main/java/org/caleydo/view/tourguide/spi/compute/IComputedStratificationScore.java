/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.spi.compute;

import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.algorithm.IComputeScoreFilter;
import org.caleydo.view.tourguide.spi.algorithm.IStratificationAlgorithm;
import org.caleydo.view.tourguide.spi.score.IRegisteredScore;
import org.caleydo.view.tourguide.spi.score.IScore;

/**
 * declares that the given {@link IScore} must be computed on a stratification base
 *
 * @author Samuel Gratzl
 *
 */
public interface IComputedStratificationScore extends IRegisteredScore {
	/**
	 * already in the cache?
	 *
	 * @param a
	 * @return
	 */
	boolean contains(IComputeElement a);

	/**
	 * put the result in the cache
	 *
	 * @param a
	 * @param value
	 */
	void put(IComputeElement a, double value);

	/**
	 * returns the algorithm to compute this score
	 *
	 * @return
	 */
	IStratificationAlgorithm getAlgorithm();

	/**
	 * returns the filter apply for skipping invalid combination pairs
	 *
	 * @return
	 */
	IComputeScoreFilter getFilter();
}
