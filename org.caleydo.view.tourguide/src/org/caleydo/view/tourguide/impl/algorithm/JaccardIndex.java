/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.impl.algorithm;

import java.util.Set;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.algorithm.IGroupAlgorithm;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Samuel Gratzl
 *
 */
public class JaccardIndex implements IGroupAlgorithm {
	private static final JaccardIndex instance = new JaccardIndex();

	public static JaccardIndex get() {
		return instance;
	}

	private JaccardIndex() {

	}

	@Override
	public void init(IProgressMonitor monitor) {
		// nothing todo
	}

	@Override
	public String getAbbreviation() {
		return "JI";
	}

	@Override
	public String getDescription() {
		return "Jaccard Index against ";
	}

	@Override
	public IDType getTargetType(IComputeElement a, IComputeElement b) {
		return a.getIdType();
	}

	@Override
	public float compute(Set<Integer> a, Group ag, Set<Integer> b, Group bg, IProgressMonitor monitior) {
		return Statistics.jaccardIndex(a, b);
	}
}
