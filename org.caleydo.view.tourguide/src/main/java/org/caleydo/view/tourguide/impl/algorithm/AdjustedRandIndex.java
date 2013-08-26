/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.impl.algorithm;

import java.util.List;
import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.algorithm.IStratificationAlgorithm;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Samuel Gratzl
 *
 */
public class AdjustedRandIndex implements IStratificationAlgorithm {
	private static final AdjustedRandIndex instance = new AdjustedRandIndex();

	public static AdjustedRandIndex get() {
		return instance;
	}

	private AdjustedRandIndex() {

	}

	@Override
	public void init(IProgressMonitor monitor) {
		// nothing todo
	}

	@Override
	public String getAbbreviation() {
		return "AR";
	}

	@Override
	public String getDescription() {
		return "Adjusted Rand of ";
	}

	@Override
	public IDType getTargetType(IComputeElement a, IComputeElement b) {
		return a.getIdType();
	}

	@Override
	public float compute(List<Set<Integer>> a, List<Set<Integer>> b, IProgressMonitor monitor) {
		return Statistics.randIndex(a, b);
	}
}

