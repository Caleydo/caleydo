/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.spi.compute;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.algorithm.IGroupAlgorithm;
import org.caleydo.view.tourguide.spi.score.IGroupBasedScore;
import org.caleydo.view.tourguide.spi.score.IRegisteredScore;
import org.caleydo.view.tourguide.spi.score.IScore;

/**
 * declares that the given {@link IScore} must be computed on a group base
 *
 * @author Samuel Gratzl
 *
 */
public interface IComputedGroupScore extends IRegisteredScore, IGroupBasedScore {
	/**
	 * already in the cache?
	 *
	 * @param a
	 * @return
	 */
	boolean contains(IComputeElement a, Group ag);

	/**
	 * put the result in the cache
	 *
	 * @param a
	 * @param value
	 */
	void put(Group ag, double value);

	/**
	 * returns the algorithm to compute this score
	 *
	 * @return
	 */
	IGroupAlgorithm getAlgorithm();

	/**
	 * returns the filter apply for skipping invalid combination pairs
	 *
	 * @return
	 */
	IComputeScoreFilter getFilter();
}
