/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.impl;

import java.util.List;
import java.util.Set;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.algorithm.IStratificationAlgorithm;
import org.eclipse.core.runtime.IProgressMonitor;

import com.google.common.collect.ImmutableSortedSet;

/**
 * @author Samuel Gratzl
 *
 */
public class GeneSetMappedAlgorithm implements IStratificationAlgorithm {
	private final Perspective perspective;
	private final boolean returnPercentage;
	private Set<Integer> ids;

	public GeneSetMappedAlgorithm(Perspective perspective, boolean percentage) {
		this.perspective = perspective;
		this.returnPercentage = percentage;
	}

	@Override
	public void init(IProgressMonitor monitor) {
		final IDType source = perspective.getIdType();
		IIDTypeMapper<Integer,Integer> mapper = IDMappingManagerRegistry.get().getIDMappingManager(source).getIDTypeMapper(source,source.getIDCategory().getPrimaryMappingType());
		ids = ImmutableSortedSet
.copyOf(mapper.apply(perspective.getVirtualArray()));
	}

	@Override
	public IDType getTargetType(IComputeElement a, IComputeElement b) {
		return perspective.getIdType().getIDCategory().getPrimaryMappingType();
	}

	@Override
	public final float compute(List<Set<Integer>> a, List<Set<Integer>> b, IProgressMonitor monitor) {
		return compute(a.iterator().next(), monitor);
	}

	private float compute(Set<Integer> geneSet, IProgressMonitor monitor) {
		int intersection = 0;
		for (Integer id : geneSet)
			if (ids.contains(id))
				intersection++;
		if (returnPercentage)
			return ((float) intersection) / geneSet.size();
		else
			return intersection;
	}

	@Override
	public String getAbbreviation() {
		return "MP";
	}

	@Override
	public String getDescription() {
		return returnPercentage ? "Percentage of Mapped Genes against " : "Number of Mapped Genes against";
	}

}
