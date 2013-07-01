/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.spi.algorithm;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * similar to {@link IGroupAlgorithm} but this time for a whole stratification consisting of a a list of groups
 *
 * @author Samuel Gratzl
 *
 */
public interface IStratificationAlgorithm extends IAlgorithm{
	/**
	 * computes the score between the two stratifications identified by their collection of group sets
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	float compute(List<Set<Integer>> a, List<Set<Integer>> b, IProgressMonitor monitor);

	/**
	 * returns the abbreviation of this algorithm
	 *
	 * @return
	 */
	@Override
	String getAbbreviation();

	@Override
	String getDescription();
}
